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

import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.dom.xml.Node;
import org.teavm.jso.webgl.WebGLContextAttributes;

import loon.Graphics;
import loon.LGame;
import loon.canvas.Canvas;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.opengl.GL20;
import loon.opengl.TextureSource;
import loon.teavm.TeaGame.TeaSetting;
import loon.teavm.dom.HTMLDocumentExt;
import loon.teavm.dom.WebGLContextAttributesExt;
import loon.teavm.gl.WebGL20;
import loon.teavm.gl.WebGLContext;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.utils.PathUtils;
import loon.utils.Scale;

public class TeaGraphics extends Graphics {

	private final TeaSetting config;

	private final CanvasRenderingContext2D dummyCtx;

	private final HTMLElement measureElement;
	private final Map<Font, TeaFontMetrics> fontMetrics = new HashMap<Font, TeaFontMetrics>();

	private HTMLElement rootElement;
	final HTMLCanvasElement canvas;
	final Loon loonApp;
	private final RectBox initSize = new RectBox();
	private final Dimension screenSize = new Dimension();
	private boolean graphicsFullSize;

	private static final String HEIGHT_TEXT = "THEQUICKBROWNFOXJUMPEDOVERTHELAZYDOGthequickbrownfoxjumpedoverthelazydog_-+!.,[]0123456789";
	private static final String EMWIDTH_TEXT = "m";

