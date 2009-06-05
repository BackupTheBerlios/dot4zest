package org.eclipse.zest.dot.export.test_data;

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
 * Zest graph sample input for the Zest-To-Dot transformation. Contains
 * everything that is currently supported by the transformation: node and edge
 * labels, edge styles.
 */
public class SampleGraph extends Graph {
    /**
     * {@link Graph#Graph(Composite, int)}
     * @param parent The parent
     * @param style The style bits
     */
    public SampleGraph(final Composite parent, final int style) {
        super(parent, style);

        /* Global settings: */
        setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        setLayoutAlgorithm(new TreeLayoutAlgorithm(
                LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

        /* Nodes: */
        GraphNode n1 = new GraphNode(this, SWT.NONE, "Node");
        GraphNode n2 = new GraphNode(this, SWT.NONE, "Node");
        GraphNode n3 = new GraphNode(this, SWT.NONE, "Leaf1");
        GraphNode n4 = new GraphNode(this, SWT.NONE, "Leaf2");

        /* Connection from n1 to n2: */
        GraphConnection n1n2 = new GraphConnection(this, SWT.NONE, n1, n2);
        n1n2.setText("Edge");
        n1n2.setLineStyle(SWT.LINE_DASH);

        /* Connection from n2 to n3: */
        GraphConnection n2n3 = new GraphConnection(this, SWT.NONE, n2, n3);
        n2n3.setText("Edge");
        n2n3.setLineStyle(SWT.LINE_DASH);

        /* Connection from n2 to n4: */
        GraphConnection n2n4 = new GraphConnection(this, SWT.NONE, n2, n4);
        n2n4.setText("Dotted");
        n2n4.setLineStyle(SWT.LINE_DOT);
    }
    /**
     * Displays this graph in a shell.
     * @param args Not used
     */
    public static void main(final String[] args) {
        Display d = new Display();
        Shell shell = new Shell(d);
        shell.setText(SampleGraph.class.getSimpleName());
        shell.setLayout(new FillLayout());
        shell.setSize(200, 250);
        new SampleGraph(shell, SWT.NONE);
        shell.open();
        while (!shell.isDisposed()) {
            while (!d.readAndDispatch()) {
                d.sleep();
            }
        }
    }
}
