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
 * @author Fabian Steeg (fsteeg)
 */
public final class TestDotImport {
    @Test
    public void getGraphInstance() {
        /*
         * This is not really working, it only appears to be, as the generated
         * file will be compiled by the IDE and will then be available to the
         * classloader. A symptom of this is that it will only work unless the
         * DOT string representation below is not altered. It does however test
         * the thing that is working an in use: getting a Graph instance
         * corresponding to a template (as long as the template is one of the
         * generated files from the import bundle).
         */
        // TODO add API that does only what actually works, and use that
        IGraphCreator converter = new GraphCreatorViaInternalJdtCompiler();
        Shell shell = new Shell();
        String dot = "digraph SampleGraph {1;2;3;4; 1->2;2->3;2->4}";
        Graph graph = converter.create(shell, SWT.NONE, dot);
        Assert.assertNotNull("Created graph must exist!", graph);
        Assert.assertEquals(4, graph.getNodes().size());
        Assert.assertEquals(3, graph.getConnections().size());
        System.out.println(String.format("Imported '%s' to Graph '%s' of type '%s'", dot, graph,
                graph.getClass().getSimpleName()));
        // open(shell);
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
