/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.zest.dot;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.zest.core.widgets.Graph;

/**
 * This wizard page previews the Zest graph generated from the input in the
 * first page.
 * @author Fabian Steeg (fsteeg)
 */
public final class ZestGraphWizardPagePreview extends WizardPage {

    private Graph previewGraph;
    private Composite composite;
    private GraphFromDot importer;

    /**
     * Constructor for ZestGraphWizardPage.
     */
    public ZestGraphWizardPagePreview(final GraphFromDot importer) {
        super("wizardPage");
        this.importer = importer;
        setTitle("Generated Zest Graph Preview");
        setDescription("This Zest Graph will be generated from the input given in the first page");
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(final Composite parent) {
        composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        createPreviewRow(composite);
        setControl(composite);
    }

    private void createPreviewRow(final Composite composite) {
        Label label = new Label(composite, SWT.NULL);
        label.setText("&Preview:");
        previewGraph = importer.create(composite, SWT.BORDER,
                ((ZestGraphWizardPageInput) getWizard().getPreviousPage(this))
                        .getInputText());
        setupLayout();
        label = new Label(composite, SWT.NULL);
        label.setText("");

    }
    private void setupLayout() {
        if (previewGraph != null) {
            GridData gd = new GridData(GridData.FILL_BOTH);
            previewGraph.setLayout(new GridLayout());
            previewGraph.setLayoutData(gd);
        }
    }

    void updateGraph(final String inputText) {
        System.out.println("Updating to: " + inputText);
        if (previewGraph != null) {
            previewGraph.dispose();
        }
        if (composite != null) {
            previewGraph = importer.create(composite, SWT.BORDER, inputText);
            setupLayout();
            composite.layout();
        }
    }

}
