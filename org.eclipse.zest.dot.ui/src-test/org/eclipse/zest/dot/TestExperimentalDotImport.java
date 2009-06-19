/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest.dot;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.junit.Test;

/**
 * Tests for the {@link ExperimentalDotImport} class.
 * @author Fabian Steeg (fsteeg)
 */
public class TestExperimentalDotImport {
    static final String DOT_TEXT = "digraph ExperimentalGraph{1;2;3;4; 1->2;2->3;2->4}";

    /**
     * Import instance by compiling the generated Zest graph using the Java
     * Compiler API (downside: requires Java 6)
     */
    @Test
    public void viaJavaCompilerApi() {
        test(new GraphFromDotViaJavaCompilerApi());
    }

    /**
     * Import instance by compiling the generated Zest graph using the Eclipse
     * JDT compiler (downside: API is internal)
     */
    @Test
    public void viaInternalJdtCompiler() {
        test(new GraphFromDotViaInternalJdtCompiler());
    }

    /**
     * Test importing a DOT graph to a Zest graph instance directly. Internally,
     * this compiles the generated Zest class and loads it using Reflection.
     */
    private void test(final GraphFromDot converter) {
        Shell shell = new Shell();
        Graph graph = converter.create(shell, SWT.NONE, DOT_TEXT);
        Assert.assertNotNull(graph);
        System.out.println(String.format(
                "Imported '%s' to Graph '%s' of type '%s'", DOT_TEXT, graph,
                graph.getClass().getSimpleName()));
        //open(shell); // blocks UI when running tests
    }

    @SuppressWarnings( "unused" )
    // optional when running the tests
    private static void open(final Shell shell) {
        shell.setText(GlobalEdgeGraph.class.getSimpleName());
        shell.setLayout(new FillLayout());
        shell.setSize(200, 250);
        shell.open();
        while (!shell.isDisposed()) {
            while (!shell.getDisplay().readAndDispatch()) {
                shell.getDisplay().sleep();
            }
        }
    }
}
