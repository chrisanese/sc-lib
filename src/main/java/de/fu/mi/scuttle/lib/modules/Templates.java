package de.fu.mi.scuttle.lib.modules;

import static de.fu.mi.scuttle.lib.util.UtilityMethods.map;
import static de.fu.mi.scuttle.lib.util.UtilityMethods.pair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.fu.mi.scuttle.lib.util.UtilityMethods;
import de.fu.mi.scuttle.lib.web.AbstractScuttleModule;
import de.fu.mi.scuttle.lib.web.Crucial;
import de.fu.mi.scuttle.lib.web.JSONResponse;
import de.fu.mi.scuttle.lib.web.MountPoint;
import de.fu.mi.scuttle.lib.web.PlainResponse;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleResponse;
import de.fu.mi.scuttle.lib.web.ScuttleServlet;

/**
 * 
 * @author Julian Fleischer
 */
@Crucial
@MountPoint("templates")
public class Templates extends AbstractScuttleModule<ScuttleServlet> {

    private final Map<String, TemplatesData> templates = new HashMap<>();

    private class TemplatesData {

        private final File dir;
        private byte[] bytes;
        private byte[] bytesCompressed;

        public TemplatesData(final File dir) throws Exception {
            this.dir = dir;

            if (!parent().getMeta().isDebugBuild()) {
                load();
            }
        }

        private void load() throws Exception {
            final File[] templates = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String fileName) {
                    return fileName.endsWith(".mustache");
                }
            });
            final File[] partialTemplates = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String fileName) {
                    return fileName.endsWith(".mustachep");
                }
            });
            final JSONObject templatesData = new JSONObject();
            final JSONObject jsonTemplates = new JSONObject();
            final JSONObject jsonPartials = new JSONObject();

            for (final File template : templates) {
                final String content = Files.toString(template, Charsets.UTF_8);
                String name = template.getName();
                name = name.substring(0, name.length() - 9);
                jsonTemplates.put(name, content);
            }
            for (final File template : partialTemplates) {
                final String content = Files.toString(template, Charsets.UTF_8);
                String name = template.getName();
                name = name.substring(0, name.length() - 10);
                jsonPartials.put(name, content);
            }
            templatesData.put("templates", jsonTemplates);
            templatesData.put("partials", jsonPartials);

            final ByteArrayOutputStream data = new ByteArrayOutputStream();
            final OutputStreamWriter dataWriter = new OutputStreamWriter(
                    data, Charsets.UTF_8);
            templatesData.write(dataWriter);
            dataWriter.close();
            bytes = data.toByteArray();
            bytesCompressed = UtilityMethods.gzipCompress(bytes);
        }

        public byte[] getBytes() throws Exception {
            if (parent().getMeta().isDebugBuild()) {
                load();
            }
            return bytes;
        }

        public byte[] getBytesCompressed() throws Exception {
            if (parent().getMeta().isDebugBuild()) {
                load();
            }
            return bytesCompressed;
        }
    }

    private final JSONObject emptyData = new JSONObject() {
        {
            this.put("templates", new JSONObject());
            this.put("partials", new JSONObject());
        }
    };

    public Templates(final ScuttleServlet parent) throws Exception {
        super(parent);

        init();
    }

    private void init() throws Exception {

        final String path = parent().getRealPath(
                parent().getConfig().optString(
                        "templatesDir", "/templates"));

        final File templatesDir = new File(path);
        final File[] templateDirs = templatesDir
                .listFiles(new FileFilter() {
                    @Override
                    public boolean accept(final File file) {
                        return file.isDirectory();
                    }
                });

        for (final File dir : templateDirs) {
            this.templates.put(dir.getName(), new TemplatesData(dir));
        }

    }

    @Override
    public ScuttleResponse handle(final ScuttleRequest req) throws Exception {

        final TemplatesData data = templates.get(req.getPath());
        if (data == null) {
            return new JSONResponse(emptyData);
        } else {
            if (req.acceptsGzip()) {
                return new PlainResponse(data.getBytesCompressed(), map(
                        pair("Content-Encoding", "gzip"),
                        pair("Content-Type", "application/json"),
                        pair("Cache-Control", parent().getMeta().isDebugBuild()
                                ? "no-cache" : "public")));
            } else {
                return new PlainResponse(data.getBytes(), map(
                        pair("Content-Type", "application/json"),
                        pair("Cache-Control", parent().getMeta().isDebugBuild()
                                ? "no-cache" : "public")));
            }
        }
    }

}
