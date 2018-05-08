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
package org.jemmy.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.jemmy.JemmyException;
import org.jemmy.Point;
import org.jemmy.Rectangle;
import org.jemmy.TimeoutExpiredException;
import org.jemmy.action.GetAction;
import org.jemmy.env.Environment;
import org.jemmy.env.TestOut;
import org.jemmy.env.Timeout;
import org.jemmy.image.Image;
import org.jemmy.interfaces.*;
import org.jemmy.timing.State;

/**
 * This is a wrap which holds reference to a control without UI hierarchy. It
 * also encapsulates all the logic to deal with the underlying control, in terms
 * of implementations of ControlInterface.
 *
 * @see Wrap#as(java.lang.Class)
 * @see Wrap#is(java.lang.Class)
 * @param <CONTROL> type of the encapsulated object.
 * @author shura, erikgreijus
 */
@ControlType(Object.class)
@ControlInterfaces({Mouse.class, Keyboard.class, Drag.class})
public abstract class Wrap<CONTROL extends Object> {

    /**
     *
     */
    public static final String BOUNDS_PROP_NAME = "bounds";
    /**
     *
     */
    public static final String CLICKPOINT_PROP_NAME = "clickPoint";
    /**
     *
     */
    public static final String CONTROL_CLASS_PROP_NAME = "control.class";
    /**
     *
     */
    public static final String CONTROL_PROP_NAME = "control";
    /**
     *
     */
    public static final String INPUT_FACTORY_PROPERTY = "input.control.interface.factory";
    /**
     *
     */
    public static final String IMAGE_LOADER_PROPERTY = "image.loader";
    /**
     *
     */
    public static final String IMAGE_CAPTURER_PROPERTY = "image.capturer";
    /**
     *
     */
    public static final String TEXT_PROP_NAME = "text";
    /**
     *
     */
    public static final String POSITION_PROP_NAME = "position";
    /**
     *
     */
    public static final String VALUE_PROP_NAME = "value";
    /**
     *
     */
    public static final String WRAPPER_CLASS_PROP_NAME = "wrapper.class";
    /**
     *
     */
    public static final String TOOLTIP_PROP_NAME = "tooltip";
    /**
     *
     */
    public static final String NAME_PROP_NAME = "name";
    /**
     *
     */
    public static final Timeout WAIT_STATE_TIMEOUT = new Timeout("wait.state", 1000);
    /**
     *
     */
    public static final String OUTPUT = Wrap.class.getName() + ".OUTPUT";
    private static DefaultWrapper theWrapper = new DefaultWrapper(Environment.getEnvironment());

    static {
        Environment.getEnvironment().initTimeout(WAIT_STATE_TIMEOUT);
        Environment.getEnvironment().initOutput(OUTPUT, TestOut.getNullOutput());
        Environment.getEnvironment().initTimeout(Mouse.CLICK);
        Environment.getEnvironment().initTimeout(Drag.BEFORE_DRAG_TIMEOUT);
        Environment.getEnvironment().initTimeout(Drag.BEFORE_DROP_TIMEOUT);
        Environment.getEnvironment().initTimeout(Drag.IN_DRAG_TIMEOUT);
        Environment.getEnvironment().initTimeout(Keyboard.PUSH);
    }

    /**
     *
     * @return
     */
    public static DefaultWrapper getWrapper() {
        return theWrapper;
    }
    CONTROL node;
    Environment env;

    /**
     * Fur null source.
     *
     * @see org.jemmy.env.Environment
     * @param env The environment
     */
    protected Wrap(Environment env) {
        this.env = env;
        node = null;
        fillTheProps(false);
    }

    /**
     *
     * @see org.jemmy.env.Environment
     * @param env The environment
     * @param node The encapsulated object
     */
    protected Wrap(Environment env, CONTROL node) {
        this.env = env;
        this.node = node;
    }

    /**
     *
     * @see org.jemmy.env.Environment
     * @return environment instance used by this
     */
    public Environment getEnvironment() {
        return env;
    }

    public void setEnvironment(Environment env) {
        this.env = env;
    }

    /**
     *
     * @return The encapsulated object
     */
    @Property(CONTROL_PROP_NAME)
    public CONTROL getControl() {
        return node;
    }

    /**
     * Return default point to click, drag. This implementation returns the
     * center must be overriden if something different is desired.
     *
     * @return
     */
    @Property(CLICKPOINT_PROP_NAME)
    public Point getClickPoint() {
        return new Point(getScreenBounds().width / 2, (getScreenBounds().height / 2));
    }

