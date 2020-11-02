package loon.srpg.view;

import loon.core.graphics.Screen;
import loon.srpg.field.SRPGField;


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
public class SRPGFieldChoiceView extends SRPGView {

	private int width, height;

	private int tileWidth, tileHeight;

	private int[] select;
	
	public SRPGFieldChoiceView() {
		super.exist = false;
		super.cache = false;
	}

	public SRPGFieldChoiceView(int tileWidth, int tileHeight, int w, int h) {
	this.set(tileWidth, tileHeight, w, h);
	}

	public SRPGFieldChoiceView(SRPGField field) {
		this.set(field.getTileWidth(), field.getTileHeight(), field.getWidth(),
				field.getHeight());
	}
	
	public void set(SRPGField field) {
		this.set(field.getTileWidth(), field.getTileHeight(), field.getWidth(),
				field.getHeight());
	}
	
	public void set(int tileWidth, int tileHeight, int w, int h) {
		super.exist = true;
		super.cache = false;
		this.width = w;
		this.height = h;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.select = new int[2];
		this.select[0] = -1;
		this.select[1] = -1;
	}

	public void setFieldSelect(int[] res) {
		select = res;
	}

	public void setX(int i) {
		select[0] = i;
	}

	public void setY(int i) {
		select[1] = i;
	}

	public boolean fieldSelect(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}
		x /= tileWidth;
		y /= tileHeight;
		if (x < width && y < height) {
			select[0] = x;
			select[1] = y;
			return true;
		} else {
			return false;
		}
	}

	public void fieldSelectInput(int x, int y) {
		if (fieldSelect(x, y)) {
			setExist(false);
			setCacheExist(true);
		}
	}

	public int[] getContent() {
		return select;
	}

	public int[] getCacheContent() {
		setExist(false);
		setCacheExist(false);
		return select;
	}

	public int[] choiceWait() {
		return choiceWait(false);
	}

	public int[] choiceWait(boolean flag) {
		if (!viewWait(flag)) {
			getCacheContent();
			return null;
		} else {
			return getCacheContent();
		}
	}

	public int[] choiceWait(Screen screen) {
		return choiceWait(screen, false);
	}

	public int[] choiceWait(Screen screen, boolean flag) {
		if (!viewWait(screen, flag)) {
			getCacheContent();
			return null;
		} else {
			return getCacheContent();
		}
	}

}
