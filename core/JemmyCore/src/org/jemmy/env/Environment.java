/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.jemmy.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.jemmy.JemmyException;
import org.jemmy.action.ActionExecutor;
import org.jemmy.action.DefaultExecutor;
import org.jemmy.control.Wrap;
import org.jemmy.image.ImageCapturer;
import org.jemmy.image.ImageLoader;
import org.jemmy.input.CharBindingMap;
import org.jemmy.interfaces.ControlInterfaceFactory;
import org.jemmy.timing.Waiter;

/**
 * @author shura, mrkam, erikgreijus
 */
public class Environment {
    public static final String JEMMY_PROPERTIES_FILE_PROPERTY = "jemmy.properties";
    public static final String TIMEOUTS_FILE_PROPERTY = "timeouts";
    /**
     * Information output for Environment class
     */
    public static final String OUTPUT = Environment.class.getName() + ".OUTPUT";
    private final static Environment env = new Environment(null);

    public static Environment getEnvironment() {
        return env;
    }

    static {
        env.setOutput(new TestOut(System.in, System.out, System.err));
        env.setExecutor(new DefaultExecutor());
    }
    private HashMap<PropertyKey, Object> environment = new HashMap<PropertyKey, Object>();
    private Environment parent;

    public Environment(Environment parent) {
        this.parent = parent;
        environment = new HashMap<PropertyKey, Object>();
        if (parent == null) {
            loadProperties(System.getProperty(JEMMY_PROPERTIES_FILE_PROPERTY));
        }
    }

    public Environment() {
        this(getEnvironment());
    }

    public Environment getParentEnvironment() {
        return parent;
    }

    public void setParentEnvironment(Environment parent) {
        this.parent = parent;
    }

