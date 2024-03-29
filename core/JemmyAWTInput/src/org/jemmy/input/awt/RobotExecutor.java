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


import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jemmy.JemmyException;
import org.jemmy.Rectangle;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;
import org.jemmy.image.awt.AWTImage;
import org.jemmy.image.Image;
import org.jemmy.image.awt.PNGDecoder;
import org.jemmy.image.awt.PNGEncoder;
import org.jemmy.timing.State;
import org.jemmy.timing.Waiter;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Mouse.MouseButton;

/**
 *
 * @author КАМ
 */
class RobotExecutor {

    private static RobotExecutor instance;
    private AWTMap awtMap = null;

    public static RobotExecutor get() {
        if (instance == null) {
            instance = new RobotExecutor();
        }
        return instance;
    }

    /**
     * A reference to the robot instance.
     */
    protected ClassReference robotReference = null;
    protected Timeout autoDelay;
    private boolean inited = false;
    private boolean runInOtherJVM = false;
    private boolean ready = false;
    private boolean connectionEstablished = false;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;
    private int connectionPort;
    private String connectionHost;
    public static final int CONNECTION_TIMEOUT = Integer.parseInt(
            (String)Environment.getEnvironment().getProperty(
            AWTRobotInputFactory.OTHER_VM_CONNECTION_TIMEOUT_PROPERTY,
            Integer.toString(60000 * 15))); // 15 min

    public RobotExecutor() {
    }

    void setAWTMap(AWTMap awtMap) {
        this.awtMap = awtMap;
    }

    AWTMap getAWTMap() {
        if (awtMap == null) {
            awtMap = new AWTMap();
        }
        return awtMap;
    }

    private void ensureInited() {
        if (!inited) {
             runInOtherJVM = Boolean.parseBoolean((String)Environment.getEnvironment()
                     .getProperty(AWTRobotInputFactory.OTHER_VM_PROPERTY,
                     Boolean.toString(runInOtherJVM)));
             inited = true;
        }
    }

    public Image createScreenCapture(Rectangle screenRect) {
         Object result = makeAnOperation("createScreenCapture", new Object[] {
            new java.awt.Rectangle(screenRect.x, screenRect.y, screenRect.width,
                    screenRect.height) },
            new Class[] { java.awt.Rectangle.class });
         if (result.getClass().isAssignableFrom(BufferedImage.class)) {
             return new AWTImage(BufferedImage.class.cast(result));
         } else {
             throw new JemmyException("Screen capture (" + result
                     + ") is not a BufferedImage");
         }
    }

    public Object makeAnOperation(String method, Object[] params, Class[] paramClasses) {
        ensureInited();
        if (runInOtherJVM) {
            return makeAnOperationRemotely(method, params, paramClasses);
        } else {
            return makeAnOperationLocally(method, params, paramClasses);
        }
    }

    public void exit() {
        ensureInited();
        if (runInOtherJVM) {
            ensureConnection();
            try {
                outputStream.writeObject("exit");
                connectionEstablished = false;
                deleteProperties();
            } catch (IOException ex) {
                throw new JemmyException("Failed to invoke exit", ex);
            }
        }
    }

    private Object makeAnOperationLocally(String method, Object[] params, Class[] paramClasses) {
        if (robotReference == null) {
            initRobot();
        }
        try {
            convert(method, params, paramClasses);
            Object result = robotReference.invokeMethod(method, params, paramClasses);
            synchronizeRobot();
            return result;
        } catch (InvocationTargetException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        } catch (IllegalStateException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        } catch (NoSuchMethodException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        } catch (IllegalAccessException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        }
    }

    private int convert(Object obj) {
        if (MouseButton.class.isAssignableFrom(obj.getClass())) {
            return awtMap.convert((MouseButton)obj);
        } else if (KeyboardButton.class.isAssignableFrom(obj.getClass())) {
            return awtMap.convert((KeyboardButton)obj);
        } else {
            throw new JemmyException("Unable to recognize object", obj);
        }
    }

    private static final Set<String> convertables = new HashSet<String>(Arrays.asList(new String[] {"mousePress", "mouseRelease", "keyPress", "keyRelease"}));

    private void convert(String method, Object[] params, Class[] paramClasses) {
        if (convertables.contains(method))
            for (int i = 0; i < params.length; i++) {
            params[i] = new Integer(convert(params[i]));
            paramClasses[i] = Integer.TYPE;
        }
    }

