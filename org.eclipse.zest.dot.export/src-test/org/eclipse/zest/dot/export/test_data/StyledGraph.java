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
/** Zest graph generated from Graphviz DOT graph 'StyledGraph'. */
public class StyledGraph extends Graph {
	public StyledGraph(final Composite parent, final int style) {
		super(parent, style);
		String nodeLabel = null;
		String edgeLabel = null;
		int edgeStyle = 1; /* ZestStyles.CONNECTIONS_SOLID doesn't work for me, investigate, is this a bug? */
		setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);

		setLayoutAlgorithm(new TreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		GraphNode n1 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "1"));
		GraphNode n2 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "2"));
		GraphNode n3 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "3"));
		GraphNode n4 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "4"));
		GraphNode n5 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "5"));

		/* Connection from n1 to n2: */
		GraphConnection n1n2 = new GraphConnection(this, SWT.NONE, n1, n2);
		n1n2.setText(tryGlobal(edgeLabel, ""));
		n1n2.setLineStyle(edgeStyle);
		n1n2.setLineStyle(SWT.LINE_DASH);

		/* Connection from n2 to n3: */
		GraphConnection n2n3 = new GraphConnection(this, SWT.NONE, n2, n3);
		n2n3.setText(tryGlobal(edgeLabel, ""));
		n2n3.setLineStyle(edgeStyle);
		n2n3.setLineStyle(SWT.LINE_DOT);

		/* Connection from n3 to n4: */
		GraphConnection n3n4 = new GraphConnection(this, SWT.NONE, n3, n4);
		n3n4.setText(tryGlobal(edgeLabel, ""));
		n3n4.setLineStyle(edgeStyle);
		n3n4.setLineStyle(SWT.LINE_DASHDOT);

		/* Connection from n3 to n5: */
		GraphConnection n3n5 = new GraphConnection(this, SWT.NONE, n3, n5);
		n3n5.setText(tryGlobal(edgeLabel, ""));
		n3n5.setLineStyle(edgeStyle);

	}
	private String tryGlobal(final String global, final String name) {
		return global == null ? name : global;
	}
	public String toString() {
		return super.toString();
	}
	public static void main(final String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText(StyledGraph.class.getSimpleName());
		shell.setLayout(new FillLayout());
		shell.setSize(200, 250);
		new StyledGraph(shell, SWT.NONE);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
