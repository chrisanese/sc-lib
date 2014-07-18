package de.fu.mi.scuttle.lib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import com.google.common.base.Charsets;

public class LanguageTemplateFactory {

    public LanguageTemplate compile(final String str) {
        final StringReader r = new StringReader(str);
        try {
            return compile(r);
        } catch (final IOException exc) {
            // does not happen with a StringReader
            return null;
        }
    }

    public LanguageTemplate compile(final File f) throws IOException {
        try (FileInputStream s = new FileInputStream(f)) {
            return compile(s);
        }
    }

    public LanguageTemplate compile(final InputStream s) throws IOException {
        final InputStreamReader r = new InputStreamReader(s, Charsets.UTF_8);
        return compile(new BufferedReader(r));
    }

    public LanguageTemplate compile(final Reader r) throws IOException {
        final LanguageTemplate tpl = new LanguageTemplate();

        final HashmarkTokenizer t = new HashmarkTokenizer(r);

        boolean isReadingVariable = false;
        while (t.nextToken()) {
            if (t.isHashmark() && t.getToken().length() == 2) {
                isReadingVariable = !isReadingVariable;
            } else if (isReadingVariable) {
                tpl.addVariable(t.getToken());
            } else {
                tpl.addText(t.getToken());
            }
        }

        return tpl;
    }
}
