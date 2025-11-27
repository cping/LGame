/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.teavm.assets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.file.File;
import org.teavm.jso.file.FileList;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

import loon.LSystem;
import loon.teavm.Loon;
import loon.teavm.TeaBlob;
import loon.teavm.TeaGame.TeaSetting;
import loon.teavm.TeaResourceLoader;
import loon.teavm.assets.AssetPreloader.FileType;
import loon.teavm.dom.ConvertUtils;
import loon.teavm.dom.DataTransferWrapper;
import loon.teavm.dom.DragEventWrapper;
import loon.teavm.dom.FileReaderWrapper;
import loon.teavm.utils.StreamUtils;
import loon.utils.TArray;

public class AssetLoadImpl implements AssetLoader {

	public int assetTotal = -1;

	private static final String ASSET_FOLDER = LSystem.getPathPrefix();
	private static final String SCRIPTS_FOLDER = "scripts/";

	public final String baseUrl;

	private TArray<QueueAsset> assetInQueue;
	private HashSet<String> assetDownloading;

	private AssetDownloader assetDownloader;

	private int maxMultiDownloadCount = 5;

	private AssetPreloader preloader;

	private Loon loonApp;

	public AssetLoadImpl(AssetPreloader preloader, String newBaseURL, Loon loon, AssetDownloader assetDownloader) {
		this.preloader = preloader;
		this.assetDownloader = assetDownloader;
		loonApp = loon;
		baseUrl = newBaseURL;
		assetInQueue = new TArray<QueueAsset>();
		assetDownloading = new HashSet<String>();
	}

	public Loon getLoonApp() {
		return loonApp;
	}

	public void setupFileDrop(HTMLCanvasElement canvas, Loon loon) {
		TeaSetting config = loon.getConfig();
		if (config.windowListener != null) {
			HTMLDocument document = canvas.getOwnerDocument();
			document.addEventListener("dragenter", new EventListener<Event>() {
				@Override
				public void handleEvent(Event evt) {
					evt.preventDefault();
				}
			}, false);
			document.addEventListener("dragover", new EventListener<Event>() {
				@Override
				public void handleEvent(Event evt) {
					evt.preventDefault();
				}
			}, false);
			document.addEventListener("drop", new EventListener<Event>() {
				@Override
				public void handleEvent(Event evt) {
					evt.preventDefault();
					DragEventWrapper event = (DragEventWrapper) evt;
					DataTransferWrapper dataTransfer = event.getDataTransfer();
					FileList files = dataTransfer.getFiles();
					downloadDroppedFile(config, files);
				}
			});
		}
	}

	private JSPromise<AssetData> getFile(String name, File fileWrapper) {
		JSPromise<AssetData> success = new JSPromise<AssetData>((resolve, reject) -> {
			FileReaderWrapper fileReader = FileReaderWrapper.create();
			fileReader.readAsArrayBuffer(fileWrapper);

			fileReader.addEventListener("load", new EventListener<Event>() {
				@Override
				public void handleEvent(Event evt) {
					FileReaderWrapper target = (FileReaderWrapper) evt.getTarget();
					ArrayBuffer arrayBuffer = target.getResultAsArrayBuffer();
					Int8Array data = new Int8Array(arrayBuffer);
					byte[] bytes = ConvertUtils.toByteArray(data);
					AssetData fielData = new AssetData(name, bytes);
					resolve.accept(fielData);
				}
			});
		});

		return success;
	}

	private void downloadDroppedFile(TeaSetting config, FileList files) {
		int totalDraggedFiles = files.getLength();
		if (totalDraggedFiles > 0) {

			JSArray<JSPromise<AssetData>> promises = new JSArray<JSPromise<AssetData>>();
			for (int i = 0; i < totalDraggedFiles; i++) {
				File fileWrapper = files.get(i);
				String name = fileWrapper.getName();

				if (config.windowListener.acceptFileDropped(name)) {
					JSPromise<AssetData> promiss = getFile(name, fileWrapper);
					promises.push(promiss);
				}
			}

			JSPromise<JSArrayReader<AssetData>> all = JSPromise.all(promises);
			all.then(array -> {
				int length = array.getLength();
				AssetData[] arr = new AssetData[length];
				for (int i = 0; i < length; i++) {
					AssetData fileData = array.get(i);
					arr[i] = fileData;
				}
				config.windowListener.filesDropped(arr);
				return "success";
			}, reason -> {
				return "failure";
			}).onSettled(() -> {
				return null;
			});
		}
	}

	@Override
	public String getAssetUrl() {
		return baseUrl + ASSET_FOLDER;
	}

	@Override
	public String getScriptUrl() {
		return baseUrl + SCRIPTS_FOLDER;
	}

