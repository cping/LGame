package loon.action.avg.drama;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionTween;
import loon.action.avg.drama.RocScript.ScriptException;
import loon.action.map.Field2D;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 这是一个特殊的精灵类，它并不执行任何渲染或者图像操作，而是用于添加一个脚本循环到游戏中去
 */
public class RocSSprite extends LObject<ISprite> implements ISprite {

	private boolean _visible, _loopScript;

	private RocScript _script;

	private long _delay;

	private LTimer _waitTimer;

	private Object _result = null;

	private Sprites _sprites = null;

	public RocSSprite(CommandLink link) {
		this(link.getValue(), false, false);
	}

	public RocSSprite(CommandLink link, boolean useScriptFile) {
		this(link.getValue(), useScriptFile, false);
	}

	public RocSSprite(String script, boolean useScriptFile) {
		this(script, useScriptFile, false);
	}

	public RocSSprite(String script, boolean useScriptFile, boolean debug) {
		this(script, useScriptFile, debug, 0);
	}

	public RocSSprite(String script, boolean useScriptFile, boolean debug, long delay) {
		try {
			this._script = new RocScript(script, useScriptFile);
			this._script.call(debug);
			this._delay = delay;
			this._waitTimer = new LTimer(_delay);
			this._visible = true;
			this._loopScript = false;
			this.setDelay(delay);
			this.setName("RocSSprite");
		} catch (ScriptException ex) {
			throw LSystem.runThrow("ROC Script load exception", ex);
		}
	}

	public long getDelay() {
		return _waitTimer.getDelay();
	}

	public void setDelay(long d) {
		this._delay = d;
		this._waitTimer.setDelay(_delay);
	}

	public Object getResult() {
		return this._result;
	}

	@Override
	public void update(long elapsedTime) {
		if (_visible) {
			if (_script != null) {
				try {
					if (_waitTimer.action(elapsedTime)) {
						_script.resetWait();
						_waitTimer.setDelay(_delay);
						for (; !_script.isCompleted();) {
							_result = _script.next();
							long waitTime = _script.waitSleep();
							if (waitTime != -1) {
								if (waitTime == RocFunctions.JUMP_TYPE) {
									_script.reset();
									return;
								}
								_waitTimer.setDelay(waitTime);
								return;
							}
						}
						if (_loopScript && _script.isCompleted()) {
							_script.reset();
						}
					}
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public float getWidth() {
		return 1;
	}

	@Override
	public float getHeight() {
		return 1;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public void createUI(GLEx g) {

	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {

	}

	@Override
	public RectBox getCollisionBox() {
		return null;
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	/**
	 * 返回脚本解释器
	 * 
	 * @return
	 */
	public RocScript getScript() {
		return _script;
	}

	/**
	 * 如果此函数为true，则循环解析脚本
	 * 
	 * @return
	 */
	public boolean isLoopScript() {
		return _loopScript;
	}

	/**
	 * 如果此函数为true，则循环解析脚本
	 * 
	 * @param l
	 */
	public void setLoopScript(boolean l) {
		this._loopScript = l;
	}

	@Override
	public void setColor(LColor c) {

	}

	@Override
	public LColor getColor() {
		return null;
	}

	@Override
	public String toString() {
		return _script.toString();
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	@Override
	public float getScaleX() {
		return 0;
	}

	@Override
	public float getScaleY() {
		return 0;
	}

	@Override
	public void setScale(float sx, float sy) {
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return false;
	}

	@Override
	public RectBox getRectBox() {
		return null;
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	@Override
	public void setSprites(Sprites ss) {
		if (this._sprites == ss) {
			return;
		}
		this._sprites = ss;
	}

	@Override
	public Sprites getSprites() {
		return this._sprites;
	}

	@Override
	public Screen getScreen() {
		if (this._sprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this._sprites.getScreen() == null ? LSystem.getProcess().getScreen() : this._sprites.getScreen();
	}

	public boolean isClosed() {
		return isDisposed();
	}

	@Override
	public void close() {
		setState(State.DISPOSED);
	}
}
