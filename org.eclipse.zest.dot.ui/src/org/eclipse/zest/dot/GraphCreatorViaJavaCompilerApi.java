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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;

/**
 * EXPERIMENTAL - NOT REALLY WORKING YET
 * <p/>
 * Import DOT to a Zest Graph instance via the Java compiler API.
 * @author Fabian Steeg (fsteeg)
 */
final class GraphCreatorViaJavaCompilerApi implements IGraphCreator {

    /**
     * {@inheritDoc}
     * @see org.eclipse.zest.dot.IGraphCreator#create(org.eclipse.swt.widgets.Composite,
     *      int)
     */
    public Graph create(final Composite parent, final int style,
            final String dot) {
        File zestFile = DotImport.importDotString(dot);
        URL url = compileWithJavaCompiler(zestFile);
        Graph graph = ExperimentalDotImport.loadGraph(DotImport.graphName(dot),
                url, parent, style);
        return graph;
    }

    private URL compileWithJavaCompiler(final File zestFile) {
        /* Create and set up the compiler: */
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager manager = jc.getStandardFileManager(null, null,
                null);
        File outputDir = zestFile.getParentFile();
        List<String> options = new ArrayList<String>();
        options.add("-d");
        options.add(outputDir.getAbsolutePath());
        /* Compile the generated Zest graph: */
        jc.getTask(null, manager, null, options, null,
                manager.getJavaFileObjects(zestFile)).call();
        try {
            manager.close();
            /* Return the URL of the folder where the compiled class file is: */
            return outputDir.toURI().toURL();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
