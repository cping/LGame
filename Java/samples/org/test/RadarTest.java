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
import loon.canvas.LColor;
import loon.component.LRadar;

public class RadarTest extends Stage {

	@Override
	public void create() {
		setBackground(LColor.yellow);
		LRadar radar = new LRadar(60, 60, 200, 300);
		//radar.setDrawMode(Mode.Octagon);
		radar.addDrop(125, 65, LColor.yellow);
		centerOn(radar);
		add(radar);
		add(MultiScreenTest.getBackButton(this, 2));
	}

}
