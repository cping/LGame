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
package loon.utils.res.loaders;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

/**
 * 资源预加载控制用类
 */
public class PreloadControl implements LRelease {

	/**
	 * 预加载进度管理器
	 */
	protected class PreloadProcess extends RealtimeProcess {

		private PreloadControl _control;

		private PreloadAssets _assets;

		private float _maxValue;

		public PreloadProcess(PreloadControl control, PreloadAssets assets, float max) {
			this._control = control;
			this._assets = assets;
			this._maxValue = max;
		}

		@Override
		public void run(LTimerContext time) {

			if (!_assets.completed()) {
				_assets.detection();
			}

			_control.updatePercent((_maxValue - _assets.waiting()), _maxValue);
			_control.preloadProgress(_control._percent);

			if (_assets.completed()) {
				_control.prefinish();
				kill();
			}

		}
	}

	private PreloadAssets _preAssets;

	private TArray<String> _otherAssets;

	private boolean _closedFreeResource;

	private float _preMaxFileCount;

	private float _percent;

	private float _maxPercent;

	private long _preloadInterval;

	private PreloadLoader _loader;

	public PreloadControl(PreloadLoader loader) {
		this(loader, false);
	}

	public PreloadControl(PreloadLoader loader, boolean freeRes) {
		this(loader, null, freeRes);
	}

	public PreloadControl(PreloadLoader loader, TArray<String> others, boolean freeRes) {
		this._loader = loader;
		this._closedFreeResource = freeRes;
		this.loadAssets(others);
	}

	public void createPreloadAssets() {
		if (_closedFreeResource && this._preAssets != null) {
			this._preAssets.close();
			this._preAssets = null;
		}
		this._preAssets = new PreloadAssets();
	}

	public PreloadControl loadAssets(String... others) {
		if (others == null || others.length == 0) {
			return this;
		}
		return loadAssets(StringUtils.getStringsToList(others));
	}

	public PreloadControl loadAssets(TArray<String> others) {
		if (others != null) {
			this._otherAssets = new TArray<String>(others);
		}
		return this;
	}

	/**
	 * 开始加载资源
	 */
	public void prestart() {
		this.createPreloadAssets();
		this.preload(_preAssets);
		if (_otherAssets != null) {
			for (int i = 0; i < _otherAssets.size; i++) {
				String path = _otherAssets.get(i);
				if (StringUtils.isNotEmpty(path)) {
					_preAssets.load(path);
				}
			}
		}
		this.setPercentMax(this._preMaxFileCount = _preAssets.waiting());
		if (_preMaxFileCount > LSystem.DEFAULT_MAX_PRE_SIZE) {
			throw new LSysException(
					"The count of preloaded data cannot be greater than " + LSystem.DEFAULT_MAX_PRE_SIZE);
		}
		if (_preMaxFileCount == 0) {
			this.prefinish();
		} else {
			PreloadProcess preload = new PreloadProcess(this, _preAssets, this._preMaxFileCount);
			preload.setProcessType(GameProcessType.Preload);
			preload.setDelay(this._preloadInterval);
			RealtimeProcessManager.get().addProcess(preload);
		}
	}

	/**
	 * 资源加载完毕
	 */
	public void prefinish() {
		_loader.prefinish();
	}

	/**
	 * 资源预加载用函数,异步加载指定资源
	 * 
	 * @param assets
	 */
	public void preload(PreloadAssets assets) {
		_loader.preload(assets);
	}

	/**
	 * 预载资源已完成进度
	 * 
	 * @param percent
	 */
	public void preloadProgress(float percent) {
		_loader.preloadProgress(percent);
	}

	public PreloadControl prereload() {
		clear();
		prestart();
		return this;
	}

	public PreloadAssets getPreloadAssets() {
		return this._preAssets;
	}

	public PreloadControl setPercentMax(float max) {
		this._maxPercent = MathUtils.clamp(max, 0f, LSystem.DEFAULT_MAX_PRE_SIZE);
		return this;
	}

	public PreloadControl setPercent(float cur) {
		return setPercent(cur, LSystem.DEFAULT_MAX_PRE_SIZE);
	}

	public PreloadControl setPercent(float cur, float max) {
		this._percent = MathUtils.clamp(cur, 0f, LSystem.DEFAULT_MAX_PRE_SIZE);
		this._maxPercent = MathUtils.clamp(max, 0f, LSystem.DEFAULT_MAX_PRE_SIZE);
		return this;
	}

	public PreloadControl updatePercent(float num) {
		return updatePercent(num, _maxPercent);
	}

	public PreloadControl updatePercent(float num, float max) {
		this._percent = MathUtils.clamp(num, 0f, LSystem.DEFAULT_MAX_PRE_SIZE) / max;
		return this;
	}

	public PreloadControl addPercent() {
		return updatePercent(_percent++);
	}

	public PreloadControl removePercent() {
		return updatePercent(_percent--);
	}

	public PreloadControl resetPercent() {
		this._percent = 0f;
		return this;
	}

	public float getMaxPercent() {
		return _maxPercent;
	}

	public float getPercent() {
		return _percent;
	}

	public float getPreloadInterval() {
		return _preloadInterval / LSystem.SECOND;
	}

	public PreloadControl setPreloadInterval(float second) {
		this._preloadInterval = (long) (LSystem.SECOND * second);
		return this;
	}

	public boolean isClosedFreeResource() {
		return _closedFreeResource;
	}

	public PreloadControl setClosedFreeResource(boolean c) {
		this._closedFreeResource = c;
		return this;
	}

	public void dispose() {
		if (this._preAssets != null) {
			this._preAssets.close();
			this._preAssets = null;
		}
		if (this._otherAssets != null) {
			this._otherAssets.clear();
			this._otherAssets = null;
		}
	}

	public void clear() {
		_preMaxFileCount = _percent = _maxPercent = 0;
		_preloadInterval = 0;
	}

	public boolean isFinished() {
		return _preMaxFileCount == 0;
	}

	@Override
	public void close() {
		if (_closedFreeResource) {
			dispose();
		}
		clear();
	}

}
