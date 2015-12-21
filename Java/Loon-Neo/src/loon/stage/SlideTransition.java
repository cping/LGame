package loon.stage;

public class SlideTransition extends EaseTransition<SlideTransition> {

	protected final float _originX, _originY;
	protected Dir _dir = Dir.LEFT;
	protected float _osx, _osy, _odx, _ody, _nsx, _nsy;

	public SlideTransition up() {
		return dir(Dir.UP);
	}

	public SlideTransition down() {
		return dir(Dir.DOWN);
	}

	public SlideTransition left() {
		return dir(Dir.LEFT);
	}

	public SlideTransition right() {
		return dir(Dir.RIGHT);
	}

	public SlideTransition dir(Dir dir) {
		_dir = dir;
		return this;
	}

	public SlideTransition(StageSystem stack) {
		_originX = stack.originX;
		_originY = stack.originY;
	}

	@Override
	public void init(Stage o, Stage n) {
		super.init(o, n);
		switch (_dir) {
		case UP:
			_odx = _originX;
			_ody = _originY - o.height();
			_nsx = _originX;
			_nsy = _originY + n.height();
			break;
		case DOWN:
			_odx = _originX;
			_ody = _originY + o.height();
			_nsx = _originX;
			_nsy = _originY - n.height();
			break;
		case LEFT:
		default:
			_odx = _originX - o.width();
			_ody = _originY;
			_nsx = _originX + n.width();
			_nsy = _originY;
			break;
		case RIGHT:
			_odx = _originX + o.width();
			_ody = _originY;
			_nsx = _originX - n.width();
			_nsy = _originY;
			break;
		}
		_osx = o.players.tx();
		_osy = o.players.ty();
		n.players.setTranslation(_nsx, _nsy);
	}

	@Override
	public boolean update(Stage o, Stage n, float elapsed) {
		float ox = _easing.applyClamp(_originX, _odx - _originX, elapsed,
				_duration);
		float oy = _easing.applyClamp(_originY, _ody - _originY, elapsed,
				_duration);
		o.players.setTranslation(ox, oy);
		float nx = _easing.applyClamp(_nsx, _originX - _nsx, elapsed, _duration);
		float ny = _easing.applyClamp(_nsy, _originY - _nsy, elapsed, _duration);
		n.players.setTranslation(nx, ny);
		return elapsed >= _duration;
	}

	@Override
	public void complete(Stage o, Stage n) {
		super.complete(o, n);
		o.players.setTranslation(_osx, _osy);
	}

	@Override
	protected float defaultDuration() {
		return 520;
	}
}
