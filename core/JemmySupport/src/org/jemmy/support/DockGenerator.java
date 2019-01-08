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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.jemmy.control.Wrap;
import org.jemmy.dock.Dock;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Parent;
import org.jemmy.lookup.LookupCriteria;

/**
 *
 * @author shura
 */
class DockGenerator {

    private HashMap<String, String> primitiveTypesSubstitutions =
            new HashMap<String, String>();

    {
        primitiveTypesSubstitutions.put(int.class.getName(), Integer.class.getName());
        primitiveTypesSubstitutions.put(long.class.getName(), Long.class.getName());
        primitiveTypesSubstitutions.put(float.class.getName(), Float.class.getName());
        primitiveTypesSubstitutions.put(double.class.getName(), Double.class.getName());
        primitiveTypesSubstitutions.put(boolean.class.getName(), Boolean.class.getName());
    }
    private final ProcessingEnvironment env;
    private final List<ControlSupport> support;

    public DockGenerator(ProcessingEnvironment processingEnv, List<ControlSupport> docks) {
        this.env = processingEnv;
        this.support = docks;
    }

    void generate() {
        for (ControlSupport cs : support) {
            //TODO add DockInfo in every FX Wrap, then uncomment this check
            //if (cs.getDockInfo() != null) {
            if (!cs.isPlaceholder()) {
                TypeElement tel = (TypeElement) cs.getWrap().asElement();
                env.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "Generating dock for " + tel.getQualifiedName());
                //construct dock class name
                String dockName = cs.getDockName();
                String dockPackage = dockName.substring(0, dockName.lastIndexOf("."));
                String dockShortName = dockName.substring(dockName.lastIndexOf(".") + 1);
                //superclass
                TypeElement superTel = (TypeElement) ((DeclaredType) ((TypeElement) cs.getWrap().asElement()).getSuperclass()).asElement();
                String superDockName = Dock.class.getName();
                for (ControlSupport csi : support) {
                    if (env.getTypeUtils().isSameType(csi.getWrap(), superTel.asType())) {
                        superDockName = csi.getDockName();
                    }
                }
                boolean anonymous = (cs.getDockInfo() != null && cs.getDockInfo().anonymous());
                StringBuilder body = replace(body_source,
                        PACKAGE_PLACEHOLDER, dockPackage,
                        DOCK_PLACEHOLDER, dockShortName,
                        SUPERDOCK_PLACEHOLDER, superDockName,
                        WRAP_PLACEHOLDER, tel.getQualifiedName().toString(),
                        "$SEE_WRAP$", anonymous ? "" : "\t@see " + tel.getQualifiedName().toString());
                //constructors
                String controlClassName = ((TypeElement) cs.getControl().asElement()).getQualifiedName().toString();
                StringBuilder constructors = new StringBuilder();
                if (cs.getDefaultWrapper() != null) {
                    constructors.append(replace(control_constructor_source,
                            DOCK_PLACEHOLDER, dockShortName,
                            CONTROL_PLACEHOLDER, controlClassName,
                            "$WRAP_METHOD$", ((TypeElement) cs.getDefaultWrapper().
                            getMethod().getEnclosingElement()).getQualifiedName()
                            + "." + cs.getDefaultWrapper().getMethod().
                            getSimpleName().toString()));
                }
                constructors.append(replace(anonymous ? anon_wrap_constructor_source : wrap_constructor_source,
                        DOCK_PLACEHOLDER, dockShortName,
                        CONTROL_PLACEHOLDER, controlClassName,
                        "$PUBLIC$", anonymous ? "protected" : "public",
                        WRAP_PLACEHOLDER, tel.getQualifiedName().toString()).toString());
                constructors.append(replace(criteria_constructors_source,
                        DOCK_PLACEHOLDER, dockShortName,
                        CONTROL_PLACEHOLDER, controlClassName));
                boolean needsSubtypeLookups = cs.getDockInfo() != null && cs.getDockInfo().generateSubtypeLookups();
                if (needsSubtypeLookups) {
                    constructors.append(replace(criteria_subclass_constructors_source,
                            DOCK_PLACEHOLDER, dockShortName,
                            CONTROL_PLACEHOLDER, controlClassName));
                }
                String defaultParentMethodCall = null;
                String defaultParentDescription = null;
                if (cs.getDefaultParent() != null) {
                    defaultParentMethodCall = ((TypeElement) cs.getDefaultParent().
                            getMethod().getEnclosingElement()).getQualifiedName()
                            + "." + cs.getDefaultParent().getMethod().
                            getSimpleName().toString();
                    defaultParentDescription = cs.getDefaultParent().getDescription();
                    constructors.append(replace(default_parent_criteria_constructors_source,
                            DOCK_PLACEHOLDER, dockShortName,
                            CONTROL_PLACEHOLDER, controlClassName,
                            "$LOOKUP_METHOD$", defaultParentMethodCall,
                            "$DEFAULT_PARENT_DESCRIPTION$", defaultParentDescription));
                    if (needsSubtypeLookups) {
                        constructors.append(replace(default_parent_criteria_subclass_constructors_source,
                                DOCK_PLACEHOLDER, dockShortName,
                                CONTROL_PLACEHOLDER, controlClassName,
                                "$LOOKUP_METHOD$", defaultParentMethodCall,
                                "$DEFAULT_PARENT_DESCRIPTION$", defaultParentDescription));
                    }
                }
                //object lookup constructors
                for (int i = 0; i < cs.getObjectLookups().size(); i++) {
                    StringBuilder declaredParameters = glueParameters(cs.getObjectLookups().get(i).getParams(), false, true);
                    StringBuilder javadocParameters = glueParameters(cs.getObjectLookups().get(i).getParams(), true, true);
                    StringBuilder usedParameters = glueParameters(cs.getObjectLookups().get(i).getParams(), false, false);
                    if (usedParameters.length() > 0) {
                        usedParameters.insert(0, ", ");
                        javadocParameters.insert(0, ", ");
                    }
                    usedParameters.insert(0, cs.getObjectLookups().get(i).getDeclaringType().getQualifiedName() + "."
                            + cs.getObjectLookups().get(i).getMethod().getSimpleName() + "($LOOKUP_SUBTYPE$");
                    usedParameters.append(")");
                    javadocParameters.insert(0, cs.getObjectLookups().get(i).getDeclaringType().getQualifiedName() + "#"
                            + cs.getObjectLookups().get(i).getMethod().getSimpleName() + "(" + Class.class.getName());
                    javadocParameters.append(")");
                    String comma = (cs.getObjectLookups().get(i).getParams().size() > 0) ? "," : "";
                    constructors.append(replace(object_lookup_constructors_source,
                            DOCK_PLACEHOLDER, dockShortName,
                            CONTROL_PLACEHOLDER, controlClassName,
                            "$DECLARED_PARAMETERS$", declaredParameters.toString(),
                            "$USED_PARAMETERS$", usedParameters.toString(),
                            "$JAVADOC_PARAMETERS$", javadocParameters.toString(),
                            "$LOOKUP_SUBTYPE$", controlClassName + ".class",
                            "$LOOKUP_DESCRIPTION$", "by " + cs.getObjectLookups().get(i).getDescription(),
                            "$COMMA$", comma));
                    if (needsSubtypeLookups) {
                        constructors.append(replace(object_lookup_subclass_constructors_source,
                                DOCK_PLACEHOLDER, dockShortName,
                                CONTROL_PLACEHOLDER, controlClassName,
                                "$DECLARED_PARAMETERS$", declaredParameters.toString(),
                                "$USED_PARAMETERS$", usedParameters.toString(),
                                "$JAVADOC_PARAMETERS$", javadocParameters.toString(),
                                "$LOOKUP_SUBTYPE$", "cls",
                                "$LOOKUP_DESCRIPTION$", "by " + cs.getObjectLookups().get(i).getDescription(),
                                "$COMMA$", comma));
                    }
                    if (cs.getDefaultParent() != null) {
                        constructors.append(replace(default_parent_object_lookup_constructors_source,
                                DOCK_PLACEHOLDER, dockShortName,
                                CONTROL_PLACEHOLDER, controlClassName,
                                "$LOOKUP_METHOD$", defaultParentMethodCall,
                                "$DEFAULT_PARENT_DESCRIPTION$", defaultParentDescription,
                                "$DECLARED_PARAMETERS$", declaredParameters.toString(),
                                "$USED_PARAMETERS$", usedParameters.toString(),
                                "$JAVADOC_PARAMETERS$", javadocParameters.toString(),
                                "$LOOKUP_SUBTYPE$", controlClassName + ".class",
                                "$LOOKUP_DESCRIPTION$", "by " + cs.getObjectLookups().get(i).getDescription(),
                                "$COMMA$", comma));
                        if (needsSubtypeLookups) {
                            constructors.append(replace(default_parent_object_lookup_subclass_constructors_source,
                                    DOCK_PLACEHOLDER, dockShortName,
                                    CONTROL_PLACEHOLDER, controlClassName,
                                    "$LOOKUP_METHOD$", defaultParentMethodCall,
                                    "$DEFAULT_PARENT_DESCRIPTION$", defaultParentDescription,
                                    "$DECLARED_PARAMETERS$", declaredParameters.toString(),
                                    "$USED_PARAMETERS$", usedParameters.toString(),
                                    "$JAVADOC_PARAMETERS$", javadocParameters.toString(),
                                    "$LOOKUP_SUBTYPE$", "cls",
                                    "$LOOKUP_DESCRIPTION$", "by " + cs.getObjectLookups().get(i).getDescription(),
                                    "$COMMA$", comma));
                        }
                    }
                }
                replace(body, "$CONSTRUCTORS$", constructors.toString());
                //wrapper
                replace(body, "$WRAP_GETTER$", replace(
                        anonymous ? anon_wrap_getter_source : wrap_getter_source,
                        CONTROL_PLACEHOLDER, controlClassName,
                        WRAP_PLACEHOLDER, tel.getQualifiedName().toString()).toString());
                //interfaces
                StringBuilder interfaces = new StringBuilder();
                for (ControlInterfaceSupport ci : cs.getInterfaces()) {
//                    boolean skip_shortcuts = false;
                    DeclaredType innerType = ci.getEncapsulates();
                    String interface_source_orig;
                    if (anonymous) {
                        interface_source_orig = (innerType != null)
                                ? typed_interface_getter_source
                                : interface_getter_source;
                    } else {
                        switch (ci.getKind()) {
                            case SELF:
                                interface_source_orig = (innerType != null)
                                        ? implemented_typed_interface_getter_source
                                        : implemented_interface_getter_source;
                                break;
                            case ANNOTATION:
                                if (ci.getMethod().getParameters().isEmpty()) {
                                    interface_source_orig = (innerType != null)
                                            ? annotation_typed_interface_getter_source
                                            : annotation_interface_getter_source;
                                } else if (ci.getMethod().getParameters().size() == 1) {
                                    if (innerType == null) {
                                        throw new IllegalStateException("@As for an untyped interface should have no parameters: "
                                                + ci.getMethod().toString());
                                    }
                                    interface_source_orig = annotation_typed_interface_class_getter_source;
//                                    skip_shortcuts = true; //a complexity would overweight benefits
                                } else {
                                    throw new IllegalStateException("@As method could only have one or no parameters: "
                                            + ci.getMethod().toString());
                                }
                                break;
                            default:
                                interface_source_orig = (innerType != null)
                                        ? typed_interface_getter_source
                                        : interface_getter_source;
                                break;
                        }
                    }
                    StringBuilder one_interface_getter = replace(
                            interface_source_orig,
                            "$INTERFACE$", ((TypeElement) ci.getType().asElement()).getQualifiedName().toString(),
                            "$METHOD$", (ci.getMethod() != null) ? ci.getMethod().getSimpleName().toString() : "",
                            WRAP_PLACEHOLDER, tel.getQualifiedName().toString());
                    if (innerType != null) {
                        replace(one_interface_getter, "$INTERFACE_TYPE$",
                                ((TypeElement) innerType.asElement()).getQualifiedName().toString());
                    }
                    String nm;
                    if (ci.getName() != null) {
                        nm = ci.getName();
                    } else {
                        nm = "as" + ci.getType().asElement().getSimpleName();
                    }
                    replace(one_interface_getter, "$INTERFACE_GETTER$", nm);
                    interfaces.append(one_interface_getter);
//                    if (!skip_shortcuts) {
                    for (ControlInterfaceSupport.ShortcutSupport sc : ci.getShortcuts()) {
                        StringBuilder returnType = new StringBuilder(Processor.toString(sc.getReturnType()));
                        if (sc.getReturnGenerics().size() > 0) {
                            returnType.append("<");
                            for (DeclaredType rgt : sc.getReturnGenerics()) {
                                returnType.append(((TypeElement) rgt.asElement()).getQualifiedName()).
                                        append(",");
                            }
                            returnType.deleteCharAt(returnType.length() - 1).append(">");
                        }
                        StringBuilder scb = replace(shortcut_methods_source,
                                "$INTERFACE$", ((TypeElement) ci.getType().asElement()).getQualifiedName().toString(),
                                "$INTERFACE_GETTER$", nm,
                                "$ORIG_SHORTCUT_NAME$", sc.getMethod().getSimpleName().toString(),
                                "$NEW_SHORTCUT_NAME$", sc.getName(),
                                "$DECLARED_PARAMETERS$", glueParameters(sc.getParams(), false, true).toString(),
                                "$USED_PARAMETERS$", glueParameters(sc.getParams(), false, false).toString(),
                                "$JAVADOC_PARAMETERS$", glueParameters(sc.getParams(), true, true).toString(),
                                "$RETURN_TYPE$", returnType.toString(),
                                "$RETURN_STATEMENT$", sc.getMethod().getReturnType().toString().equals("void") ? "" : "return ");
                        interfaces.append(scb);
//                        }
                    }
                }
                replace(body, "$INTERFACES$", interfaces.toString());
                StringBuilder properties = new StringBuilder();
                //method properties
                for (MappedPropertySupport ps : cs.getProperties()) {
                    String type = (ps.getType() != null)
                            ? Processor.toString(ps.getType())
                            : Object.class.getName();
                    type = (type == null) ? Object.class.getName() : type;
                    String mptype;
                    if (primitiveTypesSubstitutions.containsKey(type)) {
                        mptype = primitiveTypesSubstitutions.get(type);
                    } else {
                        mptype = type;
                    }
                    properties.append(replace(ps.isMethod() ? method_property_getter_source : field_property_getter_source,
                            "$PROP_NAME$", ps.getName(),
                            "$PROP_TYPE$", type,
                            "$MAP_PROP_TYPE$", mptype,
                            "$CLASS_PARAMETER$", (ps.getType() != null) ? mptype + ".class, " : "",
                            "$ORIG_NAME$", ps.getName(),
                            "$GETTER_NAME$", createGetterName(ps.getName())));
                    if (ps.isWaitable()) {
                        properties.append(replace(property_waiter_source,
                                DOCK_PLACEHOLDER, dockShortName,
                                "$PROP_NAME$", ps.getName(),
                                "$PROP_TYPE$", type,
                                "$MAP_PROP_TYPE$", mptype,
                                "$WAITER_NAME$", createWaiterName(ps.getName()),
                                "$GETTER_NAME$", createGetterName(ps.getName())));
                    }
                }
                //declared properties
                for (DirectPropertySupport ps : cs.getPropertyMethods()) {
                    String ptype = Processor.toString(ps.getMethod().getReturnType()), mptype;
                    if (primitiveTypesSubstitutions.containsKey(ptype)) {
                        mptype = primitiveTypesSubstitutions.get(ptype);
                    } else {
                        mptype = ptype;
                    }
                    properties.append(replace(
                            anonymous ? anon_declared_property_getter_source : declared_property_getter_source,
                            "$PROP_NAME$", ps.getName(),
                            "$PROP_TYPE$", ptype,
                            "$MAPPED_PROP_TYPE$", mptype,
                            "$GETTER_NAME$", createGetterName(ps.getName()),
                            "$METHOD_NAME$", ps.getMethod().getSimpleName().toString()));
                    if (ps.isWaitable()) {
                        properties.append(replace(property_waiter_source,
                                DOCK_PLACEHOLDER, dockShortName,
                                "$PROP_NAME$", ps.getName(),
                                "$PROP_TYPE$", ptype,
                                "$WAITER_NAME$", createWaiterName(ps.getName()),
                                "$GETTER_NAME$", createGetterName(ps.getName())));
                    }
                }
                replace(body, "$PROPERTIES$", properties.toString());
                replace(body, "$MULTIPLE_LOOKUPS$", cs.isMultipleCriteria() ? "..." : "");
                try {
                    JavaFileObject f = env.getFiler().
                            createSourceFile(dockName);
                    Writer w = f.openWriter();
                    PrintWriter pw = new PrintWriter(w);
                    pw.print(body);
                    pw.flush();
                    pw.close();
                } catch (IOException ex) {
                    env.getMessager().printMessage(Diagnostic.Kind.WARNING, ex.getMessage());
                }
            }
        }
    }

    private static String createGetterName(String propString) {
        String result;
        if (!propString.startsWith("get") && !propString.startsWith("is")) {
            result = "get" + toUpperCaseCamel(propString);
        } else {
            result = propString;
        }
        return result.replace('.', '_');
    }

    private static String createWaiterName(String propString) {
        String result;
        if (propString.startsWith("get")) {
            result = propString.substring(3);
        } else if (propString.startsWith("is")) {
            result = propString.substring(2);
        } else {
            result = propString;
        }
        return "wait" + toUpperCaseCamel(result);
    }

    private static String toUpperCaseCamel(String camel) {
        StringBuilder res = new StringBuilder(camel);
        res.replace(0, 1, res.substring(0, 1).toUpperCase());
        int dot = res.indexOf(".");
        while (dot > 0) {
            res.delete(dot, dot + 1);
            if (res.length() > dot) {
                res.replace(dot, dot + 1, res.substring(dot, dot + 1).toUpperCase());
            }
            dot = res.indexOf(".");
        }
        return res.toString();
    }

    private StringBuilder replace(String b, String... replacements) {
        return replace(new StringBuilder(b), replacements);
    }

    private StringBuilder replace(StringBuilder b, String... replacements) {
        assert replacements.length % 2 == 0;
        for (int j = 0; j < replacements.length / 2; j++) {
            int i;
            while ((i = b.indexOf(replacements[j * 2])) > -1) {
                b.replace(i, i + replacements[j * 2].length(), replacements[j * 2 + 1]);
            }
        }
        return b;
    }

    private StringBuilder glueParameters(List<SupportParameter> params, boolean javadoc, boolean declaration) {
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < params.size(); j++) {
            if (j > 0) {
                result.append(", ");
            }
            if (declaration) {
                String v = Processor.toString(params.get(j).getType());
                if (j < params.size() && v.contains("[]")) {
                    v = v.replace("[]", "...");
                }
                result.append(v);
                if (!javadoc) {
                    result.append(" ");
                }
            }
            if (!javadoc) {
                result.append(params.get(j).getName());
            }
        }
        return result;
    }
    private static final String CONTROL_PLACEHOLDER = "$CONTROL$";
    private static final String PACKAGE_PLACEHOLDER = "$PACKAGE$";
    private static final String SUPERDOCK_PLACEHOLDER = "$SUPERDOCK$";
    private static final String WRAP_PLACEHOLDER = "$WRAP$";
    private static final String DOCK_PLACEHOLDER = "$DOCK$";
    private static final String body_source =
            "package $PACKAGE$;\n"
            + "import " + Wrap.class.getName() + ";\n"
            + "import " + Parent.class.getName() + ";\n"
            + "import " + LookupCriteria.class.getName() + ";\n"
            + "import " + Environment.class.getName() + ";\n"
            + "/**\n"
            + "This is a convenience class generated by information available through annotations in class $WRAP$\n"
            + "$SEE_WRAP$\n"
            + "*/\n"
            + "public class $DOCK$ extends $SUPERDOCK$ {\n"
            + "$CONSTRUCTORS$"
            + "$WRAP_GETTER$"
            + "$INTERFACES$"
            + "$PROPERTIES$"
            + "}";
    private static final String control_constructor_source =
            "\t/**Creates dock for a previously found control\n"
            + "\t@see Environment*/\n"
            + "\tpublic $DOCK$(Environment env, $CONTROL$ control) {\n"
            + "\t\tsuper($WRAP_METHOD$(env, $CONTROL$.class, control));\n"
            + "\t}\n";
    private static final String anon_wrap_constructor_source =
            "\t/**Creates dock for a wrapped control*/\n"
            + "\t$PUBLIC$ $DOCK$(Wrap<? extends $CONTROL$> wrap) {\n"
            + "\t\tsuper(wrap);\n"
            + "\t}\n";
    private static final String wrap_constructor_source =
            "\t/**Creates dock for a wrapped control\n"
            + "\t@see $WRAP$*/\n"
            + "\t$PUBLIC$ $DOCK$(Wrap<? extends $CONTROL$> wrap) {\n"
            + "\t\tsuper(wrap);\n"
            + "\t}\n";
    private static final String criteria_constructors_source =
            "\t/**Looks for an <code>index</code>'th <code>$CONTROL$</code> by a criteria"
            + " within <code>parent</code>\n"
            + "\t@see LookupCriteria*/\n"
            + "\tpublic $DOCK$(Parent<? super $CONTROL$> parent, int index, LookupCriteria<$CONTROL$>$MULTIPLE_LOOKUPS$ criteria) {\n"
            + "\t\tthis(lookup(parent, $CONTROL$.class, index, criteria));\n"
            + "\t}\n"
            + "\t/**Looks for a <code>$CONTROL$</code> by a criteria"
            + " within <code>parent</code>\n"
            + "\t@see LookupCriteria*/\n"
            + "\tpublic $DOCK$(Parent<? super $CONTROL$> parent, LookupCriteria<$CONTROL$>$MULTIPLE_LOOKUPS$ criteria) {\n"
            + "\t\tthis(parent, 0, criteria);\n"
            + "\t}\n";
    private static final String object_lookup_constructors_source =
            "\t/**Looks for an <code>index</code>'th <code>$CONTROL$</code> $LOOKUP_DESCRIPTION$"
            + " within <code>parent</code>\n"
            + "\t@see $JAVADOC_PARAMETERS$*/\n"
            + "\tpublic $DOCK$(Parent<? super $CONTROL$> parent, int index$COMMA$ $DECLARED_PARAMETERS$) {\n"
            + "\t\tthis(parent, index, $USED_PARAMETERS$);\n"
            + "\t}\n"
            + "\t/**Looks for a <code>$CONTROL$</code> $LOOKUP_DESCRIPTION$"
            + " within <code>parent</code>\n"
            + "\t@see $JAVADOC_PARAMETERS$*/\n"
            + "\tpublic $DOCK$(Parent<? super $CONTROL$> parent$COMMA$ $DECLARED_PARAMETERS$) {\n"
            + "\t\tthis(parent, $USED_PARAMETERS$);\n"
            + "\t}\n";
    private static final String default_parent_criteria_constructors_source =
            "\t/**Looks for a <code>$CONTROL$</code> by a criteria"
            + " within $DEFAULT_PARENT_DESCRIPTION$\n"
            + "\t@see LookupCriteria*/\n"
            + "\tpublic $DOCK$(LookupCriteria<$CONTROL$>$MULTIPLE_LOOKUPS$ criteria) {\n"
            + "\t\tthis(0, criteria);\n"
            + "\t}\n"
            + "\t/**Looks for an <code>index</code>'th <code>$CONTROL$</code> by a criteria"
            + " within $DEFAULT_PARENT_DESCRIPTION$\n"
            + "\t@see LookupCriteria*/\n"
            + "\tpublic $DOCK$(int index, LookupCriteria<$CONTROL$>$MULTIPLE_LOOKUPS$ criteria) {\n"
            + "\t\tthis($LOOKUP_METHOD$($CONTROL$.class), index, criteria);\n"
            + "\t}\n";
    private static final String default_parent_object_lookup_constructors_source =
            "\t/**Looks for a <code>$CONTROL$</code> $LOOKUP_DESCRIPTION$"
            + " within $DEFAULT_PARENT_DESCRIPTION$\n"
            + "\t@see $JAVADOC_PARAMETERS$*/\n"
            + "\tpublic $DOCK$($DECLARED_PARAMETERS$) {\n"
            + "\t\tthis(0, $USED_PARAMETERS$);\n"
            + "\t}\n"
            + "\t/**Looks for an <code>index</code>'th <code>$CONTROL$</code> $LOOKUP_DESCRIPTION$"
            + " within $DEFAULT_PARENT_DESCRIPTION$\n"
            + "\t@see $JAVADOC_PARAMETERS$*/\n"
            + "\tpublic $DOCK$(int index$COMMA$ $DECLARED_PARAMETERS$) {\n"
            + "\t\tthis($LOOKUP_METHOD$($CONTROL$.class), index, $USED_PARAMETERS$);\n"
            + "\t}\n";
    private static final String criteria_subclass_constructors_source =
            "\t/**Looks for an <code>index</code>'th <code>SUBCLASS</code> by a criteria"
            + " within <code>parent</code>\n"
            + "\t@see LookupCriteria*/\n"
            + "\tpublic <SUBCLASS extends $CONTROL$> $DOCK$(Parent<? super $CONTROL$> parent, Class<SUBCLASS> cls, int index, LookupCriteria<SUBCLASS>$MULTIPLE_LOOKUPS$ criteria) {\n"
            + "\t\tthis(lookup(parent, cls, index, criteria));\n"
            + "\t}\n"
            + "\t/**Looks for a <code>SUBCLASS</code> by a criteria"
            + " within <code>parent</code>\n"
            + "\t@see LookupCriteria*/\n"
            + "\tpublic <SUBCLASS extends $CONTROL$> $DOCK$(Parent<? super $CONTROL$> parent, Class<SUBCLASS> cls, LookupCriteria<SUBCLASS>$MULTIPLE_LOOKUPS$ criteria) {\n"
            + "\t\tthis(parent, cls, 0, criteria);\n"
            + "\t}\n";
    private static final String object_lookup_subclass_constructors_source =
            "\t/**Looks for an <code>index</code>'th <code>SUBCLASS</code> $LOOKUP_DESCRIPTION$"
            + " within <code>parent</code>\n"
            + "\t@see $JAVADOC_PARAMETERS$*/\n"
            + "\tpublic <SUBCLASS extends $CONTROL$> $DOCK$(Parent<? super $CONTROL$> parent, Class<SUBCLASS> cls, int index$COMMA$ $DECLARED_PARAMETERS$) {\n"
            + "\t\tthis(parent, cls, index, $USED_PARAMETERS$);\n"
            + "\t}\n"
            + "\t/**Looks for a <code>SUBCLASS</code> $LOOKUP_DESCRIPTION$"
            + " within <code>parent</code>\n"
            + "\t@see $JAVADOC_PARAMETERS$*/\n"
            + "\tpublic <SUBCLASS extends $CONTROL$> $DOCK$(Parent<? super $CONTROL$> parent, Class<SUBCLASS> cls$COMMA$ $DECLARED_PARAMETERS$) {\n"
            + "\t\tthis(parent, cls, $USED_PARAMETERS$);\n"
            + "\t}\n";
    private static final String default_parent_criteria_subclass_constructors_source =
            "\t/**Looks for a <code>SUBCLASS</code> by a criteria"
            + " within $DEFAULT_PARENT_DESCRIPTION$\n"
            + "\t@see LookupCriteria*/\n"
            + "\tpublic <SUBCLASS extends $CONTROL$> $DOCK$(Class<SUBCLASS> cls, LookupCriteria<SUBCLASS>$MULTIPLE_LOOKUPS$ criteria) {\n"
            + "\t\tthis(cls, 0, criteria);\n"
            + "\t}\n"
            + "\t/**Looks for an <code>index</code>'th <code>SUBCLASS</code> by a criteria"
            + " within $DEFAULT_PARENT_DESCRIPTION$\n"
            + "\t@see LookupCriteria*/\n"
            + "\tpublic <SUBCLASS extends $CONTROL$> $DOCK$(Class<SUBCLASS> cls, int index, LookupCriteria<SUBCLASS>$MULTIPLE_LOOKUPS$ criteria) {\n"
            + "\t\tthis($LOOKUP_METHOD$(cls), cls, index, criteria);\n"
            + "\t}\n";
    private static final String default_parent_object_lookup_subclass_constructors_source =
            "\t/**Looks for a <code>SUBCLASS</code> $LOOKUP_DESCRIPTION$"
            + " within $DEFAULT_PARENT_DESCRIPTION$\n"
            + "\t@see $JAVADOC_PARAMETERS$*/\n"
            + "\tpublic <SUBCLASS extends $CONTROL$> $DOCK$(Class<SUBCLASS> cls$COMMA$ $DECLARED_PARAMETERS$) {\n"
            + "\t\tthis(cls, 0, $USED_PARAMETERS$);\n"
            + "\t}\n"
            + "\t/**Looks for an <code>index</code>'th <code>SUBCLASS</code> $LOOKUP_DESCRIPTION$"
            + " within $DEFAULT_PARENT_DESCRIPTION$\n"
            + "\t@see $JAVADOC_PARAMETERS$*/\n"
            + "\tpublic <SUBCLASS extends $CONTROL$> $DOCK$(Class<SUBCLASS> cls, int index$COMMA$ $DECLARED_PARAMETERS$) {\n"
            + "\t\tthis($LOOKUP_METHOD$(cls), cls, index, $USED_PARAMETERS$);\n"
            + "\t}\n";
    private static final String wrap_getter_source =
            "\t/**Returns wrap\n"
            + "\t@see $WRAP$*/\n"
            + "\t@Override\n"
            + "\tpublic $WRAP$<? extends $CONTROL$> wrap() {\n"
            + "\t\treturn ($WRAP$<? extends $CONTROL$>)super.wrap();\n"
            + "\t}\n"
            + "\t/**Returns control*/\n"
            + "\t@Override\n"
            + "\tpublic $CONTROL$ control() {\n"
            + "\t\treturn wrap().getControl();\n"
            + "\t}\n";
    private static final String anon_wrap_getter_source =
            "\t/**Returns wrap\n"
            + "\t@see Wrap*/\n"
            + "\t@Override\n"
            + "\tpublic Wrap<? extends $CONTROL$> wrap() {\n"
            + "\t\treturn (Wrap<? extends $CONTROL$>)super.wrap();\n"
            + "\t}\n"
            + "\t/**Returns control*/\n"
            + "\t@Override\n"
            + "\tpublic $CONTROL$ control() {\n"
            + "\t\treturn wrap().getControl();\n"
            + "\t}\n";
    private static final String interface_getter_source =
            "\t/**Allows to use as <code>$INTERFACE$</code>\n"
            + "\t@see $INTERFACE$*/\n"
            + "\tpublic $INTERFACE$ $INTERFACE_GETTER$() {\n"
            + "\t\treturn wrap().as($INTERFACE$.class);\n"
            + "\t}\n";
    private static final String implemented_interface_getter_source =
            "\t/**Allows to use as <code>$INTERFACE$</code>\n"
            + "\t@see $INTERFACE$*/\n"
            + "\tpublic $INTERFACE$ $INTERFACE_GETTER$() {\n"
            + "\t\treturn wrap();\n"
            + "\t}\n";
    private static final String annotation_interface_getter_source =
            "\t/**Allows to use as <code>$INTERFACE$</code>\n"
            + "\t@see $INTERFACE$\n"
            + "\t@see $WRAP$#$METHOD$()*/\n"
            + "\tpublic $INTERFACE$ $INTERFACE_GETTER$() {\n"
            + "\t\treturn wrap().$METHOD$();\n"
            + "\t}\n";
    private static final String typed_interface_getter_source =
            "\t/**Allows to use as <code>$INTERFACE$&lt;$INTERFACE_TYPE$&gt;</code>\n"
            + "\t@see $INTERFACE$*/\n"
            + "\tpublic $INTERFACE$<$INTERFACE_TYPE$> $INTERFACE_GETTER$() {\n"
            + "\t\treturn wrap().as($INTERFACE$.class, $INTERFACE_TYPE$.class);\n"
            + "\t}\n";
    private static final String implemented_typed_interface_getter_source =
            "\t/**Allows to use as <code>$INTERFACE$&lt;$INTERFACE_TYPE$&gt;</code>\n"
            + "\t@see $INTERFACE$*/\n"
            + "\tpublic $INTERFACE$<$INTERFACE_TYPE$> $INTERFACE_GETTER$() {\n"
            + "\t\treturn wrap();\n"
            + "\t}\n";
    private static final String annotation_typed_interface_getter_source =
            "\t/**Allows to use as <code>$INTERFACE$&lt;$INTERFACE_TYPE$&gt;</code>\n"
            + "\t@see $INTERFACE$\n"
            + "\t@see $WRAP$#$METHOD$()*/\n"
            + "\tpublic $INTERFACE$<$INTERFACE_TYPE$> $INTERFACE_GETTER$() {\n"
            + "\t\treturn wrap().$METHOD$();\n"
            + "\t}\n";
    private static final String annotation_typed_interface_class_getter_source =
            "\t/**Allows to use as <code>$INTERFACE$&lt;$INTERFACE_TYPE$&gt;</code>\n"
            + "\t@see $INTERFACE$\n"
            + "\t@see #$INTERFACE_GETTER$(Class)\n"
            + "\t@see $WRAP$#$METHOD$(Class)*/\n"
            + "\tpublic $INTERFACE$<$INTERFACE_TYPE$> $INTERFACE_GETTER$() {\n"
            + "\t\treturn wrap().$METHOD$($INTERFACE_TYPE$.class);\n"
            + "\t}\n"
            + "\t/**Allows to use as <code>$INTERFACE$&lt;T&gt;</code>\n"
            + "\t@see $INTERFACE$\n"
            + "\t@see #$INTERFACE_GETTER$()\n"
            + "\t@see $WRAP$#$METHOD$(Class)*/\n"
            + "\tpublic <T extends $INTERFACE_TYPE$> $INTERFACE$<T> $INTERFACE_GETTER$(Class<T> type) {\n"
            + "\t\treturn wrap().$METHOD$(type);\n"
            + "\t}\n";
    private static final String property_getter_source =
            "\t/**Returns a result of <code>$PROP_NAME$</code> method*/\n"
            + "\tpublic $PROP_TYPE$ $GETTER_NAME$() {\n"
            + "\t\treturn wrap().getProperty($CLASS_PARAMETER$\"$PROP_NAME$\");\n"
            + "\t}\n";
    private static final String method_property_getter_source =
            "\t/**Returns a result of <code>$PROP_NAME$</code> method*/\n"
            + "\tpublic $PROP_TYPE$ $GETTER_NAME$() {\n"
            + "\t\treturn new org.jemmy.action.GetAction<$MAP_PROP_TYPE$>() {\n"
            + "\t\t\t@Override\n"
            + "\t\t\tpublic void run(Object... parameters) {\n"
            + "\t\t\t\tsetResult(control().$ORIG_NAME$());\n"
            + "\t\t\t}\n"
            + "\t\t}.dispatch(wrap().getEnvironment());\n"
            + "\t}\n";
    private static final String field_property_getter_source =
            "\t/**Returns a result of <code>$PROP_NAME$</code> method*/\n"
            + "\tpublic $PROP_TYPE$ $GETTER_NAME$() {\n"
            + "\t\treturn new org.jemmy.action.GetAction<$MAP_PROP_TYPE$>() {\n"
            + "\t\t\t@Override\n"
            + "\t\t\tpublic void run(Object... parameters) {\n"
            + "\t\t\t\tsetResult(control().$ORIG_NAME$);\n"
            + "\t\t\t}\n"
            + "\t\t}.dispatch(wrap().getEnvironment());\n"
            + "\t}\n";
    private static final String property_waiter_source =
            "\t/**Waits for a \"<code>$PROP_NAME$</code>\" property to be equal to a parameter\n"
            + "\t@throws org.jemmy.TimeoutExpiredException*/\n"
            + "\tpublic void $WAITER_NAME$($PROP_TYPE$ expected) {\n"
            + "\t\twrap().waitState(new org.jemmy.timing.State<$MAP_PROP_TYPE$>() {\n"
            + "\t\t\tpublic $PROP_TYPE$ reached() {\n"
            + "\t\t\t\treturn $DOCK$.this.$GETTER_NAME$();\n"
            + "\t\t\t}\n"
            + "\t\t}, expected);\n"
            + "\t}\n";
    private static final String declared_property_getter_source =
            "\t/**Returns $PROP_NAME$ property*/\n"
            + "\tpublic $PROP_TYPE$ $GETTER_NAME$() {\n"
            + "\t\treturn wrap().$METHOD_NAME$();\n"
            + "\t}\n";
    private static final String anon_declared_property_getter_source =
            "\t/**Returns $PROP_NAME$ property*/\n"
            + "\tpublic $PROP_TYPE$ $GETTER_NAME$() {\n"
            + "\t\treturn wrap().getProperty($MAPPED_PROP_TYPE$.class, \"$PROP_NAME$\");\n"
            + "\t}\n";
    private static final String shortcut_methods_source =
            "\t/**Calls <code>$INTERFACE_GETTER$().$ORIG_SHORTCUT_NAME$($DECLARED_PARAMETERS$);</code>\n"
            + "\t@see $INTERFACE$#$ORIG_SHORTCUT_NAME$($JAVADOC_PARAMETERS$)*/\n"
            + "\tpublic $RETURN_TYPE$ $NEW_SHORTCUT_NAME$($DECLARED_PARAMETERS$) {\n"
            + "\t\t $RETURN_STATEMENT$$INTERFACE_GETTER$().$ORIG_SHORTCUT_NAME$($USED_PARAMETERS$);\n"
            + "\t}\n";
}
