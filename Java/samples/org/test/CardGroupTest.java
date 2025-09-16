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
import loon.component.LCardGroup;
import loon.component.LPaper;

public class CardGroupTest extends Stage {

	@Override
	public void create() {
		LCardGroup cards = new LCardGroup();

		for (int i = 0; i < 5; i++) {
			cards.add(new LPaper("assets/1.png"));
		}
		// cards.setClickCardToMoveUp(false);
		// cards.updateCards();
		add(cards);

	}

}
