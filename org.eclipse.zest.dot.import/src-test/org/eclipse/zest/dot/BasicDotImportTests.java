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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotImport} class.
 * @author Fabian Steeg (fsteeg)
 */
public final class BasicDotImportTests {
    /**
     * Test execution of File-based DOT-to-Zest transformations for a simple
     * directed graph.
     */
    @Test
    public void directedGraph() {
        importFrom(new File("resources/simple_digraph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a simple
     * undirected graph.
     */
    @Test
    public void undirectedGraph() {
        importFrom(new File("resources/simple_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a labeled
     * graph.
     */
    @Test
    public void labeledGraph() {
        importFrom(new File("resources/labeled_graph.dot"));
    }
    
    /**
     * Test execution of File-based DOT-to-Zest transformations for a 
     * graph using style attributes for edges.
     */
    @Test
    public void styledGraph() {
        importFrom(new File("resources/styled_graph.dot"));
    }
    
    static void importFrom(final File file) {
        Assert.assertTrue("DOT input file must exist", file.exists());
        File zest = DotImport.of(file);
        Assert.assertNotNull("Resulting file must not be null", zest);
        Assert.assertTrue("Resulting file must exist", zest.exists());
        System.out.println(String.format(
                "Transformed DOT in '%s' to Zest in '%s'", file, zest));
    }
}
