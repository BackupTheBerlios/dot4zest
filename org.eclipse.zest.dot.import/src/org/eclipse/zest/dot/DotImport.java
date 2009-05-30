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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.openarchitectureware.workflow.WorkflowRunner;
import org.openarchitectureware.workflow.monitor.NullProgressMonitor;
import org.openarchitectureware.workflow.monitor.ProgressMonitor;

/**
 * Transformation of DOT files to Zest Graph subclasses.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotImport {
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
    public static void importDotFile(final File file) {
        importDotFile(file, DEFAULT_OUTPUT_FOLDER);
    }

    /**
     * @param dotFile The DOT file to transform to a Zest representation
     * @param targetDirectory The directory to create the generated file in
     * @return The Java file containing the definition of a Zest graph subclass
     *         generated from the given DOT file
     */
    public static void importDotFile(final File dotFile,
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
        /*
         * Returning the generated File here turns out to be trickier than
         * expected. Ideally, I'd like to be able to say: give the generated
         * file the same name as the input file, but with the .java ending
         * instead. This would make testing very easy. I currently don't see an
         * easy way to do that, as the name of the target file is defined in the
         * template, not as part of the workflow. Also, it is not required to
         * provide basic functionality (as the generated files are in the
         * specified target directory), so for now we return nothing here.
         */
    }

    private enum Slot {
        MODEL_FILE("modelFile"), TARGET_DIR("targetDir");
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
        return properties;
    }

    private static File loadWorkflow() {
        File oawFile = null;
        try {
            URL resolved = WORKFLOW;
            /*
             * If we don't check the protocol here, the FileLocator throws a
             * NullPointerException if the WORKFLOW URL is a normal file URL.
             */
            if (!resolved.getProtocol().equals("file")) {
                resolved = FileLocator.resolve(resolved);
            }
            oawFile = new File(resolved.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return oawFile;
    }
}
