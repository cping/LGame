package loon.action.avg.drama;

import loon.LSystem;
import loon.Log;
import loon.utils.StringUtils;

/**
 * 脚本log
 *
 */
public class DefScriptLog implements IScriptLog {

	private boolean _show = true;

	private Log _log = null;

	public DefScriptLog() {
		_log = LSystem.base().log();
	}

	@Override
	public void err(String mes, Object... o) {
		if (!_show) {
			return;
		}
		if (o != null && o.length > 0) {
			_log.info(StringUtils.format(mes, o));
		} else {
			_log.info(mes);
		}
	}

	@Override
	public void info(String mes, Object... o) {
		if (!_show) {
			return;
		}
		if (o != null && o.length > 0) {
			_log.info(StringUtils.format(mes, o));
		} else {
			_log.info(mes);
		}
	}

	@Override
	public void err(Object mes) {
		if (!_show) {
			return;
		}
		if (mes != null) {
			_log.info(mes.toString());
		}
	}

	@Override
	public void info(Object mes) {
		if (!_show) {
			return;
		}
		if (mes != null) {
			_log.info(mes.toString());
		}
	}

	@Override
	public void line(Object mes) {
		if (!_show) {
			return;
		}
		if (mes != null) {
			_log.info(mes.toString());
		}
	}

	@Override
	public void show(boolean flag) {
		_show = flag;
	}

}