	public TeaGraphics(final Loon loon, final LGame game, final TeaSetting cfg) {
		super(game, new WebGL20(), game.setting.scaling() ? Scale.ONE : new Scale(TeaBase.get().getDevicePixelRatio()));
		this.loonApp = loon;
		this.config = cfg;

		final Window mainWindow = TeaBase.get().getWindow();

		HTMLDocumentExt doc = TeaBase.get().getDocument();

		CSSStyleDeclaration bodyStyle = doc.getBody().getStyle();
		bodyStyle.setProperty("margin", "0");
		bodyStyle.setProperty("overflow", "hidden");

		this.rootElement = (HTMLElement) loon.getMainCanvas().getParentNode();
		if (rootElement == null) {
			rootElement = doc.getDocumentElement();
		}

		dummyCtx = TeaCanvasUtils.getContext2d(TeaCanvasUtils.createCanvas(doc));

		canvas = doc.createCanvasElement();

		CSSStyleDeclaration canvasStyle = canvas.getStyle();
		canvasStyle.setProperty("top", "0px");
		canvasStyle.setProperty("left", "0px");
		canvasStyle.setProperty("background", "#000000");

		rootElement.replaceChild(canvas, loon.getMainCanvas());
		loon.setMainCanvasElement(canvas);

		measureElement = TeaCanvasUtils.createDiv(doc);
		CSSStyleDeclaration style = measureElement.getStyle();
		style.setProperty("visibility", "hidden");
		style.setProperty("position", "absolute");
		style.setProperty("top", "-500px");
		style.setProperty("overflow", "visible");
		style.setProperty("whiteSpace", "nowrap");

		if (canvas.getParentNode() != null) {
			canvas.getParentNode().appendChild(measureElement);
		} else {
			rootElement.appendChild(measureElement);
		}

		if (config.scaling()) {
			setSize(config.width_zoom > 0 ? config.width_zoom : rootElement.getOffsetWidth(),
					config.height_zoom > 0 ? config.height_zoom : rootElement.getOffsetHeight());
		} else {
			setSize(config.width > 0 ? config.width : rootElement.getOffsetWidth(),
					config.height > 0 ? config.height : rootElement.getOffsetHeight());
		}
		initSize.setSize(config.getShowWidth(), config.getShowHeight());

		WebGLContextAttributesExt attrs = (WebGLContextAttributesExt) WebGLContextAttributes.create();
		attrs.setAntialias(config.antiAliasing);
		attrs.setStencil(config.stencil);
		attrs.setAlpha(config.transparentCanvas);
		attrs.setPremultipliedAlpha(config.premultipliedAlpha);
		attrs.setPreserveDrawingBuffer(config.preserveDrawingBuffer);
		attrs.setPowerPreference(config.powerPreference);

		WebGLContext glc = (WebGLContext) TeaCanvasUtils.getContextWebGL(canvas, attrs);

		if (glc == null) {
			webglError(doc);
			throw new RuntimeException("Unable to create GL context");
		}

		((WebGL20) gl).init(glc);

		if (config.scaling()) {
			glc.viewport(0, 0, config.width_zoom, config.height_zoom);
		} else {
			glc.viewport(0, 0, config.width, config.height);
		}
		mainWindow.addEventListener("pageshow", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				game.resume();
			}
		});
		mainWindow.addEventListener("pagehide", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				game.pause();
			}
		});
		mainWindow.addEventListener("visibilitychange", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				String state = doc.getVisibilityState();
				if ("hidden".equals(state)) {
					game.pause();
				} else if ("visible".equals(state)) {
					game.resume();
				}
			}
		});

		if (config.fullscreen || config.allowScreenResize || config.isFixedSize()) {
			mainWindow.addEventListener("resize", new EventListener<Event>() {
				@Override
				public void handleEvent(Event event) {
					if (!game.isRunning()) {
						return;
					}
					final float width = config.fullscreen ? config.getShowWidth() : config.width;
					final float height = config.fullscreen ? config.getShowHeight() : config.height;
					final int clientWidth = MathUtils.clamp(mainWindow.getInnerWidth(), config.width, getClientWidth());
					final int clientHeight = MathUtils.clamp(mainWindow.getInnerHeight(), config.height,
							getClientHeight());
					if (clientWidth <= 0 || clientHeight <= 0) {
						return;
					}
					final int maxWidth = MathUtils.max(clientWidth, Loon.getScreenWidthJSNI());
					final int maxHeight = MathUtils.max(clientHeight, Loon.getScreenHeightJSNI());
					if (config.fullscreen && width >= maxWidth && height >= maxHeight) {
						return;
					}
					if ((width != clientWidth || height != clientHeight)) {
						if (config.fullscreen) {
							setSize(maxWidth, maxHeight);
						} else {
							setSize(clientWidth, clientHeight);
						}
						float experimentalScale = MathUtils.min(maxWidth / width, maxHeight / height);
						rootElement.setAttribute("style",
								"width:" + experimentalScale * width + "px; " + "height:" + experimentalScale * height
										+ "px; " + "position:absolute; left:" + 0 + "px; top:" + 0 + "px");
						doc.getBody().setClassName("fullscreen");
						graphicsFullSize = true;
					} else if ((config.fullscreen && graphicsFullSize)
							&& (clientWidth != initSize.width || clientHeight != initSize.height)) {
						setSize(initSize.width, initSize.height);
						rootElement.removeAttribute("style");
						doc.getBody().setClassName(null);
						graphicsFullSize = false;
					}
				}
			});

		}
		mainWindow.addEventListener("deviceorientation", new EventListener<Event>() {

			@Override
			public void handleEvent(Event evt) {
				int clientWidth = canvas.getClientWidth();
				int clientHeight = canvas.getClientHeight();
				if (clientWidth <= 0) {
					clientWidth = getClientWidth();
				}
				if (clientHeight <= 0) {
					clientHeight = getClientHeight();
				}
				if (clientWidth <= 0 || clientHeight <= 0) {
					return;
				}
				game.log().info("update screen size width :" + clientWidth + " height :" + clientHeight);
				setSize(clientWidth, clientHeight);
			}

		});
	}

	protected void webglError(HTMLDocumentExt doc) {
		HTMLElement container = TeaCanvasUtils.createDiv(doc);
		container.setId("webgl-graphics-context-lost");
		CSSStyleDeclaration style = container.getStyle();
		style.setProperty("position", "absolute");
		style.setProperty("zIndex", "99");
		style.setProperty("top", "50%");
		style.setProperty("left", "50%");
		style.setProperty("display", "flex");
		style.setProperty("flexDirection", "column");
		style.setProperty("transform", "translate(-50%, -50%)");
		style.setProperty("backgroundColor", "white");
		style.setProperty("padding", "10px");
		style.setProperty("borderStyle", "solid 1px");
		HTMLElement div = TeaCanvasUtils.createDiv(doc);
		div.setInnerHTML("<h1>There was an issue rendering, please refresh the page.</h1>\r\n" + "<div>\r\n"
				+ "<p>Loon WebGL Graphics Context Lost</p>\r\n" + "\r\n"
				+ "<button id=\"webgl-graphics-reload\">Refresh Page</button>\r\n" + "\r\n"
				+ "<p>There are a few reasons this might happen:</p>\r\n" + "<ul>\r\n"
				+ "<li>Two or more pages are placing a high demand on the GPU</li>\r\n"
				+ "<li>Another page or operation has stalled the GPU and the browser has decided to reset the GPU</li>\r\n"
				+ "<li>The computer has multiple GPUs and the user has switched between them</li>\r\n"
				+ "<li>Graphics driver has crashed or restarted</li>\r\n" + "<li>Graphics driver was updated</li>\r\n"
				+ "</ul>\r\n" + "</div>");
		container.appendChild(div);
		Node parent = canvas.getParentNode();
		if (parent != null) {
			parent.removeChild(canvas);
			parent.appendChild(container);
		} else {
			rootElement.removeChild(canvas);
			rootElement.appendChild(container);
		}
		HTMLElement button = div.querySelector("#webgl-graphics-reload");
		if (button != null) {
			button.addEventListener("click", new EventListener<Event>() {

				@Override
				public void handleEvent(Event evt) {
					TeaBase.get().reload();
				}

			});
		}

	}

	public void restoreSize() {
		setSize(initSize.width, initSize.height);
	}

	public boolean isGraphicsFullSize() {
		return graphicsFullSize;
	}

	public HTMLCanvasElement getCanvas() {
		return canvas == null ? loonApp.getMainCanvas() : canvas;
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
		screenSize.setSize(getClientWidth(), getClientHeight());
		return screenSize;
	}

	public int getClientWidth() {
		int result = TeaBase.get().getClientWidth();
		if (result > 0) {
			return result;
		}
		return Loon.getScreenWidthJSNI();
	}

	public int getClientHeight() {
		int result = TeaBase.get().getClientHeight();
		if (result > 0) {
			return result;
		}
		return Loon.getScreenHeightJSNI();
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
		HTMLCanvasElement elem = TeaCanvasUtils.createCanvas();
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
			CSSStyleDeclaration style = measureElement.getStyle();
			style.setProperty("fontSize", font.size + "px");
			style.setProperty("fontWeight", "normal");
			style.setProperty("fontStyle", "normal");
			final String ext = PathUtils.getExtension(fontName).trim().toLowerCase();
			if ((game instanceof TeaGame) && ("ttf".equals(ext) || "otf".equals(ext))) {
				style.setProperty("src", ((TeaAssets) game.assets()).getURLPath(fontName));
				style.setProperty("fontFamily", PathUtils.getBaseFileName(fontName));
			} else {
				style.setProperty("fontFamily", "'" + TeaFont.getFontName(fontName) + "'"
						+ ",'Microsoft YaHei','STHeiti','PingFang SC','WenQuanYi Micro Hei',monospace,sans-serif");
			}
			measureElement.setInnerText(HEIGHT_TEXT);
			switch (font.style) {
			case BOLD:
				style.setProperty("fontWeight", "bold");
				break;
			case ITALIC:
				style.setProperty("fontStyle", "italic");
				break;
			case BOLD_ITALIC:
				style.setProperty("fontWeight", "bold");
				style.setProperty("fontStyle", "italic");
				break;
			default:
				break;
			}
			style.setProperty("padding", "0 0");
			final float doubleSize = font.size * 2;
			float height = measureElement.getOffsetHeight();
			measureElement.setInnerText(EMWIDTH_TEXT);
			float emwidth = measureElement.getOffsetWidth();
			if (height >= doubleSize) {
				height = (height - font.size);
			}
			if (emwidth >= doubleSize) {
				emwidth = (emwidth - font.size);
			}
			if (height <= 0) {
				height = font.size + MathUtils.ifloor(font.size / 6);
			}
			if (emwidth <= 0) {
				emwidth = font.size / 2;
			}
			metrics = new TeaFontMetrics(font, height, emwidth);
			fontMetrics.put(font, metrics);
		}
		return metrics;
	}

}