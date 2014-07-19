package de.fu.mi.scuttle.lib.modules;

import static de.fu.mi.scuttle.lib.util.UtilityMethods.map;
import static de.fu.mi.scuttle.lib.util.UtilityMethods.pair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.fu.mi.scuttle.lib.util.Pair;
import de.fu.mi.scuttle.lib.util.UtilityMethods;
import de.fu.mi.scuttle.lib.util.YuiCompressor;
import de.fu.mi.scuttle.lib.web.AbstractScuttleModule;
import de.fu.mi.scuttle.lib.web.Crucial;
import de.fu.mi.scuttle.lib.web.MountPoint;
import de.fu.mi.scuttle.lib.web.PlainResponse;
import de.fu.mi.scuttle.lib.web.ScuttleRequest;
import de.fu.mi.scuttle.lib.web.ScuttleResponse;
import de.fu.mi.scuttle.lib.web.ScuttleServlet;

/**
 * This Handler fetches templates for the front end modules.
 * 
 * @author Julian Fleischer
 * @since 2013-10-19
 */
@Crucial
@MountPoint("scripts")
public class Scripts extends AbstractScuttleModule<ScuttleServlet> {

	private final Map<String, ScriptData> scriptData = new HashMap<>();
	Mustache template;

	public Scripts(final ScuttleServlet parent) throws Exception {
		super(parent);

		init();
	}

	private class ScriptData {

		private final File moduleDir;
		private byte[] bytes;
		private byte[] bytesCompressed;

		public ScriptData(final File moduleDir) throws IOException {
			this.moduleDir = moduleDir;
			if (!parent().getMeta().isDebugBuild()) {
				load();
			}
		}

		public byte[] getBytes() throws IOException {
			if (parent().getMeta().isDebugBuild()) {
				load();
			}
			return bytes;
		}

		public byte[] getBytesCompressed() throws IOException {
			if (parent().getMeta().isDebugBuild()) {
				load();
			}
			return bytesCompressed;
		}

		private Pair<File[], Integer> findFiles() {
			final String[] moduleFiles = moduleDir.list(new FilenameFilter() {
				@Override
				public boolean accept(final File dir, final String name) {
					return name.endsWith(".js");
				}
			});
			Arrays.sort(moduleFiles);

			final File[] files = new File[moduleFiles.length];
			int size = 2 * files.length;
			for (int i = 0; i < files.length; i++) {
				files[i] = new File(moduleDir, moduleFiles[i]);
				size += (int) files[i].length();
			}
			return pair(files, size);
		}

		private String makeModule(final String script) throws IOException {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try (OutputStreamWriter writer = new OutputStreamWriter(out,
					Charsets.UTF_8)) {
				template.execute(
						writer,
						map(pair("modName", moduleDir.getName()),
								pair("source", script)));
			}
			final String module = new String(out.toByteArray(), Charsets.UTF_8);
			return module;
		}

		private void load() throws IOException {
			final Pair<File[], Integer> filesAndSize = findFiles();
			final File[] files = filesAndSize.fst();
			final int size = filesAndSize.snd();

			final StringBuilder scripts = new StringBuilder(size);
			for (final File file : files) {
				scripts.append(Files.toString(file, Charsets.UTF_8));
			}
			final String script = makeModule(scripts.toString());

			final boolean compress = parent().getConfig().optBoolean(
					"compressJs", true)
					&& !parent().getMeta().isDebugBuild();
			if (compress) {
				final String yuiJar = parent().getConfig().optString(
						"yuiCompressorJar", "js/yuicompressor-2.4.8.jar");
				bytes = YuiCompressor.compressJs(parent().getRealPath(yuiJar),
						script);
			} else {
				bytes = script.getBytes(Charsets.UTF_8);
			}
			bytesCompressed = UtilityMethods.gzipCompress(bytes);
		}
	}

	private static Mustache loadModuleTemplate() throws IOException {
		final InputStream moduleDef = Scripts.class
				.getResourceAsStream("module.js");
		try (Reader reader = new InputStreamReader(moduleDef)) {
			final DefaultMustacheFactory factory = new DefaultMustacheFactory();
			return factory.compile(reader, "module");
		}
	}

	private void init() throws Exception {

		template = loadModuleTemplate();

		final String path = parent().getRealPath(
				parent().getConfig().optString("jsModulesDir", "/js/modules"));
		final File jsDir = new File(path);

		final File[] modules = jsDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(final File pathname) {
				return pathname.isDirectory();
			}
		});

		for (final File moduleDir : modules) {
			scriptData.put(moduleDir.getName(), new ScriptData(moduleDir));
		}
	}

	@Override
	public ScuttleResponse handle(final ScuttleRequest req) throws Exception {

		final ScriptData data = scriptData.get(req.getPath());
		if (data == null) {
			return new PlainResponse("");
		} else {
			final String cacheControl = parent().getMeta().isDebugBuild() ? "no-cache"
					: "private";
			if (req.acceptsGzip()) {
				return new PlainResponse(data.getBytesCompressed(), map(
						pair("Content-Encoding", "gzip"),
						pair("Content-Type",
								"application/javascript; charset=utf-8"),
						pair("Cache-Control", cacheControl)));
			} else {
				return new PlainResponse(data.getBytes(), map(
						pair("Content-Type",
								"application/javascript; charset=utf-8"),
						pair("Cache-Control", cacheControl)));
			}
		}
	}

}
