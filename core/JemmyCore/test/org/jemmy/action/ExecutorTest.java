/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.action;

import org.jemmy.JemmyException;
import org.jemmy.env.Environment;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class ExecutorTest {
    TestExecutor executor = new TestExecutor();
    Environment env = Environment.getEnvironment();

    private AtomicBoolean execute(boolean dispatch, Runnable actions) {
        AtomicBoolean completed = new AtomicBoolean(false);
        executor.execute(env, dispatch, Action.instantiate(() -> {
            actions.run();
            completed.set(true);
        }));
        return completed;
    }
    @Test
    public void execute() {
        assertTrue(execute(false, () -> {}).get());
    }
    @Test
    public void executeQueue() throws InterruptedException {
        assertTrue(execute(true, () -> {}).get());
    }

    private AtomicBoolean executeDetached(boolean dispatch, Runnable actions) throws InterruptedException {
        AtomicBoolean completed = new AtomicBoolean(false);
        executor.executeDetached(env, dispatch, Action.instantiate(() -> {
            actions.run();
            completed.set(true);}));
        return completed;
    }
    private void wait(AtomicBoolean b) throws InterruptedException {
        //busy loop 'cause it's a test
        long start = System.currentTimeMillis();
        while(!b.get()) {
            Thread.sleep(100);
            if((System.currentTimeMillis() - start) > 1000) fail();
        }
    }
    @Test
    public void executeDetached() throws InterruptedException {
        wait(executeDetached(false, () -> {}));
    }
    @Test
    public void executeQueueDetached() throws InterruptedException {
        wait(executeDetached(true, () -> {}));
    }

    private void executeException(boolean dispatch) {
        Action action = Action.instantiate(() -> {
            throw new RuntimeException();
        });
        executor.execute(env, dispatch, action);
    }
    @Test
    public void executeException() {
        try {
            executeException(false);
            fail();
        } catch (JemmyException e) {
        } catch (Exception e) {
            fail("Wrong exception type", e);
        }
    }
    @Test
    public void executeExceptionQueue() {
        try {
            executeException(true);
            fail();
        } catch (JemmyException e) {
        } catch (Exception e) {
            fail("Wrong exception type", e);
        }
    }

    private Action executeExceptionDetached(boolean dispatch) throws InterruptedException {
        Action action = Action.instantiate(() -> {
            throw new RuntimeException();
        });
        executor.executeDetached(env, dispatch, action);
        return action;
    }
    private void waitException(Action a) throws InterruptedException {
        //busy loop 'cause it's a test
        long start = System.currentTimeMillis();
        Throwable exc;
        while((exc = a.getThrowable()) == null) {
            Thread.sleep(10);
            if((System.currentTimeMillis() - start) > 1000)
                fail();
        }
        assertTrue(exc instanceof RuntimeException);
    }
    @Test
    public void executeExceptionDetached() throws InterruptedException {
        waitException(executeExceptionDetached(false));
    }
    @Test
    public void executeExceptionQueueDetached() throws InterruptedException {
        waitException(executeExceptionDetached(true));
    }
}
