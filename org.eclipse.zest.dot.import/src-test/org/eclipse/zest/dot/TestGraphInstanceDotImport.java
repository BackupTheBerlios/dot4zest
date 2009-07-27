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
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.junit.Test;

/**
 * Tests for dynamic import of DOT to a Zest graph instance.
 * @author Fabian Steeg (fsteeg)
 */
public final class TestGraphInstanceDotImport {
    private final GraphCreatorInterpreter interpreter = new GraphCreatorInterpreter();

    @Test
    public void digraphType() {
        Graph graph = interpreter.create(new Shell(), SWT.NONE, "digraph Sample{1}");
        Assert.assertNotNull("Created graph must not be null", graph);
        Assert.assertEquals(ZestStyles.CONNECTIONS_DIRECTED, graph.getConnectionStyle());
    }

    @Test
    public void graphType() {
        Graph graph = interpreter.create(new Shell(), SWT.NONE, "graph Sample{1}");
        Assert.assertNotNull("Created graph must not be null", graph);
        Assert.assertNotSame(ZestStyles.CONNECTIONS_DIRECTED, graph.getConnectionStyle());

    }

    @Test
    public void nodeDefaultLabel() {
        Graph graph = interpreter.create(new Shell(), SWT.NONE, "graph Sample{1}");
        Assert.assertNotNull("Created graph must not be null", graph);
        Assert.assertEquals("1", ((GraphNode) graph.getNodes().get(0)).getText());
    }

    @Test
    public void nodeCount() {
        Graph graph = interpreter.create(new Shell(), SWT.NONE, "graph Sample{1;2}");
        Assert.assertNotNull("Created graph must not be null", graph);
        Assert.assertEquals(2, graph.getNodes().size());
    }

    @Test
    public void edgeCount() {
        Graph graph =
                interpreter.create(new Shell(), SWT.NONE,
                        "graph Sample{1;2;1->2;2->2;1->1[label=\"Edge1\"]}");
        Assert.assertNotNull("Created graph must not be null", graph);
        Assert.assertEquals(3, graph.getConnections().size());
    }

    @Test
    public void nodeLabel() {
        Graph graph =
                interpreter.create(new Shell(), SWT.NONE, "graph Sample{1[label=\"Node1\"];}");
        Assert.assertNotNull("Created graph must not be null", graph);
        Assert.assertEquals("Node1", ((GraphNode) graph.getNodes().get(0)).getText());
    }

    @Test
    public void edgeLabel() {
        Graph graph =
                interpreter
                        .create(new Shell(), SWT.NONE, "graph Sample{1;2;1->2[label=\"Edge1\"]}");
        Assert.assertNotNull("Created graph must not be null", graph);
        Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections().get(0)).getText());
    }

    @Test
    public void edgeStyle() {
        Shell parent = new Shell();
        Graph graph = interpreter.create(parent, SWT.NONE, "graph Sample{1;2;1->2[style=dashed]}");
        Assert.assertNotNull("Created graph must not be null", graph);
        Assert.assertEquals(SWT.LINE_DASH, ((GraphConnection) graph.getConnections().get(0))
                .getLineStyle());
        // open(parent);
    }

    /**
     * Import instance by interpreting the parsed AST.
     */
    @Test
    public void viaInterpreter() {
        test(new GraphCreatorInterpreter());

    }

    /**
     * EXPERIMENTAL - NOT WORKING Import instance by compiling the generated
     * Zest graph using the Java Compiler API (downside: requires Java 6)
     */
    // @Test
    public void viaJavaCompilerApi() {
        /*
         * Implementation using Java compiler API requires Java 6.
         */
        Assert.fail("Java 6 dependency deactivated");
        // test(new GraphCreatorViaJavaCompilerApi());
    }

    /**
     * EXPERIMENTAL - NOT WORKING Import instance by compiling the generated
     * Zest graph using the Eclipse JDT compiler (API is internal, probably
     * makes no sense to use this)
     */
    // @Test
    public void viaInternalJdtCompiler() {
        test(new GraphCreatorViaInternalJdtCompiler());

    }

    /**
     * Test importing a DOT graph to a Zest graph instance directly. Internally,
     * this compiles the generated Zest class and loads it using Reflection.
     */
    static void test(final IGraphCreator converter) {

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
        // open(shell); // blocks UI when running tests
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
        // open(shell); // blocks UI when running tests
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
