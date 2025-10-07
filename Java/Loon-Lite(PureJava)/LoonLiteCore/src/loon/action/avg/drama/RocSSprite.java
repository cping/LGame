package loon.action.avg.drama;

import loon.LObject;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.action.sprite.ISprite;
import loon.action.sprite.SpriteCollisionListener;
import loon.action.sprite.Sprites;
import loon.canvas.LColor;
import loon.events.ResizeListener;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;

/**
 * 这是一个特殊的精灵类，它并不执行任何渲染或者图像操作，而是用于添加一个脚本循环到游戏中去
 */
public class RocSSprite extends LObject<ISprite> implements ISprite {

	private ResizeListener<RocSSprite> _resizeListener;

	private boolean _visible;

	private RocSTask _task;

	private Object _result = null;

	private Sprites _sprites = null;

	private ISprite _sprite = null;

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
			this._task = new RocSTask(script, useScriptFile, debug, delay);
			this._visible = true;
			this.setDelay(delay);
			this.setName("RocSSprite");
		} catch (Exception ex) {
			throw new LSysException("ROC Script load exception", ex);
		}
	}

	public long getDelay() {
		return _task.getDelay();
	}

	public RocSSprite setDelay(long d) {
		this._task.setDelay(d);
		return this;
	}

	public float getDelayS() {
		return _task.getDelayS();
	}

	public Object getResult() {
		return this._result;
	}

	@Override
	public void update(long elapsedTime) {
		if (_visible) {
			_task.call(elapsedTime);
			if (_sprite != null) {
				_sprite.update(elapsedTime);
			}
		}
	}

	@Override
	public float getWidth() {
		return _sprite != null ? _sprite.getWidth() : 1f;
	}

	@Override
	public float getHeight() {
		return _sprite != null ? _sprite.getHeight() : 1f;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
		if (_sprite != null) {
			_sprite.setVisible(v);
		}
	}

	@Override
	public boolean isVisible() {
		return _sprite != null ? _sprite.isVisible() : _visible;
	}

	@Override
	public void createUI(GLEx g) {
		if (_sprite != null) {
			_sprite.createUI(g);
		}
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (_sprite != null) {
			_sprite.createUI(g, offsetX, offsetY);
		}
	}

	@Override
	public RectBox getCollisionBox() {
		return _sprite != null ? _sprite.getCollisionBox() : null;
	}

	@Override
	public LTexture getBitmap() {
		return _sprite != null ? _sprite.getBitmap() : null;
	}

	/**
	 * 返回脚本解释器
	 *
	 * @return
	 */
	public RocScript getScript() {
		return _task.getScript();
	}

	/**
	 * 如果此函数为true，则循环解析脚本
	 *
	 * @return
	 */
	public boolean isLoopScript() {
		return _task.isLoopScript();
	}

	/**
	 * 如果此函数为true，则循环解析脚本
	 *
	 * @param l
	 */
	public void setLoopScript(boolean l) {
		this._task.setLoopScript(l);
	}

	@Override
	public void setColor(LColor c) {
		if (_sprite != null) {
			_sprite.setColor(c);
		}
	}

	@Override
	public LColor getColor() {
		return _sprite != null ? _sprite.getColor() : null;
	}

	@Override
	public String toString() {
		return _task.getScript().toString();
	}

	@Override
	public Field2D getField2D() {
		return _sprite != null ? _sprite.getField2D() : null;
	}

	@Override
	public float getScaleX() {
		return _sprite != null ? _sprite.getScaleX() : 1f;
	}

	@Override
	public float getScaleY() {
		return _sprite != null ? _sprite.getScaleY() : 1f;
	}

	public RocSSprite setScale(float scale) {
		setScale(scale, scale);
		return this;
	}

	@Override
	public void setScale(float sx, float sy) {
		if (_sprite != null) {
			_sprite.setScale(sx, sy);
		}
	}

	@Override
	public boolean isBounded() {
		return _sprite != null ? _sprite.isBounded() : false;
	}

	@Override
	public boolean isContainer() {
		return _sprite != null ? _sprite.isContainer() : false;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return _sprite != null ? _sprite.inContains(x, y, w, h) : false;
	}

	@Override
	public RectBox getRectBox() {
		return _sprite != null ? _sprite.getRectBox() : null;
	}

	@Override
	public ActionTween selfAction() {
		return _sprite != null ? _sprite.selfAction() : PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return _sprite != null ? _sprite.isActionCompleted() : PlayerUtils.isActionCompleted(this);
	}

	@Override
	public ISprite setSprites(Sprites ss) {
		if (this._sprites == ss) {
			return this;
		}
		if (_sprite != null) {
			_sprite.setSprites(ss);
		}
		this._sprites = ss;
		return this;
	}

	@Override
	public Sprites getSprites() {
		return _sprite != null ? _sprite.getSprites() : this._sprites;
	}

	@Override
	public Screen getScreen() {
		if (_sprite != null) {
			return _sprite.getScreen();
		}
		if (this._sprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this._sprites.getScreen() == null ? LSystem.getProcess().getScreen() : this._sprites.getScreen();
	}

	@Override
	public float getFixedWidthOffset() {
		return _sprite != null ? _sprite.getFixedWidthOffset() : 0f;
	}

	@Override
	public ISprite setFixedWidthOffset(float widthOffset) {
		if (_sprite != null) {
			_sprite.setFixedWidthOffset(widthOffset);
		}
		return this;
	}

	@Override
	public float getFixedHeightOffset() {
		return _sprite != null ? _sprite.getFixedHeightOffset() : 0f;
	}

	@Override
	public ISprite setFixedHeightOffset(float heightOffset) {
		if (_sprite != null) {
			_sprite.setFixedHeightOffset(heightOffset);
		}
		return this;
	}

	@Override
	public boolean collides(ISprite other) {
		return _sprite != null ? _sprite.collides(other) : false;
	}

	@Override
	public boolean collidesX(ISprite other) {
		return _sprite != null ? _sprite.collidesX(other) : false;
	}

	@Override
	public boolean collidesY(ISprite other) {
		return _sprite != null ? _sprite.collidesY(other) : false;
	}

	@Override
	public float getOffsetX() {
		return _sprite != null ? _sprite.getOffsetX() : 0f;
	}

	@Override
	public float getOffsetY() {
		return _sprite != null ? _sprite.getOffsetY() : 0f;
	}

	public ResizeListener<RocSSprite> getResizeListener() {
		return _resizeListener;
	}

	public RocSSprite setResizeListener(ResizeListener<RocSSprite> listener) {
		this._resizeListener = listener;
		return this;
	}

	@Override
	public void onResize() {
		if (_resizeListener != null) {
			if (_sprite != null) {
				_sprite.onResize();
			}
			_resizeListener.onResize(this);
		}
	}

	@Override
	public void onCollision(ISprite coll, int dir) {
		if (_sprite != null) {
			_sprite.onCollision(coll, dir);
		}
	}

	@Override
	public ISprite triggerCollision(SpriteCollisionListener sc) {
		return this;
	}

	@Override
	public ISprite setOffset(Vector2f v) {
		if (_sprite != null) {
			_sprite.setOffset(v);
		}
		return this;
	}

	@Override
	public ISprite setSize(float w, float h) {
		if (_sprite != null) {
			_sprite.setSize(w, h);
		}
		return this;
	}

	@Override
	public boolean showShadow() {
		if (_sprite != null) {
			_sprite.showShadow();
		}
		return false;
	}

	@Override
	public boolean autoXYSort() {
		if (_sprite != null) {
			return _sprite.autoXYSort();
		}
		return false;
	}

	@Override
	public ISprite buildToScreen() {
		if (_sprites != null) {
			_sprites.add(this);
			return this;
		}
		getScreen().add(this);
		return this;
	}

	@Override
	public ISprite removeFromScreen() {
		if (_sprites != null) {
			_sprites.remove(this);
			return this;
		}
		getScreen().remove(this);
		return this;
	}

	public ISprite getSprite() {
		return _sprite;
	}

	public ISprite setSprite(ISprite s) {
		this._sprite = s;
		return this;
	}

	@Override
	public ISprite resetAnchor() {
		return this;
	}

	@Override
	public ISprite setAnchor(float sx, float sy) {
		return this;
	}

	@Override
	protected void _onDestroy() {
		if (_sprite != null) {
			_sprite.close();
		}
		_resizeListener = null;
	}

}
