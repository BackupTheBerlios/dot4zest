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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.mwe.core.WorkflowRunner;
import org.eclipse.emf.mwe.core.monitor.NullProgressMonitor;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;

/**
 * Transformation of DOT files or strings to Zest Graph subclasses.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotImport {
    private static final String ZEST_TEMPLATE = "Zest";// .xpt = filename
    private static final String ZEST_ANIMATION_TEMPLATE = "ZestAnimation";// .xpt
    static final File DEFAULT_OUTPUT_FOLDER = new File("src-gen/org/eclipse/zest/dot");
    private static final URL WORKFLOW = DotImport.class.getResource("Generator.mwe");
    static final File DEFAULT_INPUT_FOLDER = new File("resources/input");
    private File dotFile;
    private DotAst dotAst;

    /**
     * @param dotFile The DOT file to import
     */
    public DotImport(final File dotFile) {
        this.dotFile = dotFile;
        load();
    }

    /**
     * @param dotFile The DOT file to import
     */
    public DotImport(final IFile dotFile) {
        try {
            this.dotFile = resolve(dotFile.getLocationURI().toURL());
            load();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param dotString The DOT graph to import
     */
    public DotImport(final String dotString) {
        init(dotString);
    }

    private void init(final String dotString) {
        this.dotFile = writeToTempFile(dotString);
        load();
    }

    private void load() {
        this.dotAst = new DotAst(this.dotFile);
    }

    /**
     * @return The errors the parser reported when parsing the given DOT graph
     */
    public List<String> getErrors() {
        return dotAst.errors();
    }

    /**
     * @return The name of the DOT graph
     */
    public String getName() {
        return dotAst.graphName();
    }

    /**
     * @param parent The parent to create the Zest graph in
     * @param style The style bits for the Zest graph
     * @return The Zest graph instantiated from the imported DOT
     */
    public Graph newGraphInstance(final Composite parent, final int style) {
        // TODO switch to a string as the member holding the DOT to avoid read-write here
        return new GraphCreatorInterpreter().create(parent, style, read(dotFile));
    }

    /**
     * @return The Java file containing the definition of a Zest graph subclass generated from the given DOT
     *         graph, placed in the default output folder
     */
    public File newGraphSubclass() {
        return importDotFile(dotFile, DEFAULT_OUTPUT_FOLDER);
    }

    /**
     * @param outputDirectory The directory to place the generated file in
     * @return The Java file containing the definition of a Zest graph subclass generated from the given DOT
     *         graph
     */
    public File newGraphSubclass(final File outputDirectory) {
        return importDotFile(dotFile, outputDirectory);
    }

    /**
     * @param outputDirectory The directory to place the generated file in
     * @return The Java file containing the definition of a Zest graph subclass generated from the given DOT
     *         graph
     */
    public File newGraphSubclass(final IContainer outputDirectory) {
        try {
            return importDotFile(dotFile, resolve(outputDirectory.getLocationURI().toURL()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param dotFile The DOT file to transform to a Zest representation
     * @param targetDirectory The directory to create the generated file in
     * @return The Java file containing the definition of a Zest graph subclass generated from the given DOT
     *         file
     */
    private File importDotFile(final File dotFile, final File targetDirectory) {
        File fixedDotFile = fix(dotFile);
        String dotLocation = fixedDotFile.getAbsolutePath();
        File oawFile = loadWorkflow();
        String oawLocation = oawFile.getAbsolutePath();
        Map<String, String> properties = setupProps(dotLocation, new Path(targetDirectory.getAbsolutePath()));
        WorkflowRunner workflowRunner = new WorkflowRunner();
        ProgressMonitor monitor = new NullProgressMonitor();
        workflowRunner.run(oawLocation, monitor, properties, new HashMap<String, String>());
        return findResultFile(fixedDotFile, targetDirectory);
    }

    /**
     * Workaround for the current DOT-Parser.
     * @param dotFile The DOT file to fix
     * @return A file with the content of the given file, surrounded with "graphs{ graph ... }"
     */
    static File fix(final File dotFile) {
        String content = read(dotFile);
        File file = writeToTempFile("graphs { graph " + content + "}");
        return file;
    }
    
    /**
     * @return The DOT AST parsed from the DOT source
     */
    DotAst getDotAst() {
        return this.dotAst;
    }

    private static String read(final File dotFile) {
        StringBuilder builder = new StringBuilder();
        try {
            Scanner s = new Scanner(dotFile);
            while (s.hasNextLine()) {
                builder.append(s.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private File findResultFile(final File dotFile, final File targetDirectory) {
        String name = dotAst.graphName();
        File resultFile = new File(targetDirectory, name + ".java");
        if (!resultFile.exists()) {
            throw new IllegalStateException(resultFile + " does not exist.");
        }
        System.out.println("Zest file: " + resultFile.getAbsolutePath());
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
        MODEL_FILE("modelFile"), TARGET_DIR("targetDir"), TEMPLATE_NAME("templateName");
        private String v;

        Slot(final String v) {
            this.v = v;
        }
    }

    private static Map<String, String> setupProps(final String dotLocation, final IPath targetDirectory) {
        Map<String, String> properties = new HashMap<String, String>();
        try {
            properties.put(Slot.MODEL_FILE.v, new File(dotLocation).toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        properties.put(Slot.TARGET_DIR.v, targetDirectory.toFile().getAbsolutePath());
        properties.put(Slot.TEMPLATE_NAME.v, animated(dotLocation) ? ZEST_ANIMATION_TEMPLATE : ZEST_TEMPLATE);
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
         * If we don't check the protocol here, the FileLocator throws a NullPointerException if the WORKFLOW
         * URL is a normal file URL.
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
