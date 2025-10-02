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
import loon.utils.ArrayByte;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;
import loon.utils.res.ResourceLocal;

public abstract class Assets {

	private final TArray<Sound> soundCache = new TArray<Sound>(LSystem.DEFAULT_MAX_CACHE_SIZE);

	// 为了方便直接转码到C#和C++，无法使用匿名内部类(也就是在构造内直接构造实现的方式)，只能都写出类来……
	// PS:别提delegate，委托那玩意写出来太不优雅了，而且大多数J2C#的工具也不能直接转换过去……
	private static class ImageRunnable implements Runnable {

		private final ImageImpl _impl;

		private final String _path;

		private final Assets _assets;

		ImageRunnable(ImageImpl img, String path, Assets assets) {
			this._impl = img;
			this._path = path;
			this._assets = assets;
		}

		@Override
		public void run() {
			try {
				_impl.succeed(_assets.load(_path));
			} catch (Throwable e) {
				_impl.fail(e);
			}
		}

	}

	private static class TextRunnable implements Runnable {

		private final GoPromise<String> _result;

		private final String _path;

		private final Assets _assets;

		TextRunnable(GoPromise<String> res, String path, Assets assets) {
			this._result = res;
			this._path = path;
			this._assets = assets;
		}

		@Override
		public void run() {
			try {
				_result.succeed(_assets.getTextSync(_path));
			} catch (Throwable t) {
				_result.fail(t);
			}
		}

	}

	private static class ByteRunnable implements Runnable {

		private final GoPromise<byte[]> _result;

		private final String _path;

		private final Assets _assets;

		ByteRunnable(GoPromise<byte[]> res, String path, Assets assets) {
			this._result = res;
			this._path = path;
			this._assets = assets;
		}

		@Override
		public void run() {
			try {
				_result.succeed(_assets.getBytesSync(_path));
			} catch (Throwable t) {
				_result.fail(t);
			}
		}
	}

	public void setPathPrefixEmpty() {
		setPathPrefix(LSystem.EMPTY);
	}

	public void setPathPrefix(String prefix) {
		if (StringUtils.isEmpty(prefix)) {
			LSystem.setPathPrefix(LSystem.EMPTY);
			return;
		}
		if (prefix.startsWith("/") || prefix.endsWith("/")) {
			throw new LSysException("Prefix must not start or end with '/'.");
		}
		LSystem.setPathPrefix((prefix.length() == 0) ? prefix : (prefix + LSystem.SLASH));
	}

	public String getPathPrefix() {
		return LSystem.getPathPrefix();
	}

	protected static final String[] SUFFIXES = { ".wav", ".mp3", ".ogg", ".m4a", ".aac" };

	protected final Asyn asyn;

	public final ResourceLocal getJsonResource(String path) {
		return new ResourceLocal(path);
	}

	public Image getImageSync(String path) {
		ImageImpl image = createImage(false, 0, 0, path);
		try {
			image.succeed(load(path));
		} catch (Throwable e) {
			image.fail(e);
		}
		return image;
	}

	public Image getImage(final String path) {
		final ImageImpl image = createImage(true, 0, 0, path);
		asyn.invokeAsync(new ImageRunnable(image, path, this));
		return image;
	}

	public Image getRemoteImage(String url) {
		return getRemoteImage(url, 0, 0);
	}

	public Image getRemoteImage(String url, int width, int height) {
		LSysException error = new LSysException(
				"Remote image loading not yet supported: " + url + "@" + width + "x" + height);
		ImageImpl image = createImage(false, width, height, url);
		image.fail(error);
		return image;
	}

	public abstract Sound getSound(String path);

	public Sound getMusic(String path) {
		Sound sound = getSound(path);
		soundCache.add(sound);
		return sound;
	}

	public abstract String getTextSync(String path) throws Exception;

	public GoFuture<String> getText(final String path) {
		final GoPromise<String> result = asyn.deferredPromise();
		asyn.invokeAsync(new TextRunnable(result, path, this));
		return result;
	}

	public ArrayByte getArrayByte(String path) throws Exception {
		return new ArrayByte(getBytesSync(path));
	}

	public abstract byte[] getBytesSync(String path) throws Exception;

	public GoFuture<byte[]> getBytes(final String path) {
		final GoPromise<byte[]> result = asyn.deferredPromise();
		asyn.invokeAsync(new ByteRunnable(result, path, this));
		return result;
	}

	protected Assets(Asyn s) {
		this.asyn = s;
	}

	protected abstract ImageImpl.Data load(String path) throws Exception;

	protected abstract ImageImpl createImage(boolean async, int rawWidth, int rawHeight, String source);

	protected static String getPath(String path) {
		final String configPath = LSystem.getPathPrefix();
		final String fixPath = StringUtils.isEmpty(configPath) ? LSystem.EMPTY : configPath;
		if (path.indexOf(fixPath) == -1) {
			path = fixPath + path;
		}
		int pathLen;
		do {
			pathLen = path.length();
			path = path.replaceAll("[^/]+/\\.\\./", LSystem.EMPTY);
		} while (path.length() != pathLen);
		path = path.replace("\\", "/");
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	public void close() {
		for (Sound s : soundCache) {
			if (s != null) {
				s.stop();
				s.release();
			}
		}
	}
}
