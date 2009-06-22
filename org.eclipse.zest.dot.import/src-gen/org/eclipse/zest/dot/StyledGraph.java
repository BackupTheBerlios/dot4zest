package org.eclipse.zest.dot;
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
import org.eclipse.zest.layouts.algorithms.*;
import org.eclipse.zest.dot.DotExport;
import org.junit.Test;
/** Zest graph generated from Graphviz DOT graph 'StyledGraph'. */
public class StyledGraph extends Graph {
	public StyledGraph(final Composite parent, final int style) {
		super(parent, style);
		String nodeLabel = null;
		String edgeLabel = null;
		int edgeStyle = SWT.LINE_SOLID;
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
	/** @return This graph as a DOT representation, to be rendered with Graphviz. */
	public String toDot() {
		return DotExport.exportZestGraph(this);
	}

	/* Support to run this graph as a Java application or a JUnit test: */

	public static void main(final String[] args) {
		final Shell shell = createShell();
		String dot = new StyledGraph(shell, SWT.NONE).toDot();
		System.out.println("Graph as DOT:\n" + dot);
		open(shell);
	}

	private static Shell createShell() {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText(StyledGraph.class.getSimpleName());
		shell.setLayout(new FillLayout());
		shell.setSize(200, 250);
		return shell;
	}

	/** Default constructor for JUnit. Creates a new graph in a new shell, with no style. */
	public StyledGraph() { // for JUnit
		this(createShell(), SWT.NONE);
	}

	@Test
	public void test() {
		String dot = this.toDot();
		System.out.println("Graph as DOT:\n" + dot);
		open(this.getShell());
	}

	private static void open(final Shell shell) {
		shell.open();
		while (!shell.isDisposed()) {
			while (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}
}