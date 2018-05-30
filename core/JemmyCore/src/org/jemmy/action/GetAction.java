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
package org.jemmy.action;


import org.jemmy.env.Environment;


/**
 * An action to get some value.
 *
 * @author shura
 */
public abstract class GetAction<T> extends Action {

    private boolean finished = false;
    private T result = null;

    public GetAction() {
    }

    public boolean isFinished() {
        return finished;
    }

    public T getResult() {
        return result;
    }

    protected void setResult(T result) {
        this.result = result;
        finished = true;
    }

    /**
     * Dispatches action through the system UI queue to get the result.
     * @param env Environment to
     * {@linkplain Environment#getExecutor() get} executor and to pass to
     * {@linkplain ActionExecutor#execute(org.jemmy.env.Environment, boolean,
     * org.jemmy.action.Action, java.lang.Object[]) execute()} method.
     * @param parameters Parameters to pass to {@linkplain
     * #run(java.lang.Object[]) run()} method.
     * @return value returned by {@linkplain #getResult() getResult()} method.
     */
    public T dispatch(Environment env, Object... parameters) {
        env.getExecutor().execute(env, true, this, parameters);
        return getResult();
    }

}
