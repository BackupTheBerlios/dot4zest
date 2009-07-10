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
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.openarchitectureware.workflow.WorkflowRunner;
import org.openarchitectureware.workflow.monitor.NullProgressMonitor;
import org.openarchitectureware.workflow.monitor.ProgressMonitor;

/**
 * Transformation of DOT files or strings to Zest Graph subclasses.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotImport {
    private static final String ZEST_TEMPLATE = "Zest";// .xpt = filename
    private static final String ZEST_ANIMATION_TEMPLATE = "ZestAnimation";// .xpt
    static final File DEFAULT_OUTPUT_FOLDER = new File(
            "src-gen/org/eclipse/zest/dot");
    private static final URL WORKFLOW = DotImport.class
            .getResource("Generator.oaw");
    static final File DEFAULT_INPUT_FOLDER = new File("resources/input");

    private DotImport() { /* Enforce non-instantiability */}

    /**
     * @param file The DOT file to import
     * @return The generated file, placed in the default output folder
     */
    public static File importDotFile(final File file) {
        return importDotFile(file, DEFAULT_OUTPUT_FOLDER);
    }

    /**
     * @param file The DOT file to import
     * @return The generated file, placed in the default output folder
     */
    public static File importDotFile(final IFile file) {
        try {
            return importDotFile(resolve(file.getLocationURI().toURL()),
                    DEFAULT_OUTPUT_FOLDER);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param dotFile The DOT file to transform to a Zest representation
     * @param targetDirectory The directory to create the generated file in
     * @return The Java file containing the definition of a Zest graph subclass
     *         generated from the given DOT file
     */
    public static File importDotFile(final File dotFile,
            final File targetDirectory) {
        String dotLocation = dotFile.getAbsolutePath();
        File oawFile = loadWorkflow();
        String oawLocation = oawFile.getAbsolutePath();
        Map<String, String> properties = setupProps(dotLocation, new Path(
                targetDirectory.getAbsolutePath()));
        WorkflowRunner workflowRunner = new WorkflowRunner();
        ProgressMonitor monitor = new NullProgressMonitor();
        workflowRunner.run(oawLocation, monitor, properties,
                new HashMap<String, String>());
        return findResultFile(dotFile, targetDirectory);
    }

    /**
     * @param file The DOT file to import
     * @param dest The container to store the resulting Zest graph in
     * @return The generated Zest graph Java source file
     */
    public static File importDotFile(final IFile file, final IContainer dest) {
        try {
            return importDotFile(resolve(file.getLocationURI().toURL()),
                    resolve(dest.getLocationURI().toURL()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param dotText The DOT graph to import
     * @param targetDirectory The container for the file to generate
     */
    public static File importDotString(final String dotText,
            final IContainer targetDirectory) {
        URL url;
        try {
            url = targetDirectory.getLocationURI().toURL();
            importDotString(dotText, resolve(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            return findResultFile(writeToTempFile(dotText), new File(resolve(
                    targetDirectory.getLocationURI().toURL()).toURI()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.err.println("Could not find result file in: " + targetDirectory);
        return null;
    }

    /**
     * @param dotText The DOT graph to import
     * @param targetDirectory The directory to store the file to generate
     * @return The Java file containing the definition of a Zest graph subclass
     *         generated from the given DOT text
     */
    public static File importDotString(final String dotText,
            final File targetDirectory) {
        return importDotFile(writeToTempFile(dotText), targetDirectory);
    }

    /**
     * @param dot The DOT graph
     * @return The file generated from the DOT graph
     */
    public static File importDotString(final String dot) {
        return importDotString(dot, DotImport.DEFAULT_OUTPUT_FOLDER);
    }

    /**
     * @param dotText The DOT graph
     * @return The errors the parser reported when parsing the given DOT graph
     */
    public static List<String> errors(final String dotText) {
        return DotAst.errors(writeToTempFile(dotText));
    }

    /**
     * @param dotFile The DOT file
     * @return The errors the parser reported when parsing the given DOT graph
     */
    public static List<String> errors(final File dotFile) {
        return DotAst.errors(dotFile);
    }

    /**
     * @param dotFile The DOT file
     * @return The errors the parser reported when parsing the given DOT graph
     */
    public static List<String> errors(final IFile dotFile) {
        try {
            return DotAst.errors(resolve(dotFile.getLocationURI().toURL()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * @param dotText The DOT graph representation
     * @return The name of the DOT graph
     */
    public static String graphName(final String dotText) {
        return DotAst.graphName(writeToTempFile(dotText));
    }

    /**
     * @param dotFile The DOT file
     * @return The name of the DOT graph defined in the given file
     */
    public static String graphName(final File dotFile) {
        return DotAst.graphName(dotFile);
    }

    /**
     * @param dotFile The DOT file
     * @return The name of the DOT graph defined in the given file
     */
    public static String graphName(final IFile dotFile) {
        try {
            return DotAst.graphName(resolve(dotFile.getLocationURI().toURL()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File findResultFile(final File dotFile,
            final File targetDirectory) {
        String name = DotAst.graphName(dotFile);
        File resultFile = new File(targetDirectory, name + ".java");
        if (!resultFile.exists()) {
            throw new IllegalStateException(resultFile + " does not exist.");
        }
        return resultFile;
    }

    private static File writeToTempFile(final String text) {
        try {
            File input = File.createTempFile("zest-import", ".dot");
            FileWriter writer = new FileWriter(input);
            writer.write(text);
            writer.flush();
            writer.close();
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private enum Slot {
        MODEL_FILE("modelFile"), TARGET_DIR("targetDir"), TEMPLATE_NAME(
                "templateName");
        private String v;

        Slot(final String v) {
            this.v = v;
        }
    }

    private static Map<String, String> setupProps(final String dotLocation,
            final IPath targetDirectory) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(Slot.MODEL_FILE.v, dotLocation);
        properties.put(Slot.TARGET_DIR.v, targetDirectory.toFile()
                .getAbsolutePath());
        properties.put(Slot.TEMPLATE_NAME.v, animated(dotLocation)
                ? ZEST_ANIMATION_TEMPLATE : ZEST_TEMPLATE);
        return properties;
    }

    private static boolean animated(final String dotLocation) {
        try {
            StringBuilder builder = new StringBuilder();
            Scanner s = new Scanner(new File(dotLocation));
            while (s.hasNextLine()) {
                builder.append(s.nextLine());
            }
            return builder.toString().contains("cluster_");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static File loadWorkflow() {
        File oawFile = null;
        oawFile = resolve(WORKFLOW);
        return oawFile;
    }

    private static File resolve(final URL url) {
        File oawFile = null;
        URL resolved = url;
        /*
         * If we don't check the protocol here, the FileLocator throws a
         * NullPointerException if the WORKFLOW URL is a normal file URL.
         */
        if (!url.getProtocol().equals("file")) {
            try {
                resolved = FileLocator.resolve(resolved);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            oawFile = new File(resolved.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return oawFile;
    }
}
