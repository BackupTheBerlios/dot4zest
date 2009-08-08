/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.zest.dot;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import org.eclipse.core.runtime.FileLocator;

/**
 * Static helper methods for working with files.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotFileUtils {
    private DotFileUtils() {/* Enforce non-instantiability */}

    /**
     * @param url The URL to resolve (can be workspace-relative)
     * @return The file corresponding to the given URL
     */
    public static File resolve(final URL url) {
        File resultFile = null;
        URL resolved = url;
        /*
         * If we don't check the protocol here, the FileLocator throws a NullPointerException if the URL is a
         * normal file URL.
         */
        if (!url.getProtocol().equals("file")) {
            try {
                resolved = FileLocator.resolve(resolved);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            resultFile = new File(resolved.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return resultFile;
    }

    /**
     * @param text The string to write out to a temp file
     * @return The temp file containing the given string
     */
    public static File write(final String text) {
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

    /**
     * @param file The file to read into a string
     * @return The string containing the contents of the given file
     */
    public static String read(final File file) {
        StringBuilder builder = new StringBuilder();
        try {
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                builder.append(s.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * @param closeable The closable to safely close
     */
    public static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Recursively copies the contents of the source folder to the destination folder
     * @param sourceRootFolder The source root folder
     * @param destinationRootFolder The destination root folder
     */
    public static void copyAllFiles(final File sourceRootFolder, final File destinationRootFolder) {
        for (String name : sourceRootFolder.list()) {
            File source = new File(sourceRootFolder, name);
            /* The resources we copy over are versioned in this bundle. */
            if (source.getName().equals("CVS")) {
                continue;
            }
            if (source.isDirectory()) {
                // Recursively create sub-directories:
                File destinationFolder = new File(destinationRootFolder, source.getName());
                if (!destinationFolder.mkdirs() && !destinationFolder.exists()) {
                    throw new IllegalStateException("Could not create: " + destinationFolder);
                }
                copyAllFiles(source, destinationFolder);
            } else {
                // Copy individual files:
                copySingleFile(destinationRootFolder, name, source);
            }
        }
    }

    /**
     * @param destinationFolder The destination folder
     * @param newFileName The name for the new file
     * @param sourceFile The source file to be copied into a new file in the destination folder, with the
     *            specified name
     */
    public static void copySingleFile(final File destinationFolder, final String newFileName,
            final File sourceFile) {
        File destinationFile = new File(destinationFolder, newFileName);
        InputStream sourceStream = null;
        FileOutputStream destinationStream = null;
        try {
            sourceStream = sourceFile.toURI().toURL().openStream();
            destinationStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = sourceStream.read(buffer)) != -1) {
                destinationStream.write(buffer, 0, bytesRead);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(sourceStream);
            close(destinationStream);
        }
    }
}
