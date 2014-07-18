package de.fu.mi.scuttle.lib.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.fu.mi.scuttle.lib.util.ProcessExecutor.ExecutionException;
import de.fu.mi.scuttle.lib.util.ProcessExecutor.Result;

/**
 * Static methods for invoking the yui compressor as a separate process.
 * 
 * The YuiCompressor uses its own patched version of Rhino but uses the same
 * class names. The patched version does not play well with other dependencies
 * that require rhino, therefor it is wise to execute yui in a separate process
 * (You can also try class loader voodoo).
 * 
 * @author Julian Fleischer
 * @since 2013-11-04
 */
public class YuiCompressor {

    public static byte[] compressCss(
            final String yuiJar,
            final String cssScript) throws IOException {
        return compress(yuiJar, "css", cssScript);
    }

    public static byte[] compressJs(
            final String yuiJar,
            final String jsScript,
            final String... options) throws IOException {
        return compress(yuiJar, "js", jsScript, options);
    }

    private static byte[] compress(
            final String yuiJar,
            final String scriptType,
            final String script,
            final String... options) throws IOException {

        final File inFile = File.createTempFile("yui-in-", "." + scriptType);
        final File outFile = File.createTempFile("yui-out-", "." + scriptType);

        try {

            Files.write(script, inFile, Charsets.UTF_8);

            final List<String> args = Arrays.asList(
                    "java", "-jar", yuiJar,
                    inFile.getAbsolutePath(),
                    "-o", outFile.getAbsolutePath(),
                    "--type", scriptType,
                    "--charset", "utf-8");

            if (options != null && options.length > 0) {
                args.addAll(Arrays.asList(options));
            }

            final ProcessBuilder pb = new ProcessBuilder(args);

            final Result res = ProcessExecutor.executeProcess(pb);

            if (res.wasException()) {
                final Exception exc = res.getException();
                if (exc instanceof IOException) {
                    throw (IOException) exc;
                } else {
                    throw new IOException(exc);
                }
            }

            if (!res.wasSuccessfull()) {
                throw new ExecutionException(res);
            }

            return Files.toByteArray(outFile);

        } finally {
            inFile.delete();
            outFile.delete();
        }
    }
}
