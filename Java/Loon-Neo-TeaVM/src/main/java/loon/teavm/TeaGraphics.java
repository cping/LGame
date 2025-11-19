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
package loon.teavm;

import java.util.HashMap;
import java.util.Map;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.webgl.WebGLContextAttributes;
import org.teavm.jso.webgl.WebGLRenderingContext;

import loon.Graphics;
import loon.LGame;
import loon.LSystem;
import loon.Platform.Orientation;
import loon.canvas.Canvas;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.opengl.GL20;
import loon.opengl.TextureSource;
import loon.teavm.TeaGame.TeaSetting;
import loon.teavm.dom.HTMLDocumentExt;
import loon.teavm.gl.WebGL20;
import loon.teavm.gl.WebGLContext;
import loon.utils.GLUtils;
import loon.utils.PathUtils;
import loon.utils.Scale;

public class TeaGraphics extends Graphics {

	private final TeaSetting config;
	private final HTMLCanvasElement dummyCanvas;
	private final CanvasRenderingContext2D dummyCtx;

	private final HTMLElement measureElement;
	private final Map<Font, TeaFontMetrics> fontMetrics = new HashMap<Font, TeaFontMetrics>();

	final HTMLElement rootElement;
	final HTMLCanvasElement canvas;

	private final Dimension screenSize = new Dimension();

	private static final String HEIGHT_TEXT = "THEQUICKBROWNFOXJUMPEDOVERTHELAZYDOGthequickbrownfoxjumpedoverthelazydog_-+!.,[]0123456789";
	private static final String EMWIDTH_TEXT = "m";

	static float experimentalScale = 1;

	public TeaGraphics(final HTMLElement root, final LGame game, final TeaSetting cfg) {
		super(game, new WebGL20(), game.setting.scaling() ? Scale.ONE : new Scale(TeaBase.get().getDevicePixelRatio()));

		final Window mainWindow = TeaBase.get().getWindow();
		this.config = cfg;
		HTMLDocumentExt doc = TeaBase.get().getDocument();
		this.dummyCanvas = doc.createCanvasElement();
		this.dummyCtx = (CanvasRenderingContext2D) dummyCanvas.getContext("2d");

		this.rootElement = root;

		measureElement = doc.createElement("div");
		measureElement.getStyle().setProperty("visibility", "hidden");
		measureElement.getStyle().setProperty("position", "absolute");
		measureElement.getStyle().setProperty("top", "-500px");
		measureElement.getStyle().setProperty("overflow", "visible");
		measureElement.getStyle().setProperty("whiteSpace", "nowrap");
		root.appendChild(measureElement);

		canvas = (HTMLCanvasElement) TeaBase.get().getDocument().createElement("canvas");
		root.appendChild(canvas);
		if (config.scaling()) {
			setSize(config.width_zoom > 0 ? config.width_zoom : root.getOffsetWidth(),
					config.height_zoom > 0 ? config.height_zoom : root.getOffsetHeight());
		} else {
			setSize(config.width > 0 ? config.width : root.getOffsetWidth(),
					config.height > 0 ? config.height : root.getOffsetHeight());
		}
		WebGLContextAttributes attrs = WebGLContextAttributes.create();
		attrs.setAntialias(config.antiAliasing);
		attrs.setStencil(config.stencil);
		attrs.setAlpha(config.transparentCanvas);
		attrs.setPremultipliedAlpha(config.premultipliedAlpha);
		attrs.setPreserveDrawingBuffer(config.preserveDrawingBuffer);

		WebGLContext glc = (WebGLContext) canvas.getContext("webgl", attrs);

		if (glc == null) {
			throw new RuntimeException("Unable to create GL context");
		}

		((WebGL20) gl).init(glc);

		if (config.scaling()) {
			glc.viewport(0, 0, config.width_zoom, config.height_zoom);
		} else {
			glc.viewport(0, 0, config.width, config.height);
		}

		if (config.fullscreen) {

			mainWindow.addEventListener("resize", new EventListener<Event>() {
				@Override
				public void handleEvent(Event event) {
					final float clientWidth = mainWindow.getInnerWidth();
					final float clientHeight = mainWindow.getInnerHeight();
					if (Loon.getScreenWidthJSNI() == clientWidth && Loon.getScreenHeightJSNI() == clientHeight) {
						float width = LSystem.viewSize.width(), height = LSystem.viewSize.height();
						experimentalScale = Math.min(Loon.getScreenWidthJSNI() / width, Loon.getScreenHeightJSNI() / height);

						int yOfs = (int) ((Loon.getScreenHeightJSNI() - height * experimentalScale) / 3.f);
						int xOfs = (int) ((Loon.getScreenWidthJSNI() - width * experimentalScale) / 2.f);
						rootElement.setAttribute("style",
								"width:" + experimentalScale * width + "px; " + "height:" + experimentalScale * height
										+ "px; " + "position:absolute; left:" + xOfs + "px; top:" + yOfs);
						doc.getBody().setClassName("fullscreen");
					} else {
						experimentalScale = 1;
						rootElement.removeAttribute("style");
						doc.getBody().setClassName("unfullscreen");
					}
				}
			});
		}
		mainWindow.addEventListener("deviceorientation", new EventListener<Event>() {

			@Override
			public void handleEvent(Event evt) {

				int width = Loon.self.getContainerWidth();
				int height = Loon.self.getContainerHeight();
				game.log().info("update screen size width :" + width + " height :" + height);
				setSize(width, height);

			}

		});

	}

