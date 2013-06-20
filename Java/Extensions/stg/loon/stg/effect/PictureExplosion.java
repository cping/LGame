package loon.stg.effect;

import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.stg.STGPlane;


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
public class PictureExplosion implements Picture {

	int count = 0;

	int x;

	int y;

	PictureExplosion() {
		this.x = this.y = 5;
	}

	@Override
	public boolean paint(GLEx g, STGPlane p) {
		switch (this.count) {
		case 4:
			this.x = this.y = 27;
			break;
		case 8:
			this.x = 5;
			this.y = 27;
			break;
		case 12:
			this.x = 27;
			this.y = 5;
			break;
		case 16:
			this.x = this.y = 16;
		}
		g.setColor(LColor.yellow);
		g.fillOval(p.posX + this.x - this.count % 4 * 4, p.posY + this.y
				- this.count % 4 * 4, this.count % 4 * 8, this.count % 4 * 8);
		++this.count;
		return true;
	}

	@Override
	public void dispose() {
	
	}

}
