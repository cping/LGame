package org.loon.framework.javase.game.core.graphics.component.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.loon.framework.javase.game.core.LSystem;

/**
 * Copyright 2008 - 2009
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
public class AWTYesNoCancelDialog extends Dialog implements ActionListener,
		KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Button yesButton, noButton, cancelButton;

	private boolean cancelPressed, yesPressed, firstPaint = true;

	public AWTYesNoCancelDialog(String title, String message) {
		this(LSystem.getSystemHandler().getWindow(), title, message);
	}

	public AWTYesNoCancelDialog(Window parent, String title, String message) {
		super((Frame) parent, title, true);
		this.setLayout(new BorderLayout());
		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		AWTLabel label = new AWTLabel(message);
		label.setFont(new Font("黑体", 0, 15));
		panel.add(label);
		this.add("North", panel);
		panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 8));
		this.yesButton = new Button(" 确定 ");
		this.noButton = new Button(" 取消 ");
		this.cancelButton = new Button(" 放弃 ");
		this.yesButton.addActionListener(this);
		this.noButton.addActionListener(this);
		this.cancelButton.addActionListener(this);
		this.yesButton.addKeyListener(this);
		this.noButton.addKeyListener(this);
		this.cancelButton.addKeyListener(this);
		if (LSystem.isMacOS()) {
			panel.add(noButton);
			panel.add(cancelButton);
			panel.add(yesButton);
			setResizable(false);
		} else {
			panel.add(yesButton);
			panel.add(noButton);
			panel.add(cancelButton);
		}
		this.add("South", panel);
		this.setResizable(false);
		this.setModal(true);
		this.pack();
		LSystem.centerOn(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			cancelPressed = true;
		} else if (e.getSource() == yesButton) {
			yesPressed = true;
		}
		closeDialog();
	}

	public boolean cancelPressed() {
		return cancelPressed;
	}

	public boolean yesPressed() {
		return yesPressed;
	}

	void closeDialog() {
		setVisible(false);
		dispose();
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_Y
				|| keyCode == KeyEvent.VK_S) {
			yesPressed = true;
			closeDialog();
		} else if (keyCode == KeyEvent.VK_N || keyCode == KeyEvent.VK_D) {
			closeDialog();
		} else if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_C) {
			cancelPressed = true;
			closeDialog();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (firstPaint) {
			yesButton.requestFocus();
			firstPaint = false;
		}
	}

}
