package org.eclipse.zest.dot.test_data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * Zest graph sample input for the Zest-To-Dot transformation demonstrating node
 * and edge label support.
 */
public class LabeledGraph extends Graph {
    /**
     * {@link Graph#Graph(Composite, int)}
     * @param parent The parent
     * @param style The style bits
     */
    public LabeledGraph(final Composite parent, final int style) {
        super(parent, style);

        /* Global settings: */
        setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        setLayoutAlgorithm(new TreeLayoutAlgorithm(
                LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

        /* Nodes: */
        GraphNode n1 = new GraphNode(this, SWT.NONE, "One");
        GraphNode n2 = new GraphNode(this, SWT.NONE, "Two");
        GraphNode n3 = new GraphNode(this, SWT.NONE, "3");
        GraphNode n4 = new GraphNode(this, SWT.NONE, "4");

        /* Connection from n1 to n2: */
        GraphConnection n1n2 = new GraphConnection(this, SWT.NONE, n1, n2);
        n1n2.setText("+1");

        /* Connection from n1 to n3: */
        GraphConnection n1n3 = new GraphConnection(this, SWT.NONE, n1, n3);
        n1n3.setText("+2");

        /* Connection from n3 to n4: */
        new GraphConnection(this, SWT.NONE, n3, n4);

    }
    /**
     * Displays this graph in a shell.
     * @param args Not used
     */
    public static void main(final String[] args) {
        Display d = new Display();
        Shell shell = new Shell(d);
        shell.setText(LabeledGraph.class.getSimpleName());
        shell.setLayout(new FillLayout());
        shell.setSize(200, 250);
        new LabeledGraph(shell, SWT.NONE);
        shell.open();
        while (!shell.isDisposed()) {
            while (!d.readAndDispatch()) {
                d.sleep();
            }
        }
    }
}
