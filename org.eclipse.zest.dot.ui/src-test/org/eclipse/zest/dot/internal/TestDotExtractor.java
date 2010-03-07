/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest.dot.internal;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotExtractor}.
 * @author Fabian Steeg (fsteeg)
 */
@SuppressWarnings( "serial" )
public class TestDotExtractor {
    /* Testing input and output values: */
    Map<String, String> values = new HashMap<String, String>(){{
      put("/** Javadoc stuff graph name{a;b;a->b} and more */", "graph name{a;b;a->b}");
      put("/** Javadoc stuff graph long_name{a;b;a->b} and more */", "graph long_name{a;b;a->b}");
      put("/* Java block comment \n stuff digraph{a;b;a->b} and more */", "digraph{a;b;a->b}");
      put("Stuff about a graph and then graph{a;b;a->b} and more ", "graph{a;b;a->b}");
      put("Stuff about a graph and then with breaks graph{a\nb\na->b} and more ", "graph{a\nb\na->b}");
      put("Stuff about a graph and then digraph{a;b;a->b} and more ", "digraph{a;b;a->b}");
    }};
    @Test
    public void extractDot(){
        for (String input : values.keySet()) {
            String expected = values.get(input);
            String output = new DotExtractor(input).getDotString();
            Assert.assertEquals(/*String.format("Incorrect DOT extraction for '%s';", input), */expected, output);
        }
    }
}
