package de.fu.mi.scuttle.lib.modules;

import static de.fu.mi.scuttle.lib.util.UtilityMethods.map;
import static de.fu.mi.scuttle.lib.util.UtilityMethods.pair;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import com.google.common.base.Charsets;
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

/**
 * This Handler fetches templates for the front end modules.
 * 
 * @author Julian Fleischer
 * @since 2013-09-19
 */
@Crucial
@MountPoint("libs")
public class Libs extends AbstractScuttleModule<ScuttleServlet> {

	private byte[] bytes;
	private byte[] bytesCompressed;

	public Libs(final ScuttleServlet parent) throws Exception {
		super(parent);

		init();
	}

	private void init() throws Exception {

		final String path = parent().getRealPath(
				parent().getConfig().optString("jsLibDir", "/js/lib"));
		final File libDir = new File(path);

		final String[] libs = libDir.list(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.endsWith(".js") && !name.startsWith("_");
			}
		});
		Arrays.sort(libs);

		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < libs.length; i++) {
			final String fileName = libs[i];
			builder.append(Files.toString(new File(libDir, fileName),
					Charsets.UTF_8));
			builder.append("\n");
		}
		final String concatenatedScripts = builder.toString();
		builder.setLength(0);

		final boolean compress = parent().getConfig().optBoolean(
				"compressLibs", true)
				&& !parent().getMeta().isDebugBuild();
		if (compress) {
			final String yuiJar = parent().getConfig().optString(
					"yuiCompressorJar", "js/yuicompressor-2.4.8.jar");
			bytes = YuiCompressor.compressJs(parent().getRealPath(yuiJar),
					concatenatedScripts);
		} else {
			bytes = concatenatedScripts.getBytes(Charsets.UTF_8);
		}
		bytesCompressed = UtilityMethods.gzipCompress(bytes);
	}

	@Override
	public ScuttleResponse handle(final ScuttleRequest req) throws Exception {
		if (req.acceptsGzip()) {
			return new PlainResponse(bytesCompressed, map(
					pair("Content-Encoding", "gzip"),
					pair("Content-Type",
							"application/javascript; charset=UTF-8"),
					pair("Cache-Control", "public")));
		} else {
			return new PlainResponse(bytes, map(
					pair("Content-Type",
							"application/javascript; charset=UTF-8"),
					pair("Cache-Control", "public")));
		}
	}
}
