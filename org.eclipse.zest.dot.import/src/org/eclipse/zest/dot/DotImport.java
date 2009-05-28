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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openarchitectureware.workflow.WorkflowRunner;
import org.openarchitectureware.workflow.monitor.NullProgressMonitor;
import org.openarchitectureware.workflow.monitor.ProgressMonitor;

/**
 * Transformation of DOT files to Zest Graph subclasses.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotImport {
    private static final File OUTPUT_FOLDER = new File("src-gen/org/eclipse/zest/dot");
    private static final URL WORKFLOW = DotImport.class
            .getResource("Generator.oaw");

    private DotImport() { /* Enforce non-instantiability */}

    /**
     * @param dotFile The DOT file to transform to a Zest representation
     * @return The Java file containing the definition of a Zest graph subclass
     *         generated from the given DOT file
     */
    public static File of(final File dotFile) {
        String dotLocation = dotFile.getAbsolutePath();
        File oawFile = loadWorkflow();
        String oawLocation = oawFile.getAbsolutePath();
        Map<String, String> properties = setupProps(dotLocation);
        WorkflowRunner workflowRunner = new WorkflowRunner();
        ProgressMonitor monitor = new NullProgressMonitor();
        workflowRunner.run(oawLocation, monitor, properties,
                new HashMap<String, String>());
        String[] list = OUTPUT_FOLDER.list();
        if (list.length == 0) {
            throw new IllegalStateException("No output written to "
                    + OUTPUT_FOLDER);
        }
        // TODO find a clean way to get the result file location
        return new File(OUTPUT_FOLDER, list[1]);
    }

    private enum Slot {
        MODEL_FILE("modelFile"), TARGET_DIR("targetDir");
        private String v;

        Slot(final String v) {
            this.v = v;
        }
    }

    private static Map<String, String> setupProps(final String dotLocation) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(Slot.MODEL_FILE.v, dotLocation);
        properties.put(Slot.TARGET_DIR.v, OUTPUT_FOLDER.getAbsolutePath());
        return properties;
    }

    private static File loadWorkflow() {
        File oawFile = null;
        try {
            oawFile = new File(WORKFLOW.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return oawFile;
    }
}
