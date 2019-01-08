/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
package org.jemmy.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.jemmy.control.*;
import org.jemmy.dock.DefaultWrapper;
import org.jemmy.dock.*;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.Drag;
import org.jemmy.interfaces.Keyboard;
import org.jemmy.interfaces.Mouse;

/**
 *
 * @author shura
 */
public class ControlSupport {

    private final DeclaredType wrap;
    //this could not be final 'cause an order of wraps is not defined
    //and hence inheritance is figured out later
    private DeclaredType superWrap = null;
    private final DeclaredType control;
    private final List<ControlInterfaceSupport> interfaces = new LinkedList<ControlInterfaceSupport>();
    private final List<LookupSupport> objectLookups = new LinkedList<LookupSupport>();
    private final List<MappedPropertySupport> properties = new LinkedList<MappedPropertySupport>();
    private final List<DirectPropertySupport> propertyMethods = new LinkedList<DirectPropertySupport>();
    private final List<ControlInterfaceSupport.ShortcutSupport> shortcuts = new LinkedList<ControlInterfaceSupport.ShortcutSupport>();
    private final DeclaredType preferredParent;
    private final DefaultParentSupport defaultParent;
    private final DefaultWrapperSupport defaultWrapper;
    private final DockInfo dockInfo;
    private final String dockName;
    private final boolean placeholder;
    private final DeclaredType ci, w;
    private final boolean multipleCriteria;

    private ControlSupport(ProcessingEnvironment env) {
        ci = null;
        w = null;
        placeholder = true;
        this.wrap = (DeclaredType) env.getElementUtils().
                getTypeElement(Wrap.class.getName()).asType();
        this.control = (DeclaredType) env.getElementUtils().
                getTypeElement(Object.class.getName()).asType();
        defaultParent = null;
        preferredParent = null;
        defaultWrapper = null;
        dockInfo = null;
        multipleCriteria = false;
        dockName = Dock.class.getName();
        //TODO while implementing external wrap superclasses learn to pull interfaces from there
        interfaces.add(new ControlInterfaceSupport(ControlInterfaceSupport.Kind.ANNOTATION,
                (DeclaredType) env.getElementUtils().
                getTypeElement(Mouse.class.getName()).asType(), null, "mouse",
                null, shortcuts));
        interfaces.add(new ControlInterfaceSupport(ControlInterfaceSupport.Kind.ANNOTATION,
                (DeclaredType) env.getElementUtils().
                getTypeElement(Keyboard.class.getName()).asType(), null, "keyboard",
                null, shortcuts));
        interfaces.add(new ControlInterfaceSupport(ControlInterfaceSupport.Kind.ANNOTATION,
                (DeclaredType) env.getElementUtils().
                getTypeElement(Drag.class.getName()).asType(), null, "drag",
                null, shortcuts));
    }

