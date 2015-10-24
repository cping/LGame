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
package loon.android;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import loon.Assets;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.utils.Scale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.http.AndroidHttpClient;

public class AndroidAssets extends Assets {

	public class BitmapOptions extends BitmapFactory.Options {

		public Scale scale;
	}

	public interface BitmapOptionsAdjuster {

		void adjustOptions(String path, BitmapOptions options);
	}

	private final AndroidGame game;
	private final AssetManager assetMgr;
	private String pathPrefix = "";
	private Scale assetScale = null;

	private BitmapOptionsAdjuster optionsAdjuster = new BitmapOptionsAdjuster() {
		public void adjustOptions(String path, BitmapOptions options) {
		}
	};

	public AndroidAssets(AndroidGame game) {
		super(game.asyn());
		this.game = game;
		this.assetMgr = game.activity.getResources().getAssets();
		this.setPathPrefix("");
	}

	public void setAssetScale(float scaleFactor) {
		this.assetScale = new Scale(scaleFactor);
	}

	public void setBitmapOptionsAdjuster(BitmapOptionsAdjuster optionsAdjuster) {
		this.optionsAdjuster = optionsAdjuster;
	}

	@Override
	public Image getRemoteImage(final String url, int width, int height) {
		final ImageImpl image = createImage(true, width, height, url);
		asyn.invokeAsync(new Runnable() {
			public void run() {
				try {
					BitmapOptions options = createOptions(url, false, Scale.ONE);
					Bitmap bmp = downloadBitmap(url, options);
					image.succeed(new ImageImpl.Data(options.scale, bmp, bmp
							.getWidth(), bmp.getHeight()));
				} catch (Exception error) {
					image.fail(error);
				}
			}
		});
		return image;
	}

	protected AndroidAudio _audio;

	protected AndroidAudio getNativeAudio() {
		if (_audio == null) {
			_audio = new AndroidAudio();
		}
		return _audio;
	}

	@Override
	public Sound getSound(String path) {
		if (_audio == null) {
			_audio = new AndroidAudio();
		}
		return _audio.createSound(path);
	}

	@Override
	public Sound getMusic(String path) {
		if (_audio == null) {
			_audio = new AndroidAudio();
		}
		return _audio.createMusic(path);
	}

	@Override
	public String getTextSync(String path) throws Exception {
		InputStream is = openAsset(path);
		try {
			StringBuilder fileData = new StringBuilder(1000);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
			reader.close();
			return fileData.toString();
		} finally {
			is.close();
		}
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		InputStream is = openAsset(path);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			while (true) {
				int r = is.read(buf);
				if (r == -1) {
					break;
				}
				out.write(buf, 0, r);
			}
			return out.toByteArray();
		} finally {
			is.close();
		}
	}

	@Override
	protected ImageImpl createImage(boolean async, int rwid, int rhei,
			String source) {
		return new AndroidImage(game, async, rwid, rhei, source);
	}

	@Override
	protected ImageImpl.Data load(String path) throws Exception {
		Exception error = null;
		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			try {
				InputStream is = openAsset(rsrc.path);
				try {
					BitmapOptions options = createOptions(path, true,
							rsrc.scale);
					Bitmap bitmap = BitmapFactory.decodeStream(is, null,
							options);
					return new ImageImpl.Data(options.scale, bitmap,
							bitmap.getWidth(), bitmap.getHeight());
				} finally {
					is.close();
				}
			} catch (FileNotFoundException ex) {
				error = ex;
			} catch (Exception e) {
				error = e;
				break;
			}
		}
		game.log().warn("Could not load image: " + pathPrefix + path, error);
		throw error != null ? error : new FileNotFoundException(path);
	}

	Typeface getTypeface(String path) {
		return Typeface.createFromAsset(assetMgr, getPath(path));
	}

	protected AssetFileDescriptor openAssetFd(String path) throws IOException {
		String fullPath = getPath(path);
		return assetMgr.openFd(fullPath);
	}

	protected Scale assetScale() {
		return (assetScale != null) ? assetScale : game.graphics().scale();
	}

	protected InputStream openAsset(String path) throws IOException {
		String fullPath = getPath(path);
		InputStream is = assetMgr.open(fullPath, AssetManager.ACCESS_STREAMING);
		if (is == null) {
			throw new FileNotFoundException("Missing resource: " + fullPath);
		}
		return is;
	}

	protected BitmapOptions createOptions(String path, boolean purgeable,
			Scale scale) {
		BitmapOptions options = new BitmapOptions();
		options.inScaled = false;
		options.inDither = true;
		options.inPreferredConfig = game.graphics().preferredBitmapConfig;
		options.inPurgeable = purgeable;
		options.inInputShareable = true;
		options.scale = scale;
		optionsAdjuster.adjustOptions(path, options);
		return options;
	}

	protected Bitmap downloadBitmap(String url, BitmapOptions options)
			throws Exception {
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpGet getRequest = new HttpGet(url);
		try {
			HttpResponse response = client.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Header[] headers = response.getHeaders("Location");
				if (headers != null && headers.length > 0) {
					return downloadBitmap(
							headers[headers.length - 1].getValue(), options);
				}
				throw new Exception("Error " + statusCode
						+ " while retrieving bitmap from " + url);
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new Exception("getEntity returned null for " + url);
			}
			InputStream in = null;
			try {
				in = entity.getContent();
				return BitmapFactory.decodeStream(in, null, options);
			} finally {
				if (in != null) {
					in.close();
				}
				entity.consumeContent();
			}

		} catch (Exception e) {
			getRequest.abort();
			game.reportError("bitmap from " + url, e);
			throw e;
		} finally {
			client.close();
		}
	}
}
