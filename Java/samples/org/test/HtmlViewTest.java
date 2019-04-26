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

import loon.HorizontalAlign;
import loon.Stage;
import loon.canvas.LColor;
import loon.component.LHtmlView;
import loon.utils.HtmlCmd;

public class HtmlViewTest extends Stage{

	@Override
	public void create() {
		HtmlCmd html = new HtmlCmd();
		html.body();
		html.b("text").brn();
		html.p("teteett", HorizontalAlign.CENTER);
		html.fontSize(3).color("red").text("it is red text!").end().brn();
		html.fontSize(2).color("blue").text("it is blue text!").end().brn();
		html.fontFace("dialog").color("green").text("it is green text!").end();
		html.end();
		LHtmlView view = new LHtmlView(0, 0);
		view.loadText(html.toString());
		add(view);
		setBackground(LColor.white);
	}

}
