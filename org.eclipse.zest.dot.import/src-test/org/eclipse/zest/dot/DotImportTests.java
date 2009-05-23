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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotImport} class.
 * @author Fabian Steeg (fsteeg)
 */
public final class DotImportTests {
    private static final File DOT = new File("resources/sample.dot");

    /** Test execution of File-based DOT-to-Zest transformations. */
    @Test
    public void testImport() {
        Assert.assertTrue("DOT input file must exist", DOT.exists());
        File zest = DotImport.of(DOT);
        Assert.assertNotNull("Resulting file must not be null", zest);
        Assert.assertTrue("Resulting file must exist", zest.exists());
        System.out.println(String.format(
                "Transformed DOT in '%s' to Zest in '%s'", DOT, zest));
    }
}
