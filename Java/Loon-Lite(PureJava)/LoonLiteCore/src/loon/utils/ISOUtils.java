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
package loon.utils;

import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.Vector3f;

/**
 * 斜视地图坐标换算统一处理用类
 */
public final class ISOUtils {

	private ISOUtils() {
	}

	public enum MirrorMode {
		NONE, HORIZONTAL, VERTICAL, BOTH
	}

	public enum ProjectionMode {
		ISOMETRIC, ORTHOGRAPHIC, OBLIQUE
	}

	public static class IsoLight {
		// 光源方向
		public float x, y, z;
		// 光照强度
		public float intensity;
		// 漫反射系数
		public float diffuse;
		// 高光系数
		public float specular;
		// 高光锐度
		public float shininess;
		// 是否动态光源（如技能光效）
		public boolean isDynamic;
		// 闪烁速度（动态光源用）
		public float flickerSpeed;
		// 闪烁范围
		public float flickerRange;

		private float elapsedTime = 0f;

		public IsoLight(float x, float y, float z, float intensity, float diffuse, float specular, float shininess) {
			this(x, y, z, intensity, diffuse, specular, shininess, false, 0f, 0f);
		}

		public IsoLight(float x, float y, float z, float intensity, float diffuse, float specular, float shininess,
				boolean isDynamic, float flickerSpeed, float flickerRange) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.intensity = intensity;
			this.diffuse = diffuse;
			this.specular = specular;
			this.shininess = shininess;
			this.isDynamic = isDynamic;
			this.flickerSpeed = flickerSpeed;
			this.flickerRange = flickerRange;
		}

