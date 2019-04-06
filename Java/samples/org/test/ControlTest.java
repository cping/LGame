package org.test;

import loon.Stage;
import loon.component.LControl;
import loon.component.LControl.DigitalListener;

public class ControlTest extends Stage {

	@Override
	public void create() {

		LControl c = new LControl(66, 66);

		c.setControl(new DigitalListener() {

			@Override
			public void up45() {

			}

			@Override
			public void up() {

			}

			@Override
			public void right45() {

			}

			@Override
			public void right() {

			}

			@Override
			public void left45() {

			}

			@Override
			public void left() {

			}

			@Override
			public void down45() {

			}

			@Override
			public void down() {

			}
		});
		add(c);

		add(MultiScreenTest.getBackButton(this,0));
	}

}
