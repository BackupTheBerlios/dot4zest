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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * A DOT template that can act as input for the wizard.
 * @author Fabian Steeg (fsteeg)
 */
final class ZestGraphTemplate {

    private ZestGraphTemplate() {}

    private String name;
    private String content;

    private static List<ZestGraphTemplate> availableTemplates = new ArrayList<ZestGraphTemplate>();

    /**
     * @return The names of the available DOT templates
     */
    static String[] availableTemplateNames() {
        List<String> names = new ArrayList<String>();
        for (ZestGraphTemplate template : availableTemplates) {
            names.add(template.name);
        }
        return names.toArray(new String[] {});
    }

    /**
     * @return The DOT contents of the available templates
     */
    static String[] availableTemplateContents() {
        List<String> contents = new ArrayList<String>();
        for (ZestGraphTemplate template : availableTemplates) {
            contents.add(template.content);
        }
        return contents.toArray(new String[] {});
    }

    static {
        /* Get the DOT testing files and use them as templates in the wizard: */
        Bundle bundle = Platform.getBundle("org.eclipse.zest.dot.import");
        URL root = FileLocator.findEntries(bundle, new Path("resources/tests"))[0];
        File rootFolder;
        try {
            rootFolder = new File(FileLocator.resolve(root).toURI());
            for (String file : rootFolder.list()) {
                if (file.endsWith(".dot")) {
                    String name = formatName(file);
                    availableTemplates.add(new ZestGraphTemplate(name.trim(),
                            read(new File(rootFolder, file))));
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * We split at _ and capitalize segments, to get:
     * <p/>
     * "sample_graph.dot" -> "Sample Graph"
     */
    private static String formatName(final String file) {
        String name = "";
        String[] tokens = file.split("\\.")[0].split("_");
        for (String string : tokens) {
            String upper = Character.toUpperCase(string.charAt(0))
                    + string.substring(1).toLowerCase();
            name += " " + upper;
        }
        return name;
    }

    private ZestGraphTemplate(final String name, final String content) {
        this.name = name;
        this.content = content;
    }

    private static String read(final File file) {
        StringBuilder builder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                /* We trim the header, if any: */
                if (!nextLine.trim().startsWith("/***")
                        && !nextLine.trim().startsWith("*")) {
                    builder.append(nextLine + "\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

}
