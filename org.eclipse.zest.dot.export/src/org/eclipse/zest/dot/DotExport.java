/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.zest.dot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.eclipse.zest.core.widgets.Graph;

/**
 * Transformation of Zest Graph instances to DOT strings or files.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotExport {
    private DotExport() {/* enforce non-instantiability */}

    /**
     * Export a Zest Graph to a DOT string.
     * @param graph The Zest graph to export to DOT
     * @return The DOT representation of the given Zest graph
     */
    public static String exportZestGraph(final Graph graph) {
        return graphToDot(graph);
    }

    /**
     * Export a Zest Graph to a DOT file.
     * @param graph The Zest graph to export to DOT
     * @param destination The file to store the DOT export
     */
    public static void exportZestGraph(final Graph graph, final File destination) {
        write(graphToDot(graph), destination);
    }

    /**
     * @param graph The graph to get a name for
     * @return A name for the given graph, that can be used as a filename and as
     *         a valid name for a DOT graph
     */
    static String name(final Graph graph) {
        String simpleClassName = graph.getClass().getSimpleName();
        /* The exact name 'Graph' is not valid for rendering with Graphviz: */
        return simpleClassName.equals("Graph")
                ? "Zest" + simpleClassName : simpleClassName;
    }

    private static String graphToDot(final Graph graph) {
        String raw = new DotTemplate().generate(graph);
        raw = removeBlankLines(raw);
        return raw;
    }

    private static String removeBlankLines(final String raw) {
        Scanner scanner = new Scanner(raw);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.trim().equals("")) {
                builder.append(line + "\n");
            }
        }
        return builder.toString();
    }

    private static void write(final String dot, final File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(dot);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
