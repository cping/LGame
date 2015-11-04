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
package loon;

import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

public abstract class Assets {

	protected static String pathPrefix = "assets/";

	public void setPathPrefix(String prefix) {
		if (prefix.startsWith("/") || prefix.endsWith("/")) {
			throw new IllegalArgumentException(
					"Prefix must not start or end with '/'.");
		}
		pathPrefix = (prefix.length() == 0) ? prefix : (prefix + "/");
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	protected static final String[] SUFFIXES = { ".wav", ".mp3" };

	protected final Asyn asyn;

	public Image getImageSync(String path) {
		ImageImpl image = createImage(false, 0, 0, path);
		try {
			image.succeed(load(path));
		} catch (Exception e) {
			image.fail(e);
		}
		return image;
	}

	public Image getImage(final String path) {
		final ImageImpl image = createImage(true, 0, 0, path);
		asyn.invokeAsync(new Runnable() {
			public void run() {
				try {
					image.succeed(load(path));
				} catch (Exception e) {
					image.fail(e);
				}
			}
		});
		return image;
	}

	public Image getRemoteImage(String url) {
		return getRemoteImage(url, 0, 0);
	}

	public Image getRemoteImage(String url, int width, int height) {
		Exception error = new Exception(
				"Remote image loading not yet supported: " + url + "@" + width
						+ "x" + height);
		ImageImpl image = createImage(false, width, height, url);
		image.fail(error);
		return image;
	}

	public abstract Sound getSound(String path);

	public Sound getMusic(String path) {
		return getSound(path);
	}

	public abstract String getTextSync(String path) throws Exception;

	public GoFuture<String> getText(final String path) {
		final GoPromise<String> result = asyn.deferredPromise();
		asyn.invokeAsync(new Runnable() {
			public void run() {
				try {
					result.succeed(getTextSync(path));
				} catch (Throwable t) {
					result.fail(t);
				}
			}
		});
		return result;
	}

	public abstract byte[] getBytesSync(String path) throws Exception;

	public GoFuture<byte[]> getBytes(final String path) {
		final GoPromise<byte[]> result = asyn.deferredPromise();
		asyn.invokeAsync(new Runnable() {
			public void run() {
				try {
					result.succeed(getBytesSync(path));
				} catch (Throwable t) {
					result.fail(t);
				}
			}
		});
		return result;
	}

	protected Assets(Asyn s) {
		this.asyn = s;
	}

	protected abstract ImageImpl.Data load(String path) throws Exception;

	protected abstract ImageImpl createImage(boolean async, int rawWidth,
			int rawHeight, String source);

	protected static String getPath(String path) {
		if (path.indexOf(pathPrefix) == -1) {
			path = pathPrefix + path;
		}
		int pathLen;
		do {
			pathLen = path.length();
			path = path.replaceAll("[^/]+/\\.\\./", "");
		} while (path.length() != pathLen);
		return path.replace("\\", "/");
	}
}