		public void update(float deltaTime) {
			if (isDynamic) {
				elapsedTime += deltaTime;
				float flicker = MathUtils.sin(elapsedTime * flickerSpeed) * flickerRange;
				intensity = MathUtils.max(0, intensity + flicker);
			}
		}
	}

	// 配置类
	public static class IsoConfig {

		public enum ProjectionMode {
			ISOMETRIC, ORTHOGRAPHIC, OBLIQUE
		}

		// 瓦片间默认高低差
		public HeightOffsetMode heightOffsetMode = HeightOffsetMode.NONE;
		// 默认差值
		public float baseLayerOffset = 5f;
		// 自定义模式用
		public float[] customLayerOffsets = null;

		public float tileWidth = 64f;
		public float tileHeight = 32f;
		public float heightScale = 0.5f;
		public float offsetX = 0f;
		public float offsetY = 0f;
		public float scaleX = 1f;
		public float scaleY = 1f;
		public float rotationAngle = 0f;

		public ProjectionMode projectionMode = ProjectionMode.ISOMETRIC;
		public MirrorMode mirror = MirrorMode.NONE;

		public float obliqueAngle = MathUtils.toRadians(45);
		public float obliqueScale = 0.5f;

		public float perspective = 0f;
		public float cameraHeight = 100f;
		public float cameraDistance = 500f;
		public float tiltAngle = 0f;

		public TArray<IsoLight> lights = new TArray<IsoLight>();
		public int renderLayerCount = 5;
		public float[] layerScale = new float[] { 1.0f, 1.0f, 1.1f, 1.2f, 1.3f };

		public IsoConfig() {
		}

		public IsoConfig(float tileWidth, float tileHeight, float heightScale, float offsetX, float offsetY,
				float scaleX, float scaleY, float rotationAngle, ProjectionMode projectionMode, MirrorMode mirror,
				float obliqueAngle, float obliqueScale, float perspective, float cameraHeight, float cameraDistance,
				float tiltAngle, int renderLayerCount, float[] layerScale) {
			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;
			this.heightScale = heightScale;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.rotationAngle = rotationAngle;
			this.projectionMode = projectionMode;
			this.mirror = mirror;
			this.obliqueAngle = obliqueAngle;
			this.obliqueScale = obliqueScale;
			this.perspective = perspective;
			this.cameraHeight = cameraHeight;
			this.cameraDistance = cameraDistance;
			this.tiltAngle = tiltAngle;
			this.renderLayerCount = renderLayerCount;
			this.layerScale = layerScale != null ? layerScale : null;
		}

		public IsoConfig(IsoConfig other) {
			this.tileWidth = other.tileWidth;
			this.tileHeight = other.tileHeight;
			this.heightScale = other.heightScale;
			this.offsetX = other.offsetX;
			this.offsetY = other.offsetY;
			this.scaleX = other.scaleX;
			this.scaleY = other.scaleY;
			this.rotationAngle = other.rotationAngle;
			this.projectionMode = other.projectionMode;
			this.mirror = other.mirror;
			this.obliqueAngle = other.obliqueAngle;
			this.obliqueScale = other.obliqueScale;
			this.perspective = other.perspective;
			this.cameraHeight = other.cameraHeight;
			this.cameraDistance = other.cameraDistance;
			this.tiltAngle = other.tiltAngle;
			this.renderLayerCount = other.renderLayerCount;
			this.layerScale = other.layerScale;
			this.lights.clear();
			this.lights.addAll(other.lights.cpy());
		}

		public Vector2f getIsoTileSize(float cellWidth, float cellHeight, Vector2f result) {
			if (result == null) {
				result = new Vector2f();
			}
			if (MathUtils.abs(cellWidth / cellHeight - 2f) < 0.01f) {
				return result.set(cellWidth / 2f, cellHeight / 2f);
			} else if (cellWidth == cellHeight) {
				return result.set(cellWidth / 2f, cellHeight / 4f);
			} else {
				return result.set(cellWidth / 2f, cellHeight / 2f);
			}
		}

		public void setTileSize(int width, int height) {
			this.tileWidth = width;
			this.tileHeight = height;
			this.heightScale = (float) height / (float) width;
		}

		public void setScale(float sx, float sy) {
			this.scaleX = sx;
			this.scaleY = sy;
		}

		public void setScale(float s) {
			this.setScale(s, s);
		}

		public IsoConfig cpy() {
			return new IsoConfig(this);
		}

		public float getTileCenterX() {
			return tileWidth / 2;
		}

		public float getTileCenterY() {
			return tileHeight / 2;
		}

		public static IsoConfig defaultConfig() {
			IsoConfig cfg = new IsoConfig();
			cfg.lights.add(new IsoLight(0.5f, 0.5f, 1f, 1f, 1f, 0f, 16f));
			cfg.lights.add(new IsoLight(0.2f, 0.3f, 0.8f, 0.8f, 0.8f, 0.2f, 32f, true, 0.002f, 0.1f));
			return cfg;
		}

		public void updateDynamicLights(float deltaTime) {
			for (IsoLight light : lights) {
				if (light.isDynamic) {
					light.update(deltaTime);
				}
			}
		}
	}

	// 地图瓦片可选的默认瓦片间高低差模式
	public static enum HeightOffsetMode {
		// 无算法
		NONE,
		// 固定差值
		CONSTANT,
		// 差值随层数递增
		SCALE_BY_LAYER,
		// 奇偶层交替差值
		ALTERNATE,
		// 波浪式上下起伏
		WAVE,
		// 随机偏移
		RANDOM,
		// 使用自定义数组
		CUSTOM,
		// 混合模式
		COMBINED
	}

	// 返回结果类
	public static class IsoResult {
		public Vector2f screenPos;
		public float brightness;
		// 分层偏移
		public Vector2f[] layerOffsets;

		public IsoResult() {
			this(new Vector2f(), 0f);
		}

		public IsoResult(Vector2f screenPos, float brightness) {
			this.screenPos = screenPos;
			this.brightness = brightness;
			this.layerOffsets = new Vector2f[5];
			for (int i = 0; i < 5; i++) {
				this.layerOffsets[i] = new Vector2f(screenPos.x, screenPos.y - i * 5);
			}
		}

		public void fixPosition(IsoConfig config) {
			if (config.heightOffsetMode == HeightOffsetMode.NONE) {
				return;
			}
			float snapX = config.tileWidth * config.scaleX;
			screenPos.x = MathUtils.round(screenPos.x / snapX) * snapX;
			float baseY = screenPos.y;
			float offsetY = 0f;
			for (int i = 0; i < layerOffsets.length; i++) {
				layerOffsets[i].x = screenPos.x;
				switch (config.heightOffsetMode) {
				case NONE:
					offsetY = 0;
					break;
				case CONSTANT:
					offsetY = i * config.baseLayerOffset;
					break;
				case SCALE_BY_LAYER:
					offsetY = (i * i) * config.baseLayerOffset;
					break;
				case ALTERNATE:
					offsetY = (i % 2 == 0 ? config.baseLayerOffset : config.baseLayerOffset * 2);
					break;
				case WAVE:
					offsetY = (float) (MathUtils.sin(i * 0.5f) * config.baseLayerOffset);
					break;
				case RANDOM:
					offsetY = (MathUtils.random() - 0.5f) * config.baseLayerOffset * 2;
					break;
				case COMBINED:
					offsetY = i * config.baseLayerOffset
							+ (float) (MathUtils.sin(i * 0.5f) * config.baseLayerOffset * 0.5f)
							+ (MathUtils.random() - 0.5f) * config.baseLayerOffset;
					break;
				case CUSTOM:
					if (config.customLayerOffsets != null && i < config.customLayerOffsets.length) {
						offsetY = config.customLayerOffsets[i];
					} else {
						offsetY = 0f;
					}
					break;
				}
				layerOffsets[i].y = baseY - offsetY;
			}
			screenPos.y = (baseY - offsetY);
		}

	}

	public static IsoConfig forTileWidth(int width) {
		IsoConfig config = IsoConfig.defaultConfig();
		config.tileWidth = width;
		int height = width / 2;
		config.tileHeight = height;
		config.heightScale = (float) height / (float) width;
		config.projectionMode = IsoConfig.ProjectionMode.ISOMETRIC;
		config.scaleX = config.scaleY = 1f;
		config.offsetX = 0;
		config.offsetY = 0;
		config.rotationAngle = 0f;
		config.lights.clear();
		config.lights.add(new IsoLight(0.5f, 0.5f, 1f, 1f, 0.8f, 0.2f, 32f));
		return config;
	}

	public static IsoConfig forTileHeight(int height) {
		IsoConfig config = IsoConfig.defaultConfig();
		int width = height * 2;
		config.tileWidth = width;
		config.tileHeight = height;
		config.heightScale = (float) height / (float) width;
		config.projectionMode = IsoConfig.ProjectionMode.ISOMETRIC;
		config.scaleX = config.scaleY = 1f;
		config.offsetX = 0;
		config.offsetY = 0;
		config.rotationAngle = 0f;
		config.lights.clear();
		config.lights.add(new IsoLight(0.5f, 0.5f, 1f, 1f, 0.8f, 0.2f, 32f));
		return config;
	}

	/**
	 * 正向投影
	 * 
	 * @param gx
	 * @param gy
	 * @param cheight
	 * @param config
	 * @return
	 */
	public static IsoResult isoTransform(int gx, int gy, float cheight, IsoConfig config) {
		return isoTransform(gx, gy, 0f, cheight, config);
	}

	public static IsoResult isoTransform(int gx, int gy, float cwidth, float cheight, IsoConfig config) {
		return isoTransform(gx, gy, cwidth, cheight, config, null, null);
	}

	/**
	 * 正向投影
	 * 
	 * @param gx
	 * @param gy
	 * @param cwidth
	 * @param cheight
	 * @param config
	 * @param pos
	 * @return
	 */
	public static IsoResult isoTransform(int gx, int gy, float cwidth, float cheight, IsoConfig config, Vector2f pos,
			IsoResult isoResult) {
		if (pos == null) {
			pos = new Vector2f();
		}
		float cosA = MathUtils.cos(config.rotationAngle);
		float sinA = MathUtils.sin(config.rotationAngle);
		float x = gx * cosA - gy * sinA;
		float y = gx * sinA + gy * cosA;

		float cellWidth = cwidth <= 0 ? config.tileWidth : cwidth;
		float cellHeight = cheight <= 0 ? config.tileHeight : cheight;
		float z = cellHeight * config.heightScale;

		switch (config.mirror) {
		case HORIZONTAL:
			x = -x;
			break;
		case VERTICAL:
			y = -y;
			break;
		case BOTH:
			x = -x;
			y = -y;
			break;
		default:
			break;
		}

		float isoX, isoY;
		switch (config.projectionMode) {
		case ORTHOGRAPHIC:
			isoX = x * cellWidth;
			isoY = y * cellHeight - z;
			break;
		case OBLIQUE:
			float cosOb = MathUtils.cos(config.obliqueAngle);
			float sinOb = MathUtils.sin(config.obliqueAngle);
			isoX = x * cellWidth + z * config.obliqueScale * cosOb;
			isoY = y * cellHeight - z * config.obliqueScale * sinOb;
			break;
		default: // ISOMETRIC
			Vector2f isoSize = config.getIsoTileSize(cellWidth, cellHeight, pos);
			float isoTileWidth = isoSize.x;
			float isoTileHeight = isoSize.y;
			isoX = (x - y) * isoTileWidth;
			isoY = (x + y) * isoTileHeight - z;
		}

		isoX = isoX * config.scaleX + config.offsetX;
		isoY = isoY * config.scaleY + config.offsetY;

		// 光照计算
		float brightness = 0f;
		for (IsoLight light : config.lights) {
			float len = MathUtils.sqrt(light.x * light.x + light.y * light.y + light.z * light.z);
			float lx = light.x / len, ly = light.y / len, lz = light.z / len;
			float ndotl = MathUtils.max(0, lx * 0 + ly * 0 + lz * 1);
			float diffuse = ndotl * light.diffuse;
			float specular = MathUtils.pow(ndotl, light.shininess) * light.specular;
			brightness += (diffuse + specular) * light.intensity;
		}
		brightness = MathUtils.min(1f, brightness);
		if (isoResult == null) {
			isoResult = new IsoResult(pos.set(isoX, isoY), brightness);
		} else {
			isoResult.screenPos = pos.set(isoX, isoY);
			isoResult.brightness = brightness;
		}
		isoResult.fixPosition(config);
		return isoResult;
	}

	private final static IsoConfig defaultConfig = IsoConfig.defaultConfig();

	public static Vector2f isoTransform(int gx, int gy, int cellSizeX, int cellSizeY, int rotationMode, float height,
			int heightScale) {
		defaultConfig.heightScale = heightScale;
		defaultConfig.rotationAngle = rotationModeToAngle(rotationMode);
		return isoTransform(gx, gy, cellSizeX, cellSizeY, defaultConfig).screenPos;
	}

	/**
	 * 反算为正常坐标
	 * 
	 * @param screenX
	 * @param screenY
	 * @param height
	 * @param config
	 * @return
	 */
	public static Vector3f screenToGrid(float screenX, float screenY, float cwidth, float cheight, IsoConfig config) {
		float x = (screenX - config.offsetX) / config.scaleX;
		float y = (screenY - config.offsetY) / config.scaleY;
		float cellWidth = cwidth <= 0 ? config.tileWidth : cwidth;
		float cellHeight = cheight <= 0 ? config.tileHeight : cheight;
		float gx, gy;
		float z = cellHeight * config.heightScale;
		switch (config.projectionMode) {
		case ORTHOGRAPHIC:
			gx = x / cellWidth;
			gy = (y + z) / cellHeight;
			break;
		case OBLIQUE:
			float cosOb = MathUtils.cos(config.obliqueAngle);
			float sinOb = MathUtils.sin(config.obliqueAngle);
			float adjX = x - z * config.obliqueScale * cosOb;
			float adjY = y + z * config.obliqueScale * sinOb;
			gx = adjX / cellWidth;
			gy = adjY / cellHeight;
			break;
		default: // ISOMETRIC
			Vector2f isoSize = config.getIsoTileSize(cellWidth, cellHeight, null);
			float isoTileWidth = isoSize.x;
			float isoTileHeight = isoSize.y;
			gx = (x / isoTileWidth + y / isoTileHeight) / 2f;
			gy = (-x / isoTileWidth + y / isoTileHeight) / 2f;
		}
		return new Vector3f(gx, gy, z);
	}

	private static float rotationModeToAngle(int rotationMode) {
		switch (rotationMode) {
		case 1:
			return MathUtils.toRadians(90);
		case 2:
			return MathUtils.toRadians(180);
		case 3:
			return MathUtils.toRadians(270);
		default:
			return 0f;
		}
	}

	public static TArray<Vector2f> isoTransformBatch(TArray<float[]> coords, IsoConfig config) {
		TArray<Vector2f> results = new TArray<Vector2f>();
		for (float[] c : coords) {
			int gx = (int) c[0], gy = (int) c[1];
			float h = c.length > 2 ? c[2] : 0f;
			results.add(isoTransform(gx, gy, 0, h, config).screenPos);
		}
		return results;
	}

	public static TArray<IsoResult> isoTransformBatchWithLight(TArray<float[]> coords, IsoConfig config) {
		TArray<IsoResult> results = new TArray<IsoResult>();
		for (float[] c : coords) {
			int gx = (int) c[0], gy = (int) c[1];
			float h = c.length > 2 ? c[2] : 0f;
			results.add(isoTransform(gx, gy, 0, h, config));
		}
		return results;
	}

	/**
	 * 旋转模式转换
	 * 
	 * @param config
	 * @param rotationMode
	 */
	public static void setRotationMode(IsoConfig config, int rotationMode) {
		config.rotationAngle = rotationModeToAngle(rotationMode);
	}

	/**
	 * 计算瓦片碰撞框
	 * 
	 * @param gx
	 * @param gy
	 * @param width
	 * @param height
	 * @param config
	 * @param pos
	 * @return
	 */
	public static RectBox getTileBounds(int gx, int gy, float width, float height, IsoConfig config, Vector2f pos) {
		IsoResult result = isoTransform(gx, gy, width, height, config, pos, null);
		// 使用投影比例计算碰撞框
		Vector2f isoSize = config.getIsoTileSize(width, height, pos);
		float scaledWidth = isoSize.x * config.scaleX * 2;
		float scaledHeight = isoSize.y * config.scaleY * 2;
		return new RectBox(result.screenPos.x - scaledWidth / 2, result.screenPos.y - scaledHeight / 2, scaledWidth,
				scaledHeight);
	}

	/**
	 * 是否点击了指定瓦片
	 * 
	 * @param gx
	 * @param gy
	 * @param width
	 * @param height
	 * @param screenX
	 * @param screenY
	 * @param config
	 * @param pos
	 * @return
	 */
	public static boolean isTileClicked(int gx, int gy, float width, float height, float screenX, float screenY,
			IsoConfig config, Vector2f pos) {
		RectBox bounds = getTileBounds(gx, gy, width, height, config, pos);
		float dx = MathUtils.abs(screenX - (bounds.x + bounds.width / 2)) / (bounds.width / 2);
		float dy = MathUtils.abs(screenY - (bounds.y + bounds.height / 2)) / (bounds.height / 2);
		return (dx + dy) <= 1.0f + 0.05f;
	}

	/**
	 * 分层渲染坐标计算
	 * 
	 * @param gx
	 * @param gy
	 * @param width
	 * @param height
	 * @param layerIndex
	 * @param config
	 * @param result
	 * @return
	 */
	public static Vector2f getLayeredScreenPos(int gx, int gy, float width, float height, int layerIndex,
			IsoConfig config, Vector2f result) {
		IsoResult baseResult = isoTransform(gx, gy, width, height, config, result, null);
		Vector2f pos = baseResult.layerOffsets[Math.min(layerIndex, config.renderLayerCount - 1)];
		Vector2f isoSize = config.getIsoTileSize(width, height, result);
		float snapX = isoSize.x * config.scaleX;
		float snapY = isoSize.y * config.scaleY;
		pos.x = MathUtils.round(pos.x / snapX) * snapX;
		pos.y = MathUtils.round(pos.y / snapY) * snapY;
		return pos;
	}

	/**
	 * 用于在两组大小不等的瓦片中计算某特效位置
	 * 
	 * @param gx
	 * @param gy
	 * @param cellWidth
	 * @param cellHeight
	 * @param config
	 * @param offsetX
	 * @param offsetY
	 * @param result
	 * @return
	 */
	public static Vector2f getEffectScreenPos(int gx, int gy, float cellWidth, float cellHeight, IsoConfig config,
			float offsetX, float offsetY, Vector2f result) {
		IsoResult baseResult = isoTransform(gx, gy, cellWidth, cellHeight, config, result, null);
		Vector2f effectPos = baseResult.screenPos.cpy();

		// 使用投影比例来计算偏移
		Vector2f isoSize = config.getIsoTileSize(cellWidth, cellHeight, result);
		effectPos.x += offsetX * (isoSize.x / cellWidth);
		effectPos.y += offsetY * (isoSize.y / cellHeight);

		effectPos.x = MathUtils.round(effectPos.x);
		effectPos.y = MathUtils.round(effectPos.y);
		return effectPos;
	}

	/**
	 * 修正角色显示位置
	 * 
	 * @param gx
	 * @param gy
	 * @param width
	 * @param height
	 * @param config
	 * @param result
	 * @return
	 */
	public static Vector2f fixCharacterPosition(int gx, int gy, float width, float height, IsoConfig config,
			Vector2f result) {
		Vector2f pos = isoTransform(gx, gy, width, height, config, result, null).screenPos;

		// 使用投影比例修正角色锚点
		Vector2f isoSize = config.getIsoTileSize(width, height, result);
		pos.x -= isoSize.x * config.scaleX;
		pos.y -= isoSize.y * config.scaleY;

		pos.x = MathUtils.round(pos.x);
		pos.y = MathUtils.round(pos.y);
		return pos;
	}

}
