package org.eclipse.zest;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.dot.DotExport;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * Zest graph sample input for the Zest-To-Dot transformation. Contains
 * everything that is currently supported by the transformation: node and edge
 * labels, edge styles.
 * <p/>
 * Uses the actual Zest Graph class and populates an instance of that, instead
 * of subclassing the Zest Graph and exporting the subclass (as in the samples
 * used for testing, which are based on Graphs generated using the
 * org.eclipse.zest.dot.import bundle).
 */
public final class DotExportSample {
    public static void main(final String[] args) {
        Display d = new Display();
        Shell shell = new Shell(d);
        /* Set up a directed Zest graph with a single connection: */
        Graph graph = new Graph(shell, SWT.NONE);
        graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        GraphConnection edge = new GraphConnection(graph, SWT.NONE, 
                new GraphNode(graph, SWT.NONE, "Node 1"), 
                new GraphNode(graph, SWT.NONE, "Node 2"));
        edge.setText("A dotted edge");
        edge.setLineStyle(SWT.LINE_DOT);
        /* Export the Zest graph to a DOT string or a DOT file: */
        System.out.println(DotExport.exportZestGraph(graph));
        DotExport.exportZestGraph(graph, new File("src-gen/DirectSample.dot"));
        /* Show the Zest graph: */
        graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
                LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
        shell.setLayout(new FillLayout());
        shell.setSize(200, 250);
        shell.open();
        while (!shell.isDisposed()) {
            while (!d.readAndDispatch()) {
                d.sleep();
            }
        }
    }
    private DotExportSample() { /* enforce non-instantiability */}
}
