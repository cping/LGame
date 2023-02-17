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
package org.test;

import loon.Stage;
import loon.action.sprite.Picture;
import loon.component.LLabel;
import loon.utils.res.loaders.PreloadAssets;

public class PreloadTest extends Stage{

	private LLabel label;

	private Picture pic;

	@Override
	public void preload(PreloadAssets assets) {
		//加载110个数据
		for (int i = 0; i < 110; i++) {
			assets.context("test" + i, String.valueOf(i));
		}
		//加载test.txt别名r
		assets.text("test.txt", "r");
		//加载ball.png别名r1
		assets.texture("ball.png", "r1");
		label = LLabel.make("数据测试", 105, 155);
		add(label);
		pic = new Picture(120, 120);
		add(pic);
	}

	@Override
	public void preloadProgress(float p) {
		label.setText("数据加载进度 : " + (p * 100) + "%");
	}

	@Override
	public void create() {
		PreloadAssets preload = getPreloadAssets();
		pic.setImage(preload.getTexture("r1").get());
		add(MultiScreenTest.getBackButton(this,2));
	}

}
