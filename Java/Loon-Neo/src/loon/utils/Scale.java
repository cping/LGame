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
package loon.utils;

import loon.geom.Vector2f;
import loon.utils.reply.Pair;

/**
 * 浮点参数缩放操作用工具类
 *
 */
public class Scale {

	public enum Mode {
		NONE, FILL, FILL_X, FILL_Y, FIT, STRETCH
	}

	public static class ScaledResource {

		public final Scale scale;

		public final String path;

		public ScaledResource(Scale scale, String path) {
			this.scale = scale;
			this.path = path;
		}

		@Override
		public String toString() {
			return scale + ": " + path;
		}
	}

	public static final Scale ONE = new Scale(1f);

	public final float factor;

	public Scale(float factor) {
		this.factor = factor;
	}

	public float scaled(float length) {
		return factor * length;
	}

	public Pair<Vector2f, Vector2f> scaledSize(Mode scaling, float srcWidth, float srcHeight, float tarWidth,
			float tarHeight) {
		return scaledSize(scaling, srcWidth, srcHeight, tarWidth, tarHeight);
	}

	/**
	 * 成比例的缩放目标大小为指定大小
	 * 
	 * @param scaling
	 * @param powerOfTwo
	 * @param srcWidth
	 * @param srcHeight
	 * @param tarWidth
	 * @param tarHeight
	 * @return
	 */
	public Pair<Vector2f, Vector2f> scaledSize(Mode mode, boolean powerOfTwo, float srcWidth, float srcHeight,
			float tarWidth, float tarHeight) {

		float targetRatio = 1f;
		float sourceRatio = 1f;
		float scaleValue = 1f;

		Vector2f sizeResult = new Vector2f();
		Vector2f scaleResult = new Vector2f();

		switch (mode) {
		case FILL:
			targetRatio = tarHeight / tarWidth;
			sourceRatio = srcHeight / srcWidth;
			scaleValue = targetRatio < sourceRatio ? tarWidth / srcWidth : tarHeight / srcHeight;
			if (powerOfTwo) {
				scaleValue = MathUtils.previousPowerOfTwo(MathUtils.ceil(scaleValue));
			}
			sizeResult.set(srcWidth * scaleValue, srcHeight * scaleValue);
			scaleResult.set(scaleValue, scaleValue);
			break;
		case FILL_X:
			scaleValue = tarWidth / srcWidth;
			if (powerOfTwo) {
				scaleValue = MathUtils.previousPowerOfTwo(MathUtils.ceil(scaleValue));
			}
			sizeResult.set(srcWidth * scaleValue, srcHeight * scaleValue);
			scaleResult.set(scaleValue, scaleValue);
			break;
		case FILL_Y:
			scaleValue = tarHeight / srcHeight;
			if (powerOfTwo) {
				scaleValue = MathUtils.previousPowerOfTwo(MathUtils.ceil(scaleValue));
			}
			sizeResult.set(srcWidth * scaleValue, srcHeight * scaleValue);
			scaleResult.set(scaleValue, scaleValue);
			break;
		case FIT:
			targetRatio = tarHeight / tarWidth;
			sourceRatio = srcHeight / srcWidth;
			scaleValue = targetRatio > sourceRatio ? tarWidth / srcWidth : tarHeight / srcHeight;
			if (powerOfTwo) {
				scaleValue = MathUtils.previousPowerOfTwo(MathUtils.floor(scaleValue));
			}
			sizeResult.set(srcWidth * scaleValue, srcHeight * scaleValue);
			scaleResult.set(scaleValue, scaleValue);
			break;
		case STRETCH:
			float scaleX = tarWidth / srcWidth;
			float scaleY = tarHeight / srcHeight;
			if (powerOfTwo) {
				scaleX = MathUtils.previousPowerOfTwo(MathUtils.ceil(scaleX));
				scaleY = MathUtils.previousPowerOfTwo(MathUtils.ceil(scaleY));
			}
			sizeResult.set(tarWidth, tarHeight);
			scaleResult.set(scaleX, scaleY);
			break;
		case NONE:
		default:
			sizeResult.set(srcWidth, srcHeight);
			scaleResult.set(1f);
			break;
		}
		return Pair.get(sizeResult, scaleResult);
	}

	public int scaledCeil(float length) {
		return MathUtils.iceil(scaled(length));
	}

	public int scaledFloor(float length) {
		return MathUtils.ifloor(scaled(length));
	}

	public float invScaled(float length) {
		return length / factor;
	}

	public int invScaledFloor(float length) {
		return MathUtils.ifloor(invScaled(length));
	}

	public int invScaledCeil(float length) {
		return MathUtils.iceil(invScaled(length));
	}

	public TArray<ScaledResource> getScaledResources(String path) {
		TArray<ScaledResource> rsrcs = new TArray<ScaledResource>();
		rsrcs.add(new ScaledResource(this, computePath(path, factor)));
		for (float rscale = MathUtils.ifloor(factor); rscale > 1; rscale -= 1) {
			if (rscale != factor)
				rsrcs.add(new ScaledResource(new Scale(rscale), computePath(path, rscale)));
		}
		rsrcs.add(new ScaledResource(ONE, path));
		return rsrcs;
	}

	private String computePath(String path, float scale) {
		if (scale <= 1f) {
			return path;
		}
		int scaleFactor = (int) (scale * 10);
		if (scaleFactor % 10 == 0) {
			scaleFactor /= 10;
		}
		int didx = path.lastIndexOf(".");
		if (didx == -1) {
			return path;
		} else {
			return path.substring(0, didx) + "@" + scaleFactor + "x" + path.substring(didx);
		}
	}

	@Override
	public String toString() {
		return "x" + factor;
	}
}
