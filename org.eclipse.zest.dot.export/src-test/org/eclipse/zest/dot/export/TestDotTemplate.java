/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest.dot.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.dot.DotTemplate;
import org.eclipse.zest.dot.export.test_data.LabeledGraph;
import org.eclipse.zest.dot.export.test_data.SimpleDigraph;
import org.eclipse.zest.dot.export.test_data.SimpleGraph;
import org.eclipse.zest.dot.export.test_data.StyledGraph;
import org.junit.Test;

/**
 * Tests for the generated {@link DotTemplate} class.
 * @author Fabian Steeg (fsteeg)
 *
 */
public class TestDotTemplate {
    private static final File OUTPUT = new File("src-gen");
    private static Shell shell = new Shell();

    @Test
    public void simpleGraph() {
        testDotGeneration(new SimpleGraph(shell, SWT.NONE));
    }

    @Test
    public void directedGraph() {
        testDotGeneration(new SimpleDigraph(shell, SWT.NONE));
    }
    
    @Test
    public void labeledGraph() {
        testDotGeneration(new LabeledGraph(shell, SWT.NONE));
    }
    
    @Test
    public void styledGraph() {
        testDotGeneration(new StyledGraph(shell, SWT.NONE));
    }

    private void testDotGeneration(final Graph graph) {
        String dot = new DotTemplate().generate(graph);
        Assert
                .assertTrue(
                        "DOT representation must contain simple class name of Zest input!",
                        dot.contains(graph.getClass().getSimpleName()));
        System.out.println(dot);
        File file = new File(OUTPUT, graph.getClass().getSimpleName() + ".dot");
        write(dot, file);
        Assert.assertTrue("Generated file must exist!", file.exists());
    }

    private void write(final String dot, final File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(dot);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // TODO: the two methods below are almost duplicates from dot.import,
    // but we don't want to depend on that here. Is there a different place
    // they would make more sense? Perhaps import should depend on export?

    public static void wipeDefaultOutput() {
        File defaultOutputFolder = OUTPUT;
        String[] files = defaultOutputFolder.list();
        int deleted = 0;
        for (String file : files) {
            File deletionCandidate = new File(defaultOutputFolder, file);
            /*
             * Relying on hidden is not safe on all platforms, so we double
             * check so that no .cvsignore files etc. are deleted:
             */
            if (!deletionCandidate.isHidden()
                    && !deletionCandidate.getName().startsWith(".")) {
                boolean delete = deletionCandidate.delete();
                if (delete) {
                    deleted++;
                }
            }
        }
        int dotFiles = countDotFiles(defaultOutputFolder);
        Assert
                .assertEquals(
                        "Default output directory should contain no DOT files before tests run;",
                        0, dotFiles);
        System.out.println(String.format("Deleted %s files in %s", deleted,
                defaultOutputFolder));
    }

    private static int countDotFiles(final File folder) {
        String[] list = folder.list();
        int dotFiles = 0;
        for (String name : list) {
            if (name.endsWith(".dot")) {
                dotFiles++;
            }
        }
        return dotFiles;
    }
}
