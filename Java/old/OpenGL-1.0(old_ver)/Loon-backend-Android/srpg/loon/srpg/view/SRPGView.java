package loon.srpg.view;

import loon.core.LRelease;
import loon.core.graphics.Screen;


/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class SRPGView implements LRelease {

	protected boolean cancelflag;

	protected boolean exist;

	protected boolean cache;

	protected SRPGView() {
		this.exist = false;
		this.cache = false;
	}

	public boolean viewWait() {
		return viewWait(false);
	}

	public boolean viewWait(boolean flag) {
		boolean ret = false;
		do {
			if (isCacheExist() || !isExist()) {
				break;
			}
			if (!cancelflag || !flag) {
				continue;
			}
			ret = cancelflag;
			break;
		} while (true);
		setCancel(false);
		if (flag) {
			return !ret;
		} else {
			return true;
		}
	}

	public boolean viewWait(Screen screen) {
		return viewWait(screen, false);
	}

	public boolean viewWait(Screen screen, boolean flag) {
		boolean ret = false;
		do {
			if (isCacheExist() || !isExist()) {
				break;
			}
			if (cancelflag && flag) {
				ret = cancelflag;
				break;
			}
			try {
				screen.wait();
			} catch (Exception ex) {
			}
		} while (true);
		setCancel(false);
		try {
			screen.wait();
		} catch (Exception ex) {
		}
		if (flag) {
			return !ret;
		} else {
			return true;
		}

	}

	public void setCancel(boolean flag) {
		cancelflag = flag;
	}

	public boolean getCancel() {
		return cancelflag;
	}

	public boolean isCacheExist() {
		return cache;
	}

	public void setCacheExist(boolean flag) {
		this.cache = flag;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean flag) {
		this.exist = flag;

	}

	@Override
	public void dispose() {

	}

}
