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
package org.jemmy.lookup;

import java.io.PrintStream;
import java.util.List;
import org.jemmy.interfaces.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.env.Environment;
import org.jemmy.env.TestOut;
import org.jemmy.timing.State;

/**
 * Default implementation of Lookup. Regularly, it is enough just override this
 * implementation and allow it to handle sub-lookups creation.
 * @see AbstractLookup#lookup(org.jemmy.lookup.LookupCriteria)
 * @see AbstractLookup#lookup(java.lang.Class, org.jemmy.lookup.LookupCriteria)
 * @param <CONTROL>
 * @author shura
 */
public abstract class AbstractLookup<CONTROL> extends AbstractParent<CONTROL> implements Lookup<CONTROL> {

    static final String PREFIX_DELTA = "| ";
    private ArrayList<CONTROL> found;
    Environment env;
    private Class<CONTROL> clss;
    private LookupCriteria<CONTROL> criteria = null;
    private Wrapper wrapper;

    /**
     * Identifies output where lookup progress is printed.
     * @see Environment#getOutput(java.lang.String)
     * @see AbstractLookup#wait(int)
     * @see AbstractLookup#size()
     */
    public static final String OUTPUT = AbstractLookup.class.getName() + ".OUTPUT";


    static {
        Environment.getEnvironment().initTimeout(WAIT_CONTROL_TIMEOUT);
        Environment.getEnvironment().initOutput(OUTPUT, TestOut.getNullOutput());
    }

    /**
     * This actual lookup logic is delegated to <code>getCildren(java.lang.Object)</code>
     * method
     * @see AbstractLookup#getChildren(java.lang.Object)
     * @param env
     * @param controlClass
     * @param criteria
     * @param wrapper
     */
    public AbstractLookup(Environment env, Class<CONTROL> controlClass, LookupCriteria<CONTROL> criteria, Wrapper wrapper) {
        this.env = env;
        found = new ArrayList<CONTROL>();
        this.clss = controlClass;
        this.criteria = criteria;
        this.wrapper = wrapper;
    }

    Wrapper getWrapper() {
        return wrapper;
    }

    /**
     *
     * @return
     */
    LookupCriteria<CONTROL> getCriteria() {
        return criteria;
    }

    /**
     *
     * @return
     */
    Environment getEnvironment() {
        return env;
    }

    /**
     *
     * @return The class of the sount controls.
     */
    Class<CONTROL> getControlClass() {
        return clss;
    }

    public <T extends CONTROL> Lookup<T> lookup(Class<T> controlClass, LookupCriteria<T> criteria) {
        return new ClassLookupImpl<CONTROL, T>(env, this, controlClass, criteria, wrapper);
    }

    public Lookup<CONTROL> lookup(LookupCriteria<CONTROL> criteria) {
        return new ClassLookupImpl<CONTROL, CONTROL>(env, this, clss, criteria, wrapper);
    }

    /**
     * Waits for certain number of controls to fit criteria.
     * Depending on how outputs set, prints out info about the lookup.
     * @param count
     * @return this, after the count of found number of found controls
     * exceeds the required.
     * @see AbstractLookup#OUTPUT
     */
    public Lookup<? extends CONTROL> wait(final int count) {
        getEnvironment().getOutput(OUTPUT).println("Waiting for " + count + " controls of " + clss.getName() + " class fitting criteria " + criteria);
        env.getWaiter(Lookup.WAIT_CONTROL_TIMEOUT.getName()).ensureState(new State<Integer>() {

            public Integer reached() {
                if(found.size() < count)
                    refresh();
                return (found.size() >= count) ? found.size() : null;
            }

            @Override
            public String toString() {
                return "Waiting for " + count + " " + clss.getName() + " controls to be found adhering to "
                        + criteria;
            }

        });
        return this;
    }

    /**
     * Gets the number of controls which fit criteria.
     * Depending on how outputs set, prints out info about the lookup.
     * @see AbstractLookup#OUTPUT
     * @return
     */
    public int size() {
        getEnvironment().getOutput(OUTPUT).println("Getting number of controls of " + clss.getName() + " class fitting criteria " + criteria);
        refresh();
        return found.size();
    }

    /**
     *
     */
    void refresh() {
        found.clear();
        List childen = getChildren(null);
        if (childen != null) {
            for (Object c : childen) {
                add(c);
            }
        }
    }

    @SuppressWarnings("element-type-mismatch")
    private void add(Object subparent) {
        if (subparent != null/* && !found.contains(subparent)*/) {
            if (clss.isInstance(subparent) && check(clss.cast(subparent))) {
                found.add(clss.cast(subparent));
            }
            List childen = getChildren(subparent);
            if (childen != null) {
                for (Object child : childen) {
                    add(child);
                }
            }
        }
    }

    /**
     *
     * @param control
     * @return
     */
    protected boolean check(CONTROL control) {
        return control != null && criteria.check(control);
    }

    /**
     * Returns Wrap of the control with specified index
     * @param index
     * @return Wrap
     */
    public Wrap<? extends CONTROL> wrap(int index) {
        return instantiate(get(index));
    }

    /**
     *
     * @return
     */
    public Wrap<? extends CONTROL> wrap() {
        return wrap(0);
    }

    /**
     * @{inheritDoc}
     */
    public <INTERFACE extends ControlInterface> INTERFACE as(int index, Class<INTERFACE> interfaceClass) {
        return wrap(index).as(interfaceClass);
    }

