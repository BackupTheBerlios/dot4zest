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

/**
 * Tests for the {@link DotImport} class with graphs using different layout
 * algorithms.
 * @author Fabian Steeg (fsteeg)
 */
public final class LayoutDotImportTests {
    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using the Zest {@link TreeLayoutAlgorithm}.
     */
    @Test
    public void treeLayout() {
        BasicDotImportTests.importFrom(new File(
                "resources/layout_tree_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using the Zest {@link SpringLayoutAlgorithm}.
     */
    @Test
    public void springLayout() {
        BasicDotImportTests.importFrom(new File(
                "resources/layout_spring_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using the Zest {@link RadialLayoutAlgorithm}.
     */
    @Test
    public void radialLayout() {
        BasicDotImportTests.importFrom(new File(
                "resources/layout_radial_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using the Zest {@link GridLayoutAlgorithm}.
     */
    @Test
    public void gridLayout() {
        BasicDotImportTests.importFrom(new File(
                "resources/layout_grid_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using a {@link CustomLayoutAlgorithm}.
     */
    @Test
    public void customLayout() {
        BasicDotImportTests.importFrom(new File(
                "resources/layout_custom_graph.dot"));
    }

}
