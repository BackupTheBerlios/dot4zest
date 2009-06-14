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
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.core.builder.ProblemFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;

/**
 * Import DOT to a Zest Graph instance via the internal Eclipse JDT compiler.
 * @author Fabian Steeg (fsteeg)
 */
@SuppressWarnings( "restriction" )
// The downside of this solution is it uses internal API; upside is it works
// with Java 5 (contrary to the other solution, based on the Java compiler API)
class GraphFromDotViaInternalJdtCompiler implements GraphFromDot {

    private String dot;

    /**
     * @param dotText
     */
    public GraphFromDotViaInternalJdtCompiler(final String dotText) {
        this.dot = dotText;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.zest.dot.GraphFromDot#create(org.eclipse.swt.widgets.Composite,
     *      int)
     */
    @Override
    public Graph create(final Composite parent, final int style) {
        File zestFile = DotImport.importDotString(dot);
        URL url = compileWithInternalJdtCompiler(zestFile);
        Graph graph = ExperimentalDotImport.loadGraph(DotImport.graphName(dot),
                url, parent, style);
        return graph;
    }

    private URL compileWithInternalJdtCompiler(final File zestFile) {
        INameEnvironment nameEnvironment = new FileSystem(new String[0],
                new String[0], "UTF-8");
        CompilerOptions compilerOptions = new CompilerOptions();
        org.eclipse.jdt.internal.compiler.Compiler compiler = new org.eclipse.jdt.internal.compiler.Compiler(
                nameEnvironment, DefaultErrorHandlingPolicies
                        .proceedWithAllProblems(), compilerOptions,
                new ICompilerRequestor() {
                    @Override
                    public void acceptResult(final CompilationResult result) {}
                }, ProblemFactory.getProblemFactory(Locale.getDefault()));

        compiler
                .compile(new org.eclipse.jdt.internal.compiler.env.ICompilationUnit[] { new org.eclipse.jdt.internal.compiler.env.ICompilationUnit() {
                    @Override
                    public char[] getFileName() {
                        return zestFile.getName().toCharArray();
                    }
                    @Override
                    public char[][] getPackageName() {
                        return null;
                    }
                    @Override
                    public char[] getMainTypeName() {
                        return null;
                    }
                    @Override
                    public char[] getContents() {
                        return read(zestFile).toCharArray();
                    }
                } });
        try {
            URL url = zestFile.getParentFile().toURI().toURL();
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String read(final File dotFile) {
        try {
            Scanner scanner = new Scanner(dotFile);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine() + "\n");
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