    ControlSupport(DeclaredType wrap, DeclaredType control, ProcessingEnvironment env) {
        ci = (DeclaredType) env.getElementUtils().
                getTypeElement(ControlInterface.class.getName()).asType();
        w = (DeclaredType) env.getTypeUtils().erasure(env.getElementUtils().
                getTypeElement(Wrap.class.getName()).asType());
        placeholder = false;
        this.wrap = wrap;
        this.control = control;
        TypeMirror pWrap = wrap;
        TypeElement typeEl = (TypeElement) ((DeclaredType) pWrap).asElement();
        dockInfo = typeEl.getAnnotation(DockInfo.class);
        AnnotationMirror ciAM = Processor.findAnnotation(typeEl, ControlInterfaces.class);
        if (ciAM != null) {
            List<TypeMirror> implemented = new ArrayList<TypeMirror>();
            findImplementedInterfaces(wrap, implemented, env);
            List<ExecutableElement> annotated = new ArrayList<ExecutableElement>();
            findAnnotatedInterfaces(wrap, annotated, env);
            List<DeclaredType> value = Processor.getClassArrayValue(Processor.getElementValue(ciAM, "value"));
            List<DeclaredType> encapsulates;
            AnnotationValue v = Processor.getElementValue(ciAM, "encapsulates");
            if (v != null) {
                encapsulates = Processor.getClassArrayValue(v);
            } else {
                encapsulates = Collections.emptyList();
            }
            List<String> name;
            v = Processor.getElementValue(ciAM, "name");
            if (v != null) {
                name = Processor.getStringArrayValue(v);
            } else {
                name = Collections.EMPTY_LIST;
            }
            for (int i = 0; i < value.size(); i++) {
                DeclaredType interfaceType = value.get(i);
                DeclaredType encapsulatedType = (encapsulates.size() > i) ? encapsulates.get(i) : null;
                ControlInterfaceSupport.Kind kind = ControlInterfaceSupport.Kind.METHOD;
                ExecutableElement method = null;
                for (TypeMirror tm : implemented) {
                    if (env.getTypeUtils().isSameType(interfaceType, env.getTypeUtils().erasure(tm))) {
                        if (encapsulatedType == null) {
                            kind = ControlInterfaceSupport.Kind.SELF;
                        } else {
                            if (((DeclaredType) tm).getTypeArguments().size() == 1
                                    && env.getTypeUtils().isSameType(encapsulatedType,
                                    ((DeclaredType) tm).getTypeArguments().get(0))) {
                                kind = ControlInterfaceSupport.Kind.SELF;
                            }
                        }
                    }
                }
                for (ExecutableElement mthd : annotated) {
                    if (env.getTypeUtils().isSameType(interfaceType, env.getTypeUtils().erasure(mthd.getReturnType()))) {
                        if (encapsulatedType == null) {
                            kind = ControlInterfaceSupport.Kind.ANNOTATION;
                            method = mthd;
                        } else {
                            AnnotationMirror asAM = Processor.findAnnotation(mthd, As.class);
                            if (asAM.getElementValues().size() > 0) {
                                DeclaredType vAE = Processor.getClassValue(Processor.getElementValue(asAM, "value"));
                                if (!env.getTypeUtils().isSameType(vAE, env.getElementUtils().getTypeElement(Void.class.getName()).asType())
                                        && env.getTypeUtils().isSameType(vAE, encapsulatedType)) {
                                    kind = ControlInterfaceSupport.Kind.ANNOTATION;
                                    method = mthd;
                                }
                            }
                        }
                    }
                }
                interfaces.add(new ControlInterfaceSupport(kind, interfaceType,
                        encapsulatedType,
                        (name.size() > i) ? name.get(i) : ("as" + value.get(i).asElement().getSimpleName()),
                        method, shortcuts));
            }
        }
        AnnotationMirror ppAM = Processor.findAnnotation(typeEl, PreferredParent.class);
        //PreferredParent ppa = typeEl.getAnnotation(PreferredParent.class);
        if(ppAM != null) {
            System.out.println("found a preffered wrap for " + typeEl.toString());
            preferredParent = Processor.getClassValue(Processor.getElementValue(ppAM, "value"));
            //prefferedParent = ppa.value();
        } else {
            System.out.println("no preffered wrap for " + typeEl.toString());
            preferredParent = null;
        }
        AnnotationMirror mpAM = Processor.findAnnotation(typeEl, MethodProperties.class);
        AnnotationMirror fpAM = Processor.findAnnotation(typeEl, FieldProperties.class);
        DefaultParentSupport dParent = null;
        DefaultWrapperSupport dWrapper = null;
        boolean origWrap = true;
        do {
            for (Element el : typeEl.getEnclosedElements()) {
                if (el instanceof ExecutableElement) {
                    ExecutableElement eel = (ExecutableElement) el;
                    if (dParent == null) {
                        DefaultParent defaultParentAnn = el.getAnnotation(DefaultParent.class);
                        if (defaultParentAnn != null) {
                            dParent = new DefaultParentSupport(eel, defaultParentAnn.value());
                        }
                    }
                    if (dWrapper == null) {
                        DefaultWrapper defaultWrapperAnn = el.getAnnotation(DefaultWrapper.class);
                        if (defaultWrapperAnn != null) {
                            dWrapper = new DefaultWrapperSupport(eel, origWrap);
                        }
                    }
                    ObjectLookup olA = el.getAnnotation(ObjectLookup.class);
                    if (olA != null) {
                        if (eel.getParameters().isEmpty()
                                || !eel.getParameters().get(0).asType().toString().startsWith(Class.class.getName())) { //TODO classname
                            throw new IllegalStateException("Expect first parameter to ba a class but found "
                                    + (eel.getParameters().isEmpty() ? "none" : eel.getParameters().get(0).asType().toString()));
                        }
                        LookupSupport newLs = new LookupSupport(typeEl, eel, olA.value());
                        copyParameters(eel, 1, newLs.getParams());
                        boolean found = false;
                        for (LookupSupport ls : objectLookups) {
                            if (ls.equalInTypes(newLs)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            objectLookups.add(newLs);
                        }
                    }
                    if (origWrap) {
                        Property pA = el.getAnnotation(Property.class);
                        if (pA != null) {
                            if (eel.getParameters().size() > 0) {
                                throw new IllegalStateException("Property getter must have no parameters: " + eel.getSimpleName());
                            }
                            boolean found = false;
                            for (DirectPropertySupport dps : propertyMethods) {
                                if (pA.value().equals(dps.getName())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                propertyMethods.add(new DirectPropertySupport(pA.value(), eel, pA.waitable()));
                            }
                        }
                    }
                }
            }
            typeEl = (TypeElement) ((DeclaredType) typeEl.getSuperclass()).asElement();
            origWrap = false;
        } while (!typeEl.getQualifiedName().toString().equals(Wrap.class.getName()));
        defaultParent = dParent;
        defaultWrapper = dWrapper;
        if (mpAM != null) {
            addProperties(mpAM, true);
        }
        if (fpAM != null) {
            addProperties(fpAM, false);
        }
        multipleCriteria = (dockInfo != null) ? dockInfo.multipleCriteria() : true;
        if (dockInfo != null && dockInfo.name().length() > 0) {
            dockName = dockInfo.name();
        } else {
            String wrapName = ((TypeElement) wrap.asElement()).getQualifiedName().toString();
            dockName = wrapName.substring(0, wrapName.lastIndexOf(".")) + "."
                    + control.asElement().getSimpleName().toString() + "Dock";
        }
    }

    public boolean isMultipleCriteria() {
        return multipleCriteria;
    }

    private void findImplementedInterfaces(DeclaredType wrap, List<TypeMirror> interfaces, ProcessingEnvironment processingEnv) {
        if (processingEnv.getTypeUtils().isSameType(w, processingEnv.getTypeUtils().erasure(wrap))) {
            return;
        }
        for (TypeMirror i : ((TypeElement) wrap.asElement()).getInterfaces()) {
            if (processingEnv.getTypeUtils().isAssignable(i, ci)) {
                interfaces.add(i);
            }
        }
//        findImplementedInterfaces((DeclaredType)((TypeElement) wrap.asElement()).getSuperclass(),
//                interfaces, processingEnv);
    }

    private void findAnnotatedInterfaces(DeclaredType wrap, List<ExecutableElement> interfaces, ProcessingEnvironment processingEnv) {
        if (processingEnv.getTypeUtils().isSameType(w, processingEnv.getTypeUtils().erasure(wrap))) {
            return;
        }
        for (Element el : ((TypeElement) wrap.asElement()).getEnclosedElements()) {
            As as = el.getAnnotation(As.class);
            if (as != null) {
                if (el instanceof ExecutableElement) {
                    interfaces.add((ExecutableElement) el);
                } else {
                    throw new IllegalStateException("@As applied to something else but a method " + el.toString());
                }
            }
        }
//        findAnnotatedInterfaces((DeclaredType)((TypeElement) wrap.asElement()).getSuperclass(),
//                interfaces, processingEnv);
    }

    public DeclaredType getPreferredParent() {
        return preferredParent;
    }

    /**
     *
     * @return
     */
    public DeclaredType getSuperWrap() {
        return superWrap;
    }

    /**
     *
     * @return
     */
    public boolean isPlaceholder() {
        return placeholder;
    }

    /**
     *
     * @return
     */
    public String getDockName() {
        return dockName;
    }

    private void addProperties(AnnotationMirror am, boolean isMethod) {
        List<String> value = Processor.getStringArrayValue(Processor.getElementValue(am, "value"));
        List<Boolean> waitable;
        if(Processor.getElementValue(am, "waitable") != null) {
            waitable = Processor.getBooleanArrayValue(Processor.getElementValue(am, "waitable"));
        } else {
            waitable = Collections.EMPTY_LIST;
        }
        List<DeclaredType> types;
        AnnotationValue v = Processor.getElementValue(am, "types");
        if (v != null) {
            types = Processor.getClassArrayValue(v);
        } else {
            types = Collections.EMPTY_LIST;
        }
        for (int i = 0; i < value.size(); i++) {
            boolean found = false;
            for (MappedPropertySupport ps : properties) {
                if (ps.getName().equals(value.get(i))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                TypeMirror type;
                if (types.size() > i) {
                    type = types.get(i);
                } else {
                    type = findType(control, value.get(i), isMethod);
                }
                properties.add(new MappedPropertySupport(value.get(i), type, isMethod,
                        (waitable.size() > i) ? waitable.get(i) : false));
            }
        }
    }

    /**
     *
     * @return
     */
    public DeclaredType getControl() {
        return control;
    }

    /**
     *
     * @return
     */
    public List<LookupSupport> getObjectLookups() {
        return objectLookups;
    }

    /**
     *
     * @return
     */
    public DeclaredType getWrap() {
        return wrap;
    }

    /**
     *
     * @return
     */
    public List<ControlInterfaceSupport> getInterfaces() {
        return interfaces;
    }

    /**
     *
     * @return
     */
    public List<MappedPropertySupport> getProperties() {
        return properties;
    }

    /**
     *
     * @return
     */
    public List<DirectPropertySupport> getPropertyMethods() {
        return propertyMethods;
    }

    /**
     *
     * @return
     */
    public DefaultParentSupport getDefaultParent() {
        return defaultParent;
    }

    /**
     *
     * @return
     */
    public DefaultWrapperSupport getDefaultWrapper() {
        return defaultWrapper;
    }

    /**
     *
     * @return
     */
    public DockInfo getDockInfo() {
        return dockInfo;
    }

    static void copyParameters(ExecutableElement eel, int firstParam, List<SupportParameter> params) {
        for (int i = firstParam; i < eel.getParameters().size(); i++) {
            VariableElement ve = eel.getParameters().get(i);
            params.add(new SupportParameter(ve.getSimpleName().toString(), ve.asType()));
        }
    }

    private TypeMirror findType(DeclaredType control, String name, boolean isMethod) {
        TypeMirror tp = control;
        TypeElement te;
        while (tp instanceof DeclaredType && tp.getKind() != TypeKind.NULL) {
            te = (TypeElement) ((DeclaredType) tp).asElement();
            for (Element e : te.getEnclosedElements()) {
                if (e instanceof ExecutableElement) {
                    ExecutableElement ee = (ExecutableElement) e;
                    if (isMethod && e.getKind() == ElementKind.METHOD
                            || !isMethod && e.getKind() == ElementKind.FIELD) {
                        if (e.getSimpleName().toString().equals(name)
                                && (!isMethod || ee.getParameters().isEmpty())) {
                            return e.asType();
                        }
                    }
                }
            }
            tp = te.getSuperclass();
        }
        return null;
    }

    //TODO - do a quicksort, at least
    /**
     *
     * @param controls
     * @param env
     */
    public static void linkSuperClasses(List<ControlSupport> controls, ProcessingEnvironment env) {
        ControlSupport root = null;
        for (ControlSupport cs : controls) {
            DeclaredType superWrap = (DeclaredType) ((TypeElement) cs.getWrap().asElement()).getSuperclass();
            boolean foundOne = false;
            for (ControlSupport csi : controls) {
                if (((TypeElement) csi.getWrap().asElement()).getQualifiedName().toString().equals(
                        ((TypeElement) superWrap.asElement()).getQualifiedName().toString())) {
                    //that would mean we have found a super wrap among the compiled ones
                    foundOne = true;
                    break;
                }
            }
            if (!foundOne) {
                //the super-wrap is in classpath somewhere
                //for now only org.jemmy.control.Wrap could be external
                //TODO improve DockInfo to allow external parents
                if (((TypeElement) superWrap.asElement()).getQualifiedName().
                        toString().equals(Wrap.class.getName())) {
                    if (root == null) {
                        root = new ControlSupport(env);
                    }
                } else {
                    throw new IllegalStateException("Unknown parent Wrap type "
                            + ((TypeElement) superWrap.asElement()).getQualifiedName().toString());
                }
            }
            cs.superWrap = superWrap;
        }
        controls.add(root);
    }
}
