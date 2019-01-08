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
import java.io.OutputStream;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author shura
 */
class DumpGenerator {

    public static final String ROOT = "root";
    public static final String CONTROL = "control";
    public static final String WRAP = "wrap";
    public static final String SUPER_WRAP = "super-wrap";
    public static final String DOCK_NAME = "dock-name";
    public static final String PREFERRED_PARENT = "preferred-parent";
    public static final String DEFAULT_PARENT = "default-parent";
    public static final String METHOD = "method";
    public static final String CLASS = "class";
    public static final String DESCRIPTION = "description";
    public static final String DEFAULT_WRAPPER = "default-wrapper";
    public static final String INTERFACES = "interfaces";
    public static final String INTERFACE = "interface";
    public static final String TYPE = "type";
    public static final String ENCAPSULATES = "encapsulates";
    public static final String NAME = "name";
    public static final String LOOKUPS = "lookups";
    public static final String LOOKUP = "lookup";
    public static final String DECLARING = "declaring";
    public static final String PARAMETERS = "parameters";
    public static final String PARAMETER = "parameter";
    public static final String PROPERTIES = "properties";
    public static final String PROPERTY = "property";

    private final ProcessingEnvironment processingEnv;
    private final List<ControlSupport> docks;

    public DumpGenerator(ProcessingEnvironment processingEnv, List<ControlSupport> docks) {
        this.processingEnv = processingEnv;
        this.docks = docks;
    }

    void generate() throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement(ROOT);
        doc.appendChild(root);

        for (ControlSupport cs : docks) {
            Element cse = doc.createElement(CONTROL);
            root.appendChild(cse);
            cse.setAttribute(WRAP, ((TypeElement) cs.getWrap().asElement()).getQualifiedName().toString());
            if (cs.getSuperWrap() != null) {
                cse.setAttribute(SUPER_WRAP, ((TypeElement) cs.getSuperWrap().asElement()).getQualifiedName().toString());
            }
            cse.setAttribute(CONTROL, ((TypeElement) cs.getControl().asElement()).getQualifiedName().toString());
            cse.setAttribute(DOCK_NAME, cs.getDockName());
            if(cs.getPreferredParent() != null) {
                cse.setAttribute(PREFERRED_PARENT, ((TypeElement) cs.getPreferredParent().asElement()).getQualifiedName().toString());
            }
            if (cs.getDefaultParent() != null) {
                Element dp = doc.createElement(DEFAULT_PARENT);
                cse.appendChild(dp);
                dp.setAttribute(METHOD, cs.getDefaultParent().getMethod().getSimpleName().toString());
                dp.setAttribute(CLASS, ((TypeElement) cs.getDefaultParent().getMethod().getEnclosingElement()).getQualifiedName().toString());
                dp.setAttribute(DESCRIPTION, cs.getDefaultParent().getDescription());
            }
            if (cs.getDefaultWrapper() != null) {
                Element dw = doc.createElement(DEFAULT_WRAPPER);
                cse.appendChild(dw);
                dw.setAttribute(METHOD, cs.getDefaultWrapper().getMethod().getSimpleName().toString());
                dw.setAttribute(CLASS, ((TypeElement) cs.getDefaultWrapper().getMethod().getEnclosingElement()).getQualifiedName().toString());
            }
            Element les = doc.createElement(INTERFACES);
            cse.appendChild(les);
            for (ControlInterfaceSupport ci : cs.getInterfaces()) {
                Element cie = doc.createElement(INTERFACE);
                les.appendChild(cie);
                cie.setAttribute(TYPE, Processor.toStringDollar(ci.getType()));
                if (ci.getEncapsulates() != null) {
                    cie.setAttribute(ENCAPSULATES, Processor.toStringDollar(ci.getEncapsulates()));
                }
                cie.setAttribute(NAME, ci.getName());
            }
            Element cies = doc.createElement(LOOKUPS);
            cse.appendChild(cies);
            for (LookupSupport l : cs.getObjectLookups()) {
                Element le = doc.createElement(LOOKUP);
                cies.appendChild(le);
                le.setAttribute(METHOD, l.getMethod().getSimpleName().toString());
                le.setAttribute(DECLARING, l.getDeclaringType().getQualifiedName().toString());
                le.setAttribute(DESCRIPTION, l.getDescription());
                Element lpes = doc.createElement(PARAMETERS);
                le.appendChild(lpes);
                for (SupportParameter sp : l.getParams()) {
                    Element spe = doc.createElement(PARAMETER);
                    lpes.appendChild(spe);
                    spe.setAttribute(TYPE, Processor.toStringDollar(sp.getType()));
                    spe.setAttribute(NAME, sp.getName());
                }
            }
            Element pes = doc.createElement(PROPERTIES);
            cse.appendChild(pes);
            for (MappedPropertySupport mp : cs.getProperties()) {
                Element mpe = doc.createElement(PROPERTY);
                pes.appendChild(mpe);
                mpe.setAttribute(NAME, mp.getName());
                mpe.setAttribute(TYPE, Processor.toStringDollar(mp.getType()));//TODO only one StringTypeGetter needed
            }
            for (DirectPropertySupport mp : cs.getPropertyMethods()) {
                Element mpe = doc.createElement(PROPERTY);
                pes.appendChild(mpe);
                mpe.setAttribute(NAME, mp.getName());
                mpe.setAttribute(TYPE, Processor.toStringDollar(mp.getMethod().getReturnType()));
            }
        }

        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        OutputStream out = processingEnv.getFiler().
                createResource(StandardLocation.SOURCE_OUTPUT, "", "support.xml").
                openOutputStream();

        //create string from xml tree
        StreamResult result = new StreamResult(out);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
    }
}
