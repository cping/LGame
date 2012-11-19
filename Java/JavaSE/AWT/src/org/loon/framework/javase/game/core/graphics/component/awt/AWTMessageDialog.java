package org.loon.framework.javase.game.core.graphics.component.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.utils.GraphicsUtils;

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
public class AWTMessageDialog extends Dialog implements ActionListener,
		KeyListener, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Button button;

	protected AWTLabel label;

	public AWTMessageDialog(String title, String message) {
		this(LSystem.getSystemHandler().getWindow(), title, message);
	}

	public AWTMessageDialog(Window parent, String title, String message) {
		super((Frame) parent, title, true);
		this.setLayout(new BorderLayout());
		if (message == null) {
			message = "";
		}
		this.label = new AWTLabel(message);
		if (!LSystem.isLinux()) {
			label.setFont(GraphicsUtils.getFont("黑体", 0, 15));
		}
		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		panel.add(label);
		this.add("Center", panel);
		button = new Button("  确定  ");
		button.addActionListener(this);
		button.addKeyListener(this);
		panel = new Panel();
		panel.setLayout(new FlowLayout());
		panel.add(button);
		this.add("South", panel);
		this.setResizable(false);
		this.pack();
		LSystem.centerOn(this);
		this.addWindowListener(this);
		this.setModal(true);
	}

	public void actionPerformed(ActionEvent e) {
		dispose();
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE) {
			dispose();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		dispose();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}



}
