package loon.action;

/**
 * 单纯删除指定动作对象的所有缓动动画事件
 */
public class RemoveActionsTo extends ActionEvent {

	private ActionBind _removeBind;

	public RemoveActionsTo() {
		this(null);
	}

	public RemoveActionsTo(ActionBind bind) {
		this._removeBind = bind;
	}

	@Override
	public void update(long elapsedTime) {
		if (_isCompleted) {
			return;
		}
		if (_removeBind != null) {
			ActionControl.get().removeAllActions(_removeBind);
		}
		this._isCompleted = true;
	}

	@Override
	public void onLoad() {
		if (_removeBind == null) {
			_removeBind = original;
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		RemoveActionsTo update = new RemoveActionsTo(_removeBind);
		update.set(this);
		return update;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "remove";
	}
}
