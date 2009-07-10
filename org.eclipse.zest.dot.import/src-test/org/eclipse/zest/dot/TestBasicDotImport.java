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

import org.junit.Test;
import static org.eclipse.zest.dot.DotImportTestUtils.*;

/**
 * Tests for the {@link DotImport} class.
 * @author Fabian Steeg (fsteeg)
 */
public final class TestBasicDotImport {
    
    /**
     * Sample graph summarizing all that is currently supported in the DOT
     * input.
     */
    @Test
    public void sampleGraph() {
        importFrom(new File(RESOURCES_INPUT + "sample_input.dot"));
    }
    
    /**
     * Basic directed graph.
     */
    @Test
    public void basicGraph() {
        importFrom(new File(RESOURCES_TESTS + "basic_directed_graph.dot"));
    }
    
    /**
     * Test execution of File-based DOT-to-Zest transformations for a simple
     * directed graph.
     */
    @Test
    public void directedGraph() {
        importFrom(new File(RESOURCES_TESTS + "simple_digraph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a simple
     * undirected graph.
     */
    @Test
    public void undirectedGraph() {
        importFrom(new File(RESOURCES_TESTS + "simple_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a labeled
     * graph.
     */
    @Test
    public void labeledGraph() {
        importFrom(new File(RESOURCES_TESTS + "labeled_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using style attributes for edges.
     */
    @Test
    public void styledGraph() {
        importFrom(new File(RESOURCES_TESTS + "styled_graph.dot"));
    }
    
    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using global node attributes.
     */
    @Test
    public void globalNodeGraph() {
        importFrom(new File(RESOURCES_TESTS + "global_node_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using global edge attributes.
     */
    @Test
    public void globalEdgeGraph() {
        importFrom(new File(RESOURCES_TESTS + "global_edge_graph.dot"));
    }
    
    
}
