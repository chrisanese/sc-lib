package de.fu.mi.scuttle.lib.modules;

import static de.fu.mi.scuttle.lib.util.UtilityMethods.map;
import static de.fu.mi.scuttle.lib.util.UtilityMethods.pair;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;

import de.fu.mi.scuttle.lib.util.UtilityMethods;
import de.fu.mi.scuttle.lib.util.YuiCompressor;
import de.fu.mi.scuttle.lib.web.AbstractScuttleModule;
import de.fu.mi.scuttle.lib.web.Crucial;
import de.fu.mi.scuttle.lib.web.MountPoint;
import de.fu.mi.scuttle.lib.web.PlainResponse;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleResponse;
import de.fu.mi.scuttle.lib.web.ScuttleServlet;

@Crucial
@MountPoint("styles")
public class Styles extends AbstractScuttleModule<ScuttleServlet> {

    private byte[] bytes;
    private byte[] bytesCompressed;

    public Styles(final ScuttleServlet parent) throws Exception {
        super(parent);

        if (!parent().getMeta().isDebugBuild()) {
            init();
        }
    }

    private static String makeDataUrls(final File cssDir, String stylesheet)
            throws IOException {
        final Matcher matcher = Pattern.compile("url\\(\"?([^\"\\)]+)\"?\\)")
                .matcher(stylesheet);
        while (matcher.find()) {
            final String whole = matcher.group();
            final String url = matcher.group(1);
            if (url.startsWith("data:")) {
                continue;
            }
            final File image = new File(cssDir, url);
            if (image.length() > 1500) {
                continue;
            }
            String base64 = Base64.encodeBase64String(Files.toByteArray(image))
                    .replace(" ", "").replace("\n", "");
            if (url.endsWith(".gif")) {
                base64 = "data:image/gif;base64," + base64;
            } else if (url.endsWith(".jpg") || url.endsWith(".jpeg")) {
                base64 = "data:image/jpeg;base64," + base64;
            } else if (url.endsWith(".png")) {
                base64 = "data:image/png;base64," + base64;
            } else {
                continue;
            }
            stylesheet = stylesheet.replace(whole, "url(" + base64 + ")");
        }
        return stylesheet;
    }

    private void init() throws Exception {

        final String path = parent().getRealPath("/css");
        final File cssDir = new File(path);

        final String[] cssFiles = cssDir.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".css") && !name.startsWith("_");
            }
        });
        final String[] lessFiles = cssDir.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".less") && !name.startsWith("_");
            }
        });
        Arrays.sort(cssFiles);
        Arrays.sort(lessFiles);

        final StringBuilder lessBuilder = new StringBuilder();
        for (final String file : lessFiles) {
            lessBuilder.append(Files.toString(new File(cssDir, file),
                    Charsets.UTF_8));
            lessBuilder.append("\n");
        }
        final String lessCss = compileLess(lessBuilder.toString());

        final StringBuilder cssBuilder = new StringBuilder();
        for (final String file : cssFiles) {
            cssBuilder.append(Files.toString(new File(cssDir, file),
                    Charsets.UTF_8));
            cssBuilder.append("\n");
        }
        cssBuilder.append(lessCss);
        final String stylesheet = makeDataUrls(cssDir, cssBuilder.toString());
        cssBuilder.setLength(0);

        if (parent().getConfig().optBoolean("compressCss", true)) {
            final String yuiJar = parent().getConfig().optString(
                    "yuiCompressorJar", "js/yuicompressor-2.4.8.jar");
            bytes = YuiCompressor.compressCss(
                    parent().getRealPath(yuiJar),
                    stylesheet);
        } else {
            bytes = stylesheet.getBytes(Charsets.UTF_8);
        }
        bytesCompressed = UtilityMethods.gzipCompress(bytes);
    }

    String compileLess(final String less) throws ScriptException,
            IOException, NoSuchMethodException {
        if (Strings.isNullOrEmpty(less)) {
            return "";
        }

        final ScriptEngine engine = new ScriptEngineManager()
                .getEngineByExtension("js");
        final Invocable iEngine = (Invocable) engine;

        engine.eval("exports = function () {}");

        try (Reader lessRhino = new InputStreamReader(getClass()
                .getResourceAsStream(
                        "less-rhino.js"), Charsets.UTF_8)) {
            engine.eval(lessRhino);
        }

        try (Reader less150 = new InputStreamReader(
                getClass().getResourceAsStream(
                        "less-1.5.0.js"), Charsets.UTF_8)) {
            engine.eval(less150);
        }

        try (Reader lessAdapter = new InputStreamReader(
                getClass().getResourceAsStream(
                        "less-adapter.js"), Charsets.UTF_8)) {
            engine.eval(lessAdapter);
        }

        return iEngine.invokeFunction("doCompileLess", less).toString();
    }

    @Override
    public ScuttleResponse handle(final ScuttleRequest req) throws Exception {
        if (parent().getMeta().isDebugBuild()) {
            init();
        }

        if (req.acceptsGzip()) {
            return new PlainResponse(bytesCompressed, map(
                    pair("Content-Encoding", "gzip"),
                    pair("Content-Type", "text/css; charset=UTF-8"),
                    pair("Cache-Control", "public")));
        } else {
            return new PlainResponse(bytes, map(
                    pair("Content-Type", "text/css; charset=UTF-8"),
                    pair("Cache-Control", "public")));
        }
    }

}
