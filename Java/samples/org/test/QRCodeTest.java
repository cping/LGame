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
import loon.canvas.LColor;
import loon.utils.qrcode.QRCode;
import loon.utils.qrcode.QRErrorLevel;

public class QRCodeTest extends Stage {

	@Override
	public void create() {

		setBackground("back1.png");

		addLabel("扫描我,打开GITHUB", 155, 50);

		// 添加一个二维码Picture,容错等级高,大小200x200,黑色
		Picture pic = QRCode.getQRCode("https://github.com", QRErrorLevel.H)
				.createPicture(200, 200, LColor.black);
		add(pic);
		centerOn(pic);

		add(MultiScreenTest.getBackButton(this, 1));
	}

}