    /**
     * Returns control bounds in screen coordinates. These bounds could include
     * parts that are covered by other controls or clipped out by parent
     * components. If the control is not shown {@linkplain
     * JemmyException JemmyException} will be thrown.
     *
     * @return control bounds in screen coordinates.
     * @throws JemmyException if the control is not visible
     */
    @Property(BOUNDS_PROP_NAME)
    public abstract Rectangle getScreenBounds();

    /**
     * Transforms point in local control coordinate system to screen
     * coordinates.
     *
     * @param local
     * @return
     * @see #toLocal(org.jemmy.Point)
     */
    public Point toAbsolute(Point local) {
        Rectangle bounds = getScreenBounds();
        return local.translate(bounds.x, bounds.y);
    }

    /**
     * Transforms point in screen coordinates to local control coordinate
     * system.
     *
     * @param local
     * @return coordinates which should be used for mouse operations.
     * @see #toAbsolute(org.jemmy.Point)
     */
    public Point toLocal(Point local) {
        Rectangle bounds = getScreenBounds();
        return local.translate(-bounds.x, -bounds.y);
    }

    /**
     * Captures the screen area held by the component. ImageFactory performs the
     * actual capturing.
     *
     * @return TODO find a replacement
     */
    public Image getScreenImage() {
        Rectangle bounds = getScreenBounds();
        return getScreenImage(new Rectangle(0, 0, bounds.width, bounds.height));
    }

    /**
     * Captures portion of the screen area held by the component. ImageFactory
     * performs the actual capturing.
     *
     * @param rect Part of the control to capture
     * @return TODO find a replacement
     */
    public Image getScreenImage(Rectangle rect) {
        if (getEnvironment().getImageCapturer() == null) {
            throw new JemmyException("Image capturer is not specified.");
        }
        return getEnvironment().getImageCapturer().capture(this, rect);
    }

    /**
     * Waits for a portion of image to be exact the same as the parameter.
     *
     * @see Wrap#as(java.lang.Class)
     * @param golden
     * @param rect A portion of control to compare.
     * @param resID ID of a result image to save in case of failure. No image
     * saved if null.
     * @param diffID ID of a diff image to save in case of failure. No image
     * saved if null.
     */
    public void waitImage(final Image golden, final Rectangle rect, String resID, String diffID) {
        try {
            waitState(new State<Object>() {

                public Object reached() {
                    return (getScreenImage(rect).compareTo(golden) == null) ? true : null;
                }

                @Override
                public String toString() {
                    return "Control having expected image";
                }
            });
        } catch (TimeoutExpiredException e) {
            if (diffID != null) {
                getEnvironment().getOutput(OUTPUT).println("Saving difference to " + diffID);
                getScreenImage(rect).compareTo(golden).save(diffID);
            }
            throw e;
        } finally {
            if (resID != null) {
                getEnvironment().getOutput(OUTPUT).println("Saving result to " + resID);
                getScreenImage(rect).save(resID);
            }
        }
    }

    /**
     * Waits for image to be exact the same as the parameter.
     *
     * @see Wrap#as(java.lang.Class)
     * @param golden
     * @param resID ID of a result image to save in case of failure. No image
     * saved if null.
     * @param diffID ID of a diff image to save in case of failure. No image
     * saved if null.
     */
    public void waitImage(final Image golden, String resID, String diffID) {
        Rectangle bounds = getScreenBounds();
        waitImage(golden, new Rectangle(0, 0, bounds.width, bounds.height), resID, diffID);
    }

    /**
     * TODO javadoc
     *
     * @param <V>
     * @param state
     * @param value
     * @return last returned State value
     * @throws TimeoutExpiredException in case the wait is unsuccessful.
     */
    public <V> V waitState(State<V> state, V value) {
        return getEnvironment().getWaiter(WAIT_STATE_TIMEOUT).ensureValue(value, state);
    }

    /**
     * TODO javadoc
     *
     * @param <V>
     * @param state
     * @return last returned State value
     * @throws TimeoutExpiredException in case the wait is unsuccessful.
     */
    public <V> V waitState(State<V> state) {
        return getEnvironment().getWaiter(WAIT_STATE_TIMEOUT).ensureState(state);
    }

