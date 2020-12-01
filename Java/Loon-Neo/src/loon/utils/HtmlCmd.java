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
package loon.utils;

import loon.LSystem;
import loon.canvas.LColor;
import loon.component.layout.HorizontalAlign;

/**
 * Html构建器,用于构建html字符串内容.这是一个前置工具类,后面loon会提供有专门的html内容解析器.<br>
 * 可以将常见的html页面(含css支持,不含js)转化为可视内容显示到游戏窗体之中,方便用户构建复杂的可视信息内容。
 * 
 * 
 * <pre>
 * HtmlCmd html = new HtmlCmd();
 * html.body();
 * html.b("text").brn();
 * html.fontSize(3).color("red").text("it is red text!").end().brn();
 * html.fontSize(2).color("blue").text("it is blue text!").end().brn();
 * html.fontFace("dialog").color("green").text("it is green text!").end();
 * html.end();
 * System.out.println(html.toString());
 * 
 * output:
 * 
 * <!DOCTYPE html>
 *  <html>
 *	<body><b>text</b><br>
 *	<font size="3.0" color="red">it is red text</font><br>
 *	<font size="2.0" color="blue">it is blue text</font><br>
 *	<font face="dialog" color="green">it is green text</font></body>
 * </html>
 * </pre>
 */
public class HtmlCmd extends StringKeyValue {

	public static class Tag {

		protected final HtmlCmd builder;
		protected final CharSequence element;
		protected CharSequence separator = "";

		public Tag(HtmlCmd builder, CharSequence element) {
			this.builder = builder;
			this.element = element;
			begin();
		}

		protected HtmlCmd begin() {
			builder.addValue("<").addValue(element).addValue(" ");
			return builder;
		}

		public HtmlCmd end() {
			builder.addValue("</").addValue(element).addValue(">");
			return builder;
		}

		@Override
		public String toString() {
			return builder.toString();
		}

	}

	public static class Font extends Tag {

		public Font() {
			this(new HtmlCmd());
		}

		public Font(HtmlCmd builder) {
			super(builder, "font");
		}

		public Font size(int size) {
			builder.addValue(separator).addValue("size=\"").addValue(size).addValue("\"");
			separator = " ";
			return this;
		}

		public Font size(CharSequence size) {
			builder.addValue(separator).addValue("size=\"").addValue(size).addValue("\"");
			separator = " ";
			return this;
		}

		public Font color(int color) {
			return color("#" + new LColor(color).toString());
		}

		public Font color(CharSequence color) {
			builder.addValue(separator).addValue("color=\"").addValue(color).addValue("\"");
			separator = " ";
			return this;
		}

		public Font face(CharSequence face) {
			builder.addValue(separator).addValue("face=\"").addValue(face).addValue("\"");
			separator = " ";
			return this;
		}

		public Font text(CharSequence text) {
			builder.addValue(">").addValue(text);
			return this;
		}

	}

	public static class Img extends Tag {

		public Img() {
			this(new HtmlCmd());
		}

		public Img(HtmlCmd builder) {
			super(builder, "img");
		}

		public Img src(CharSequence src) {
			builder.addValue(separator).addValue("src=\"").addValue(src).addValue("\"");
			separator = " ";
			return this;
		}

		public Img alt(CharSequence alt) {
			builder.addValue(separator).addValue("alt=\"").addValue(alt).addValue("\"");
			separator = " ";
			return this;
		}

		public Img height(CharSequence height) {
			builder.addValue(separator).addValue("height=\"").addValue(height).addValue("\"");
			separator = " ";
			return this;
		}

		public Img height(int height) {
			builder.addValue(separator).addValue("height=\"").addValue(height).addValue("\"");
			separator = " ";
			return this;
		}

		public Img width(CharSequence width) {
			builder.addValue(separator).addValue("width=\"").addValue(width).addValue("\"");
			separator = " ";
			return this;
		}

		public Img width(int width) {
			builder.addValue(separator).addValue("width=\"").addValue(width).addValue("\"");
			separator = " ";
			return this;
		}

		@Override
		public HtmlCmd end() {
			builder.addValue(">");
			return builder;
		}

	}

	public HtmlCmd() {
		super("html");
	}

	public HtmlCmd begin(CharSequence element, CharSequence data) {
		addTag(element);
		addValue("<");
		addValue(element);
		if (data != null) {
			addValue(" ").addValue(data);
		}
		addValue(">");
		return this;
	}

	public HtmlCmd begin(CharSequence element) {
		return begin(element, null);
	}

	public HtmlCmd end() {
		if (getTags().isEmpty()) {
			return this;
		}
		addValue("</").addValue(removeLastTag()).addValue(">");
		return this;
	}