    /**
     * @{inheritDoc}
     */
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> interfaceClass) {
        return as(0, interfaceClass);
    }

    /**
     * @{inheritDoc}
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(int index, Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        return wrap(index).as(interfaceClass, type);
    }

    /**
     * @{inheritDoc}
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        return as(0, interfaceClass, type);
    }

    public CONTROL get(int index) {
        wait(index + 1);
        return found.get(index);
    }

    /**
     * @{inheritDoc}
     */
    public CONTROL get() {
        return get(0);
    }

    /**
     *
     * @return
     */
    List<CONTROL> getFound() {
        return found;
    }

    /**
     *
     * @return
     */
    public Class<CONTROL> getType() {
        return getControlClass();
    }

    /**
     * Override this to get subchildren of the controls in the hierarchy.
     * List could contain any type of objects - not just <code>CONTROL</code>.
     * @param subParent - one of the elements in the hierarchy reflected by this lookup.
     * If null passed - first level children are expected.
     * @return
     */
    abstract List getChildren(Object subParent);

    private String buildClassChain(Class cls) {
        StringBuilder sb = new StringBuilder(cls.getName());
        if (getType().isInterface()) {
            sb.append(" implements ").append(getType().getName());
        } else {
            do {
                cls = cls.getSuperclass();
                sb.append(" <- ").append(cls.getName());
            } while (!cls.equals(getType()) && !cls.equals(Object.class));
        }
        return sb.toString();
    }

    /**
     * Wraps the control with a <code>Wrap</code> class.
     * @see Wrap
     * @param wrap
     * @return Wrap
     */
    private Wrap<? extends CONTROL> instantiate(CONTROL control) {
        return wrapper.wrap(clss, control);
    }

    /**
     *
     * @param out
     * @param obj
     * @param prefix
     */
    protected void dumpOne(PrintStream out, CONTROL obj, String prefix) {
        Map<String, Object> data = getWrapper().wrap(getControlClass(), getControlClass().cast(obj)).getPropertiesQiuet();
        out.println(prefix + "+-" + buildClassChain(obj.getClass()));
        for (String key : data.keySet()) {
            out.print(prefix + PREFIX_DELTA + "  " + key + "=");
            if (data.get(key) == null) {
                out.println("null");
            } else {
                out.println(data.get(key));
            }
        }
    }

    /**
     *
     * @param out
     * @param lookup
     */
    protected abstract void dump(PrintStream out, Lookup<? extends CONTROL> lookup);

    public void dump(PrintStream out) {
        dump(out, this);
    }
    /*
    static class LookupImpl<T> extends AbstractLookup<T> {

    AbstractLookup<T> parent;

    public LookupImpl(Environment env, AbstractLookup<T> parent, Class<T> clss, LookupCriteria<T> criteria) {
    super(env, clss, criteria);
    this.parent = parent;
    }

    @Override
    public List<T> getChildren(T subParent) {
    return getFound();
    }

    @Override
    public Wrap<? extends T> instantiate(T control) {
    return parent.instantiate(control);
    }

    @Override
    protected void refresh() {
    parent.refresh();
    super.refresh();
    }

    }
     */

    static class ClassLookupImpl<T, ST extends T> extends AbstractLookup<ST> {

        AbstractLookup<T> parent;
        Class<ST> cls;

        public ClassLookupImpl(Environment env, AbstractLookup<T> parent, Class<ST> cls, LookupCriteria<ST> criteria, Wrapper wrapper) {
            super(env, cls, criteria, wrapper);
            this.cls = cls;
            this.parent = parent;
        }

        @Override
        protected boolean check(ST control) {
            return getControlClass().isInstance(control) && super.check(control);
        }

        @Override
        public List getChildren(Object subParent) {
            return getFound();
        }

        @Override
        protected void refresh() {
            parent.refresh();
            for (T next : parent.found) {
                if (cls.isInstance(next) && check(cls.cast(next))) {
                    super.found.add(cls.cast(next));
                }
            }
        }

        @Override
        protected void dump(PrintStream out, Lookup<? extends ST> lookup) {
            parent.dump(out, lookup);
        }
    }
    /*
    static class ClassLookupImpl<ST> extends AbstractLookup<ST> {

    AbstractLookup parent;

    public ClassLookupImpl(Environment env, AbstractLookup parent, Class<ST> cls, LookupCriteria<ST> criteria) {
    super(env, cls, criteria);
    this.parent = parent;
    }

    @Override
    protected boolean check(ST wrap) {
    return getControlClass().isInstance(wrap) && super.check(wrap);
    }

    @Override
    public List getChildren(Object subParent) {
    return getFound();
    }

    @Override
    public Wrap<? extends ST> instantiate(ST wrap) {
    return parent.instantiate(wrap);
    }

    @Override
    protected void refresh() {
    parent.refresh();
    super.refresh();
    }

    }
     */
    /*
    class ClassParent<T extends CONTROL> implements Parent<T> {

    Class clss;

    ClassParent(Class clss) {
    this.clss = clss;
    }

    public Wrap<? extends T> instantiate(T wrap) {
    return (Wrap<? extends T>) AbstractLookup.this.instantiate((CONTROL) wrap);
    }

    public Lookup<T> lookup(final LookupCriteria<T> criteria) {
    return new LookupImpl<T>(env, this, new TypedLookup<T>(clss) {

    @Override
    public boolean checkControl(T wrap) {
    return criteria.check(wrap);
    }
    });
    }
    }
     */
}
