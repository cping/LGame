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
	protected final class PreloadProcess extends RealtimeProcess {

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
			_control.setCurrentAsset(_assets.getFirstAsset());
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

	private AssetLoader _curAssetLoader;

	private boolean _closedFreeResource;

	private boolean _assetsFailure;

	private boolean _assetsLoading;

	private boolean _runThrowException;

	private int _loadedCount;

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
		if (loader == null) {
			throw new LSysException("PreloadLoader cannot be null !");
		}
		this._loader = loader;
		this._closedFreeResource = freeRes;
		this.loadAssets(others);
	}

	public void createPreloadAssets() {
		if (_closedFreeResource && this._preAssets != null) {
			this._preAssets.close();
			this._preAssets = null;
		}
		this._preAssets = new PreloadAssets(_runThrowException);
		this._assetsLoading = true;
		this._assetsFailure = false;
	}

	public PreloadControl runThrowException(boolean r) {
		_runThrowException = r;
		return this;
	}

	public boolean isRunThrowException() {
		return _runThrowException;
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
		try {
			if (_preMaxFileCount == 0) {
				this.prefinish();
			} else {
				PreloadProcess preload = new PreloadProcess(this, _preAssets, this._preMaxFileCount);
				preload.setProcessType(GameProcessType.Preload);
				preload.setDelay(this._preloadInterval);
				RealtimeProcessManager.get().addProcess(preload);
			}
		} catch (Exception e) {
			this.onError(e);
		}
	}

	/**
	 * 资源加载错误
	 * 
	 * @param e
	 */
	protected void onError(Exception e) {
		this._assetsFailure = true;
		LSystem.error("The Preload asynchronous loading failed ! " + e.getMessage());
		e.printStackTrace();
	}

	/**
	 * 资源加载完毕
	 */
	public void prefinish() {
		_loader.prefinish();
		_assetsLoading = false;
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

	public float getProgress() {
		if (_maxPercent >= 1f) {
			if (_percent > _maxPercent) {
				_percent = _maxPercent;
			}
			return (_percent * 1f / _maxPercent);
		}
		return 0f;
	}

	public float getRate() {
		final int total = this._preAssets.waiting();
		return total > 0 ? MathUtils.clamp(this._loadedCount, 0, total) / total : 1;
	}

	public int getLoadedCount() {
		return this._loadedCount;
	}

	public boolean isLoaded() {
		return MathUtils.equal(this._percent, this._maxPercent);
	}

	/**
	 * 资源加载错误
	 * 
	 * @return
	 */
	public boolean isAssetsFailure() {
		return this._assetsFailure;
	}

	/**
	 * 资源加载中
	 * 
	 * @return
	 */
	public boolean isAssetsLoading() {
		return this._assetsLoading;
	}

	public boolean isZero() {
		return MathUtils.equal(_percent, 0f);
	}

	public boolean isMax() {
		return MathUtils.equal(_percent, _maxPercent);
	}

	public PreloadControl prereload() {
		clear();
		prestart();
		return this;
	}

	protected void setCurrentAsset(AssetLoader asset) {
		_curAssetLoader = asset;
	}

	public AssetLoader getCurrentAsset() {
		return _curAssetLoader;
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
		_loadedCount++;
		return updatePercent(_percent++);
	}

	public PreloadControl removePercent() {
		_loadedCount--;
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
		this._assetsLoading = false;
	}

	public void clear() {
		_assetsLoading = _assetsFailure = false;
		_preMaxFileCount = _percent = _maxPercent = 0;
		_preloadInterval = 0;
		_loadedCount = 0;
	}

	public boolean isFinished() {
		return !_assetsLoading && _preMaxFileCount == 0;
	}

	@Override
	public void close() {
		if (_closedFreeResource) {
			dispose();
		}
		clear();
	}

}