	public HtmlCmd end(CharSequence element) {
		addValue("</").addValue(element).addValue(">");
		TArray<CharSequence> tags = getTags();
		for (int i = tags.size - 1; i > -1; i--) {
			if (tags.get(i).equals(element)) {
				tags.removeIndex(i);
				break;
			}
		}
		return this;
	}

	public HtmlCmd a(CharSequence href, CharSequence text) {
		addValue(StringUtils.format("<a href=\"{0}\">{1}</a>", href, text));
		return this;
	}

	public HtmlCmd b() {
		return begin("b");
	}

	public HtmlCmd b(CharSequence text) {
		addValue("<b>").addValue(text).addValue("</b>");
		return this;
	}

	public HtmlCmd big() {
		return begin("big");
	}

	public HtmlCmd big(CharSequence text) {
		addValue("<big>").addValue(text).addValue("</big>");
		return this;
	}

	public HtmlCmd blockquote() {
		return begin("blockquote");
	}

	public HtmlCmd blockquote(CharSequence text) {
		addValue("<blockquote>").addValue(text).addValue("</blockquote>");
		return this;
	}

	public HtmlCmd brn() {
		br().newLine();
		return this;
	}

	public HtmlCmd br() {
		addValue("<br>");
		return this;
	}

	public HtmlCmd trn() {
		tr().newLine();
		return this;
	}

	public HtmlCmd tr() {
		addValue("<tr>");
		return this;
	}

	public HtmlCmd cite() {
		return begin("cite");
	}

	public HtmlCmd cite(CharSequence text) {
		addValue("<cite>").addValue(text).addValue("</cite>");
		return this;
	}

	public HtmlCmd dfn() {
		return begin("dfn");
	}

	public HtmlCmd dfn(CharSequence text) {
		addValue("<dfn>").addValue(text).addValue("</dfn>");
		return this;
	}

	public HtmlCmd div() {
		return begin("div");
	}

	public HtmlCmd div(CharSequence align) {
		addValue(StringUtils.format("<div align=\"{0}\">", align));
		addTag("div");
		return this;
	}

	public HtmlCmd divId(CharSequence id, CharSequence text) {
		addValue("<div id=\"" + id + "\">").addValue(text).addValue("</div>");
		return this;
	}

	public HtmlCmd divClass(CharSequence clazz, CharSequence text) {
		addValue("<div class=\"" + clazz + "\">").addValue(text).addValue("</div>");
		return this;
	}

	public HtmlCmd em() {
		return begin("em");
	}

	public HtmlCmd em(CharSequence text) {
		addValue("<em>").addValue(text).addValue("</em>");
		return this;
	}

	public Font font() {
		return new Font(this);
	}

	public HtmlCmd font(int color, CharSequence text) {
		font().color(color).text(text).end();
		return this;
	}

	public HtmlCmd font(CharSequence face, CharSequence text) {
		font().face(face).text(text).end();
		return this;
	}

	public Font fontSize(CharSequence s) {
		return font().size(s);
	}

	public Font fontSize(int s) {
		return font().size(s);
	}

	public Font fontFace(CharSequence f) {
		return font().face(f);
	}

	public HtmlCmd h1() {
		return begin("h1");
	}

	public HtmlCmd h1(CharSequence text) {
		addValue("<h1>").addValue(text).addValue("</h1>");
		return this;
	}

	public HtmlCmd h2() {
		return begin("h2");
	}

	public HtmlCmd h2(CharSequence text) {
		addValue("<h2>").addValue(text).addValue("</h2>");
		return this;
	}

	public HtmlCmd h3() {
		return begin("h3");
	}

	public HtmlCmd h3(CharSequence text) {
		addValue("<h3>").addValue(text).addValue("</h3>");
		return this;
	}

	public HtmlCmd h4() {
		return begin("h4");
	}

	public HtmlCmd h4(CharSequence text) {
		addValue("<h4>").addValue(text).addValue("</h4>");
		return this;
	}

	public HtmlCmd h5() {
		return begin("h5");
	}

	public HtmlCmd h5(CharSequence text) {
		addValue("<h5>").addValue(text).addValue("</h5>");
		return this;
	}

	public HtmlCmd h6() {
		return begin("h6");
	}

	public HtmlCmd h6(CharSequence text) {
		addValue("<h6>").addValue(text).addValue("</h6>");
		return this;
	}

	public HtmlCmd i() {
		return begin("i");
	}

	public HtmlCmd i(CharSequence text) {
		addValue("<i>").addValue(text).addValue("</i>");
		return this;
	}

	public Img img() {
		return new Img(this);
	}

	public HtmlCmd img(CharSequence src, CharSequence w, CharSequence h) {
		Img temp = img().src(src).width(w).height(h);
		return temp.end();
	}

	public HtmlCmd img(CharSequence src, int w, int h) {
		Img temp = img().src(src).width(w).height(h);
		return temp.end();
	}

