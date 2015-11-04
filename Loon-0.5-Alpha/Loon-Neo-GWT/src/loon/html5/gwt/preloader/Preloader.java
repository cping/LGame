/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.html5.gwt.preloader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import loon.LRelease;
import loon.html5.gwt.GWTResourcesLoader;
import loon.html5.gwt.GWTResources.FileType;
import loon.html5.gwt.preloader.AssetFilter.AssetType;
import loon.html5.gwt.preloader.IDownloader.AssetLoaderListener;
import loon.utils.Array;
import loon.utils.ObjectMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;

public class Preloader implements LRelease {

	public interface PreloaderCallback {

		public void update(PreloaderState state);

		public void error(String file);
	}

	public ObjectMap<String, Void> directories = new ObjectMap<String, Void>();
	public ObjectMap<String, ImageElement> images = new ObjectMap<String, ImageElement>();
	public ObjectMap<String, Void> audio = new ObjectMap<String, Void>();
	public ObjectMap<String, String> texts = new ObjectMap<String, String>();
	public ObjectMap<String, Blob> binaries = new ObjectMap<String, Blob>();

	public static class Asset {
		public Asset(String url, AssetType type, long size, String mimeType) {
			this.url = url;
			this.type = type;
			this.size = size;
			this.mimeType = mimeType;
		}

		public boolean succeed;
		public boolean failed;
		public long loaded;
		public final String url;
		public final AssetType type;
		public final long size;
		public final String mimeType;
	}

	public static class PreloaderState {

		public PreloaderState(Array<Asset> assets) {
			this.assets = assets;
		}

		public long getDownloadedSize() {
			long size = 0;
			for (int i = 0; i < assets.size(); i++) {
				Asset asset = assets.get(i);
				size += (asset.succeed || asset.failed) ? asset.size : Math
						.min(asset.size, asset.loaded);
			}
			return size;
		}

		public long getTotalSize() {
			long size = 0;
			for (int i = 0; i < assets.size(); i++) {
				Asset asset = assets.get(i);
				size += asset.size;
			}
			return size;
		}

		public float getProgress() {
			long total = getTotalSize();
			return total == 0 ? 1 : (getDownloadedSize() / (float) total);
		}

		public boolean hasEnded() {
			return getDownloadedSize() == getTotalSize();
		}

		public final Array<Asset> assets;

	}

	public final String baseUrl;

	public final LocalAssetResources localRes;

	public IDownloader loader = null;

	public Preloader(String newBaseURL, LocalAssetResources res) {
		baseUrl = newBaseURL;
		localRes = res;
		GWT.create(PreloaderBundle.class);
	}

	public void preload(final String assetFileUrl,
			final PreloaderCallback callback) {
		if (localRes == null) {
			loader = new AssetDownloader();
		} else {
			loader = new LocalAssetDownloader(localRes);
		}
		loader.loadText(baseUrl + assetFileUrl,
				new AssetLoaderListener<String>() {
					@Override
					public void onProgress(double amount) {
					}

					@Override
					public void onFailure() {
						callback.error(assetFileUrl);
					}

					@Override
					public void onSuccess(String result) {
						Array<Asset> assets = new Array<Asset>();
						boolean inline = result.startsWith("list:");
						if (inline) {
							String context = result.substring(5,
									result.length());
							String[] lines = context.split(";");
							for (String line : lines) {
								String[] tokens = line.split(":");
								if (tokens.length != 4) {
									throw new RuntimeException(
											"Invalid assets description file.");
								}
								AssetType type = AssetType.Text;
								if (tokens[0].equals("i"))
									type = AssetType.Image;
								if (tokens[0].equals("b"))
									type = AssetType.Binary;
								if (tokens[0].equals("a"))
									type = AssetType.Audio;
								if (tokens[0].equals("d"))
									type = AssetType.Directory;
								long size = Long.parseLong(tokens[2]);
								if (type == AssetType.Audio
										&& !loader.isUseBrowserCache()) {
									size = 0;
								}
								assets.add(new Asset(tokens[1].trim(), type,
										size, tokens[3]));
							}
						} else {
							String[] lines = result.split("\n");
							for (String line : lines) {
								String[] tokens = line.split(":");
								if (tokens.length != 4) {
									throw new RuntimeException(
											"Invalid assets description file.");
								}
								AssetType type = AssetType.Text;
								if (tokens[0].equals("i"))
									type = AssetType.Image;
								if (tokens[0].equals("b"))
									type = AssetType.Binary;
								if (tokens[0].equals("a"))
									type = AssetType.Audio;
								if (tokens[0].equals("d"))
									type = AssetType.Directory;
								long size = Long.parseLong(tokens[2]);
								if (type == AssetType.Audio
										&& !loader.isUseBrowserCache()) {
									size = 0;
								}
								assets.add(new Asset(tokens[1].trim(), type,
										size, tokens[3]));
							}
						}
						final PreloaderState state = new PreloaderState(assets);
						for (int i = 0; i < assets.size(); i++) {
							final Asset asset = assets.get(i);

							if (contains(asset.url)) {
								asset.loaded = asset.size;
								asset.succeed = true;
								continue;
							}
							loader.load(inline ? asset.url : baseUrl
									+ asset.url, asset.type, asset.mimeType,
									new AssetLoaderListener<Object>() {
										@Override
										public void onProgress(double amount) {
											asset.loaded = (long) amount;
											callback.update(state);
										}

										@Override
										public void onFailure() {
											asset.failed = true;
											callback.error(asset.url);
											callback.update(state);
										}

										@Override
										public void onSuccess(Object result) {
											switch (asset.type) {
											case Text:
												texts.put(asset.url,
														(String) result);
												break;
											case Image:
												images.put(asset.url,
														(ImageElement) result);
												break;
											case Binary:
												binaries.put(asset.url,
														(Blob) result);
												break;
											case Audio:
												audio.put(asset.url, null);
												break;
											case Directory:
												directories
														.put(asset.url, null);
												break;
											}
											asset.succeed = true;
											callback.update(state);
										}
									});
						}

						callback.update(state);
						// loader.clear();
					}
				});
	}

