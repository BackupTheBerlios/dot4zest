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
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotImport} class.
 * @author Fabian Steeg (fsteeg)
 */
public final class BasicDotImportTests {

    /**
     * Sample graph summarizing all that is currently supported in the DOT
     * input.
     */
    @Test
    public void sampleGraph() {
        importFrom(new File("src-test/sample_input.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a simple
     * directed graph.
     */
    @Test
    public void directedGraph() {
        importFrom(new File("resources/simple_digraph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a simple
     * undirected graph.
     */
    @Test
    public void undirectedGraph() {
        importFrom(new File("resources/simple_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a labeled
     * graph.
     */
    @Test
    public void labeledGraph() {
        importFrom(new File("resources/labeled_graph.dot"));
    }

    /**
     * Test execution of File-based DOT-to-Zest transformations for a graph
     * using style attributes for edges.
     */
    @Test
    public void styledGraph() {
        importFrom(new File("resources/styled_graph.dot"));
    }

    static void importFrom(final File dotFile) {
        Assert.assertTrue("DOT input file must exist", dotFile.exists());
        DotImport.importDotFile(dotFile);
        File zest = findResultFile(dotFile, DotImport.DEFAULT_OUTPUT_FOLDER);
        Assert.assertNotNull("Resulting file must not be null", zest);
        Assert.assertTrue("Resulting file must exist", zest.exists());
        /*
         * The name of the generated file is equal to the name of the DOT graph
         * (part of the content of the DOT file, NOT the name of the file), plus
         * the ".java" extension:
         */
        Assert.assertEquals(zest.getName().split("\\.")[0],
                graphName(read(dotFile)));
        System.out.println(String.format(
                "Transformed DOT in '%s' to Zest in '%s'", dotFile, zest));
    }

    private static File findResultFile(final File dotFile,
            final File targetDirectory) {
        String name = graphName(read(dotFile));
        File resultFile = new File(targetDirectory, name + ".java");
        if (!resultFile.exists()) {
            throw new IllegalStateException(resultFile + " does not exist.");
        }
        return resultFile;
    }

    private static String graphName(final String dotContent) {
        if (dotContent == null) {
            throw new IllegalArgumentException();
        }
        /*
         * Work-around for testing, until we return the generated file when
         * doing the import: get the graph name from the actual DOT content
         * using a regular expression (while this is OK for testing, it is not
         * in general robust and therefore used for testing only).
         */
        Matcher m = Pattern.compile("[.]*?graph\\s*?(.+?)\\s*?\\{.*").matcher(
                dotContent);
        if (m.find()) {
            String name = m.group(1).trim();
            return name;
        }
        return null;
    }

    private static String read(final File dotFile) {
        try {
            StringBuilder builder = new StringBuilder();
            Scanner s = new Scanner(dotFile);
            while (s.hasNextLine()) {
                builder.append(s.nextLine());
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void wipeDefaultOutput() {
        File defaultOutputFolder = DotImport.DEFAULT_OUTPUT_FOLDER;
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
        Assert.assertTrue(
                "Default output directory should be empty before tests run",
                /* Besides the .cvsignore file, the folder should be empty: */
                DotImport.DEFAULT_OUTPUT_FOLDER.list().length == 1);
        System.out.println(String.format("Deleted %s files in %s", deleted,
                defaultOutputFolder));
    }
}