	public HtmlCmd img(CharSequence src, CharSequence alt) {
		Img temp = img().src(src).alt(alt);
		return temp.end();
	}

	public HtmlCmd p() {
		return begin("p");
	}

	public HtmlCmd p(CharSequence text, HorizontalAlign align) {
		addValue("<p align=" + align.toString() + ">").addValue(text).addValue("</p>");
		return this;
	}

	public HtmlCmd p(CharSequence text) {
		addValue("<p>").addValue(text).addValue("</p>");
		return this;
	}

	public HtmlCmd small() {
		return begin("small");
	}

	public HtmlCmd small(CharSequence text) {
		addValue("<small>").addValue(text).addValue("</small>");
		return this;
	}

	public HtmlCmd strike() {
		return begin("strike");
	}

	public HtmlCmd strike(CharSequence text) {
		addValue("<strike>").addValue(text).addValue("</strike>");
		return this;
	}

	public HtmlCmd table(CharSequence text) {
		addValue("<table>").addValue(text).addValue("</table>");
		return this;
	}

	public HtmlCmd head(CharSequence text) {
		addValue("<head>").addValue(text).addValue("</head>");
		return this;
	}

	public HtmlCmd body(CharSequence text) {
		addValue("<body>").addValue(text).addValue("</body>");
		return this;
	}

	public HtmlCmd body() {
		return begin("body");
	}

	public HtmlCmd head() {
		return begin("body");
	}

	public HtmlCmd table() {
		return begin("table");
	}

	public HtmlCmd strong() {
		return begin("strong");
	}

	public HtmlCmd strong(CharSequence text) {
		addValue("<strong>").addValue(text).addValue("</strong>");
		return this;
	}

	public HtmlCmd sub() {
		return begin("sub");
	}

	public HtmlCmd sub(CharSequence text) {
		addValue("<sub>").addValue(text).addValue("</sub>");
		return this;
	}

	public HtmlCmd sup() {
		return begin("sup");
	}

	public HtmlCmd sup(CharSequence text) {
		addValue("<sup>").addValue(text).addValue("</sup>");
		return this;
	}

	public HtmlCmd tt() {
		return begin("tt");
	}

	public HtmlCmd tt(CharSequence text) {
		addValue("<tt>").addValue(text).addValue("</tt>");
		return this;
	}

	public HtmlCmd u() {
		return begin("u");
	}

	public HtmlCmd u(CharSequence text) {
		addValue("<u>").addValue(text).addValue("</u>");
		return this;
	}

	public HtmlCmd ul() {
		return begin("ul");
	}

	public HtmlCmd hr() {
		addValue("<hr>");
		return this;
	}

	public HtmlCmd li() {
		return begin("li");
	}

	public HtmlCmd li(CharSequence text) {
		addValue("<li>").addValue(text).addValue("</li>");
		return this;
	}

	public HtmlCmd style(CharSequence text) {
		addValue("<style>").addValue(text).addValue("</style>");
		return this;
	}

	public HtmlCmd styleCss(CharSequence text) {
		addValue("<style type=\"text/css\">").addValue(text).addValue("</style>");
		return this;
	}

	public HtmlCmd style() {
		return begin("style");
	}

	public HtmlCmd linkCss(CharSequence path) {
		addValue("<link rel=\"stylesheet\" type=\"text/css\" href=\"").addValue(path + "\"/>");
		return this;
	}

	public HtmlCmd link(CharSequence text) {
		addValue("<link>").addValue(text).addValue("</link>");
		return this;
	}

	public HtmlCmd link() {
		return begin("link");
	}

	public HtmlCmd span(CharSequence text) {
		addValue("<span>").addValue(text).addValue("</span>");
		return this;
	}

	public HtmlCmd spanId(CharSequence id, CharSequence text) {
		addValue("<span id=\"" + id + "\">").addValue(text).addValue("</span>");
		return this;
	}

	public HtmlCmd spanClass(CharSequence clazz, CharSequence text) {
		addValue("<span class=\"" + clazz + "\">").addValue(text).addValue("</span>");
		return this;
	}
	
	public HtmlCmd span() {
		return begin("span");
	}

	public HtmlCmd title(CharSequence text) {
		addValue("<title>").addValue(text).addValue("</title>");
		return this;
	}

	public HtmlCmd title() {
		return begin("title");
	}

	public HtmlCmd context(CharSequence text) {
		addValue(text);
		return this;
	}

	@Override
	public String toString() {
		StrBuilder builder = new StrBuilder();
		builder.append("<!DOCTYPE html>").append(LSystem.LS).append("<" + getKey() + ">").append(LSystem.LS)
				.append(getValue()).append(LSystem.LS).append("</" + getKey() + ">");
		return builder.toString();
	}

}
