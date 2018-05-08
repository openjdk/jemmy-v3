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
package org.jemmy.input;

import org.jemmy.JemmyException;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.ControlInterfaceFactory;
import org.jemmy.interfaces.Drag;
import org.jemmy.interfaces.Keyboard;
import org.jemmy.interfaces.Mouse;
import org.jemmy.interfaces.TypeControlInterface;
import org.jemmy.operators.AWTScreen;
import org.jemmy.operators.Screen;

/**
 *
 * @author shura
 */
public class AWTRobotInputFactory implements ControlInterfaceFactory {

    /**
     * Set this Environment property to true or false to run java.awt.Robot in
     * other or the same JVM
     */
    public static final String OTHER_VM_PROPERTY = "awt.robot.othervm";

    /**
     * Set this Environment property to the name of the host where other JVM runs.
     * 'localhost' by default
     */
    public static final String OTHER_VM_HOST_PROPERTY = "awt.robot.othervm.host";

    /**
     * Set this Environment property to override the port which is used to
     * connect to other JVM
     */
    public static final String OTHER_VM_PORT_PROPERTY = "awt.robot.othervm.port";

    /**
     * Set this Environment property to to the maximum time of waiting for the
     * client to connect to the JVM where Robot is running. It also waits the same
     * amount of ms for the next connection after the previous terminates.
     * Default is 15 min.
     */
    public static final String OTHER_VM_CONNECTION_TIMEOUT_PROPERTY
            = "awt.robot.othervm.connection.timeout";

    /**
     * The name of the timeout that is used by default as the delay time for
     * java.awt.Robot
     * @see java.awt.Robot#setAutoDelay(int)
     */
    public static final String ROBOT_DELAY_TIMEOUT_NAME = "RobotDriver.DelayTimeout";

    /**
     * Set this Environment property to the maximum number of pixels between
     * mouse positions during movement
     */
    public static final String ROBOT_MOUSE_SMOOTHNESS_PROPERTY = "awt.robot.mouse.smoothness";

    /**
     * Specifies whether to run java.awt.Robot in other JVM
     * @param runInOtherJVM if true then java.awt.Robot will run in other JVM
     */
    public static void runInOtherJVM(boolean runInOtherJVM) {
        RobotExecutor.get().setRunInOtherJVM(runInOtherJVM);
    }

    /**
     * Returns runInOtherJVM setting
     * @return if true then java.awt.Robot is running in other JVM
     */
    public static boolean isRunInOtherJVM() {
        return RobotExecutor.get().isRunInOtherJVM();
    }

    /**
     * Specifies mouse movements smoothness
     * @param mouseSmoothness the maximum number of pixels between
     * mouse positions during movement
     * @see #ROBOT_MOUSE_SMOOTHNESS_PROPERTY
     */
    public static void setMouseSmoothness(int mouseSmoothness) {
        if(mouseSmoothness <= 0) {
            throw new IllegalArgumentException("Mouse smoothness should be greater than zero.");
        }
        RobotDriver.setMouseSmoothness(mouseSmoothness);
    }

    /**
     * Gets the mouse movements smoothness
     * @return the maximum number of pixels between
     * mouse positions during movement
     * @see #ROBOT_MOUSE_SMOOTHNESS_PROPERTY
     */
    public static int getMouseSmoothness() {
        return RobotDriver.getMouseSmoothness();
    }

    static {
        if(Screen.SCREEN == null) {
            Screen.setSCREEN(new AWTScreen(Environment.getEnvironment()));
        }
    }

    public AWTMap getAwtMap() {
        return RobotExecutor.get().getAWTMap();
    }

    public void setAwtMap(AWTMap awtMap) {
        RobotExecutor.get().setAWTMap(awtMap);
    }

    public <INTERFACE extends ControlInterface> INTERFACE create(Wrap<?> control, Class<INTERFACE> interfaceClass) {
        if(Mouse.class.isAssignableFrom(interfaceClass)) {
            return (INTERFACE) new MouseImpl(control);
        } else if(Keyboard.class.isAssignableFrom(interfaceClass)) {
            return (INTERFACE) new KeyboardImpl(control);
        } else if(Drag.class.isAssignableFrom(interfaceClass)) {
            return (INTERFACE) new DragImpl(control);
        }
        throw new JemmyException(AWTRobotInputFactory.class.getName() + " does not support " + interfaceClass.getName());
    }

    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE create(Wrap<?> control, Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        throw new JemmyException(AWTRobotInputFactory.class.getName() + " does not support " + interfaceClass.getName());
    }

    @Override
    public String toString() {
        return getClass().getName() + "[otherVM=" + isRunInOtherJVM() + ", mouseSmoothness=" + getMouseSmoothness() + "]";
    }
}
