package org.eclipse.zest.dot;
import org.eclipse.draw2d.Animation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.*;
/** Zest graph generated from Graphviz DOT graph 'ExperimentalAnimationLayout'. */
public class ExperimentalAnimationLayout extends Graph {
	private static String nodeLabel = null;
	private static String edgeLabel = null;
	private static int edgeStyle = SWT.LINE_SOLID;

	public ExperimentalAnimationLayout(final Composite parent, final int style) {
		super(parent, style);
		setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);

		setLayoutAlgorithm(layout, true);
	}

	private static AbstractLayoutAlgorithm layout = new RadialLayoutAlgorithm(
			LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	private GraphNode n1 = new GraphNode(this, SWT.NONE, global(nodeLabel, "1"));
	private GraphNode n2 = new GraphNode(this, SWT.NONE, global(nodeLabel, "2"));
	private GraphNode n3 = new GraphNode(this, SWT.NONE, global(nodeLabel, "3"));
	private GraphNode n4 = new GraphNode(this, SWT.NONE, global(nodeLabel, "4"));
	private GraphNode n5 = new GraphNode(this, SWT.NONE, global(nodeLabel, "5"));
	private class AnimationRunner0 implements Runnable {
		private Graph g;
		private Button b;

		public AnimationRunner0(final Graph g, final Button b1) {
			this.g = g;
			this.b = b1;
		}

		public void run() {
			Animation.markBegin();

			/* Connection from n1 to n2: */
			GraphConnection n1n2 = new GraphConnection(g, SWT.NONE, n1, n2);
			n1n2.setText(global(edgeLabel, ""));
			n1n2.setLineStyle(edgeStyle);

			g.applyLayout();
			Animation.run();
			/* For animations with further steps: */
			removeListeners(b, SWT.Selection);
			removeListeners(b, SWT.DefaultSelection);
			//label=Step

			b.addSelectionListener(listenerFor(new AnimationRunner1(g, b)));
			b.setText("Step");

		}
	}

	private class AnimationRunner1 implements Runnable {
		private Graph g;
		private Button b;

		public AnimationRunner1(final Graph g, final Button b1) {
			this.g = g;
			this.b = b1;
		}

		public void run() {
			Animation.markBegin();

			/* Connection from n1 to n3: */
			GraphConnection n1n3 = new GraphConnection(g, SWT.NONE, n1, n3);
			n1n3.setText(global(edgeLabel, ""));
			n1n3.setLineStyle(edgeStyle);

			g.applyLayout();
			Animation.run();
			/* For animations with further steps: */
			removeListeners(b, SWT.Selection);
			removeListeners(b, SWT.DefaultSelection);
			//label=Step

			b.addSelectionListener(listenerFor(new AnimationRunner2(g, b)));
			b.setText("Step");

		}
	}

	private class AnimationRunner2 implements Runnable {
		private Graph g;
		private Button b;

		public AnimationRunner2(final Graph g, final Button b1) {
			this.g = g;
			this.b = b1;
		}

		public void run() {
			Animation.markBegin();

			/* Connection from n3 to n4: */
			GraphConnection n3n4 = new GraphConnection(g, SWT.NONE, n3, n4);
			n3n4.setText(global(edgeLabel, ""));
			n3n4.setLineStyle(edgeStyle);

			/* Connection from n3 to n5: */
			GraphConnection n3n5 = new GraphConnection(g, SWT.NONE, n3, n5);
			n3n5.setText(global(edgeLabel, ""));
			n3n5.setLineStyle(edgeStyle);

			g.applyLayout();
			Animation.run();
			/* For animations with further steps: */
			removeListeners(b, SWT.Selection);
			removeListeners(b, SWT.DefaultSelection);
			//label=Step

			b.setText("Done");
			b.setEnabled(false);

		}
	}

	private static String global(final String global, final String name) {
		return global == null ? name : global;
	}

	/** @return This graph as a DOT representation, to be rendered with Graphviz. */
	public String toDot() {
		return DotExport.exportZestGraph(this);
	}

	/* Support to run this graph as a Java application: */

	public static void main(final String[] args) {
		final Shell shell = createShell();
		final ExperimentalAnimationLayout g = new ExperimentalAnimationLayout(
				shell, SWT.NONE);
		g.setLayoutData(new GridData(GridData.FILL_BOTH));
		System.out.println(g.toDot());
		Button b1 = new Button(shell, SWT.PUSH);
		b1.setText("Start");
		/* We start with the first listener */
		SelectionListener selectionListener = listenerFor(g.new AnimationRunner0(
				g, b1));
		b1.addSelectionListener(selectionListener);
		open(shell);
	}

	private static Shell createShell() {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText(ExperimentalAnimationLayout.class.getSimpleName());
		shell.setLayout(new GridLayout(1, false));
		shell.setSize(200, 200);
		return shell;
	}

	private static void open(final Shell shell) {
		shell.open();
		while (!shell.isDisposed()) {
			while (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}

	private static void removeListeners(final Button b, final int type) {
		Listener[] listeners = b.getListeners(type);
		for (Listener listener : listeners) {
			b.removeListener(type, listener);
		}
	}

	private static SelectionListener listenerFor(final Runnable runner) {
		return new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			public void widgetSelected(final SelectionEvent e) {
				runner.run();
			}
		};
	}
}
