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
        // TODO: vary Zest layout
        graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
                true);
        EObject eGraph = importer.getDotAst().graph();
        String graphType = getAttribute(eGraph, "type");
        graph.setConnectionStyle(graphType.equals("digraph") ? ZestStyles.CONNECTIONS_DIRECTED
                : ZestStyles.CONNECTIONS_SOLID);
        addContent(eGraph);
        return graph;
    }

    private void addContent(final EObject eGraph) {
        // TODO: global styles for nodes and edges
        EList<EObject> eContents = eGraph.eContents();
        for (EObject eStatementObject : eContents) {
            String eStatementClassName = eStatementObject.eClass().getName();
            if (eStatementClassName.equals("node_stmt")) {
                createNode(eStatementObject);
            }
            if (eStatementClassName.equals("edge_stmt_node")) {
                createEdge(eStatementObject);
            }
        }
    }

    private void createEdge(final EObject eStatementObject) {
        System.out.println("Edge: " + eStatementObject);
        Iterator<EObject> eEdgeContents = eStatementObject.eContents().iterator();
        String sourceNodeId = null;
        String labelValue = getNodeAttribute(eStatementObject, "label");
        System.out.println("Edge LABEL: " + labelValue);
        String styleValue = getNodeAttribute(eStatementObject, "style");
        System.out.println("Edge STYLE: " + styleValue);
        while (eEdgeContents.hasNext()) {
            EObject eEdgeContentElement = eEdgeContents.next();
            String eClassName = eEdgeContentElement.eClass().getName();
            if (eClassName.equals("node_id")) {
                sourceNodeId = getAttribute(eEdgeContentElement, "name");
                System.out.println("Source: " + sourceNodeId);
            }
            if (eClassName.equals("edgeRHS_node")) {
                createConnectionsForRhs(sourceNodeId, eEdgeContentElement, labelValue, styleValue);
            }
        }
    }

    private enum Style {
        DASHED(SWT.LINE_DASH), DOTTED(SWT.LINE_DOT), SOLID(SWT.LINE_SOLID);
        private int style;

        Style(final int style) {
            this.style = style;
        }
    }

    private void createConnectionsForRhs(final String sourceNodeId, final EObject edgeRhsElement,
            final String labelValue, final String styleValue) {
        String targetNodeId;
        Iterator<EObject> eRhsContents = edgeRhsElement.eContents().iterator();
        while (eRhsContents.hasNext()) {
            EObject eRhsContentElement = eRhsContents.next();
            if (eRhsContentElement.eClass().getName().equals("node_id")) {
                targetNodeId = getAttribute(eRhsContentElement, "name");
                System.out.println("Target: " + targetNodeId);
                if (sourceNodeId != null && targetNodeId != null) {
                    GraphConnection graphConnection =
                            new GraphConnection(graph, SWT.NONE, nodes.get(sourceNodeId), nodes
                                    .get(targetNodeId));
                    /* Set the optional label, if set in the DOT input: */
                    if (labelValue != null) {
                        graphConnection.setText(labelValue);
                    }
                    /* Set the optional style, if set in the DOT input: */
                    if (styleValue != null) {
                        Style v = Style.valueOf(styleValue.toUpperCase());
                        graphConnection.setLineStyle(v.style);
                    }
                }
            }
        }
    }

    private void createNode(final EObject eStatementObject) {
        String nodeId = getAttribute(eStatementObject, "name");
        GraphNode node = new GraphNode(graph, SWT.NONE, nodeId);
        node.setText(nodeId);
        String value = getNodeAttribute(eStatementObject, "label");
        if (value != null && value.trim().length() > 0) {
            node.setText(value);
        }
        nodes.put(nodeId, node);
    }

    private String getNodeAttribute(final EObject eStatementObject, final String attributeName) {
        Iterator<EObject> nodeContents = eStatementObject.eContents().iterator();
        while (nodeContents.hasNext()) {
            EObject nodeContentElement = nodeContents.next();
            if (nodeContentElement.eClass().getName().equals("attr_list")) {
                Iterator<EObject> attributeContents = nodeContentElement.eContents().iterator();
                while (attributeContents.hasNext()) {
                    EObject attributeElement = attributeContents.next();
                    if (attributeElement.eClass().getName().equals("a_list")) {
                        if (getAttribute(attributeElement, "name").equals(attributeName)) {
                            String label =
                                    getAttribute(attributeElement, "value").replaceAll("\"", "");
                            System.out.println(label);
                            return label;
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getAttribute(final EObject eObject, final String name) {
        Iterator<EAttribute> graphAttributes = eObject.eClass().getEAllAttributes().iterator();
        while (graphAttributes.hasNext()) {
            EAttribute a = graphAttributes.next();
            if (a.getName().equals(name)) {
                return eObject.eGet(a).toString();
            }
        }
        return null;
    }

}