    public static void main(String[] args) {
        System.setProperty("apple.awt.UIElement", "true");
        if (args.length != 0 && args.length != 1) {
            System.err.println("Usage: java ... [-D" +
                    Environment.JEMMY_PROPERTIES_FILE_PROPERTY + "=" +
                    "<.jemmy.properties full path>]" +
                    " RobotExecutor [connectionPort]");
            System.exit(-1);
        }
        if (args.length == 1) {
            Environment.getEnvironment().setProperty(
                    AWTRobotInputFactory.OTHER_VM_PORT_PROPERTY, args[0]);
        }
        RobotExecutor re = new RobotExecutor();
        try {
            re.server();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.err.flush();
            System.exit(-1);
        }
    }

    private File props;

    private void deleteProperties() {
        if (props != null) {
            props.delete();
            props = null;
        }
    }

    private void prepareProperties() {
        deleteProperties();
        try {
            props = File.createTempFile(".jemmy.othervm.", ".properties");
            props.deleteOnExit();
            PrintWriter fw = new PrintWriter(props);
            for(Field f : AWTRobotInputFactory.class.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.FINAL) != 0 &&
                        (f.getModifiers() & Modifier.STATIC) != 0 &&
                        f.getType().equals(String.class) &&
                        f.getName().startsWith("OTHER_VM_") &&
                        Environment.getEnvironment().getProperty((String)f.get(null)) != null) {
                    fw.println(f.get(null) + "=" + Environment.getEnvironment().getProperty((String)f.get(null)));
                }
            }
            fw.close();
        } catch (IllegalArgumentException ex) {
            throw new JemmyException("Failed to create temporary properties file: " + props.getAbsolutePath(), ex);
        } catch (IllegalAccessException ex) {
            throw new JemmyException("Failed to create temporary properties file: " + props.getAbsolutePath(), ex);
        } catch (IOException ex) {
            throw new JemmyException("Failed to create temporary properties file: " + props.getAbsolutePath(), ex);
        }

    }

    private void startServer() {
        try {
            prepareProperties();
            //part of the commad to get to the debugger
            //"-Xrunjdwp:transport=dt_socket,suspend=y,server=y,address=8000",
            List<String> command = new ArrayList<>();
            command.add(System.getProperty("java.home") +
                        File.separator + "bin" + File.separator + "java");
            String modulePath = System.getProperty("jdk.module.path");
            if(modulePath != null && !modulePath.isEmpty()) {
                command.add("--module-path");
                command.add(modulePath);
            }
            String classPath = System.getProperty("java.class.path");
            if(classPath != null && !classPath.isEmpty()) {
                command.add("-cp");
                command.add(classPath);
            }
            command.add("-D" + Environment.JEMMY_PROPERTIES_FILE_PROPERTY +
                    "=" + props.getAbsolutePath());
            command.add(RobotExecutor.class.getName());
            command.add(Integer.toString(connectionPort));
            System.out.println("Robot command:");
            System.out.println(command.stream().collect(Collectors.joining(" ")));
            ProcessBuilder pb = new ProcessBuilder(command);
            // TODO: Improve output
//            System.out.println("Starting server");
//            System.out.println("Command: " + pb.command());
//            System.out.flush();
            pb.redirectErrorStream(true);
            final Process p = pb.start();
            new Thread() {

                @Override
                public void run() {
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while (true) {
                        try {
                            String line = br.readLine();
                            if (line == null) {
                                break;
                            }
                            System.out.println("SERVER: " + line);
                        } catch (IOException ex) {
                            throw new JemmyException("Exception during other JVM output processing", ex);
                        }
                    }
                }
            }.start();
        } catch (IOException ex) {
            throw new JemmyException("Failed to start other JVM", ex);
        }
    }

    public void ensureConnection() {
        ensureInited();
        if (runInOtherJVM && !connectionEstablished) {
            initClientConnection();
        }
    }

    private void initClientConnection() {
        connectionHost = (String)Environment.getEnvironment().getProperty(
             AWTRobotInputFactory.OTHER_VM_HOST_PROPERTY, "localhost");
        connectionPort = Integer.parseInt((String)Environment.getEnvironment()
             .getProperty(AWTRobotInputFactory.OTHER_VM_PORT_PROPERTY,
             "53669"));
        try {
            try {
                socket = new Socket(connectionHost, connectionPort);
            } catch (IOException ex) {
                if ("localhost".equalsIgnoreCase(connectionHost)
                        || "127.0.0.1".equals(connectionHost)) {
                    // TODO Improve check for localhost
                    startServer();
                    Environment.getEnvironment().getTimeout("");
                    Timeout waitTime = new Timeout("connection wait time", 5 * 60000);
                    socket = new Waiter(waitTime).ensureState(new State<Socket>() {
                        Exception ex;
                        public Socket reached() {
                            Socket socket = null;
                            try {
                                socket = new Socket(connectionHost, connectionPort);
                            } catch (UnknownHostException ex1) {
                                ex = ex1;
                            } catch (Exception ex1) {
                                ex = ex1;
                            }
                            return socket;
                        }

                        @Override
                        public String toString() {
                            if (ex != null) {
                                // TODO: Provide better mechanics for exception handling
                                Logger.getLogger(RobotExecutor.class.getName())
                                        .log(Level.INFO, null, ex);
                            }
                            return "Waiting for connection to be established " +
                                    "with other JVM (" + connectionHost
                                    + ":" + connectionPort + ", exception: " + ex + ")";
                        }
                    });
                } else {
                    throw new JemmyException("Failed to establish socket " +
                            "connection with other JVM (" + connectionHost
                            + ":" + connectionPort + ")", ex);
                }
            }
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            connectionEstablished = true;
            ready = true;

            System.out.println("Connection established!");
            setAutoDelay(autoDelay);
        } catch (IOException ex) {
            throw new JemmyException("Failed to establish socket connection " +
                    "with other JVM (" + connectionHost + ":" + connectionPort
                    + ")", ex);
        }
    }

    public synchronized Object getProperty(String name) {
        ensureConnection();
        try {
            outputStream.writeObject("getProperty");
            outputStream.writeObject(name);
            Object result = inputStream.readObject();
            String response = (String)(inputStream.readObject());
            if (!"OK".equals(response)) {
                throw new JemmyException("Remote operation didn't succeed");
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new JemmyException("Socket communication with other JVM failed", ex);
        } catch (OptionalDataException ex) {
            throw new JemmyException("Socket communication with other JVM " +
                    "failed: OptionalDataException eof = " + ex.eof + ", " +
                    "length = " + ex.length, ex);
        } catch (IOException ex) {
            throw new JemmyException("Socket communication with other JVM failed", ex);
        }
    }

    private synchronized Object makeAnOperationRemotely(String method, Object[] params, Class[] paramClasses) {
        ensureConnection();
        try {
            outputStream.writeObject("makeAnOperation");
            outputStream.writeObject(method);
            outputStream.writeObject(params);
            outputStream.writeObject(paramClasses);
            Object result;
            String response = (String)(inputStream.readObject());
            if ("image".equals(response)) {
                result = PNGDecoder.decode(inputStream, false);
            } else {
                if (!"OK".equals(response)) {
                    throw new JemmyException("Remote operation didn't succeed");
                }
                result = inputStream.readObject();
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new JemmyException("Socket communication with other JVM failed", ex);
        } catch (OptionalDataException ex) {
            throw new JemmyException("Socket communication with other JVM " +
                    "failed: OptionalDataException eof = " + ex.eof + ", " +
                    "length = " + ex.length, ex);
        } catch (IOException ex) {
            throw new JemmyException("Socket communication with other JVM failed", ex);
        }
    }

    private void server() {
        System.out.println("Robot ready!");
        System.out.flush();
        ServerSocket sc;
        connectionPort = Integer.parseInt((String)Environment.getEnvironment()
             .getProperty(AWTRobotInputFactory.OTHER_VM_PORT_PROPERTY,
             "53669"));
        while(true) {
            Thread watchdog = new Thread("RobotExecutor.server watchdog") {

                @Override
                public void run() {
                    try {
                        Thread.sleep(CONNECTION_TIMEOUT);
                        System.out.println("Exiting server as there is no " +
                                "connection for " + CONNECTION_TIMEOUT / 60000.0
                                + " minutes");
                        System.out.flush();
                        System.exit(0);
                    } catch (InterruptedException ex) {
                        // Ignoring exception as it is okay
                    }
                }

            };
            watchdog.start();
            System.out.println("Waiting for incoming connection for up to "
                    + CONNECTION_TIMEOUT / 60000.0 + " minutes");
            try {
                sc = new ServerSocket(connectionPort);
                socket = sc.accept();
                watchdog.interrupt();
            } catch (IOException ex) {
                throw new JemmyException("Can't establish connection with client", ex);
            }
            System.out.println("Connection established!");
            try {
                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                while(true) {
                    String command = (String)inputStream.readObject();
                    if ("exit".equals(command)) {
                        System.exit(0);
                    }
                    if ("getProperty".equals(command)) {
                        String property = (String)inputStream.readObject();
                        outputStream.writeObject(Environment.getEnvironment().getProperty(property));
                        outputStream.writeObject("OK");
                    }
                    if ("makeAnOperation".equals(command)) {
                        String method = (String)inputStream.readObject();
                        Object[] params = (Object[])inputStream.readObject();
                        Class[] paramClasses = (Class[])inputStream.readObject();
                        Object result = makeAnOperationLocally(method, params,
                                paramClasses);
                        if (result instanceof BufferedImage) {
                            outputStream.writeObject("image");
                            BufferedImage image = BufferedImage.class.cast(result);
                            new PNGEncoder(outputStream, PNGEncoder.COLOR_MODE)
                                    .encode(image, false);
                        } else {
                            outputStream.writeObject("OK");
                            outputStream.writeObject(result);
                        }
                    }
                }
            } catch (ClassNotFoundException ex) {
                throw new JemmyException("Socket communication with other " +
                        "JVM failed", ex);
            } catch (IOException ex) {
                Logger.getLogger(RobotExecutor.class.getName())
                        .log(Level.SEVERE, null, ex);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RobotExecutor.class.getName()).log(
                                Level.SEVERE, "Exception during socket closing", ex);
                    }
                }
                if (sc != null) {
                    try {
                        sc.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RobotExecutor.class.getName()).log(
                                Level.SEVERE, "Exception during server socket " +
                                "closing", ex);
                    }
                }
            }
        }
    }

    private void initRobot() {
        // need to init Robot in dispatch thread because it hangs on Linux
        // (see http://www.netbeans.org/issues/show_bug.cgi?id=37476)
        if (EventQueue.isDispatchThread()) {
            doInitRobot();
        } else {
            try {
                EventQueue.invokeAndWait(new Runnable() {

                    public void run() {
                        doInitRobot();
                    }
                });
            } catch (InterruptedException ex) {
                throw new JemmyException("Failed to initialize robot", ex);
            } catch (InvocationTargetException ex) {
                throw new JemmyException("Failed to initialize robot", ex);
            }
        }
    }

    private void doInitRobot() {
        try {
            ClassReference robotClassReverence = new ClassReference("java.awt.Robot");
            robotReference = new ClassReference(robotClassReverence.newInstance(null, null));
            if (awtMap == null) {
                awtMap = new AWTMap();
            }
            setAutoDelay(autoDelay);
            ready = true;
        } catch (InvocationTargetException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        } catch (IllegalStateException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        } catch (NoSuchMethodException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        } catch (IllegalAccessException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        } catch (ClassNotFoundException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        } catch (InstantiationException e) {
            throw (new JemmyException("Exception during java.awt.Robot accessing", e));
        }
    }

    /**
     * Calls <code>java.awt.Robot.waitForIdle()</code> method.
     */
    protected void synchronizeRobot() {
        ensureInited();
        if (!runInOtherJVM) {
            // TODO: It looks like this method is rudimentary
            if (!EventQueue.isDispatchThread()) {
                if (robotReference == null) {
                    initRobot();
                }
                try {
                    robotReference.invokeMethod("waitForIdle", null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setAutoDelay(Timeout autoDelay) {
        this.autoDelay = autoDelay;
        if (ready) {
            makeAnOperation("setAutoDelay", new Object[]{new Integer((int) ((autoDelay != null) ? autoDelay.getValue() : 0))}, new Class[]{Integer.TYPE});
        }
    }

    public boolean isRunInOtherJVM() {
        ensureInited();
        return runInOtherJVM;
    }

    public void setRunInOtherJVM(boolean runInOtherJVM) {
        if (inited && this.runInOtherJVM && this.connectionEstablished && !runInOtherJVM) {
            shutdownConnection();
        }
        this.runInOtherJVM = runInOtherJVM;
        inited = true;
        ready = false;
    }

    private void shutdownConnection() {
        try {
            outputStream.writeObject("exit");
            socket.close();
            connectionEstablished = false;
        } catch (IOException ex) {
            throw new JemmyException("Failed to shutdown connection", ex);
        }
    }
}
