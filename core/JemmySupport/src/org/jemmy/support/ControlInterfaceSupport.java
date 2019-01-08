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
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.jemmy.dock.Shortcut;

/**
 *
 * @author shura
 */
public class ControlInterfaceSupport {

    public enum Kind {SELF, ANNOTATION, METHOD};

    private final DeclaredType type;
    private final DeclaredType encapsulates;
    private final String name;
    private final List<ShortcutSupport> shortcuts = new ArrayList<ShortcutSupport>();
    private final Kind kind;
    private final ExecutableElement method;

    /**
     *
     * @param type
     * @param encapsulates
     * @param name
     */
    public ControlInterfaceSupport(Kind kind, DeclaredType type, DeclaredType encapsulates,
            String name, ExecutableElement method,
            List<ShortcutSupport> allShortcuts) {
        this.kind = kind;
        this.type = type;
        this.encapsulates = encapsulates;
        this.name = name;
        this.method = method;
        collectShortcuts(type, allShortcuts);
    }

    public Kind getKind() {
        return kind;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public DeclaredType getEncapsulates() {
        return encapsulates;
    }

    /**
     *
     * @return
     */
    public DeclaredType getType() {
        return type;
    }

    /**
     *
     * @return
     */
    public List<ShortcutSupport> getShortcuts() {
        return shortcuts;
    }

    private void collectShortcuts(DeclaredType type, List<ShortcutSupport> allShortcuts) {
        for (Element e : type.asElement().getEnclosedElements()) {
            if (e instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) e;
                AnnotationMirror am = Processor.findAnnotation(e, Shortcut.class);
                if (am != null) {
                    String nm = "";
                    AnnotationValue v = Processor.getElementValue(am, "name");
                    if (v != null) {
                        nm = Processor.getStringValue(v);
                    }
                    if (nm.length() == 0) {
                        nm = method.getSimpleName().toString();
                    }
                    ShortcutSupport shortcut = new ShortcutSupport(method, nm);
                    ControlSupport.copyParameters(method, 0, shortcut.getParams());
                    if(!exists(shortcut, allShortcuts)) {
                        shortcuts.add(shortcut);
                        allShortcuts.add(shortcut);
                    }
                }
            }
        }
        TypeElement elem = (TypeElement) type.asElement();
        if (elem.getSuperclass() instanceof DeclaredType) {
            collectShortcuts((DeclaredType) elem.getSuperclass(), allShortcuts);
        }
        for (TypeMirror intf : elem.getInterfaces()) {
            collectShortcuts((DeclaredType) intf, allShortcuts);
        }
    }

    private boolean exists(ShortcutSupport shortcut, List<ShortcutSupport> shortcuts) {
        for(ShortcutSupport sc : shortcuts) {
            if(sc.getName().equals(shortcut.getName())) {
                if(sc.getParams().size() == shortcut.getParams().size()) {
                    boolean same = true;
                    for (int i = 0; i < sc.getParams().size(); i++) {
                        if(!sc.getParams().get(i).getType().equals(shortcut.getParams().get(i).getType())) {
                            same = false;
                            break;
                        }
                    }
                    if(same) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     */
    public class ShortcutSupport {

        private final ExecutableElement method;
        private final String name;
        private final List<SupportParameter> params = new LinkedList<SupportParameter>();
        private final TypeMirror returnType;
        private final List<DeclaredType> returnGenerics = new ArrayList<DeclaredType>();

        /**
         *
         * @param method
         * @param name
         */
        public ShortcutSupport(ExecutableElement method, String name) {
            this.method = method;
            this.name = name;
            returnType = method.getReturnType();
            if (returnType instanceof DeclaredType) {
                for (TypeMirror g : ((DeclaredType) returnType).getTypeArguments()) {
                    if (g.getKind() == TypeKind.TYPEVAR) {
                        if(encapsulates != null)
                            returnGenerics.add(encapsulates);
                        else if(((DeclaredType) returnType).getTypeArguments().size() > 1)
                            throw new IllegalStateException("Please specify encapsulated type for " + type.toString());
                    } else if (g instanceof DeclaredType) {
                        returnGenerics.add((DeclaredType) g);
                    } else {
                        throw new IllegalStateException("huh?");
                    }
                }
            }
        }

        /**
         *
         * @return
         */
        public ExecutableElement getMethod() {
            return method;
        }

        /**
         *
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @return
         */
        public List<SupportParameter> getParams() {
            return params;
        }

        /**
         *
         * @return
         */
        public List<DeclaredType> getReturnGenerics() {
            return returnGenerics;
        }

        /**
         *
         * @return
         */
        public TypeMirror getReturnType() {
            return returnType;
        }
    }
}
