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
public final class TestExperimentalDotImport {
    /**
     * Import instance by compiling the generated Zest graph using the Java
     * Compiler API (downside: requires Java 6)
     */
    @Test
    public void viaJavaCompilerApi() {
        /*
         * Implementation using Java compiler API requires Java 6.
         */
        // Assert.fail("Java 6 dependency deactivated");
        test(new GraphCreatorViaJavaCompilerApi());
    }

    /**
     * Import instance by compiling the generated Zest graph using the Eclipse
     * JDT compiler (downside: API is internal)
     */
    @Test
    public void viaInternalJdtCompiler() {
        test(new GraphCreatorViaInternalJdtCompiler());

    }

    /**
     * Test importing a DOT graph to a Zest graph instance directly. Internally,
     * this compiles the generated Zest class and loads it using Reflection.
     */
    private void test(final IGraphCreator converter) {

        /*
         * This is not really working, it only appears to be, as the generated
         * file will be compiled by the IDE and will then be available to the
         * classloader. A symptom of this is that it will only work starting
         * with the second run.
         */
        Shell shell = new Shell();
        String dot1 = "digraph TestingGraph {1;2;3;4; 1->2;2->3;2->4}";
        Graph graph = converter.create(shell, SWT.NONE, dot1);
        Assert.assertNotNull("Created graph must exist!", graph);
        Assert.assertEquals(4, graph.getNodes().size());
        Assert.assertEquals(3, graph.getConnections().size());
        System.out.println(String.format("Imported '%s' to Graph '%s' of type '%s'", dot1, graph,
                graph.getClass().getSimpleName()));
        
        /*
         * Check a unique name works, i.e. no existing classes on the classpath
         * could be used instead of the new one:
         */
        String dot2 =
                "digraph TestingGraph" + System.currentTimeMillis() + "{1;2;3;4; 1->2;2->3;2->4}";
        graph = converter.create(shell, SWT.NONE, dot2);
        Assert.assertNotNull("Created graph must exist!", graph);
        Assert.assertEquals(4, graph.getNodes().size());
        Assert.assertEquals(3, graph.getConnections().size());
        System.out.println(String.format("Imported '%s' to Graph '%s' of type '%s'", dot2, graph,
                graph.getClass().getSimpleName()));
        
        /*
         * Check if a DOT graph with the same name is changed when the generated
         * class is loaded:
         */
        String dot3 = "digraph TestingGraph{1;2;3 1->2;2->3}";
        graph = converter.create(shell, SWT.NONE, dot3);
        Assert.assertNotNull("Created graph must exist!", graph);
        Assert.assertEquals(3, graph.getNodes().size());
        Assert.assertEquals(2, graph.getConnections().size());
        System.out.println(String.format("Imported '%s' to Graph '%s' of type '%s'", dot3, graph,
                graph.getClass().getSimpleName()));
        // open(shell); // blocks UI when running tests
    }

    @SuppressWarnings( "unused" )
    // optional when running the tests
    private static void open(final Shell shell) {
        shell.setText("Testing");
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
