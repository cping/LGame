package org.loon.framework.javase.game.core.graphics.component.awt;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
public class AWTInputDialog extends Dialog implements ActionListener,
		KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Button button;

	private AWTLabel label;

	private TextField textMessage;
	
	public AWTInputDialog(String title, String message) {
		this(LSystem.getSystemHandler().getWindow(), title, message);
	}
	
	public AWTInputDialog(Window parent, String title, String message) {
		super((Frame) parent, title, true);

		if (message == null) {
			message = "";
		}

		setLayout(null);

		textMessage = new TextField();
		label = new AWTLabel(message);
		button = new Button();

		textMessage.setText("");
		textMessage.setFont(GraphicsUtils.getFont("黑体", 0, 25));
		textMessage.setBounds(30, 60, 270, 40);
		add(textMessage);
		textMessage.addActionListener(this);
		
		label.setBounds(25, 30, 360, 22);
		label.setFont(GraphicsUtils.getFont("黑体", 0, 15));
		add(label);

		button.setLabel(" 确定输入 ");
		button.setBounds(310, 60, 80, 40);
		add(button);
		button.addActionListener(this);
		
		this.pack();
		this.setSize(415, 120);
		this.setResizable(false);
		this.setModal(true);

		LSystem.centerOn(this);
	}
	
	public String getTextMessage(){
		return textMessage.getText();
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
	

}
