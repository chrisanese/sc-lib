package de.fu.mi.scuttle.lib.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.base.Charsets;

/**
 * A textual template that contains variables which can be replaced with
 * arbitrary values, can be constructed using a {@link LanguageTemplateFactory}.
 * 
 * @author Julian Fleischer
 * @since 2013-09-28
 */
public class LanguageTemplate {

    private SoftReference<SortedSet<String>> variableNames = null;

    private interface Node {

    }

    private static class Variable implements Node {

        private final String name;

        Variable(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class Text implements Node {

        private final String text;

        Text(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private final List<Node> nodes = new ArrayList<Node>();

    LanguageTemplate() {

    }

    // friend : LanguageTemplateFactory
    void addVariable(final String name) {
        this.nodes.add(new Variable(name));
    }

    // friend : LanguageTemplateFactory
    void addText(final String text) {
        this.nodes.add(new Text(text));
    }

    /**
     * Renders the template using the given variables as a String.
     * 
     * @param variables
     *            The map of variable-value associations to use for rendering
     *            the template.
     * @return The template rendered using the given variables as a String.
     * @since 2013-09-28
     */
    public String render(final Map<? extends Object, ? extends Object> variables) {
        final StringBuilder builder = new StringBuilder();
        render(variables, builder);
        return builder.toString();
    }

    /**
     * Renders the template using the given variables to the supplied
     * {@link StringBuilder}.
     * 
     * @param variables
     *            The map of variable-value associations to use for rendering
     *            the template.
     * @param builder
     *            The StringBuilder to render the template to.
     * @since 2013-09-28
     */
    public void render(final Map<? extends Object, ? extends Object> variables,
            final StringBuilder builder) {

        for (final Node node : nodes) {
            if (node instanceof Variable) {
                final String varName = node.toString();
                if (variables.containsKey(varName)) {
                    final Object value = variables.get(varName);
                    builder.append(value);
                }
            } else {
                builder.append(node);
            }
        }
    }

    /**
     * 
     * @param variables
     *            The map of variable-value associations to use for rendering
     *            the template.
     * @param writer
     *            The writer to render the template to.
     * @throws IOException
     *             Throws an IOException if an error occurs while writing to the
     *             supplied Writer.
     */
    public void render(final Map<? extends Object, ? extends Object> variables,
            final Writer writer)
            throws IOException {
        for (final Node node : nodes) {
            if (node instanceof Variable) {
                final String varName = node.toString();
                if (variables.containsKey(varName)) {
                    final Object value = variables.get(varName);
                    writer.append(String.valueOf(value));
                }
            } else {
                writer.append(node.toString());
            }
        }
    }

    /**
     * 
     * @param variables
     *            The map of variable-value associations to use for rendering
     *            the template.
     * @param stream
     *            The stream to render the template to.
     * @throws IOException
     *             Throws an IOException if an error occurs while writing to the
     *             supplied OutputStream.
     * @since 2013-09-28
     */
    public void render(final Map<? extends Object, ? extends Object> variables,
            final OutputStream stream) throws IOException {

        render(variables, new OutputStreamWriter(stream, Charsets.UTF_8));
    }

    /**
     * 
     * @param variables
     *            The map of variable-value associations to use for rendering
     *            the template.
     * @param stream
     *            The stream to render the template to.
     * @param charset
     *            The charset used for rendering this template.
     * @throws IOException
     *             Throws an IOException if an error occurs while writing to the
     *             supplied OutputStream.
     * @since 2013-09-28
     */
    public void render(final Map<? extends Object, ? extends Object> variables,
            final OutputStream stream, final Charset charset)
            throws IOException {

        render(variables, new OutputStreamWriter(stream, charset));
    }

    /**
     * Retrieves the names of all variables in this template.
     * 
     * @return A sorted set with the names of all variables in this template.
     * @since 2013-09-28
     */
    public SortedSet<String> getVariableNames() {
        SortedSet<String> variableNames = null;
        if (this.variableNames != null) {
            variableNames = this.variableNames.get();
        }
        if (variableNames != null) {
            return variableNames;
        }
        variableNames = new TreeSet<String>();
        for (final Node node : this.nodes) {
            if (node instanceof Variable) {
                variableNames.add(node.toString());
            }
        }
        variableNames = Collections.unmodifiableSortedSet(variableNames);
        this.variableNames = new SoftReference<SortedSet<String>>(variableNames);
        return variableNames;
    }
}
