/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest.dot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.mwe.emf.StandaloneSetup;
import org.openarchitectureware.graphviz.parser.XtextParser;
import org.openarchitectureware.workflow.util.ResourceLoaderImpl;
import org.openarchitectureware.xtext.parser.IXtextParser;
import org.openarchitectureware.xtext.resource.AbstractXtextResource;
import org.openarchitectureware.xtext.resource.IXtextResourceFactory;

/**
 * Walks the AST of a DOT graph, e.g. to extract the name (used to name and
 * later identify the generated file).
 * @author Fabian Steeg (fsteeg)
 */
final class DotAst {
    private DotAst() {/* Enforce non-instantiability */}
    /**
     * @param dotFile The DOT file to parse
     * @return The name of the DOT graph described in the given file
     */
    static String graphName(final File dotFile) {
        EObject graph = graph(dotFile);
        Iterator<EAttribute> graphAttributes = graph.eClass()
                .getEAllAttributes().iterator();
        while (graphAttributes.hasNext()) {
            EAttribute a = graphAttributes.next();
            /* We return the name attribute of the graph: */
            if (a.getName().equals("name")) {
                return (String) graph.eGet(a);
            }
        }
        System.err.println("Could not find name attribute in: " + graph);
        return "";
    }

    /**
     * @param dotFile The DOT file to parse
     * @return The graph EObjects to walk or inspect
     */
    static EObject graph(final File dotFile) {
        /* We load the input DOT file: */
        Resource res = loadResource(dotFile);
        EList<EObject> contents = res.getContents();
        EObject graphs = contents.iterator().next();
        /* We assume one graph per file, i.e. we take the first only: */
        EObject graph = graphs.eContents().iterator().next();
        return graph;
    }

    /**
     * @param dotFile The DOT file to parse
     * @return The errors reported by the parser when parsing the given file
     */
    static List<String> errors(final File dotFile) {
        List<String> result = new ArrayList<String>();
        EList<Diagnostic> errors = loadResource(dotFile).getErrors();
        Iterator<Diagnostic> i = errors.iterator();
        while (i.hasNext()) {
            Diagnostic next = i.next();
            result.add(String.format("Error in line %s: %s ", next
                    .getLine(), next.getMessage()));
        }
        return result;
    }

    private static Resource loadResource(final File file) {
        new StandaloneSetup().setPlatformUri("..");
        DotResourceFactory.register();
        ResourceSet set = new ResourceSetImpl();
        Resource res = set.getResource(URI.createURI(file.toURI().toString()),
                true);
        if (!res.isLoaded()) {
            try {
                res.load(Collections.EMPTY_MAP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private static class DotResource extends AbstractXtextResource {
        public DotResource(final URI uri) {
            super(uri);
            setFormattingExtension("org::openarchitectureware::graphviz::Formatting");
            setResourceLoader(new ResourceLoaderImpl(XtextParser.class
                    .getClassLoader()));
        }

        @Override
        protected IXtextParser createParser(final InputStream inputStream) {
            return new XtextParser(inputStream);
        }

    }

    private static class DotResourceFactory extends ResourceFactoryImpl
            implements IXtextResourceFactory {
        @Override
        public Resource createResource(final URI uri) {
            return new DotResource(uri);
        }

        public static void register() {
            Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
                    "dot", new DotResourceFactory());
        }
    }
}