    /**
     * ***********************************************************************
     */
    /*
     * INTERFACES
     */
    /**
     * ***********************************************************************
     */
    private Method findAsMethod(Class<? extends ControlInterface> interfaceClass, Class type) {
        while (type != null) {
            for (Method m : getClass().getMethods()) {
                As as = m.getAnnotation(As.class);
                Class returnType = m.getReturnType();
                if (as != null && interfaceClass.isAssignableFrom(returnType) && as.value().equals(type)) {
                    if (m.getParameterTypes().length > 0 && type.equals(Void.class)
                            || m.getParameterTypes().length > 1 && !type.equals(Void.class)) {
                        throw new IllegalStateException("wrong number of parameters in an @As method");
                    }
                    return m;
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    /**
     * Checks if the control could be treated as a ControlInterface. If it is,
     * <code>Wrap#as(java.lang.Class)</code> will be called. This implementation
     * checks whether the class implements the necessary interface. It also
     * works for root interfaces such as
     * <code>MouseTarget</code> and
     * <code>KeyTarget</code>, which implementations are encapsulated. If some
     * other functionality is desired, must be overriden together with
     * <code>as(java.lang.Class)</code>
     *
     * @see Wrap#is(java.lang.Class)
     * @param <INTERFACE>
     * @param interfaceClass
     * @return
     */
    public <INTERFACE extends ControlInterface> boolean is(Class<INTERFACE> interfaceClass) {
        if (interfaceClass.isInstance(this)) {
            return true;
        }
        return findAsMethod(interfaceClass, Void.class) != null;
    }

    /**
     * Checks if the control could be treated as a parametrized
     * ControlInterface. If it is,
     * <code>Wrap#as(java.lang.Class, java.lang.Class)</code> will be called.
     * This implementation checks whether the class implements the necessary
     * interface. It also works for root interfaces such as
     * <code>MouseTarget</code> and
     * <code>KeyTarget</code>, which implementations are encapsulated. If some
     * other functionality is desired, must be overriden together with
     * <code>as(java.lang.Class)</code>
     *
     * @see Wrap#is(java.lang.Class)
     * @param <TYPE>
     * @param <INTERFACE>
     * @param interfaceClass
     * @param type The parameter class.
     * @return
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> boolean is(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        if (interfaceClass.isInstance(this)) {
            if (interfaceClass.cast(this).getType().isAssignableFrom(type)) {
                return true;
            }
        }
        return findAsMethod(interfaceClass, type) != null;
    }

    private Object callAsMethod(Class<? extends ControlInterface> interfaceClass, Class type) {
        Method m = findAsMethod(interfaceClass, type);
        if (m != null) {
            try {
                if (m.getParameterTypes().length == 0) {
                    return m.invoke(this);
                } else if (m.getParameterTypes().length == 1) {
                    return m.invoke(this, !type.equals(Void.class) ? type : Object.class);
                } else {
                    throw new InterfaceException(this, interfaceClass);
                }
            } catch (IllegalAccessException ex) {
                throw new JemmyException("Unable to call method \"" + m.getName() + "()\"", ex, this);
            } catch (IllegalArgumentException ex) {
                throw new JemmyException("Unable to call method \"" + m.getName() + "()\"", ex, this);
            } catch (InvocationTargetException ex) {
                throw new JemmyException("Unable to call method \"" + m.getName() + "()\"", ex, this);
            }
        }
        return null;
    }

    /**
     * Returns an implementation of interface associated with this object. First
     * it checks
     *
     * @see Wrap#is(java.lang.Class)
     * @param <INTERFACE>
     * @param interfaceClass
     * @return
     */
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> interfaceClass) {
        if (interfaceClass.isInstance(this)) {
            return interfaceClass.cast(this);
        }

        Object res = callAsMethod(interfaceClass, Void.class);
        if (res != null) {
            return (INTERFACE) res;
        }

        throw new InterfaceException(this, interfaceClass);
    }

    /**
     * Returns an implementation of interface associated with the object.
     *
     * @see Wrap#is(java.lang.Class)
     * @param <TYPE>
     * @param <INTERFACE>
     * @param interfaceClass
     * @param type The parameter class.
     * @return
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        if (interfaceClass.isInstance(this)) {
            if (interfaceClass.cast(this).getType().isAssignableFrom(type)) {
                return interfaceClass.cast(this);
            }
        }

        Object res = callAsMethod(interfaceClass, type);
        if (res != null) {
            return (INTERFACE) res;
        }

        throw new InterfaceException(this, interfaceClass);
    }
    /**
     * ***********************************************************************
     */
    /*
     * INPUT
     */
    /**
     * ***********************************************************************
     */
    private Mouse mouse = null;
    private Drag drag = null;
    private Keyboard keyboard = null;

    /**
     * A shortcut to
     * <code>as(MouseTarget.class).mouse()</code>
     *
     * @return
     */
    @As(Mouse.class)
    public Mouse mouse() {
        if (mouse == null) {
            mouse = getEnvironment().getInputFactory().create(this, Mouse.class);
        }
        return mouse;
    }

    /**
     * A shortcut to
     * <code>as(MouseTarget.class).drag()</code>
     *
     * @return
     */
    @As(Drag.class)
    public Drag drag() {
        if (drag == null) {
            drag = getEnvironment().getInputFactory().create(this, Drag.class);
        }
        return drag;
    }

    /**
     * A shortcut to
     * <code>as(KeyTarget.class).wrap()</code>
     *
     * @return
     */
    @As(Keyboard.class)
    public Keyboard keyboard() {
        if (keyboard == null) {
            keyboard = getEnvironment().getInputFactory().create(this, Keyboard.class);
        }
        return keyboard;
    }
    /**
     * ***********************************************************************
     */
    /*
     * PROPERTIES
     */
    /**
     * ***********************************************************************
     */
    private HashMap<String, Object> properties = new HashMap<String, Object>();

    /**
     *
     * @return
     */
    @Property(CONTROL_CLASS_PROP_NAME)
    public Class<?> getControlClass() {
        return getControl().getClass();
    }

    private void fillTheProps(boolean quiet) {
        properties.clear();
        properties.put(WRAPPER_CLASS_PROP_NAME, getClass());
        readAnnotationProps(quiet);
        readControlProps(quiet);
    }

    private void readControlProps(boolean quiet) {
        Class<?> cls = getClass();
        do {
            if (cls.isAnnotationPresent(FieldProperties.class)) {
                for (String s : cls.getAnnotation(FieldProperties.class).value()) {
                    Object value;
                    try {
                        value = getFieldProperty(s);
                    } catch (Exception e) {
                        getEnvironment().getOutput().printStackTrace(e);
                        value = e.toString();
                        if (!(e instanceof JemmyException) && !quiet) {
                            throw new JemmyException("Exception while getting property \"" + s + "\"", e);
                        }
                    }
                    properties.put(s, value);
                }
            }
            if (cls.isAnnotationPresent(MethodProperties.class)) {
                for (String s : cls.getAnnotation(MethodProperties.class).value()) {
                    Object value;
                    try {
                        value = getMethodProperty(s);
                    } catch (Exception e) {
                        getEnvironment().getOutput().printStackTrace(e);
                        value = e.toString();
                        if (!(e instanceof JemmyException) && !quiet) {
                            throw new JemmyException("Exception while getting property \"" + s + "\"", e);
                        }
                    }
                    properties.put(s, value);
                }
            }
        } while ((cls = cls.getSuperclass()) != null);
    }

    private void addAnnotationProps(Class cls, boolean quiet) {
        for (Method m : cls.getMethods()) {
            if (m.isAnnotationPresent(Property.class)) {
                String name = m.getAnnotation(Property.class).value();
                if (!properties.containsKey(name)) {
                    Object value;
                    try {
                        value = getProperty(this, m);
                    } catch (Exception e) {
                        if (quiet) {
                            getEnvironment().getOutput().printStackTrace(e);
                            value = e.toString();
                        } else {
                            throw new JemmyException("Exception while getting property \"" + name + "\"", e);
                        }
                    }
                    properties.put(name, value);
                }
            }
        }
    }

    private void readAnnotationProps(boolean quiet) {
        Class cls = getClass();
        do {
            addAnnotationProps(cls, quiet);
        } while ((cls = cls.getSuperclass()) != null);
        for (Class intf : getClass().getInterfaces()) {
            addAnnotationProps(intf, quiet);
        }
    }

    private void checkPropertyMethod(Method m) {
        if (m.getParameterTypes().length > 0) {
            throw new JemmyException("Method marked by @Property must not have parameters: "
                    + m.getDeclaringClass().getName() + "." + m.getName());
        }
    }

    private Method getPropertyMethod(Class cls, String name) {
        Class scls = cls;
        do {
            for (Method m : scls.getMethods()) {
                if (m.isAnnotationPresent(Property.class) && m.getAnnotation(Property.class).value().equals(name)) {
                    checkPropertyMethod(m);
                    return m;
                }
            }
        } while ((scls = scls.getSuperclass()) != null);
        for (Class intf : cls.getInterfaces()) {
            for (Method m : intf.getMethods()) {
                if (m.isAnnotationPresent(Property.class) && m.getAnnotation(Property.class).value().equals(name)) {
                    checkPropertyMethod(m);
                    return m;
                }
            }
        }
        return null;
    }

    private Object getProperty(Object object, Method m) {
        Property prop = m.getAnnotation(Property.class);
        try {
            return m.invoke(object);
        } catch (IllegalAccessException ex) {
            throw new JemmyException("Unable to obtain property \"" + ((prop != null) ? prop.value() : "null") + "\"", ex, this);
        } catch (IllegalArgumentException ex) {
            throw new JemmyException("Unable to obtain property \"" + ((prop != null) ? prop.value() : "null") + "\"", ex, this);
        } catch (InvocationTargetException ex) {
            throw new JemmyException("Unable to obtain property \"" + ((prop != null) ? prop.value() : "null") + "\"", ex, this);
        }
    }

    /**
     * Get property of the wrapped object. Uses first available from <nl>
     * <li>methods annotated by
     * <code>org.jemmy.control.Property</code></li> <li>wrapped object methods
     * listed in
     * <code>org.jemmy.control.MethodProperties</code></li> <li>wrapped object
     * fields listed in
     * <code>org.jemmy.control.FieldProperties</code></li> </nl>
     *
     * @param name property name
     * @throws JemmyException if no property found
     * @see Property
     * @see MethodProperties
     * @see FieldProperties
     * @return property value
     */
    public Object getProperty(String name) {
        if (WRAPPER_CLASS_PROP_NAME.equals(name)) {
            return getClass();
        }
        Method m = getPropertyMethod(this.getClass(), name);
        if (m != null) {
            return getProperty(this, m);
        }
        if (hasMethodProperty(name)) {
            return getMethodProperty(name);
        }
        if (hasFieldProperty(name)) {
            return getFieldProperty(name);
        }
        throw new JemmyException("No property \"" + name + "\"", this);
    }

    private Object getInterfaceProperty(Class cls, Object instance, String name) {
        Method m = getPropertyMethod(cls, name);
        if (m != null) {
            return getProperty(instance, m);
        }
        throw new JemmyException("No property \"" + name + "\" in interface " + cls.getName(), instance);
    }

    /**
     * Get property out of the control interface. Refer to the interface doc to
     * find out what properties are provided.
     *
     * @param <INTERFACE>
     * @param name
     * @param intrfc
     * @return
     */
    public <INTERFACE extends ControlInterface> Object getProperty(String name, Class<INTERFACE> intrfc) {
        return getInterfaceProperty(intrfc, as(intrfc), name);
    }

    /**
     * Get property out of the control interface. Refer to the interface doc to
     * find out what properties are provided.
     *
     * @param <TYPE>
     * @param <INTERFACE>
     * @param name
     * @param intrfc
     * @param type
     * @return
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> Object getProperty(String name, Class<INTERFACE> intrfc, Class<TYPE> type) {
        return getInterfaceProperty(intrfc, as(intrfc, type), name);
    }

    /**
     * Wait for the property
     * <code>property</code> to get the specified value.
     * <code>WAIT_STATE_TIMOUT</code> timeout is used
     *
     * @param property name of the property being waited for
     * @param value property value to wait
     */
    public void waitProperty(final String property, final Object value) {
        getEnvironment().getWaiter(WAIT_STATE_TIMEOUT).ensureValue(value, new State<Object>() {

            public Object reached() {
                return getProperty(property);
            }

            @Override
            public String toString() {
                return "Control having property " + property + " expected value '" + value + "' (Property = '" + getProperty(property) + "')";
            }
        });
    }

    /**
     * Wait for the property
     * <code>property</code> of control interface to get the specified value.
     * <code>WAIT_STATE_TIMOUT</code> timeout is used
     *
     * @param <INTERFACE>
     * @param property
     * @param intrfc
     * @param value
     */
    public <INTERFACE extends ControlInterface> void waitProperty(final String property, final Class<INTERFACE> intrfc, final Object value) {
        Object instance = as(intrfc);
        getEnvironment().getWaiter(WAIT_STATE_TIMEOUT).ensureValue(value, new State<Object>() {

            public Object reached() {
                return getProperty(property, intrfc);
            }

            @Override
            public String toString() {
                return "Interface " + intrfc.getName() + " having property " + property + " expected value '" + value + "' (Property = '" + getProperty(property, intrfc) + "')";
            }
        });
    }

    /**
     * Wait for the property
     * <code>property</code> of control interface to get the specified value.
     * <code>WAIT_STATE_TIMOUT</code> timeout is used
     *
     * @param <TYPE>
     * @param <INTERFACE>
     * @param property
     * @param intrfc
     * @param type
     * @param value
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> void waitProperty(final String property, final Class<INTERFACE> intrfc, final Class<TYPE> type, final Object value) {
        getEnvironment().getWaiter(WAIT_STATE_TIMEOUT).ensureValue(value, new State<Object>() {

            public Object reached() {
                return getProperty(property, intrfc, type);
            }

            @Override
            public String toString() {
                return "Interface " + intrfc.getName() + " having property " + property + " expected value '" + value + "' (Property = '" + getProperty(property) + "')";
            }
        });
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean hasFieldProperty(String name) {
        Class<?> cls = getClass();
        do {
            if (cls.isAnnotationPresent(FieldProperties.class)) {
                FieldProperties props = cls.getAnnotation(FieldProperties.class);
                if (contains(props.value(), name)) {
                    return true;
                }
            }
        } while ((cls = cls.getSuperclass()) != null);
        return false;
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean hasMethodProperty(String name) {
        Class<?> cls = getClass();
        do {
            if (cls.isAnnotationPresent(MethodProperties.class)) {
                MethodProperties props = cls.getAnnotation(MethodProperties.class);
                if (contains(props.value(), name)) {
                    return true;
                }
            }
        } while ((cls = cls.getSuperclass()) != null);
        return false;
    }

    private boolean contains(String[] values, String name) {
        for (int i = 0; i < values.length; i++) {
            if (name.equals(values[i])) {
                return true;
            }

        }
        return false;
    }

    /**
     *
     * @param name
     * @return
     */
    public Object getFieldProperty(final String name) {
        if (!hasFieldProperty(name)) {
            throw new JemmyException("No \"" + name + "\" field property specified on " + getClass().getName());
        }
        GetAction action = new GetAction() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(getControl().getClass().getField(name).get(getControl()));
            }
        };
        Object result = action.dispatch(env);
        if (action.getThrowable() != null) {
            throw new JemmyException("Unable to obtain property \"" + name + "\"", action.getThrowable(), this);
        }
        return result;
    }

    /**
     *
     * @param name
     * @return
     */
    public Object getMethodProperty(final String name) {
        if (!hasMethodProperty(name)) {
            throw new JemmyException("No \"" + name + "\" method property specified on " + getClass().getName());
        }
        GetAction action = new GetAction() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(getControl().getClass().getMethod(name).invoke(getControl()));
            }

            @Override
            public String toString() {
                return "Getting property \"" + name + "\" on " + getClass().getName();
            }
        };
        Object result = action.dispatch(env);
        if (action.getThrowable() != null) {
            throw new JemmyException("Unable to obtain property \"" + name + "\"", action.getThrowable(), this);
        }
        return result;
    }

    /**
     *
     * @param <P>
     * @param valueClass
     * @param name
     * @return
     */
    public <P> P getProperty(Class<P> valueClass, String name) {
        return valueClass.cast(getProperty(name));
    }

    /**
     * Returns a a map of all known controls properties including values from
     * methods marked by
     * <code>@Property</code> and values of methods/field from
     * <code>@MethodProperties</code>/
     * <code>FieldProperties</code> correspondingly.
     *
     * @return a map of properties
     * @throws Runtime exception should there be an exception thrown while
     * getting a property
     */
    public HashMap<String, Object> getProperties() {
        fillTheProps(false);
        return properties;
    }

    /**
     * Returns a a map of all controls properties which is possible to obtain.
     * Similar to
     * <code>getProperties()</code> only exception is swallowed should there be
     * an exception thrown while getting a property.
     *
     * @return a map of properties which were possible to obtain.
     */
    public HashMap<String, Object> getPropertiesQiuet() {
        fillTheProps(true);
        return properties;
    }
}
