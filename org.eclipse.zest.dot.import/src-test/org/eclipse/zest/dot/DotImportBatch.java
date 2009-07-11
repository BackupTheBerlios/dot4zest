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

/**
 * Batch transformation of all DOT files in a given folder to Zest Graph
 * subclasses, usable as a console application.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotImportBatch {
    private DotImportBatch() { /* Enforce non-instantiability */}
    /**
     * Simple main to transform all DOT files in a folder to Zest Graph
     * implementations.
     * @param args First: input folder name; Second: output folder name; if none
     *            are specified default values are selected
     */
    public static void main(final String[] args) {
        File inputFolder = DotImport.DEFAULT_INPUT_FOLDER;
        File outputFolder = DotImport.DEFAULT_OUTPUT_FOLDER;
        if (args.length > 0) {
            File inputCandidate = new File(args[0]);
            if (!inputCandidate.exists() || !inputCandidate.canRead()) {
                System.err.println(String.format(
                        "Cannot read files from folder %s", inputCandidate));
            } else {
                inputFolder = inputCandidate;
            }
        } else if (args.length > 1) {
            File outputCandidate = new File(args[1]);
            if (!outputCandidate.exists() || !outputCandidate.canRead()
                    || !outputCandidate.canWrite()) {
                System.err.println(String.format(
                        "Cannot write files to folder %s", outputCandidate));
            } else {
                outputFolder = outputCandidate;
            }
        } else {
            System.out
                    .println("Using default input and output folders (specify as first and second argument)");
        }
        System.out.println(String.format("Using input %s and output %s",
                inputFolder.getAbsolutePath(), outputFolder.getAbsolutePath()));
        importDotFiles(inputFolder, outputFolder);
    }

    private static void importDotFiles(final File inputFolder,
            final File outputFolder) {
        String[] inputFiles = inputFolder.list();
        for (String inputFileName : inputFiles) {
            File inputFile = new File(inputFolder, inputFileName);
            if (inputFile.getName().toLowerCase().endsWith(".dot")) {
                new DotImport(inputFile).getZestFile(outputFolder);
                System.out.println(String.format("Imported %s from %s into %s",
                        inputFile.getAbsolutePath(), inputFolder
                                .getAbsolutePath(), outputFolder
                                .getAbsolutePath()));
            }
        }
    }
}
