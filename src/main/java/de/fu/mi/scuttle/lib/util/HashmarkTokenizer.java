package de.fu.mi.scuttle.lib.util;

import java.io.IOException;
import java.io.Reader;

import com.google.common.base.Strings;

class HashmarkTokenizer {

    private final Reader reader;
    private final StringBuilder builder = new StringBuilder();
    private int hashmarkCount = 0;
    private String currentToken = null;
    private boolean isHashmark = false;

    public HashmarkTokenizer(Reader reader) {
        this.reader = reader;
    }

    public boolean nextToken() throws IOException {
        int c;

        while ((c = reader.read()) >= 0) {
            if (c == '#') {
                if (hashmarkCount > 0 && hashmarkCount < 2) {
                    hashmarkCount++;
                } else {
                    if (hashmarkCount > 0) {
                        currentToken = Strings.repeat("#", hashmarkCount);
                        isHashmark = true;
                    } else {
                        currentToken = builder.toString();
                        isHashmark = false;
                        builder.setLength(0);
                    }
                    hashmarkCount = 1;
                    return true;
                }
            } else {
                builder.append((char) c);
                if (hashmarkCount > 0) {
                    currentToken = Strings.repeat("#", hashmarkCount);
                    isHashmark = true;
                    hashmarkCount = 0;
                    return true;
                }
            }
        }
        if (builder.length() > 0) {
            currentToken = builder.toString();
            isHashmark = false;
            builder.setLength(0);
            return true;
        }
        if (hashmarkCount > 0) {
            currentToken = Strings.repeat("#", hashmarkCount);
            isHashmark = true;
            hashmarkCount = 0;
            return true;
        }
        return false;
    }

    public String getToken() {
        return currentToken;
    }

    public boolean isHashmark() {
        return isHashmark;
    }
}