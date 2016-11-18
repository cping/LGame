package loon.action;

import loon.utils.Array;

public class ReplayTo extends ActionEvent {

	private Array<ActionEvent> repeatList;

	Array<ActionEvent> replays;

	Array<ActionEvent> _cache_list;

	ActionEvent currentEvent;

	boolean replay;

	int count;

	public ReplayTo(Array<ActionEvent> list) {
		this(list, true);
	}

	public ReplayTo(Array<ActionEvent> list, boolean rp) {
		this.replay = rp;
		this.set(list);
	}

	public ReplayTo set(Array<ActionEvent> list) {
		if (list == null || list.size() == 0) {
			return this;
		}
		this._cache_list = new Array<ActionEvent>(list);
		this.replays = new Array<ActionEvent>();
		for (; _cache_list.hashNext();) {
			ActionEvent result = _cache_list.next();
			if (result != null) {
				replays.add(result);
			}
		}
		_cache_list.stopNext();
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		if (replays == null) {
			_isCompleted = true;
			return;
		}
		if (replays != null) {
			if (currentEvent != null && !currentEvent.isComplete()) {
				return;
			} else if (currentEvent != null && currentEvent.isComplete()) {
				if (repeatList == null) {
					repeatList = new Array<ActionEvent>();
				}
				if (!(currentEvent instanceof ReplayTo)) {
					repeatList.add(currentEvent);
				}
			}
			ActionEvent event = replays.last();
			if (event != currentEvent && event != null) {
				replays.remove();
				if (replay) {
					if (event instanceof ReplayTo && repeatList != null
							&& repeatList.size() > 0) {
						Array<ActionEvent> tmp = new Array<ActionEvent>();
						for (; repeatList.hashNext();) {
							tmp.add(repeatList.next().reverse());
						}
						repeatList.stopNext();
						((ReplayTo) event).set(tmp);
						repeatList.clear();
						repeatList.addAll(tmp);
					}
				}
				ActionControl.get().addAction(event, original);
				currentEvent = event;
			} else {
				_isCompleted = true;
			}
		}
	}

	@Override
	public void onLoad() {
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		return new ReplayTo(_cache_list, replay);
	}

	@Override
	public ActionEvent reverse() {
		if (_cache_list == null || _cache_list.size() == 0) {
			return null;
		}
		return new ReplayTo(_cache_list.reverse(), replay);
	}

	@Override
	public String getName() {
		return "replay";
	}
}
