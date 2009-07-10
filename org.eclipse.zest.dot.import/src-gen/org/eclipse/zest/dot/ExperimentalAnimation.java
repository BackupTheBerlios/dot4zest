package org.eclipse.zest.dot;
import java.util.List;
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
/** Zest graph generated from Graphviz DOT graph 'ExperimentalAnimation'. */
public class ExperimentalAnimation extends Graph {
	static String nodeLabel = null;
	static String edgeLabel = null;
	static int edgeStyle = SWT.LINE_SOLID;
	public ExperimentalAnimation(final Composite parent, final int style) {
		super(parent, style);
		setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);

		setLayoutAlgorithm(new TreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	GraphNode n1 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "1"));
	GraphNode n2 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "2"));
	GraphNode n3 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "3"));
	GraphNode n4 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "4"));
	GraphNode n5 = new GraphNode(this, SWT.NONE, tryGlobal(nodeLabel, "5"));

	class AnimationRunner0 implements Runnable {
		private Graph g;
		private Button b;

		public AnimationRunner0(Graph g, Button b1) {
			this.g = g;
			this.b = b1;
		}

		public void run() {
			Animation.markBegin();

			/* Connection from n1 to n2: */
			GraphConnection n1n2 = new GraphConnection(g, SWT.NONE, n1, n2);
			n1n2.setText(tryGlobal(edgeLabel, ""));
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

	class AnimationRunner1 implements Runnable {
		private Graph g;
		private Button b;

		public AnimationRunner1(Graph g, Button b1) {
			this.g = g;
			this.b = b1;
		}

		public void run() {
			Animation.markBegin();

			/* Connection from n1 to n3: */
			GraphConnection n1n3 = new GraphConnection(g, SWT.NONE, n1, n3);
			n1n3.setText(tryGlobal(edgeLabel, ""));
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

	class AnimationRunner2 implements Runnable {
		private Graph g;
		private Button b;

		public AnimationRunner2(Graph g, Button b1) {
			this.g = g;
			this.b = b1;
		}

		public void run() {
			Animation.markBegin();

			/* Connection from n3 to n4: */
			GraphConnection n3n4 = new GraphConnection(g, SWT.NONE, n3, n4);
			n3n4.setText(tryGlobal(edgeLabel, ""));
			n3n4.setLineStyle(edgeStyle);

			/* Connection from n3 to n5: */
			GraphConnection n3n5 = new GraphConnection(g, SWT.NONE, n3, n5);
			n3n5.setText(tryGlobal(edgeLabel, ""));
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

	private static String tryGlobal(final String global, final String name) {
		return global == null ? name : global;
	}
	/** @return This graph as a DOT representation, to be rendered with Graphviz. */
	public String toDot() {
		return DotExport.exportZestGraph(this);
	}

	/* Support to run this graph as a Java application: */

	public static void main(String[] args) {
		final Shell shell = createShell();
		final ExperimentalAnimation g = new ExperimentalAnimation(shell,
				SWT.NONE);
		g.setLayoutData(new GridData(GridData.FILL_BOTH));
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
		shell.setText(ExperimentalAnimation.class.getSimpleName());
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

	private static void removeListeners(Button b, int type) {
		Listener[] listeners = b.getListeners(type);
		System.out.println(listeners.length);
		for (Listener listener : listeners) {
			b.removeListener(type, listener);
		}
	}

	public static GraphNode node(Graph g, String label) {
		/* First we check if the node already exists: */
		GraphNode node = findSourceNode(g, label);
		if (node == null) {
			/* If not, we create a new node: */
			node = new GraphNode(g, SWT.NONE, label);
		}
		return node;
	}

	private static GraphNode findSourceNode(Graph g, String label) {
		List<?> nodes = g.getNodes();
		GraphNode parent = null;
		for (Object object : nodes) {
			GraphNode node = (GraphNode) object;
			/* TODO IDs would be much cleaner here */
			if (node.getText().equals(label)) {
				parent = node;
			}
		}
		return parent;
	}

	private static SelectionListener listenerFor(final Runnable runner) {
		return new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				runner.run();
			}
		};
	}
}
