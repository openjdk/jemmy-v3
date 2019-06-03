/*
 * Copyright (c) 2007, 2017, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.input.awt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 *
 * Allows access to classes by reflection.
 *
 * @author Alexandre Iline (alexandre.iline@sun.com)
 */
public class ClassReference {

    private Class<?> cl;
    private Object instance;

    /**
     * Constructor.
     * @param o Object to work with.
     */
    public ClassReference(Object o) {
        super();
        instance = o;
        cl = o.getClass();
    }

    /**
     * Contructor.
     * The object created by this constructor can be used
     * to access static methods and fields only.
     *
     * @param className name of class
     * @throws ClassNotFoundException todo document
     */
    public ClassReference(String className)
            throws ClassNotFoundException {
        super();
        cl = Class.forName(className);
        instance = null;
    }

    /**
     * Executes class's <code>main(java.lang.String[])</code> method
     * with a zero-length <code>java.lang.String</code> array
     * as a parameter.
     *
     * @throws NoSuchMethodException when the method cannot be found.
     * @throws InvocationTargetException when the invoked method throws an exception.
     */
    public void startApplication()
            throws InvocationTargetException, NoSuchMethodException {
        String[] params = new String[0];
        startApplication(params);
    }

    /**
     * Executes class's <code>main(java.lang.String[])</code> method.
     *
     * @param params The <code>java.lang.String</code> array to pass
     * to <code>main(java.lang.String[])</code>.
     * @throws NoSuchMethodException when the method cannot be found.
     * @throws InvocationTargetException when the invoked method throws an exception.
     */
    public void startApplication(String[] params)
            throws InvocationTargetException, NoSuchMethodException {
        String[] real_params;
        if (params == null) {
            real_params = new String[0];
        } else {
            real_params = params;
        }
        String[][] methodParams = {real_params};
        Class[] classes = {real_params.getClass()};
        try {
            invokeMethod("main", methodParams, classes);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Locates method by name and parameter types and executes it.
     *
     * @param method_name Name of method.
     * @param params Method parameters.
     * @param params_classes Method parameters types.
     * @return the return value from an invocation of the Method.<br>
     * If <code>method_name</code> method is void, <code>null</code> is returned.<br>
     * If <code>method_name</code> method returns a primitive type, then
     * return wrapper class instance.
     * @throws InvocationTargetException when the invoked method throws an exception.
     * @throws NoSuchMethodException when the method cannot be found.
     * @throws IllegalAccessException when access to the class or method is lacking.
     * @throws SecurityException if access to the package or method is denied.
     */
    public Object invokeMethod(String method_name, Object[] params, Class<?>[] params_classes)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (params == null) {
            params = new Object[0];
        }
        if (params_classes == null) {
            params_classes = new Class<?>[0];
        }
        Method method = cl.getMethod(method_name,
                params_classes);
        return (method.invoke(instance, params));
    }

    /**
     * Locates constructor by parameter types and creates an instance.
     *
     * @param params An array of Method parameters.
     * @param params_classes An array of Method parameter types.
     * @return a new class instance.
     * @throws InvocationTargetException when the invoked constructor throws an exception.
     * @throws NoSuchMethodException when the constructor cannot be found.
     * @throws IllegalAccessException when access to the class or constructor is lacking.
     * @throws InstantiationException when the constructor is for an abstract class.
     * @throws SecurityException if access to the package or constructor is denied.
     */
    public Object newInstance(Object[] params, Class[] params_classes)
            throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException {
        if (params == null) {
            params = new Object[0];
        }
        if (params_classes == null) {
            params_classes = new Class[0];
        }
        Constructor constructor = cl.getConstructor(params_classes);
        return (constructor.newInstance(params));
    }

    /**
     * Returns the field value.
     * @param field_name The name of the field.
     * @return the field value
     * @see #setField
     * @throws NoSuchFieldException when the field cannot be found.
     * @throws IllegalAccessException when access to the class or constructor is lacking.
     * @throws SecurityException if access to the package or field is denied.
     */
    public Object getField(String field_name)
            throws NoSuchFieldException, IllegalAccessException {
        return (cl.getField(field_name).get(instance));
    }

    /**
     * Change a field's value.
     *
     * @param field_name The name of the field.
     * @param newValue The fields new value.
     * @see #getField
     * @throws NoSuchFieldException when the field cannot be found.
     * @throws IllegalAccessException when access to the class or constructor is lacking.
     * @throws SecurityException if access to the package or field is denied.
     */
    public void setField(String field_name, Object newValue)
            throws NoSuchFieldException, IllegalAccessException {
        cl.getField(field_name).set(instance, newValue);
    }

    /**
     * Returns all superclasses.
     * @return an array of superclasses, starting with the reference class
     * and ending with <code>java.lang.Object</code>.
     */
    public Class[] getClasses() {
        Class cls = cl;
        int count = 0;
        do {
            count++;
            cls = cls.getSuperclass();
        } while (cls != null);
        Class[] result = new Class[count];
        cls = cl;
        for (int i = 0; i < count; i++) {
            result[i] = cls;
            cls = cls.getSuperclass();
        }
        return (result);
    }
}
