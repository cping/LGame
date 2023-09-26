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

import loon.Counter;
import loon.Stage;
import loon.action.sprite.Entity;
import loon.component.LLabel;
import loon.events.Touched;

public class TweenTest extends Stage {

	@Override
	public void create() {
		Entity e = new Entity("ball.png", 66, 66);
		add(e);
		LLabel label = LLabel.make("testing");
		centerOn(label);
		add(label);
		down(new Touched() {

			@Override
			public void on(float x, float y) {
				Counter count = new Counter();
				e.selfAction().sizeTo(80, 80).doWhen(on -> {
					if (on.update(8 < 6)) {
						label.setText("when 1");
					}
				}, on -> {
					if (on.update(9 > 4)) {
						label.setText("when 2");
					}
				}).doWhile(ref -> {
					if (ref.update(count.getValue() <= 100)) {
						label.setText("testing:" + count.getValue());
						count.increment();
					}
				}).delay(3f).sizeTo(32, 32).start().dispose(() -> {
					label.setText("close");
				});

			}
		});
	}

}