	public void setSize(int width, int height) {
		rootElement.getStyle().setProperty("width", width + "px");
		rootElement.getStyle().setProperty("height", height + "px");
		canvas.setWidth(scale().scaledCeil(width));
		canvas.setHeight(scale().scaledCeil(height));
		canvas.getStyle().setProperty("width", width + "px");
		canvas.getStyle().setProperty("height", height + "px");
		int viewWidth = canvas.getWidth();
		int viewHeight = canvas.getHeight();
		if (!isAllowResize(viewWidth, viewHeight)) {
			return;
		}
		viewportChanged(scale(), viewWidth, viewHeight);
	}

	public void registerFontMetrics(String name, Font font, float lineHeight) {
		TeaFontMetrics metrics = getFontMetrics(font);
		fontMetrics.put(font, new TeaFontMetrics(font, lineHeight, metrics.emwidth));
	}

	@Override
	public Dimension screenSize() {
		screenSize.setSize(TeaBase.get().getClientWidth(), TeaBase.get().getClientHeight());
		return screenSize;
	}

	@Override
	public TextLayout layoutText(String text, TextFormat format) {
		return TeaTextLayout.layoutText(this, dummyCtx, text, format);
	}

	@Override
	public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
		return TeaTextLayout.layoutText(this, dummyCtx, text, format, wrap);
	}

	@Override
	protected Canvas createCanvasImpl(Scale scale, int pixelWidth, int pixelHeight) {
		HTMLCanvasElement elem = (HTMLCanvasElement) TeaBase.get().getDocument().createElement("canvas");
		elem.setWidth(pixelWidth);
		elem.setHeight(pixelHeight);
		return new TeaCanvas(this, new TeaImage(this, scale, elem, TextureSource.RenderCanvas));
	}

	void updateTexture(int tex, HTMLImageElement img) {
		GLUtils.bindTexture(gl, tex);
		((WebGL20) gl).glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, img);
	}

	TeaFontMetrics getFontMetrics(Font font) {
		TeaFontMetrics metrics = fontMetrics.get(font);
		if (metrics == null) {
			final String fontName = font.name;
			measureElement.getStyle().setProperty("fontSize", font.size + "px");
			measureElement.getStyle().setProperty("fontWeight", "normal");
			measureElement.getStyle().setProperty("fontStyle", "normal");
			final String ext = PathUtils.getExtension(fontName).trim().toLowerCase();
			if ((game instanceof TeaGame) && ("ttf".equals(ext) || "otf".equals(ext))) {
				measureElement.getStyle().setProperty("src", ((TeaAssets) game.assets()).getURLPath(fontName));
				measureElement.getStyle().setProperty("fontFamily", PathUtils.getBaseFileName(fontName));
			} else {
				measureElement.getStyle().setProperty("fontFamily", fontName);
			}
			measureElement.setInnerText(HEIGHT_TEXT);
			switch (font.style) {
			case BOLD:
				measureElement.getStyle().setProperty("fontWeight", "bold");
				break;
			case ITALIC:
				measureElement.getStyle().setProperty("fontStyle", "italic");
				break;
			case BOLD_ITALIC:
				measureElement.getStyle().setProperty("fontWeight", "bold");
				measureElement.getStyle().setProperty("fontStyle", "italic");
				break;
			default:
				break;
			}
			float height = measureElement.getOffsetHeight();
			measureElement.setInnerText(EMWIDTH_TEXT);
			float emwidth = measureElement.getOffsetWidth();
			metrics = new TeaFontMetrics(font, height, emwidth);
			fontMetrics.put(font, metrics);
		}
		return metrics;
	}

}