    public void loadProperties(String propFileName) {
        if (propFileName == null || propFileName.length() == 0) {
            propFileName = System.getProperty("user.home") + File.separator + ".jemmy.properties";
        }
        File propFile = new File(propFileName);
        System.out.println("Loading jemmy properties from " + propFile);
        if (propFile.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(propFile));
            } catch (IOException ex) {
                throw new JemmyException("Unable to load properties", ex, propFileName);
            }
            for (String k : props.stringPropertyNames()) {
                if (k.equals(TIMEOUTS_FILE_PROPERTY)) {
                    loadTimeouts(propFile.getParentFile(), props.getProperty(k));
                } else {
                    setProperty(k, props.getProperty(k));
                }
            }
        } else {
            System.out.println("Property file " + propFile + " does not exists. Ignoring.");
        }
    }

    private void loadTimeouts(File propDir, String file) {
        File timeoutsFile = new File(file);
        if (!timeoutsFile.isAbsolute()) {
            timeoutsFile = new File(propDir.getAbsolutePath() + File.separator + file);
        }
        System.out.println("Loading timeouts from " + timeoutsFile.getAbsolutePath());
        try {
            Properties timeouts = new Properties();
            timeouts.load(new FileInputStream(timeoutsFile));
            for (String k : timeouts.stringPropertyNames()) {
                setTimeout(k, Long.parseLong(timeouts.getProperty(k)));
            }
        } catch (IOException ex) {
            throw new JemmyException("Unable to load timeouts", ex, timeoutsFile.getAbsolutePath());
        }
    }

    public List<?> get(Class cls) {
        Set<PropertyKey> all = environment.keySet();
        ArrayList<Object> result = new ArrayList<Object>();
        for (PropertyKey key : all) {
            if (key.getCls().equals(cls)) {
                result.add(environment.get(key));
            }
        }
        return result;
    }

    public ActionExecutor setExecutor(ActionExecutor defaultExecutor) {
        return (ActionExecutor) setProperty(ActionExecutor.class, defaultExecutor);
    }

    public ActionExecutor getExecutor() {
        ActionExecutor res = (ActionExecutor) getProperty(ActionExecutor.class);
        if (res == null) {
            String executorClassName = (String) getProperty(ActionExecutor.ACTION_EXECUTOR_PROPERTY);
            try {
                res = ActionExecutor.class.cast(Class.forName(executorClassName).newInstance());
                setExecutor(res);
            } catch (InstantiationException ex) {
                throw new JemmyException("Unable to instantiate executor ", ex, executorClassName);
            } catch (IllegalAccessException ex) {
                throw new JemmyException("Unable to instantiate executor ", ex, executorClassName);
            } catch (ClassNotFoundException ex) {
                throw new JemmyException("No executorclass ", ex, executorClassName);
            }
        }
        return res;
    }

    public <T> T setProperty(Class<T> cls, Object ref, T obj) {
        return setProperty(new PropertyKey<T>(cls, ref), obj);
    }

    private <T> T setPropertyIfNotSet(Class<T> cls, Object ref, T obj) {
        return setPropertyIfNotSet(new PropertyKey<T>(cls, ref), obj);
    }

    private <T> T getProperty(Class<T> cls, Object ref) {
        return getProperty(cls, ref, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Class cls, Object ref, T defaultValue) {
        for (PropertyKey pk : environment.keySet()) {
            if (pk.equals(new PropertyKey(cls, ref))) {
                return (T) environment.get(pk);
            }
        }
        if (getParentEnvironment() != null) {
            return getParentEnvironment().getProperty(cls, ref, defaultValue);
        } else {
            return defaultValue;
        }
    }

    /**
     * @param <T> todo document
     * @param cls todo document
     * @param obj if null then property is removed
     * @return todo document
     */
    public <T> T setProperty(Class<T> cls, T obj) {
        return setProperty(cls, null, obj);
    }

    /**
     * @param <T> todo document
     * @param cls todo document
     * @param obj if null then property is removed
     * @return todo document
     */
    public <T> T setPropertyIfNotSet(Class<T> cls, T obj) {
        return setPropertyIfNotSet(cls, null, obj);
    }

    public <T> T getProperty(Class<T> cls) {
        return getProperty(cls, null);
    }

    /**
     * @param name todo document
     * @param obj if null then property is removed
     * @return todo document
     */
    public Object setProperty(String name, Object obj) {
        return setProperty(Object.class, name, obj);
    }

    public Object setPropertyIfNotSet(String name, Object obj) {
        return setPropertyIfNotSet(Object.class, name, obj);
    }

    public Object getProperty(String name) {
        return getProperty(Object.class, name);
    }

    public Object getProperty(String name, Object defaultValue) {
        return getProperty(Environment.class, name, defaultValue);
    }

    private <T> T setProperty(PropertyKey<T> key, Object value) {
        if (value == null) {
            return key.cls.cast(environment.remove(key));
        } else {
            return key.cls.cast(environment.put(key, value));
        }
    }

    private <T> T setPropertyIfNotSet(PropertyKey<T> key, T value) {
        if (getParentEnvironment() != null) {
            T res = key.cls.cast(getParentEnvironment().getProperty(key));
            if (res != null) {
                return res;
            }
        }
        T res = key.cls.cast(environment.get(key));
        if (res == null) {
            return key.cls.cast(environment.put(key, value));
        } else {
            return res;
        }
    }

    private Object getProperty(PropertyKey key) {
        return environment.get(key);
    }

    public TestOut setOutput(TestOut out) {
        return (TestOut) setProperty(TestOut.class, out);
    }

    public TestOut getOutput() {
        return (TestOut) getProperty(TestOut.class);
    }

    /**
     * Set some specific output. All classes which provide output should use
     * some specific outputs. Please consult javadoc for a class in question.
     * Use <code>null</code> to unset the property.
     *
     * @param outputName todo document
     * @param out todo document
     * @return todo document
     */
    public TestOut setOutput(String outputName, TestOut out) {
        return (TestOut) setProperty(TestOut.class, outputName, out);
    }

    /**
     * Initializes some specific output only if it is not yet set.
     *
     * @param outputName todo document
     * @param out todo document
     * @return todo document
     */
    public TestOut initOutput(String outputName, TestOut out) {
        TestOut res = (TestOut) getProperty(TestOut.class, outputName);
        if (res == null) {
            return setOutput(outputName, out);
        } else {
            return res;
        }
    }

    /**
     * Get's a specific output. If nothing assigned, returns
     * <code>getOutput()</code>
     *
     * @param outputName todo document
     * @return todo document
     */
    public TestOut getOutput(String outputName) {
        TestOut res = (TestOut) getProperty(TestOut.class, outputName);
        return (res != null) ? res : getOutput();
    }

    public Waiter getWaiter(Timeout timeout) {
        return getWaiter(timeout.getName());
    }

    public Waiter getWaiter(String timeoutName) {
        return new Waiter(getTimeout(timeoutName));
    }

    public Timeout getTimeout(Timeout timeout) {
        return getTimeout(timeout.getName());
    }

    public Timeout getTimeout(String name) {
        return (Timeout) getProperty(Timeout.class, name);
    }

    /**
     * Sets timeout.
     *
     * @param timeout Timeout to set.
     * @return replaced timeout if it was already set.
     */
    public Timeout setTimeout(Timeout timeout) {
        return (Timeout) setProperty(Timeout.class, timeout.getName(), timeout);
    }

    /**
     * Initializes timeout only if it is not set.
     *
     * @param timeout Timeout to set.
     * @return replaced timeout if it was already set.
     */
    public Timeout initTimeout(Timeout timeout) {
        if (getProperty(Timeout.class, timeout.getName()) == null) {
            return setTimeout(timeout);
        }
        return getTimeout(timeout);
    }

    /**
     * Sets new value for the timeout specified by Timeout object instance.
     *
     * @param timeout Timeout object instance which identifies the name of the
     * timeout to set.
     * @param value new value for the timout.
     * @return replaced timeout if it was already set.
     */
    public Timeout setTimeout(Timeout timeout, long value) {
        return setTimeout(timeout.getName(), value);
    }

    /**
     * Sets new value for the timeout.
     *
     * @param name Name of the timeout.
     * @param value Value of the timeout.
     * @return replaced timeout if it was already set.
     */
    public Timeout setTimeout(String name, long value) {
        return setTimeout(new Timeout(name, value));
    }

    public CharBindingMap getBindingMap() {
        return (CharBindingMap) getProperty(CharBindingMap.class);
    }

    public CharBindingMap setBindingMap(CharBindingMap map) {
        return (CharBindingMap) setProperty(CharBindingMap.class, map);
    }

    public ImageLoader getImageLoader() {
        ImageLoader res = (ImageLoader) getProperty(ImageLoader.class);
        if (res == null) {
            String loaderClass = (String) getProperty(Wrap.IMAGE_LOADER_PROPERTY);
            if (loaderClass == null) {
                throw new IllegalStateException("No image loader provided!");
            }
            try {
                res = ImageLoader.class.cast(Class.forName(String.class.cast(loaderClass)).newInstance());
                setImageLoader(res);
            } catch (InstantiationException ex) {
                throw new JemmyException("Unable to instantiate image loader ", ex, loaderClass);
            } catch (IllegalAccessException ex) {
                throw new JemmyException("Unable to instantiate image loader ", ex, loaderClass);
            } catch (ClassNotFoundException ex) {
                throw new JemmyException("No image loader class ", ex, loaderClass);
            }
        }
        return res;
    }

    public ImageCapturer getImageCapturer() {
        ImageCapturer res = (ImageCapturer) getProperty(ImageCapturer.class);
        if (res == null) {
            String capturerClass = (String) getProperty(Wrap.IMAGE_CAPTURER_PROPERTY);
            if (capturerClass == null) {
                throw new IllegalStateException("No image capturer provided!");
            }
            try {
                res = ImageCapturer.class.cast(Class.forName(String.class.cast(capturerClass)).newInstance());
                setImageCapturer(res);
            } catch (InstantiationException ex) {
                throw new JemmyException("Unable to instantiate image capturer ", ex, capturerClass);
            } catch (IllegalAccessException ex) {
                throw new JemmyException("Unable to instantiate image capturer ", ex, capturerClass);
            } catch (ClassNotFoundException ex) {
                throw new JemmyException("No image capturer class ", ex, capturerClass);
            }
        }
        return res;
    }

    public ImageLoader setImageLoader(ImageLoader imageLoader) {
        return (ImageLoader) setProperty(ImageLoader.class, imageLoader);
    }

    public ImageCapturer setImageCapturer(ImageCapturer imageCapturer) {
        getOutput(OUTPUT).println("ImageCapturer set to " + imageCapturer);
        return (ImageCapturer) setProperty(ImageCapturer.class, imageCapturer);
    }

    public ControlInterfaceFactory getInputFactory() {
        ControlInterfaceFactory res = (ControlInterfaceFactory) getProperty(ControlInterfaceFactory.class);
        if (res == null) {
            String factoryClass = (String) getProperty(Wrap.INPUT_FACTORY_PROPERTY);
            if (factoryClass != null) {
                try {
                    res = ControlInterfaceFactory.class.cast(Class.forName(String.class.cast(factoryClass)).newInstance());
                    setInputFactory(res);
                } catch (InstantiationException ex) {
                    throw new JemmyException("Unable to instantiate input factory", ex, factoryClass);
                } catch (IllegalAccessException ex) {
                    throw new JemmyException("Unable to instantiate input factory", ex, factoryClass);
                } catch (ClassNotFoundException ex) {
                    throw new JemmyException("Unable to load input factory", ex, factoryClass);
                }
            }
        }
        return res;
    }

    public ControlInterfaceFactory setInputFactory(ControlInterfaceFactory factory) {
        getOutput(OUTPUT).println("Input factory set to " + factory);
        return (ControlInterfaceFactory) setProperty(ControlInterfaceFactory.class, factory);
    }

    private static class PropertyKey<TYPE> {

        private Class<TYPE> cls;
        private Object ref;

        public PropertyKey(Class<TYPE> cls, Object ref) {
            this.cls = cls;
            this.ref = ref;
        }

        private PropertyKey(Class<TYPE> cls) {
            this(cls, null);
        }

        public Class<TYPE> getCls() {
            return cls;
        }

        public Object getRef() {
            return ref;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PropertyKey other = (PropertyKey) obj;
            if (this.cls != other.cls && (this.cls == null || !this.cls.equals(other.cls))) {
                return false;
            }
            if (this.ref != other.ref && (this.ref == null || !this.ref.equals(other.ref))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + (this.cls != null ? this.cls.hashCode() : 0);
            hash = 41 * hash + (this.ref != null ? this.ref.hashCode() : 0);
            return hash;
        }
    }
}
