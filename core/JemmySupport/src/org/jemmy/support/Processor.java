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

import java.util.*;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.Diagnostic;
import org.jemmy.control.ControlType;

/**
 * JemmySupport annotation processor.
 * One would supposed to hook this to the compilation process by adding next
 * arguments to <code>javac</code> call
 * <pre>
 * -processor Processor -Aactions=&lt;actions&gt; -s &lt;destination&gt;
 * </pre>
 * where <code>actions</code> could, at the moment, contain <code>"docks"</code>
 * and/or <code>"dump"</code>.<br/>
 * <code>"docks"</code> means that dock classes will be generated into a dir passed by
 * <code>-s</code> option. <br/>
 * <code>"dump"</code> means that there will be <code>support.xml</code> file generated
 * into a dir passed by <code>-s</code> option. <br/>
 *
 * @author shura
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {
    private static final String DOCKS = "docks";
    private static final String DUMP = "dump";

    private static final String ACTIONS = "actions";
    private final Set<String> types = new HashSet<String>();
    private final Set<String> options = new HashSet<String>();

    /**
     *
     */
    public Processor() {
        types.add(ControlType.class.getName());
        options.add(ACTIONS);
    }

    ProcessingEnvironment getProccessingEnvironment() {
        return processingEnv;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return types;
    }
    private static boolean onlyOnce = false;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        if (onlyOnce) {
            return false;
        }
        onlyOnce = true;
        List<ControlSupport> docks = new LinkedList<ControlSupport>();
        for (Element e : re.getElementsAnnotatedWith(ControlType.class)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Loading support information from " + toString(e.asType()));
            for (TypeMirror c : getClassArrayValue(getElementValue(
                    findAnnotations(e, ControlType.class).get(0), "value"))) {
                docks.add(new ControlSupport((DeclaredType) e.asType(), (DeclaredType) c, processingEnv));
            }
        }
        ControlSupport.linkSuperClasses(docks, processingEnv);
        String actions = processingEnv.getOptions().get(ACTIONS);
        if (actions.contains(DOCKS)) {
            new DockGenerator(processingEnv, docks).generate();
        }
        if (actions.contains(DUMP)) {
            try {
                new DumpGenerator(processingEnv, docks).generate();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return options;
    }

    static List<AnnotationMirror> findAnnotations(Element e, Class annotationType) {
        List<AnnotationMirror> res = new LinkedList<AnnotationMirror>();
        for (AnnotationMirror am : e.getAnnotationMirrors()) {
            if (am.getAnnotationType().toString().equals(annotationType.getName())) {
                res.add(am);
            }
        }
        return res;
    }

    static AnnotationMirror findAnnotation(Element e, Class annotationType) {
        List<AnnotationMirror> res = findAnnotations(e, annotationType);
        if (res.size() > 0) {
            return res.get(0);
        } else {
            return null;
        }
    }

    static AnnotationValue getElementValue(AnnotationMirror am, String name) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = am.getElementValues();
        for (ExecutableElement ee : values.keySet()) {
            if (ee.getSimpleName().contentEquals(name)) {
                return values.get(ee);
            }
        }
        return null;
    }
    private static final StringTypeGetter stringTypeVisitor = new StringTypeGetter(false);

    static String toString(TypeMirror type) {
        return type.accept(stringTypeVisitor, null);
    }
    private static final StringTypeGetter stringTypeVisitorDollar = new StringTypeGetter(true);

    static String toStringDollar(TypeMirror type) {
        return type.accept(stringTypeVisitorDollar, null);
    }
    private static final StringValueGetter stringValueVisitor = new StringValueGetter();

    static String getStringValue(AnnotationValue v) {
        return v.accept(stringValueVisitor, null);
    }
    private static final StringArrayValueGetter stringArrayValueVisitor = new StringArrayValueGetter();

    static List<String> getStringArrayValue(AnnotationValue v) {
        return v.accept(stringArrayValueVisitor, null);
    }
    private static final BooleanArrayValueGetter booleanArrayValueVisitor = new BooleanArrayValueGetter();

    static List<Boolean> getBooleanArrayValue(AnnotationValue v) {
        return v.accept(booleanArrayValueVisitor, null);
    }

    private static final ClassArrayValueGetter classArrayValueVisitor = new ClassArrayValueGetter();

    static List<DeclaredType> getClassArrayValue(AnnotationValue v) {
        return v.accept(classArrayValueVisitor, null);
    }

    private static final ClassValueGetter classValueVisitor = new ClassValueGetter();

    static DeclaredType getClassValue(AnnotationValue v) {
        return (DeclaredType) v.accept(classValueVisitor, null);
    }

    private static class ClassArrayValueGetter extends SimpleAnnotationValueVisitor6<List<DeclaredType>, Object> {

        @Override
        public List<DeclaredType> visitArray(List<? extends AnnotationValue> list, Object p) {
            List<DeclaredType> result = new ArrayList<DeclaredType>();
            for (AnnotationValue av : list) {
                result.add((DeclaredType) av.accept(new ClassValueGetter(), null));
            }
            return result;
        }
    }

    private static class StringArrayValueGetter extends SimpleAnnotationValueVisitor6<List<String>, Object> {

        @Override
        public List<String> visitArray(List<? extends AnnotationValue> list, Object p) {
            List<String> result = new ArrayList<String>();
            for (AnnotationValue av : list) {
                result.add(av.accept(new StringValueGetter(), null));
            }
            return result;
        }
    }

    private static class BooleanArrayValueGetter extends SimpleAnnotationValueVisitor6<List<Boolean>, Object> {

        @Override
        public List<Boolean> visitArray(List<? extends AnnotationValue> list, Object p) {
            List<Boolean> result = new ArrayList<Boolean>();
            for (AnnotationValue av : list) {
                result.add(av.accept(new BooleanValueGetter(), null));
            }
            return result;
        }
    }

    private static class ClassValueGetter extends SimpleAnnotationValueVisitor6<TypeMirror, Object> {

        @Override
        public TypeMirror visitType(TypeMirror tm, Object p) {
            return tm;
        }
    }

    private static class StringValueGetter extends SimpleAnnotationValueVisitor6<String, Object> {

        @Override
        public String visitString(String tm, Object p) {
            return tm;
        }
    }

    private static class BooleanValueGetter extends SimpleAnnotationValueVisitor6<Boolean, Object> {

        @Override
        public Boolean visitBoolean(boolean b, Object p) {
            return b;
        }
    }

    private static class StringTypeGetter extends SimpleTypeVisitor6<String, Object> {

        private final boolean dollar;

        public StringTypeGetter(boolean dollar) {
            this.dollar = dollar;
        }

        @Override
        public String visitDeclared(DeclaredType dt, Object p) {
            System.out.println("");
            ElementKind kind = ((TypeElement) dt.asElement()).getEnclosingElement().getKind();
            if(dollar && (kind == ElementKind.CLASS || kind == ElementKind.INTERFACE)) {
                return ((TypeElement)(((TypeElement) dt.asElement()).getEnclosingElement())).getQualifiedName().toString() +
                        "$" + ((TypeElement) dt.asElement()).getSimpleName().toString();
            } else {
                return ((TypeElement) dt.asElement()).getQualifiedName().toString();
            }
        }

        @Override
        public String visitPrimitive(PrimitiveType pt, Object p) {
            return pt.toString();
        }

        @Override
        public String visitArray(ArrayType at, Object p) {
            return at.getComponentType().accept(this, p) + "[]";
        }

        @Override
        public String visitTypeVariable(TypeVariable tv, Object p) {
            return tv.getUpperBound().accept(this, p);
        }

        @Override
        public String visitExecutable(ExecutableType et, Object p) {
            return et.getReturnType().accept(this, p);
        }

        @Override
        public String visitNoType(NoType notype, Object p) {
            return "void";
        }
    }
}
