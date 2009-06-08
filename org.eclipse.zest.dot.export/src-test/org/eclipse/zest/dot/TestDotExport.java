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
import java.io.FileNotFoundException;
import java.util.Scanner;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.dot.test_data.LabeledGraph;
import org.eclipse.zest.dot.test_data.SampleGraph;
import org.eclipse.zest.dot.test_data.SimpleDigraph;
import org.eclipse.zest.dot.test_data.SimpleGraph;
import org.eclipse.zest.dot.test_data.StyledGraph;
import org.junit.Test;

/**
 * Tests for the {@link DotExport} class.
 * @author Fabian Steeg (fsteeg)
 */
public class TestDotExport {
    public static final File OUTPUT = new File("src-gen");
    private static Shell shell = new Shell();

    /**
     * Zest-To-Dot transformation for a full sample graph showing all that is
     * currently supported in the Zest-To-Dot transformation.
     */
    @Test
    public void sampleGraph() {
        testDotGeneration(new SampleGraph(shell, SWT.NONE));
    }

    /** Zest-To-Dot transformation for a minimal undirected graph. */
    @Test
    public void simpleGraph() {
        testDotGeneration(new SimpleGraph(shell, SWT.NONE));
    }
    /** Zest-To-Dot transformation for a minimal directed graph. */
    @Test
    public void directedGraph() {
        testDotGeneration(new SimpleDigraph(shell, SWT.NONE));
    }
    /** Zest-To-Dot transformation for a graph with edge and node labels. */
    @Test
    public void labeledGraph() {
        testDotGeneration(new LabeledGraph(shell, SWT.NONE));
    }
    /** Zest-To-Dot transformation for a graph with styled edges (dotted, etc). */
    @Test
    public void styledGraph() {
        testDotGeneration(new StyledGraph(shell, SWT.NONE));
    }

    private void testDotGeneration(final Graph graph) {
        String dot = DotExport.exportZestGraph(graph);
        Assert
                .assertTrue(
                        "DOT representation must contain simple class name of Zest input!",
                        dot.contains(graph.getClass().getSimpleName()));
        System.out.println(dot);
        File file = new File(OUTPUT, graph.getClass().getSimpleName() + ".dot");
        DotExport.exportZestGraph(graph, file);
        Assert.assertTrue("Generated file must exist!", file.exists());
        String dotRead = read(file);
        Assert
                .assertTrue(
                        "DOT file output representation must contain simple class name of Zest input!",
                        dotRead.contains(graph.getClass().getSimpleName()));
        Assert.assertEquals("File output and String output should be equal;",
                dot, dotRead);

    }

    private String read(final File file) {
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine() + "\n");
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