	public InputStream read(String url) {
		if (texts.containsKey(url)) {
			try {
				return new ByteArrayInputStream(texts.get(url)
						.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		if (images.containsKey(url)) {
			return new ByteArrayInputStream(new byte[1]);
		}
		if (binaries.containsKey(url)) {
			return binaries.get(url).read();
		}
		if (audio.containsKey(url)) {
			return new ByteArrayInputStream(new byte[1]);
		}
		return null;
	}

	public boolean contains(String url) {
		return texts.containsKey(url) || images.containsKey(url)
				|| binaries.containsKey(url) || audio.containsKey(url)
				|| directories.containsKey(url);
	}

	public boolean isText(String url) {
		return texts.containsKey(url);
	}

	public boolean isImage(String url) {
		return images.containsKey(url);
	}

	public boolean isBinary(String url) {
		return binaries.containsKey(url);
	}

	public boolean isAudio(String url) {
		return audio.containsKey(url);
	}

	public boolean isDirectory(String url) {
		return directories.containsKey(url);
	}

	private boolean isChild(String path, String url) {
		return path.startsWith(url)
				&& (path.indexOf('/', url.length() + 1) < 0);
	}

	public GWTResourcesLoader[] list(String url) {
		ArrayList<GWTResourcesLoader> files = new ArrayList<GWTResourcesLoader>();
		for (String path : texts.keys()) {
			if (isChild(path, url)) {
				files.add(new GWTResourcesLoader(this, path, FileType.Internal));
			}
		}
		GWTResourcesLoader[] list = new GWTResourcesLoader[files.size()];
		System.arraycopy(files.toArray(), 0, list, 0, list.length);
		return list;
	}

	public GWTResourcesLoader[] list(String url, FileFilter filter) {
		ArrayList<GWTResourcesLoader> files = new ArrayList<GWTResourcesLoader>();
		for (String path : texts.keys()) {
			if (isChild(path, url) && filter.accept(new File(path))) {
				files.add(new GWTResourcesLoader(this, path, FileType.Internal));
			}
		}
		GWTResourcesLoader[] list = new GWTResourcesLoader[files.size()];
		System.arraycopy(files.toArray(), 0, list, 0, list.length);
		return list;
	}

	public GWTResourcesLoader[] list(String url, FilenameFilter filter) {
		ArrayList<GWTResourcesLoader> files = new ArrayList<GWTResourcesLoader>();
		for (String path : texts.keys()) {
			if (isChild(path, url)
					&& filter.accept(new File(url),
							path.substring(url.length() + 1))) {
				files.add(new GWTResourcesLoader(this, path, FileType.Internal));
			}
		}
		GWTResourcesLoader[] list = new GWTResourcesLoader[files.size()];
		System.arraycopy(files.toArray(), 0, list, 0, list.length);
		return list;
	}

	public GWTResourcesLoader[] list(String url, String suffix) {
		ArrayList<GWTResourcesLoader> files = new ArrayList<GWTResourcesLoader>();
		for (String path : texts.keys()) {
			if (isChild(path, url) && path.endsWith(suffix)) {
				files.add(new GWTResourcesLoader(this, path, FileType.Internal));
			}
		}
		GWTResourcesLoader[] list = new GWTResourcesLoader[files.size()];
		System.arraycopy(files.toArray(), 0, list, 0, list.length);
		return list;
	}

	public long length(String url) {
		if (texts.containsKey(url)) {
			try {
				return texts.get(url).getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				return texts.get(url).getBytes().length;
			}
		}
		if (images.containsKey(url)) {
			return 1;
		}
		if (binaries.containsKey(url)) {
			return binaries.get(url).length();
		}
		if (audio.containsKey(url)) {
			return 1;
		}
		return 0;
	}

	@Override
	public void close() {
		if (loader != null) {
			loader.clear();
		}
	}

}
