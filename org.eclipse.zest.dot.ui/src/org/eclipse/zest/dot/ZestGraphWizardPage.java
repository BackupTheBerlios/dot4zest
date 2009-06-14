/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.zest.dot;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * This wizard page allows setting the container for the new Zest graph and the
 * DOT input. The page will only accept file name without the extension OR with
 * the extension that matches the expected one (java).
 * @author Fabian Steeg (fsteeg)
 */
public final class ZestGraphWizardPage extends WizardPage {

    private static final String WIZARD_DESCRIPTION = "This wizard creates a new Zest Graph Subclass.";
    private static final String NEW_ZEST_GRAPH = "New Zest Graph";
    private static final String EXTENSION_MUST_BE_JAVA = "File extension must be \"java\"";
    private static final String NAME_MUST_BE_VALID = "File name must be valid";
    private static final String NAME_MUST_BE_SPECIFIED = "File name must be specified";
    private static final String MUST_BE_WRITABLE = "Project must be writable";
    private static final String CONTAINER_MUST_EXIST = "File container must exist";
    private static final String CONTAINER_MUST_BE_SPECIFIED = "File container must be specified";
    private static final String FILE_CONTAINER = "Select new file container";
    private static final String JAVA = "java";
    private static final String TEMPLATE = "&Template:";
    private static final String BROWSE = "Browse...";
    private static final String CONTAINER = "&Container:";
    private static final String DEFAULT_GRAPH_NAME = "CustomZestGraph";
    private static final String DEFAULT_DOT_GRAPH = "digraph "
            + DEFAULT_GRAPH_NAME + " {\n\t1; 2; \n\t1->2 \n}";
    private Text containerText;
    private ISelection selection;
    private Text inputText;
    private Combo combo;

    /**
     * Constructor for ZestGraphWizardPage.
     * @param selection The current selection
     */
    public ZestGraphWizardPage(final ISelection selection) {
        super("wizardPage");
        setTitle(NEW_ZEST_GRAPH);
        setDescription(WIZARD_DESCRIPTION);
        this.selection = selection;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        createContainerRow(composite);
        createComboRow(composite);
        createTemplateRow(composite);
        validateSelection();
        validateFields();
        setControl(composite);
    }

    private void createComboRow(final Composite composite) {
        Label label = new Label(composite, SWT.NULL);
        label.setText(TEMPLATE);
        combo = new Combo(composite, SWT.NONE);
        combo.setItems(ZestGraphTemplate.availableTemplateNames());
        combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        combo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (e.getSource() instanceof Combo) {
                    Combo combo = ((Combo) e.getSource());
                    inputText.setText(ZestGraphTemplate
                            .availableTemplateContents()[(combo
                            .getSelectionIndex())]);
                }
            }
        });
        combo.select(0);
        label = new Label(composite, SWT.NULL);
        label.setText("");
    }

    private void createContainerRow(final Composite composite) {
        Label label = new Label(composite, SWT.NULL);
        label.setText(CONTAINER);
        containerText = new Text(composite, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);
        setContainerFromSelection();
        containerText.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                validateFields();
            }
        });
        Button button = new Button(composite, SWT.PUSH);
        button.setText(BROWSE);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                handleBrowse();
            }
        });
    }

    private void createTemplateRow(final Composite composite) {
        GridData gd = new GridData(GridData.FILL_BOTH);
        Label label = new Label(composite, SWT.NULL);
        label.setText("");
        inputText = new Text(composite, SWT.BORDER | SWT.MULTI);
        inputText.setText(DEFAULT_DOT_GRAPH);
        inputText.setLayoutData(gd);
        inputText.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                validateFields();
            }
        });
        inputText.setText(ZestGraphTemplate.availableTemplateContents()[0]);
        label = new Label(composite, SWT.NULL);
        label.setText("");
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */
    private void validateSelection() {
        if (selection != null && !selection.isEmpty()
                && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1) {
                return;
            }
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                container = (obj instanceof IContainer)
                        ? (IContainer) obj : ((IResource) obj).getParent();
                containerText.setText(container.getFullPath().toString());
            }
        }
    }

    private void validateFields() {
        IResource container = ResourcesPlugin.getWorkspace().getRoot()
                .findMember(new Path(getContainerName()));
        String fileName = getFileName();
        if (getContainerName().length() == 0) {
            updateStatus(CONTAINER_MUST_BE_SPECIFIED);
        } else if (container == null
                || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
            updateStatus(CONTAINER_MUST_EXIST);
        } else if (!container.isAccessible()) {
            updateStatus(MUST_BE_WRITABLE);
        } else if (fileName.length() == 0) {
            updateStatus(NAME_MUST_BE_SPECIFIED);
        } else if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
            updateStatus(NAME_MUST_BE_VALID);
        } else if (fileName.contains(".")
                && !fileName.substring(fileName.lastIndexOf('.') + 1)
                        .equalsIgnoreCase(JAVA)) {
            updateStatus(EXTENSION_MUST_BE_JAVA);
        } else if (DotImport.errors(getInputText()).size() > 0) {
            List<String> errors = DotImport.errors(getInputText());
            updateStatus(errors.get(0));
        } else {
            updateStatus(null);
        }
    }
    private void setContainerFromSelection() {
        Object o = ((IStructuredSelection) selection).getFirstElement();
        if (o instanceof IPackageFragmentRoot) {
            containerText.setText(((IPackageFragmentRoot) o).getPath()
                    .toString());
        } else if (o instanceof IPackageFragment) {
            containerText.setText(((IPackageFragment) o).getPath().toString());
        } else if (o instanceof IResource) {
            containerText.setText(((IResource) o).getFullPath().toString());
        } else {
            containerText.setText("");
        }
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */
    private void handleBrowse() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(
                getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
                FILE_CONTAINER);
        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                containerText.setText(((Path) result[0]).toString());
            }
        }
    }

    private void updateStatus(final String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    /**
     * @return The text of the container field
     */
    String getContainerName() {
        return containerText.getText();
    }

    /**
     * @return The name of the file to create
     */
    String getFileName() {
        return DotImport.graphName(getInputText()) + "." + JAVA;
    }

    /**
     * @return The text of the DOT input filed
     */
    String getInputText() {
        return inputText.getText();
    }
}
