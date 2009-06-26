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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;

/**
 * Create a Java project, copy some resources and setup the classpath.
 * @author Fabian Steeg (fsteeg)
 */
/* TODO use non-internal wizard and pages */
@SuppressWarnings( "restriction" )
public class ZestProjectWizard extends JavaProjectWizard {

    private static final String LIB = "/libs/";
    private static final String RESOURCES = "resources/project";

    /**
     * {@inheritDoc}
     * @see org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        boolean performFinish = super.performFinish();
        try {
            URL find = FileLocator.find(Activator.getDefault().getBundle(),
                    new Path(RESOURCES), Collections.EMPTY_MAP);
            URL fileURL = FileLocator.toFileURL(find);
            File resourcesDirectory = new File(fileURL.toURI());
            IJavaElement javaElement = getCreatedElement();
            IPath path = javaElement.getPath();
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource newProject = root.findMember(path);
            File outRoot = new File(newProject.getLocationURI());
            copy(resourcesDirectory, outRoot);
            newProject.refreshLocal(IResource.DEPTH_INFINITE, null);
            setupProjectClasspath(javaElement, root, newProject);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return performFinish;
    }

    private void setupProjectClasspath(final IJavaElement javaElement,
            final IWorkspaceRoot root, final IResource newProject) {
        try {
            /* TODO currently no Jars used - would it be better this way? */
            IClasspathEntry[] classpath = javaElement.getJavaProject()
                    .getRawClasspath();
            Path jarFolderPath = new Path(newProject.getFullPath() + LIB);
            IFolder libFolder = (IFolder) root.findMember(jarFolderPath);
            IResource[] members;
            members = libFolder.members();
            IClasspathEntry[] newClasspath = new ClasspathEntry[classpath.length
                    + 2 + members.length - 1];
            for (int i = 0; i < members.length; i++) {
                newClasspath[newClasspath.length - (i + 1)] = JavaCore
                        .newLibraryEntry(members[i].getFullPath(), null, null);
            }
            IProject folder = (IProject) newProject;
            IFolder gen = folder.getFolder("src-gen");
            gen.create(true, true, null);
            newClasspath[newClasspath.length - 2] = JavaCore.newSourceEntry(gen
                    .getFullPath());
            for (int i = 0; i < classpath.length; i++) {
                newClasspath[i] = classpath[i];
            }
            newClasspath[newClasspath.length - 1] = JavaCore
                    .newContainerEntry(new Path(
                            "org.eclipse.pde.core.requiredPlugins"));
            javaElement.getJavaProject().setRawClasspath(newClasspath, null);
        } catch (JavaModelException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private void copy(final File sourceRootFolder,
            final File destinationRootFolder) throws IOException {
        for (String name : sourceRootFolder.list()) {
            File source = new File(sourceRootFolder, name);
            if (source.isDirectory()) {
                // Recursively create sub-directories:
                File destinationFolder = new File(destinationRootFolder, source
                        .getName());
                if (!destinationFolder.mkdirs()) {
                    throw new IllegalStateException("Could not create: "
                            + destinationFolder);
                }
                copy(source, destinationFolder);
            } else {
                // Copy individual files:
                File destinationFile = new File(destinationRootFolder, name);
                InputStream sourceStream = null;
                FileOutputStream destinationStream = null;
                try {
                    sourceStream = source.toURI().toURL().openStream();
                    destinationStream = new FileOutputStream(destinationFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = sourceStream.read(buffer)) != -1) {
                        destinationStream.write(buffer, 0, bytesRead);
                    }
                } finally {
                    close(sourceStream);
                    close(destinationStream);
                }
            }
        }
    }

    private void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
