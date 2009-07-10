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

import org.junit.Test;
import static org.eclipse.zest.dot.DotImportTestUtils.*;

/**
 * Tests for the {@link DotImport} class.
 * @author Fabian Steeg (fsteeg)
 */
public final class TestAnimationDotImport {

    /**
     * Minimal DOT graph to be transformed to a Zest animation.
     */
    @Test
    public void animatedGraph() {
        importFrom(new File(RESOURCES_TESTS + "experimental_animation.dot"));
    }
    
}
