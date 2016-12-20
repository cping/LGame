package loon.html5.gwt.preloader;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.linker.CrossSiteIframeLinker;

public class AssetCacheLinker extends CrossSiteIframeLinker {

	private static final HashSet<String> DEFAULT_EXTENSION_WHITELIST = new HashSet<String>(
			Arrays.asList(new String[] { "js", "an", "cfg", "pack", "fnt",
					"txt", "text", "atlas", "html", "bmp", "jpg", "jpeg",
					"png", "gif", "mp3", "ogg", "mov", "avi", "wmv", "wav",
					"webm", "css", "xml", "json", "flv", "swf" }));

	private static final String MANIFEST = "assetcache.nocache.manifest";

	@Override
	public String getDescription() {
		return "AssetCacheLinker";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context,
			ArtifactSet artifacts) throws UnableToCompleteException {
		ArtifactSet toReturn = super.link(logger, context, artifacts);

		toReturn.add(emitLandingPageCacheManifest(context, logger, toReturn,
				staticCachedFiles()));

		return toReturn;
	}

	protected boolean accept(String path) {

		if (path.equals("hosted.html") || path.endsWith(".devmode.js")) {
			return false;
		}

		if (path.equals("/")) {
			return true;
		}
		int pos = path.lastIndexOf('.');
		if (pos != -1) {
			String extension = path.substring(pos + 1);
			if (DEFAULT_EXTENSION_WHITELIST.contains(extension)) {
				return true;
			}
		}
		return false;
	}

	private Artifact<?> emitLandingPageCacheManifest(LinkerContext context,
			TreeLogger logger, ArtifactSet artifacts, String[] staticFiles)
			throws UnableToCompleteException {

		StringBuilder publicSourcesSb = new StringBuilder();
		StringBuilder publicStaticSourcesSb = new StringBuilder();

		for (@SuppressWarnings("rawtypes")
		Artifact artifact : artifacts) {
			if (artifact instanceof EmittedArtifact) {
				EmittedArtifact ea = (EmittedArtifact) artifact;
				String path = "/" + context.getModuleFunctionName() + "/"
						+ ea.getPartialPath();

				if (accept(path)) {
					publicSourcesSb.append(path + "\n");
				}
			}
		}

		if (staticFiles != null) {
			for (String staticFile : staticFiles) {
				if (accept(staticFile)) {
					publicStaticSourcesSb.append(staticFile + "\n");
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("CACHE MANIFEST\n");
		sb.append("# Unique id #" + (new Date()).getTime() + "."
				+ Math.random() + "\n");
		sb.append("# Note: must change this every time for cache to invalidate\n");
		sb.append("\n");
		sb.append("CACHE:\n");
		sb.append(publicSourcesSb.toString());
		sb.append("# Static cached files\n");
		sb.append(publicStaticSourcesSb.toString());
		sb.append("\n\n");
		sb.append("# All other resources require the user to be online.\n");
		sb.append("NETWORK:\n");
		sb.append("*\n");

		logger.log(
				TreeLogger.DEBUG,
				"Make sure you have the following"
						+ " attribute added to your landing page's <html> tag: <html manifest=\""
						+ context.getModuleFunctionName() + "/" + MANIFEST
						+ "\">");

		return emitString(logger, sb.toString(), MANIFEST);
	}

	protected String[] staticCachedFiles() {
		return null;
	}
}