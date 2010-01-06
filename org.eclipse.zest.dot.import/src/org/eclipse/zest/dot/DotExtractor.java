/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest.dot;

import java.io.File;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;

/**
 * Extracts a DOT graph substring from a string or file.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotExtractor {
    /** The DOT graph returned if the input contains no DOT graph substring. */
    public static final String NO_DOT = "graph{n1[label=\"No DOT data\"]}";
    private String input = NO_DOT;

    /**
     * @param input The string to extract a DOT graph substring from
     */
    public DotExtractor(final String input) {
        this.input = input;
    }

    /**
     * @param file The file to extract a DOT substring from
     * @throws MalformedURLException If the file cannot be resolved
     */
    public DotExtractor(final IFile file) throws MalformedURLException {
        this(DotFileUtils.read(DotFileUtils.resolve(file.getLocationURI().toURL())));
    }

    /**
     * @param file The file to extract a DOT substring from
     */
    public DotExtractor(final File file) {
        this(DotFileUtils.read(file));
    }

    /**
     * @return A DOT string extracted from the input, or {@link NO_DOT}, a valid DOT graph
     */
    public String getDotString() {
        Matcher m = Pattern.compile("((?:di)?graph\\s?[^{\\s]*\\s?\\{[^\\}]+\\})"/* , Pattern.DOTALL */).matcher(input);
        String dotString = m.find() ? m.group(1) : NO_DOT;
        return dotString.trim();
    }

}
