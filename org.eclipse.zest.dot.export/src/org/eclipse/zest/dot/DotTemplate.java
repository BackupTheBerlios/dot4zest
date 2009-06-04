package org.eclipse.zest.dot;

import org.eclipse.zest.core.widgets.*;

public class DotTemplate
{
  protected static String nl;
  public static synchronized DotTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    DotTemplate result = new DotTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "digraph ";
  protected final String TEXT_2 = "{" + NL + "\t/* Global settings */" + NL + "\tlabel=\"";
  protected final String TEXT_3 = "\"" + NL + "\t/* Nodes */" + NL + "\t";
  protected final String TEXT_4 = " ";
  protected final String TEXT_5 = ";" + NL + "\t";
  protected final String TEXT_6 = NL + "\t/* Edges */" + NL + "\t";
  protected final String TEXT_7 = " ";
  protected final String TEXT_8 = " -> ";
  protected final String TEXT_9 = ";" + NL + "\t";
  protected final String TEXT_10 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    /*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
     Graph graph = (Graph) argument; 
    stringBuffer.append(TEXT_1);
    stringBuffer.append(graph.getClass().getSimpleName());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(graph);
    stringBuffer.append(TEXT_3);
     for(Object nodeObject : graph.getNodes()){ GraphNode node = (GraphNode) nodeObject; 
    stringBuffer.append(TEXT_4);
    stringBuffer.append(node.getText());
    stringBuffer.append(TEXT_5);
     }
    stringBuffer.append(TEXT_6);
     for(Object edgeObject : graph.getConnections()){ GraphConnection edge = (GraphConnection) edgeObject; 
    stringBuffer.append(TEXT_7);
    stringBuffer.append(edge.getSource().getText());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(edge.getDestination().getText());
    stringBuffer.append(TEXT_9);
     }
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
