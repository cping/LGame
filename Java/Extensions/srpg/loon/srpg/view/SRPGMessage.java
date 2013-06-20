package loon.srpg.view;

import loon.core.LSystem;
import loon.core.geom.Vector2f;
import loon.core.graphics.LFont;
import loon.core.graphics.component.Print;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;


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
public class SRPGMessage {

	private long printTime, totalDuration;

	private Print print;

	private LFont font;

	private String str;

	public SRPGMessage(String s, LFont font, int x, int y, int w, int h) {
		set(s, font, x, y, w, h);
	}

	private void set(String s, LFont font, int x, int y, int w, int h) {
		this.str = s;
		this.font = font;
		this.print = new Print(new Vector2f(x, y), font, w, h);
		this.print.setWait(false);
		this.setMessage(s);
		this.setTipIcon(LSystem.FRAMEWORK_IMG_NAME + "creese.png");
		this.setDelay(100);
	}

	public String getString() {
		return str;
	}

	public void setDelay(long delay) {
		this.totalDuration = (delay < 1 ? 1 : delay);
	}

	public long getDelay() {
		return totalDuration;
	}

	public void setMessage(String context, boolean isComplete) {
		print.setMessage(context, font, isComplete);
	}

	public void setMessage(String s) {
		print.setMessage(s, font);
	}

	public boolean next() {
		return print.next();
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public void setWidth(int w) {
		print.setWidth(w);
	}

	public void setHeight(int h) {
		print.setHeight(h);
	}

	public int getWidth() {
		return print.getWidth();
	}

	public int getHeight() {
		return print.getHeight();
	}

	public void setX(int x) {
		print.setX(x);
	}

	public void setY(int y) {
		print.setY(y);
	}

	public int getX() {
		return print.getX();
	}

	public int getY() {
		return print.getY();
	}

	public void complete() {
		print.complete();
	}

	public boolean isComplete() {
		return print.isComplete();
	}

	public void setEnglish(boolean e) {
		print.setEnglish(true);
	}

	public boolean isEnglish() {
		return print.isEnglish();
	}

	public String getMessage() {
		return print.getMessage();
	}

	public void setLeftOffset(int left) {
		print.setLeftOffset(left);
	}

	public void setTopOffset(int top) {
		print.setTopOffset(top);
	}

	public int getLeftOffset() {
		return print.getLeftOffset();
	}

	public int getTopOffset() {
		return print.getTopOffset();
	}

	public int getMessageLength() {
		return print.getMessageLength();
	}

	public void setMessageLength(int messageLength) {
		print.setMessageLength(messageLength);
	}

	public void setTipIcon(String fileName) {
		print.setCreeseIcon(new LTexture(fileName));
	}

	public void setTipIcon(LTexture icon) {
		print.setCreeseIcon(icon);
	}

	public void setNotTipIcon() {
		print.setCreeseIcon(null);
	}

	public void update(long elapsedTime) {
		printTime += elapsedTime;
		if (printTime >= totalDuration) {
			printTime = printTime % totalDuration;
			print.next();
		}
	}

	public void left() {
		print.setLeft(true);
	}

	public LFont getFont() {
		return font;
	}

	public void draw(GLEx g) {
		print.draw(g);
	}

}
