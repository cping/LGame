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
package loon.action.sprite;

import loon.BaseIO;
import loon.Json;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.events.FrameListener;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.utils.CollectionUtils;
import loon.utils.HelperUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.PathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.Duration;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

/**
 * Entity类的动画播放扩展类,相比单纯使用Sprite类,此类更容易进行具体帧的临时播放和设置参数变更,以及监听.
 */
public class AnimatedEntity extends Entity {

	/**
	 * 加载一个动画配置文件
	 * 
	 * @param path
	 * @return
	 */
	public static AnimatedEntity config(String path) {
		if (StringUtils.isNullOrEmpty(path)) {
			throw new LSysException("The Animation file name cannot be null.");
		}
		String ext = PathUtils.getExtension(path);
		if ("xml".equals(ext)) {
			return loadXml(ext, false);
		}
		return loadJson(ext, false);
	}

	/**
	 * 加载一个xml文件形式存在的AnimatedEntity配置文件(实例见player.xml)
	 * 
	 * @param path
	 * @return
	 */
	public static AnimatedEntity loadXml(String path) {
		return loadXml(path, true);
	}

	/**
	 * 加载一个xml文件形式存在的AnimatedEntity配置文件(实例见player.xml)
	 * 
	 * @param path
	 * @param checkExt
	 * @return
	 */
	public static AnimatedEntity loadXml(String path, boolean checkExt) {
		if (StringUtils.isNullOrEmpty(path)) {
			throw new LSysException("The Animation file name cannot be null.");
		}
		String ext = PathUtils.getExtension(path);
		if (checkExt && StringUtils.isEmpty(ext)) {
			path = path + ".xml";
		}
		AnimatedEntity entity = null;
		XMLDocument doc = XMLParser.parse(path);
		String resource = null;
		String play = null;
		PointI clip = null;
		RectBox rect = null;
		if (doc != null) {
			XMLElement docElement = doc.getRoot();
			if (docElement != null) {
				if (!docElement.getName().equals("info")) {
					throw new LSysException("Invalid Animation file. The first child must be a <info> element.");
				}
				TArray<XMLElement> list = docElement.list();
				for (int i = 0; i < list.size; i++) {
					XMLElement ele = list.get(i);
					if ("res".equals(ele.getName())) {
						resource = ele.getContents();
						if (StringUtils.isNullOrEmpty(resource)) {
							throw new LSysException("The Resource file cannot be null.");
						}
					}
					if ("play".equals(ele.getName())) {
						play = ele.getContents();
					} else if ("clip".equals(ele.getName())) {
						String[] clipStr = StringUtils.split(ele.getContents(), LSystem.COMMA);
						if (clipStr.length == 1) {
							clip = new PointI(HelperUtils.toInt(clipStr[0]));
						} else if (clipStr.length > 1) {
							clip = new PointI(HelperUtils.toInt(clipStr[0]), HelperUtils.toInt(clipStr[1]));
						}
					} else if ("display".equals(ele.getName())) {
						String[] displayStr = StringUtils.split(ele.getContents(), LSystem.COMMA);
						if (displayStr.length == 1) {
							rect = RectBox.all(HelperUtils.toInt(displayStr[0]));
						} else if (displayStr.length == 2) {
							rect = new RectBox(HelperUtils.toInt(displayStr[0]), HelperUtils.toInt(displayStr[1]));
						} else if (displayStr.length == 4) {
							rect = new RectBox(HelperUtils.toInt(displayStr[0]), HelperUtils.toInt(displayStr[1]),
									HelperUtils.toInt(displayStr[2]), HelperUtils.toInt(displayStr[3]));
						}
					} else if ("ani".equals(ele.getName())) {
						if (entity == null && resource != null) {
							if (rect == null) {
								entity = new AnimatedEntity(resource, clip.x, clip.y, 0, 0, clip.x, clip.y);
							} else {
								entity = new AnimatedEntity(resource, clip.x, clip.y, rect.x, rect.y, rect.width,
										rect.height);
							}
						}
						String name = null;
						String index = null;
						String timer = null;
						TArray<XMLElement> anis = ele.list();
						for (int j = 0; j < anis.size; j++) {
							XMLElement ani = anis.get(j);
							if ("name".equals(ani.getName())) {
								name = ani.getContents();
							} else if ("index".equals(ani.getName())) {
								index = ani.getContents();
							} else if ("timer".equals(ani.getName())) {
								timer = ani.getContents();
							}
						}
						if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(index)) {
							String[] indexStr = StringUtils.split(index, LSystem.COMMA);
							int[] indexs = new int[indexStr.length];
							for (int n = 0; n < indexStr.length; n++) {
								indexs[n] = HelperUtils.toInt(indexStr[n]);
							}
							if (StringUtils.isNotEmpty(timer)) {
								entity.setPlayIndex(name, HelperUtils.toInt(timer), indexs);
							} else {
								entity.setPlayIndex(name, indexs);
							}
						}
					}
				}
			}
			if (entity == null && resource != null) {
				if (rect == null) {
					entity = new AnimatedEntity(resource, clip.x, clip.y, 0, 0, clip.x, clip.y);
				} else {
					entity = new AnimatedEntity(resource, clip.x, clip.y, rect.x, rect.y, rect.width, rect.height);
				}
			}
			if (StringUtils.isNotEmpty(play)) {
				entity.animate(play);
			}
		}
		return entity;
	}

	/**
	 * 加载一个json文件形式存在的AnimatedEntity配置文件(实例见player.json)
	 * 
	 * @param path
	 * @return
	 */
	public static AnimatedEntity loadJson(String path) {
		return loadJson(path, true);
	}

	/**
	 * 加载一个json文件形式存在的AnimatedEntity配置文件(实例见player.json)
	 * 
	 * @param path
	 * @param checkExt
	 * @return
	 */
	public static AnimatedEntity loadJson(String path, boolean checkExt) {
		if (StringUtils.isNullOrEmpty(path)) {
			throw new LSysException("The Animation file name cannot be null.");
		}
		String ext = PathUtils.getExtension(path);
		if (checkExt && StringUtils.isEmpty(ext)) {
			path = path + ".json";
		}
		AnimatedEntity entity = null;
		Object jsonObject = BaseIO.loadJsonObject(path);
		String resource = null;
		String play = null;
		PointI clip = null;
		RectBox rect = null;
		if (jsonObject != null) {
			Json.Object json = (Json.Object) jsonObject;
			if (json != null) {
				Json.Object info = json.getObject("info");
				if (info == null) {
					throw new LSysException("Invalid Animation file. The first child must be a <info> element.");
				}
				resource = info.getString("res");
				if (StringUtils.isNullOrEmpty(resource)) {
					throw new LSysException("The Resource file cannot be null.");
				}
				play = info.getString("play");
				String[] clipStr = StringUtils.split(info.getString("clip"), LSystem.COMMA);
				if (clipStr.length == 1) {
					clip = new PointI(HelperUtils.toInt(clipStr[0]));
				} else if (clipStr.length > 1) {
					clip = new PointI(HelperUtils.toInt(clipStr[0]), HelperUtils.toInt(clipStr[1]));
				}
				String[] displayStr = StringUtils.split(info.getString("display"), LSystem.COMMA);
				if (displayStr.length == 1) {
					rect = RectBox.all(HelperUtils.toInt(displayStr[0]));
				} else if (displayStr.length == 2) {
					rect = new RectBox(HelperUtils.toInt(displayStr[0]), HelperUtils.toInt(displayStr[1]));
				} else if (displayStr.length == 4) {
					rect = new RectBox(HelperUtils.toInt(displayStr[0]), HelperUtils.toInt(displayStr[1]),
							HelperUtils.toInt(displayStr[2]), HelperUtils.toInt(displayStr[3]));
				}
				Json.Array anis = info.getArray("ani");
				if (anis != null) {
					if (entity == null && resource != null) {
						if (rect == null) {
							entity = new AnimatedEntity(resource, clip.x, clip.y, 0, 0, clip.x, clip.y);
						} else {
							entity = new AnimatedEntity(resource, clip.x, clip.y, rect.x, rect.y, rect.width,
									rect.height);
						}
					}
					String name = null;
					String index = null;
					String timer = null;
					for (int j = 0; j < anis.length(); j++) {
						Json.Object ani = anis.getObject(j);
						name = ani.getString("name");
						index = ani.getString("index");
						timer = ani.getString("timer");
						if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(index)) {
							String[] indexStr = StringUtils.split(index, LSystem.COMMA);
							int[] indexs = new int[indexStr.length];
							for (int n = 0; n < indexStr.length; n++) {
								indexs[n] = HelperUtils.toInt(indexStr[n]);
							}
							if (StringUtils.isNotEmpty(timer)) {
								entity.setPlayIndex(name, HelperUtils.toInt(timer), indexs);
							} else {
								entity.setPlayIndex(name, indexs);
							}
						}
					}
				}
			}
			if (entity == null && resource != null) {
				if (rect == null) {
					entity = new AnimatedEntity(resource, clip.x, clip.y, 0, 0, clip.x, clip.y);
				} else {
					entity = new AnimatedEntity(resource, clip.x, clip.y, rect.x, rect.y, rect.width, rect.height);
				}
			}
			if (StringUtils.isNotEmpty(play)) {
				entity.animate(play);
			}
		}
		return entity;
	}

	/**
	 * 动画具体播放速度值及帧索引存储用类
	 *
	 */
	public static class PlayIndex {

		public final static PlayIndex at(int[] animates) {
			return at(220, animates);
		}

		public final static PlayIndex at(long frameTime, int[] animates) {
			if (animates == null) {
				return null;
			}
			int size = animates.length;
			long[] frames = new long[size];
			for (int i = 0; i < size; i++) {
				frames[i] = frameTime;
			}
			return create(frames, animates);
		}

		public final static PlayIndex at(long frameTime, int startIdx, int endIdx) {
			startIdx = MathUtils.abs(startIdx);
			endIdx = MathUtils.abs(endIdx);
			int size = endIdx - startIdx + 1;
			long[] frames = new long[size];
			for (int i = 0; i < size; i++) {
				frames[i] = frameTime;
			}
			return at(frames, startIdx, endIdx);
		}

		public final static PlayIndex at(long[] frames, int startIdx, int endIdx) {
			startIdx = MathUtils.abs(startIdx);
			endIdx = MathUtils.abs(endIdx);
			int size = endIdx - startIdx + 1;
			int[] animateIndexs = null;
			if (size <= 0) {
				animateIndexs = new int[2];
				animateIndexs[0] = startIdx;
				animateIndexs[1] = endIdx;
			} else {
				animateIndexs = new int[size];
				for (int i = 0; i < size; i++) {
					int idx = startIdx + i;
					animateIndexs[i] = idx;
				}
			}
			return create(frames, animateIndexs);
		}

		public final static PlayIndex at(long[] frames, int[] animates) {
			return create(frames, animates);
		}

		public final static PlayIndex create(long[] frames, int[] animates) {
			return new PlayIndex(frames, animates);
		}

		final long[] frames;

		final int[] animates;

		public PlayIndex(long[] frame, int[] ani) {
			this.frames = CollectionUtils.copyOf(frame);
			this.animates = CollectionUtils.copyOf(ani);
		}

		public long[] getFrames() {
			return CollectionUtils.copyOf(frames);
		}

		public int[] getAnimates() {
			return CollectionUtils.copyOf(animates);
		}

	}

	private final ObjectMap<String, PlayIndex> _playEvents;

	public static interface IAnimationListener {

		public void onAnimationStarted(final AnimatedEntity animatedSprite, final int initialLoopCount);

		public void onAnimationFrameChanged(final AnimatedEntity animatedSprite, final int oldFrameIndex,
				final int newFrameIndex);

		public void onAnimationLoopFinished(final AnimatedEntity animatedSprite, final int remainingLoopCount,
				final int initialLoopCount);

		public void onAnimationFinished(final AnimatedEntity animatedSprite);
	}

	private static final int FRAMEINDEX_INVALID = -1;

	private String _currentAnimateName;

	private boolean _animationRunning;
	private boolean _animationStartedFired;
	private boolean _animationDispose;

	private int _currentFrameIndex;
	private long _animationProgress;
	private int _remainingLoopCount;
	private Animation _animation;

	private final AnimationData _animationData = new AnimationData();
	private IAnimationListener _animationListener;

	public AnimatedEntity(final String[] paths, final int maxFrames, final float x, final float y, final float width,
			final float height) {
		this(Animation.getDefaultAnimation(paths, maxFrames, 0), x, y, width, height);
	}

	public AnimatedEntity(final LTexture[] texs, final int maxFrames, final float x, final float y, final float width,
			final float height) {
		this(Animation.getDefaultAnimation(texs, maxFrames, 0), x, y, width, height);
	}

	public AnimatedEntity(final String path, final int maxFrames, final int frameWidth, final int frameHeight,
			final float x, final float y, final float width, final float height) {
		this(Animation.getDefaultAnimation(path, maxFrames, frameWidth, frameHeight, 0), x, y, width, height);
	}

	public AnimatedEntity(final String path, final int frameWidth, final int frameHeight, final float x, final float y,
			final float width, final float height) {
		this(Animation.getDefaultAnimation(path, frameWidth, frameHeight, 10), x, y, width, height);
	}

	public AnimatedEntity(final Animation ani, final float x, final float y, final float width, final float height) {
		super(ani.getSpriteImage());
		this.setLocation(x, y);
		this.setSize(width, height);
		this._playEvents = new ObjectMap<String, AnimatedEntity.PlayIndex>(8);
		this._animation = ani;
		this._animationDispose = (this._animation != null);
	}

	@Override
	void onProcess(final long elapsedTime) {
		if (this._animationRunning) {

			final int loopCount = this._animationData.getLoopCount();
			final int[] frames = this._animationData.getFrames();
			final long animationDuration = this._animationData.getAnimationDuration();

			if (!this._animationStartedFired && (this._animationProgress == 0)) {
				this._animationStartedFired = true;
				if (frames == null) {
					this.setCurrentFrameIndex(this._animationData.getFirstFrameIndex());
				} else {
					this.setCurrentFrameIndex(frames[0]);
				}
				this._currentFrameIndex = 0;
				if (this._animationListener != null) {
					this._animationListener.onAnimationStarted(this, loopCount);
					this._animationListener.onAnimationFrameChanged(this, AnimatedEntity.FRAMEINDEX_INVALID, 0);
				}
			}
			this._animationProgress += Duration.ofS(elapsedTime);
			if (loopCount == AnimationData.LOOP_CONTINUOUS) {
				while (this._animationProgress > animationDuration) {
					this._animationProgress -= animationDuration;
					if (this._animationListener != null) {
						this._animationListener.onAnimationLoopFinished(this, this._remainingLoopCount, loopCount);
					}
				}
			} else {

				while (this._animationProgress > animationDuration) {
					this._animationProgress -= animationDuration;
					this._remainingLoopCount--;
					if (this._remainingLoopCount < 0) {
						break;
					} else if (this._animationListener != null) {
						this._animationListener.onAnimationLoopFinished(this, this._remainingLoopCount, loopCount);
					}
				}
			}

			if ((loopCount == AnimationData.LOOP_CONTINUOUS) || (this._remainingLoopCount >= 0)) {
				final int newFrameIndex = this._animationData.calculateCurrentFrameIndex(this._animationProgress);

				if (this._currentFrameIndex != newFrameIndex) {
					if (frames == null) {
						this.setCurrentFrameIndex(this._animationData.getFirstFrameIndex() + newFrameIndex);
					} else {
						this.setCurrentFrameIndex(frames[newFrameIndex]);
					}
					if (this._animationListener != null) {
						this._animationListener.onAnimationFrameChanged(this, this._currentFrameIndex, newFrameIndex);
					}
				}
				this._currentFrameIndex = newFrameIndex;
			} else {
				this._animationRunning = false;
				if (this._animationListener != null) {
					this._animationListener.onAnimationFinished(this);
				}
			}

			setTexture(_animation.getSpriteImage());

		}
	}

	public Animation getAnimation() {
		return this._animation;
	}

	public AnimatedEntity playAnimation() {
		this._animationRunning = true;
		return this;
	}

	public AnimatedEntity stopAnimation() {
		this._animationRunning = false;
		return this;
	}

	public AnimatedEntity stopAnimation(final int index) {
		this._animationRunning = false;
		this.setCurrentFrameIndex(index);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach) {
		this.animate(frameDurationEach, null);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount());
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final boolean loop) {
		this.animate(frameDurationEach, loop, null);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final boolean loop,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount(), loop);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final int loopCount) {
		this.animate(frameDurationEach, loopCount, null);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final int loopCount,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount(), loopCount);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity play(final String key) {
		return animate(key);
	}

	public AnimatedEntity play(final String key, final IAnimationListener animationListener) {
		return animate(key, animationListener);
	}

	public AnimatedEntity play(final String key, final boolean loop) {
		return animate(key, loop);
	}

	public AnimatedEntity play(final String key, final boolean loop, final IAnimationListener animationListener) {
		return animate(key, loop, animationListener);
	}

	public AnimatedEntity play(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop) {
		return animate(key, firstFrameIndex, lastFrameIndex, loop);
	}

	public AnimatedEntity play(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop, final IAnimationListener animationListener) {
		return animate(key, firstFrameIndex, lastFrameIndex, loop, animationListener);
	}

	public AnimatedEntity play(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount) {
		return animate(key, firstFrameIndex, lastFrameIndex, loopCount);
	}

	public AnimatedEntity play(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount, final IAnimationListener animationListener) {
		return animate(key, firstFrameIndex, lastFrameIndex, loopCount, animationListener);
	}

	public AnimatedEntity animate(final String key) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, play.animates);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final IAnimationListener animationListener) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, play.animates, animationListener);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final boolean loop) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, play.animates, loop);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final boolean loop, final IAnimationListener animationListener) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, play.animates, loop, animationListener);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, firstFrameIndex, lastFrameIndex, loop);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop, final IAnimationListener animationListener) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, firstFrameIndex, lastFrameIndex, loop, animationListener);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, firstFrameIndex, lastFrameIndex, loopCount);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount, final IAnimationListener animationListener) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, firstFrameIndex, lastFrameIndex, loopCount, animationListener);
		}
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations) {
		this.animate(frameDurations, (IAnimationListener) null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final boolean loop) {
		this.animate(frameDurations, loop, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final boolean loop,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, loop);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int loopCount) {
		this.animate(frameDurations, loopCount, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int loopCount,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, loopCount);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop) {
		this.animate(frameDurations, firstFrameIndex, lastFrameIndex, loop, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, firstFrameIndex, lastFrameIndex, loop);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount) {
		this.animate(frameDurations, firstFrameIndex, lastFrameIndex, loopCount, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, firstFrameIndex, lastFrameIndex, loopCount);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames) {
		this.animate(frameDurations, frames, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames, final boolean loop) {
		this.animate(frameDurations, frames, loop, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames, final boolean loop,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames, loop);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames, final int loopCount) {
		this.animate(frameDurations, frames, loopCount, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames, final int loopCount,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames, loopCount);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final AnimationData animationData) {
		this.animate(animationData, null);
		return this;
	}

	public AnimatedEntity animate(final AnimationData animationData, final IAnimationListener animationListener) {
		this._animationData.set(animationData);
		this.initAnimation(animationListener);
		return this;
	}

	private void initAnimation(final IAnimationListener animationListener) {
		this._animationStartedFired = false;
		this._animationListener = animationListener;
		this._remainingLoopCount = this._animationData.getLoopCount();
		this._animationProgress = 0;
		this._animationRunning = true;
	}

	public boolean isAnimationRunning() {
		return this._animationRunning;
	}

	@Override
	public AnimatedEntity pause() {
		super.pause();
		this.stopAnimation();
		return this;
	}

	@Override
	public AnimatedEntity resume() {
		super.resume();
		this.playAnimation();
		return this;
	}

	public AnimatedEntity setDelay(long d) {
		_animation.setDelay(d);
		return this;
	}

	public AnimatedEntity setDelayS(float s) {
		_animation.setDelayS(s);
		return this;
	}

	public AnimatedEntity setCurrentFrameIndex(int idx) {
		_animation.setCurrentFrameIndex(idx);
		return this;
	}

	public long getDelay() {
		return _animation.getDelay();
	}

	public float getDelayS() {
		return _animation.getDelayS();
	}

	public int getCount() {
		return _animation.length;
	}

	public AnimatedEntity setMaxFrame(int max) {
		_animation.setMaxFrame(max);
		return this;
	}

	public int getMaxFrame() {
		return _animation.getMaxFrame();
	}

	public AnimatedEntity setPlayIndex(final String key, final PlayIndex play) {
		_playEvents.put(key, play);
		return this;
	}

	public AnimatedEntity setPlayIndex(final String key, int[] animates) {
		return setPlayIndex(key, PlayIndex.at(animates));
	}

	public AnimatedEntity setPlayIndex(final String key, long frameTime, int[] animates) {
		return setPlayIndex(key, PlayIndex.at(frameTime, animates));
	}

	public AnimatedEntity setPlayIndex(final String key, long frameTime, int startIdx, int endIdx) {
		return setPlayIndex(key, PlayIndex.at(frameTime, startIdx, endIdx));
	}

	public AnimatedEntity setPlayIndex(final String key, long[] frameTime, int startIdx, int endIdx) {
		return setPlayIndex(key, PlayIndex.at(frameTime, startIdx, endIdx));
	}

	public PlayIndex getPlayIndex(final String key) {
		return _playEvents.get(key);
	}

	public PlayIndex removePlayIndex(final String key) {
		return _playEvents.remove(key);
	}

	public boolean containsPlayIndex(final String key) {
		return _playEvents.containsKey(key);
	}

	public String getCurrentAnimateName() {
		return this._currentAnimateName;
	}

	public boolean isCurrentAnimateName(final String key) {
		if (StringUtils.isNullOrEmpty(key)) {
			return false;
		}
		return key.equals(this._currentAnimateName);
	}

	public AnimatedEntity clearPlayEvents() {
		_playEvents.clear();
		return this;
	}

	@Override
	public int size() {
		return _playEvents.size;
	}

	public boolean isAnimationDispose() {
		return _animationDispose;
	}

	public AnimatedEntity setAnimationDispose(boolean dispose) {
		this._animationDispose = dispose;
		return this;
	}

	public FrameListener getFrameListener() {
		return _animation.getFrameListener();
	}

	public AnimatedEntity setFrameListener(FrameListener listener) {
		this._animation.setFrameListener(listener);
		return this;
	}

	@Override
	public void close() {
		super.close();
		this.stopAnimation();
		this.clearPlayEvents();
		if (_animationDispose && _animation != null) {
			_animation.close();
		}
	}

}
