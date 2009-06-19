/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.zest.dot;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * This is a Zest Graph new wizard. Its role is to create a new Zest Graph
 * subclass in the provided container. If the container resource (a folder or a
 * project) is selected in the workspace when the wizard is opened, it will
 * accept it as the target container. The wizard creates one file with the
 * extension "java".
 * @author Fabian Steeg (fsteeg)
 */
public final class ZestGraphWizard extends Wizard implements INewWizard {
    private static final String DOES_NOT_EXIST = "Container does not exist: ";
    private static final String CREATING = "Creating ";
    private static final String ERROR = "Error";
    private static final String OPENING_FILE = "Opening file for editing...";
    private static final String PLUGIN_ID = "org.eclipse.zest.dot.ui";
    private static final String RUNNING_FILE = "Running generated file...";
    private ZestGraphWizardPageInput inputPage;
    private ZestGraphWizardPagePreview previewPage;
    private ISelection selection;

    /** Create a new ZestGraphWizard. */
    public ZestGraphWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages() {
        inputPage = new ZestGraphWizardPageInput(selection);
        previewPage = new ZestGraphWizardPagePreview(new GraphFromDotViaInternalJdtCompiler());
        addPage(inputPage);
        addPage(previewPage);
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(final IWorkbench workbench,
            final IStructuredSelection selection) {
        this.selection = selection;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        final String containerName = inputPage.getContainerName();
        final String fileName = inputPage.getFileName();
        IRunnableWithProgress op = createRunnable(containerName, fileName);
        try {
            getContainer().run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), ERROR, realException
                    .getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private IRunnableWithProgress createRunnable(final String containerName,
            final String fileName) {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(final IProgressMonitor monitor)
                    throws InvocationTargetException {
                try {
                    doFinish(containerName, fileName, monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        return op;
    }

    private void doFinish(final String containerName, final String fileName,
            final IProgressMonitor monitor) throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IContainer)) {
            throwCoreException(DOES_NOT_EXIST + containerName);
        }
        final IContainer container = (IContainer) resource;
        createFile(container, fileName, monitor);
        refreshContainer(container, monitor);
        IFile file = container.getFile(new Path(fileName));
        openFile(container, file, monitor);
    }

    private void createFile(final IContainer container, final String fileName,
            final IProgressMonitor monitor) {
        monitor.beginTask(CREATING + fileName, 3);
        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                /* We import the DOT string given in the input text field: */
                DotImport.importDotString(inputPage.getInputText(), container);
            }
        });
        monitor.worked(1);
    }

    private void openFile(final IContainer container, final IFile file,
            final IProgressMonitor monitor) {
        monitor.setTaskName(OPENING_FILE);
        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                try {
                    container.refreshLocal(1, monitor);
                } catch (CoreException e1) {
                    e1.printStackTrace();
                }
                IWorkbenchPage page = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage();
                try {
                    IDE.openEditor(page, file, true);
                    launchOpenFile(file, monitor);
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        });
        monitor.worked(1);
    }

    private void launchOpenFile(final IFile file, final IProgressMonitor monitor) {
        monitor.setTaskName(RUNNING_FILE);
        try {
            JUnitLaunchShortcut f = new JUnitLaunchShortcut();
            f
                    .launch(PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage()
                            .getActiveEditor(), ILaunchManager.RUN_MODE);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        monitor.worked(1);
    }

    private void refreshContainer(final IContainer container,
            final IProgressMonitor monitor) {
        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                try {
                    container.refreshLocal(1, monitor);
                } catch (CoreException e1) {
                    e1.printStackTrace();
                }
            }
        });
        monitor.worked(1);
    }

    private void throwCoreException(final String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK,
                message, null);
        throw new CoreException(status);
    }
}
