/*******************************************************************************
 * Copyright (c) 2009 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest;

import org.eclipse.zest.dot.DotImportTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Main test suite for the {@code org.eclipse.zest.dot.import} bundle.
 * @author Fabian Steeg (fsteeg)
 */
@RunWith( Suite.class )
@Suite.SuiteClasses( { DotImportTests.class } )
public class DotImportSuite {}
