/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest.dot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link ZestProjectWizard}. Tests if the no-config usage of the
 * wizard works, ie. start the and hit finish.
 * @author Fabian Steeg (fsteeg)
 */
public final class TestZestProjectWizard {
    /*
     * The name of the generated file depends on the DOT graph name of the
     * sample graph that is copied from resources/project/templates to the new
     * project.
     */
    private static final String SAMPLE_GRAPH_JAVA = "SampleGraph.java";
    private IProject project;

    @Before
    public void setup() {
        ProjectHelper.assertProjectDoesntExist(ProjectHelper.PROJECT_PATH);

    }

    @After
    public void cleanup() {
        ProjectHelper.deleteProject(project);
    }

    @Test
    public void zestGraphCreation() {
        /* Run the wizard and return the name of the new project: */
        runWizard();
        /* We give the builder some time to generate files, etc. */
        delay(2000);
        /* Then we test the wizard results: */
        testProjectCreation();
        testBuilder();
        testProjectNature();
    }

    private void testBuilder() {
        Path path = new Path(ZestProjectWizard.SRC_GEN + "/"
                + ZestProjectWizard.PACKAGE.replaceAll("\\.", "/") + "/"
                + SAMPLE_GRAPH_JAVA);
        IResource generatedZestFile = project.findMember(path);
        String shouldExist = "Zest graph created by project builder should exist: "
                + path;
        Assert.assertNotNull(shouldExist, generatedZestFile);
        Assert.assertTrue(shouldExist, generatedZestFile.exists());
    }

    private void testProjectCreation() {
        String shouldExist = "Created project should exist";
        Assert.assertNotNull(shouldExist, project);
        Assert.assertTrue(shouldExist, project.exists());
    }

    private void testProjectNature() {
        try {
            IProjectNature nature = project.getNature(ZestNature.NATURE_ID);
            Assert
                    .assertNotNull(
                            "Zest nature should be present on new Zest project",
                            nature);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private void delay(final int millis) {
        // TODO: is there a more reliable way to do this?
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runWizard() {
        ZestProjectWizard wizard = new ZestProjectWizard();
        WizardDialog dialog = createDialog(wizard);
        dialog.create();
        setProjectName(wizard, ProjectHelper.PROJECT_NAME);
        dialog.setBlockOnOpen(false);
        dialog.open();
        wizard.performFinish();
        project = getNewProject(wizard);
    }

    // TODO switch to non-internal API in the ZestProjectWizard
    @SuppressWarnings( "restriction" )
    private IProject getNewProject(final ZestProjectWizard wizard) {
        IJavaElement createdElement = wizard.getCreatedElement();
        try {
            return createdElement.getCorrespondingResource().getProject();
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WizardDialog createDialog(final ZestProjectWizard wizard) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        wizard.init(workbench, null);
        WizardDialog dialog = new WizardDialog(workbench
                .getActiveWorkbenchWindow().getShell(), wizard);
        return dialog;
    }

    private void setProjectName(final ZestProjectWizard wizard,
            final String name) {
        NewJavaProjectWizardPageOne page = (NewJavaProjectWizardPageOne) wizard
                .getPages()[0];
        page.setProjectName(name);
    }
}
