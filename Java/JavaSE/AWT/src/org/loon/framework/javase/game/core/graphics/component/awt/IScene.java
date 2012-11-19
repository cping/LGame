package org.loon.framework.javase.game.core.graphics.component.awt;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

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
public interface IScene {

	public void addNotify();

	public Image getIconImage();

	public String getTitle();

	public boolean isResizable();

	public boolean isUndecorated();

	public void removeNotify();

	public void setIconImage(Image image);

	public void setResizable(boolean resizable);

	public void setTitle(String title);

	public void setUndecorated(boolean undecorated);
	
	public void addFocusListener(FocusListener l);
	
	public void  addMouseMotionListener(MouseMotionListener l);
	
	public void addWindowFocusListener(WindowFocusListener l);

	public void addWindowListener(WindowListener l);

	public void addWindowStateListener(WindowStateListener l);

	public void createBufferStrategy(int numBuffers, BufferCapabilities caps)
			throws AWTException;

	public void createBufferStrategy(int numBuffers);

	public boolean isFocusOwner();
	
	public void dispose();

	public VolatileImage createVolatileImage(int width,int height);
	
	public BufferStrategy getBufferStrategy();

	public Component getFocusOwner();

	public GraphicsConfiguration getGraphicsConfiguration();

	public boolean isFocused();

	public void pack();

	public void setBounds(int x, int y, int width, int height);

	public void setBounds(Rectangle arg0);

	public void setCursor(Cursor cursor);

	public void setFocusableWindowState(boolean focusableWindowState);

	public void setLocationRelativeTo(Component c);
	
	public void setSize(Dimension arg0);

	public void setSize(int arg0, int arg1);

	public void setVisible(boolean arg0);

	public Component add(Component comp, int index);

	public Component add(Component comp);

	public void doLayout();

	public Insets getInsets();

	public LayoutManager getLayout();

	public void remove(Component comp);

	public void remove(int index);

	public void removeAll();

	public void setFont(Font f);

	public void setLayout(LayoutManager mgr);

	public void validate();

	public void add(PopupMenu popup);

	public void addKeyListener(KeyListener l);

	public void addMouseListener(MouseListener l);

	public boolean contains(int x, int y);

	public boolean contains(Point p);

	public Rectangle getBounds();

	public Rectangle getBounds(Rectangle rv);

	public Font getFont();

	public Dimension getSize();

	public int getWidth();

	public int getHeight();

	public int getX();

	public int getY();
	
	public void setIgnoreRepaint(boolean repaint);

	public void requestFocus();

	public void setLocation(int x, int y);

	public void setLocation(Point p);
	
	public Window getWindow();
	
	public int getCodeType();

}
