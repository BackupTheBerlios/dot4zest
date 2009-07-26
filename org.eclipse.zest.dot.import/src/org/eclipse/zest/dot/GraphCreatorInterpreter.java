package org.eclipse.zest.dot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * Create a Zest graph instance from a DOT string by interpreting the AST of the
 * parsed DOT.
 * @author Fabian Steeg (fsteeg)
 */
final class GraphCreatorInterpreter implements IGraphCreator {
    private Map<String, GraphNode> nodes = new HashMap<String, GraphNode>();
    private Graph graph = null;

    /**
     * {@inheritDoc}
     * @see org.eclipse.zest.dot.IGraphCreator#create(org.eclipse.swt.widgets.Composite,
     *      int, java.lang.String)
     */
    public Graph create(final Composite parent, final int style, final String dot) {
        DotImport importer = new DotImport(dot);
        graph = new Graph(parent, style);
        // TODO: global styles
        // TODO: digraph vs. graph
        graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        // TODO: vary Zest layout
        graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
                true);
        EObject eGraph = importer.getDotAst().graph();
        System.out.println(eGraph);
        addContent(eGraph);
        return graph;
    }

    private void addContent(final EObject eGraph) {
        EList<EObject> eContents = eGraph.eContents();
        for (EObject eStatementObject : eContents) {
            String eStatementClassName = eStatementObject.eClass().getName();
            if (eStatementClassName.equals("node_stmt")) {
                // TODO: node labels
                createNode(eStatementObject);
            }
            if (eStatementClassName.equals("edge_stmt_node")) {
                // TODO: edge labels and styles
                createEdge(eStatementObject);
            }
        }
    }

    private void createEdge(final EObject eStatementObject) {
        System.out.println("Edge: " + eStatementObject);
        Iterator<EObject> eEdgeContents = eStatementObject.eContents().iterator();
        String sourceNodeId = null;
        while (eEdgeContents.hasNext()) {
            EObject eEdgeContentElement = eEdgeContents.next();
            String eClassName = eEdgeContentElement.eClass().getName();
            if (eClassName.equals("node_id")) {
                sourceNodeId = getAttribute(eEdgeContentElement, "name");
                System.out.println("Source: " + sourceNodeId);
            }
            if (eClassName.equals("edgeRHS_node")) {
                createConnectionsForRhs(sourceNodeId, eEdgeContentElement);
            }
        }
    }

    private void createConnectionsForRhs(final String sourceNodeId, final EObject edgeRhsElement) {
        String targetNodeId;
        Iterator<EObject> eRhsContents = edgeRhsElement.eContents().iterator();
        while (eRhsContents.hasNext()) {
            EObject eRhsContentElement = eRhsContents.next();
            if (eRhsContentElement.eClass().getName().equals("node_id")) {
                targetNodeId = getAttribute(eRhsContentElement, "name");
                System.out.println("Target: " + targetNodeId);
                if (sourceNodeId != null && targetNodeId != null) {
                    new GraphConnection(graph, SWT.NONE, nodes.get(sourceNodeId), nodes
                            .get(targetNodeId));
                }
            }
        }
    }

    private void createNode(final EObject eStatementObject) {
        String nodeId = getAttribute(eStatementObject, "name");
        System.out.println("Node: " + nodeId);
        GraphNode node = new GraphNode(graph, SWT.NONE, nodeId);
        nodes.put(nodeId, node);
    }

    private String getAttribute(final EObject eObject, final String name) {
        Iterator<EAttribute> graphAttributes = eObject.eClass().getEAllAttributes().iterator();
        while (graphAttributes.hasNext()) {
            EAttribute a = graphAttributes.next();
            /* We return the name attribute of the graph: */
            if (a.getName().equals(name)) {
                return (String) eObject.eGet(a);
            }
        }
        return null;
    }

}
