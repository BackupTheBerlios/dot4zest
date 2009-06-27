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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

/**
 * Create a Java project, copy some resources and setup the classpath.
 * @author Fabian Steeg (fsteeg)
 */
/* TODO use non-internal wizard and pages */
@SuppressWarnings( "restriction" )
public final class ZestProjectWizard extends JavaProjectWizard {

    static final String PACKAGE = "org.eclipse.zest.dot";
    static final String SRC_GEN = "src-gen";
    private static final String RESOURCES = "resources/project";

    /**
     * {@inheritDoc}
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(final IWorkbench workbench,
            final IStructuredSelection currentSelection) {
        super.setWindowTitle("New Zest Project");
        super.init(workbench, currentSelection);
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        boolean performFinish = super.performFinish();
        try {
            IJavaElement javaElement = getCreatedElement();
            IPath path = javaElement.getPath();
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource newProject = root.findMember(path);
            File outRoot = new File(newProject.getLocationURI());
            /*
             * We copy the required resources from this bundle to the new
             * project and setup the project's classpath (which uses the copied
             * resources):
             */
            copy(resourcesDirectory(), outRoot);
            setupProjectClasspath(javaElement, root, newProject);
            newProject.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return performFinish;
    }

    private File resourcesDirectory() throws IOException, URISyntaxException {
        URL resourcesFolderUrl = FileLocator.find(Activator.getDefault()
                .getBundle(), new Path(RESOURCES), Collections.EMPTY_MAP);
        URL fileURL = FileLocator.toFileURL(resourcesFolderUrl);
        File resourcesDirectory = new File(fileURL.toURI());
        return resourcesDirectory;
    }

    private void setupProjectClasspath(final IJavaElement javaElement,
            final IWorkspaceRoot root, final IResource newProject) {
        try {
            IClasspathEntry[] classpath = javaElement.getJavaProject()
                    .getRawClasspath();
            /*
             * We will add two items to the classpath: a src-gen source folder
             * and the Zest plugin dependencies (to get the required SWT and
             * Zest dependencies into the newly created project).
             */
            IClasspathEntry[] newClasspath = new ClasspathEntry[classpath.length + 2];
            IProject project = (IProject) newProject;
            IFolder sourceGenFolder = project.getFolder(SRC_GEN);
            sourceGenFolder.create(true, true, null);
            createPackage(project, sourceGenFolder);
            /* Copy over the existing classpath entries: */
            for (int i = 0; i < classpath.length; i++) {
                newClasspath[i] = classpath[i];
            }
            newClasspath[newClasspath.length - 2] = JavaCore
                    .newSourceEntry(sourceGenFolder.getFullPath());
            newClasspath[newClasspath.length - 1] = JavaCore
                    .newContainerEntry(new Path(
                            "org.eclipse.pde.core.requiredPlugins"));
            /* Set the updated classpath: */
            javaElement.getJavaProject().setRawClasspath(newClasspath, null);
            /* Activate the Zest project nature: */
            ToggleNatureAction.toggleNature(project);
        } catch (JavaModelException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private void createPackage(final IProject project,
            final IFolder sourceGenFolder) throws JavaModelException {
        IJavaProject javaProject = JavaCore.create(project);
        IPackageFragmentRoot newPackage = javaProject
                .getPackageFragmentRoot(sourceGenFolder);
        newPackage.createPackageFragment(PACKAGE, true,
                new NullProgressMonitor());
    }

    private void copy(final File sourceRootFolder,
            final File destinationRootFolder) throws IOException {
        for (String name : sourceRootFolder.list()) {
            File source = new File(sourceRootFolder, name);
            /* The resources we copy over are versioned in this bundle. */
            if (source.getName().equals("CVS")) {
                continue;
            }
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