	@Override
	public void preload(final String assetFileUrl, AssetLoaderListener<Void> preloadListener) {
		AssetLoaderListener<TeaBlob> listener = new AssetLoaderListener<TeaBlob>() {
			@Override
			public void onSuccess(String url, TeaBlob result) {
				assetDownloading.remove(assetFileUrl);
				Int8Array data = result.getData();
				byte[] byteArray = ConvertUtils.toByteArray(data);
				String assets = new String(byteArray);
				String[] lines = assets.split("\n");

				assetTotal = lines.length;

				for (String line : lines) {
					String[] tokens = line.split(":");
					if (tokens.length != 5) {
						throw new RuntimeException("Invalid assets description file. " + tokens.length + " " + line);
					}
					String fileTypeStr = tokens[0];
					String assetTypeStr = tokens[1];
					String assetUrl = tokens[2].trim();

					boolean shouldOverwriteLocalData = tokens[4].equals("1");
					assetUrl = assetUrl.trim();
					if (assetUrl.isEmpty()) {
						continue;
					}

					FileType fileType = FileType.Internal;
					if (fileTypeStr.equals("c")) {
						fileType = FileType.Classpath;
					} else if (fileTypeStr.equals("l")) {
						fileType = FileType.Local;
					}
					AssetType assetType = AssetType.Binary;
					if (assetTypeStr.equals("d"))
						assetType = AssetType.Directory;

					addAssetToQueue(assetUrl, assetType, fileType, shouldOverwriteLocalData);
				}
				preloadListener.onSuccess(assetFileUrl, null);
				downloadMultiAssets(null);
			}

			@Override
			public void onFailure(String url) {
				assetDownloading.remove(assetFileUrl);
				System.out.println("ErrorLoading: " + assetFileUrl);
				preloadListener.onFailure(assetFileUrl);
			}
		};

		assetDownloading.add(assetFileUrl);
		assetDownloader.load(true, getAssetUrl() + assetFileUrl, AssetType.Binary, listener);
	}

	@Override
	public boolean isAssetInQueueOrDownloading(String path) {
		String path1 = fixPath(path);
		return assetInQueue(path1) || assetDownloading.contains(path1);
	}

	@Override
	public boolean isAssetLoaded(FileType fileType, String path) {
		String fname = fixPath(path);
		TeaResourceLoader fileHandle = new TeaResourceLoader(preloader, fname, fileType);
		return fileHandle.exists();
	}

	@Override
	public void loadAsset(String path, AssetType assetType, FileType fileType) {
		loadAssetInternal(path, assetType, fileType, null, false);
	}

	@Override
	public void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<TeaBlob> listener) {
		loadAssetInternal(path, assetType, fileType, listener, false);
	}

	@Override
	public void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<TeaBlob> listener,
			boolean overwrite) {
		loadAssetInternal(path, assetType, fileType, listener, overwrite);
	}

	@Override
	public void loadScript(String path) {
		assetDownloader.loadScript(true, getScriptUrl() + path, null);
	}

	@Override
	public void loadScript(String path, AssetLoaderListener<String> listener) {
		assetDownloader.loadScript(true, getScriptUrl() + path, listener);
	}

	@Override
	public int getQueue() {
		return assetInQueue.size;
	}

	@Override
	public int getDownloadingCount() {
		return assetDownloading.size();
	}

	@Override
	public boolean isDownloading() {
		return getQueue() > 0 || getDownloadingCount() > 0;
	}

	private void loadAssetInternal(String path, AssetType assetType, FileType fileType,
			AssetLoaderListener<TeaBlob> listener, boolean overwrite) {
		addAssetToQueue(path, assetType, fileType, overwrite);
		downloadQueueAssets(listener);
	}

	private void addAssetToQueue(String path, AssetType assetType, FileType fileType, boolean overwrite) {
		String newPath = fixPath(path);

		if (newPath.isEmpty()) {
			return;
		}

		if (assetInQueue(newPath)) {
			return;
		}

		TeaResourceLoader res = new TeaResourceLoader(preloader, newPath, fileType);
		boolean exists = res.exists();
		if (!overwrite && exists) {
			return;
		}

		if (assetType == AssetType.Directory) {
			if (!exists) {
				preloader.mkdirs(res);
			}
			return;
		}
		assetInQueue.add(new QueueAsset(newPath, assetType, res));
	}

	private void downloadMultiAssets(AssetLoaderListener<TeaBlob> listener) {
		for (int i = 0; i < maxMultiDownloadCount; i++) {
			downloadQueueAssets(listener);
		}
	}

	private void downloadQueueAssets(AssetLoaderListener<TeaBlob> listener) {
		if (assetInQueue.size == 0 || assetDownloading.size() >= maxMultiDownloadCount) {
			return;
		}
		QueueAsset queueAsset = assetInQueue.removeIndex(0);
		String assetPath = queueAsset.assetUrl;
		TeaResourceLoader res = queueAsset.fileResource;
		assetDownloading.add(assetPath);

		assetDownloader.load(true, getAssetUrl() + assetPath, AssetType.Binary, new AssetLoaderListener<>() {

			@Override
			public void onFailure(String url) {
				assetDownloading.remove(assetPath);
				if (listener != null) {
					listener.onFailure(assetPath);
				}
				downloadMultiAssets(listener);
			}

			@Override
			public void onSuccess(String url, TeaBlob result) {
				assetDownloading.remove(assetPath);
				Int8Array data = result.getData();
				byte[] byteArray = ConvertUtils.toByteArray(data);
				OutputStream output = res.write(false, 4096);
				try {
					output.write(byteArray);
				} catch (IOException ex) {
					throw new RuntimeException("Error writing file: " + res + " (" + res.type() + ")", ex);
				} finally {
					StreamUtils.closeQuietly(output);
				}
				if (listener != null) {
					listener.onSuccess(assetPath, result);
				}
				downloadMultiAssets(listener);
			}
		});
	}

	private boolean assetInQueue(String path) {
		for (int i = 0; i < assetInQueue.size; i++) {
			QueueAsset queueAsset = assetInQueue.get(i);
			if (queueAsset.assetUrl.equals(path)) {
				return true;
			}
		}
		return false;
	}

	private String fixPath(String path1) {
		path1 = path1.trim().replace("\\", "/");
		if (path1.startsWith("/")) {
			path1 = path1.substring(1);
		}
		return path1;
	}
}