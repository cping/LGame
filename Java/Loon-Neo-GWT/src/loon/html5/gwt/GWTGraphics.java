/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.html5.gwt;

import java.util.Map;
import java.util.HashMap;

import loon.Graphics;
import loon.LGame;
import loon.Platform.Orientation;
import loon.canvas.Canvas;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.html5.gwt.GWTGame.GWTSetting;
import loon.html5.gwt.Loon.OrientationChangedHandler;
import loon.opengl.GL20;
import loon.opengl.TextureSource;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.utils.PathUtils;
import loon.utils.Scale;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.webgl.client.WebGLContextAttributes;
import com.google.gwt.webgl.client.WebGLRenderingContext;

public class GWTGraphics extends Graphics {

	private final GWTSetting config;
	private final CanvasElement dummyCanvas;
	private final Context2d dummyCtx;

	private final Element measureElement;
	private final Map<Font, GWTFontMetrics> fontMetrics = new HashMap<Font, GWTFontMetrics>();

	final Element rootElement;
	final CanvasElement canvas;

	private final RectBox initSize = new RectBox();

	private final Dimension screenSize = new Dimension();

	private boolean graphicsFullSize;

	private static final String HEIGHT_TEXT = "THEQUICKBROWNFOXJUMPEDOVERTHELAZYDOGthequickbrownfoxjumpedoverthelazydog_-+!.,[]0123456789";
	private static final String EMWIDTH_TEXT = "m";

	public GWTGraphics(final Panel panel, final LGame game, final GWTSetting cfg) {
		super(game, new GWTGL20(), game.setting.scaling() ? Scale.ONE : new Scale(Loon.devicePixelRatio()));

		this.config = cfg;
		Document doc = Document.get();
		this.dummyCanvas = doc.createCanvasElement();
		this.dummyCtx = dummyCanvas.getContext2d();

		Element root = panel.getElement();

		this.rootElement = root;

		measureElement = doc.createDivElement();
		measureElement.getStyle().setVisibility(Style.Visibility.HIDDEN);
		measureElement.getStyle().setPosition(Style.Position.ABSOLUTE);
		measureElement.getStyle().setTop(-500, Unit.PX);
		measureElement.getStyle().setOverflow(Style.Overflow.VISIBLE);
		measureElement.getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
		root.appendChild(measureElement);

		canvas = Document.get().createCanvasElement();
		root.appendChild(canvas);

		if (config.scaling()) {
			setSize(config.width_zoom > 0 ? config.width_zoom : root.getOffsetWidth(),
					config.height_zoom > 0 ? config.height_zoom : root.getOffsetHeight());
		} else {
			setSize(config.width > 0 ? config.width : root.getOffsetWidth(),
					config.height > 0 ? config.height : root.getOffsetHeight());
		}
		initSize.setSize(config.getShowWidth(), config.getShowHeight());

		WebGLContextAttributes attrs = WebGLContextAttributes.create();
		attrs.setAntialias(config.antiAliasing);
		attrs.setStencil(config.stencil);
		attrs.setAlpha(config.transparentCanvas);
		attrs.setPremultipliedAlpha(config.premultipliedAlpha);
		attrs.setPreserveDrawingBuffer(config.preserveDrawingBuffer);
		attrs.setPowerPreference(config.powerPreference);

		WebGLRenderingContext glc = WebGLRenderingContext.getContext(canvas, attrs);

		if (glc == null) {
			throw new RuntimeException("Unable to create GL context");
		}

		((GWTGL20) gl).init(glc);

		if (config.scaling()) {
			glc.viewport(0, 0, config.width_zoom, config.height_zoom);
		} else {
			glc.viewport(0, 0, config.width, config.height);
		}
		addEventListeners();
		if (config.fullscreen || config.allowScreenResize || config.isFixedSize()) {
			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					if (!game.isRunning()) {
						return;
					}
					final float width = config.fullscreen ? config.getShowWidth() : config.width;
					final float height = config.fullscreen ? config.getShowHeight() : config.height;
					final int clientWidth = MathUtils.clamp(event.getWidth(), config.width, Window.getClientWidth());
					final int clientHeight = MathUtils.clamp(event.getHeight(), config.height,
							Window.getClientHeight());
					if (clientWidth <= 0 || clientHeight <= 0) {
						return;
					}
					final int maxWidth = MathUtils.max(clientWidth, Loon.self.getJSNIScreenWidth());
					final int maxHeight = MathUtils.max(clientHeight, Loon.self.getJSNIScreenHeight());
					if (config.fullscreen && width >= maxWidth && height >= maxHeight) {
						return;
					}
					if (width != clientWidth || height != clientHeight) {
						if (config.fullscreen) {
							setSize(maxWidth, maxHeight);
						} else {
							setSize(clientWidth, clientHeight);
						}
						float experimentalScale = MathUtils.min(maxWidth / width, maxHeight / height);
						rootElement.setAttribute("style",
								"width:" + experimentalScale * width + "px; " + "height:" + experimentalScale * height
										+ "px; " + "position:absolute; left:" + 0 + "px; top:" + 0 + "px");
						Document.get().getBody().addClassName("fullscreen");
						graphicsFullSize = true;
					} else if ((config.fullscreen && graphicsFullSize)
							&& (clientWidth != initSize.width || clientHeight != initSize.height)) {
						setSize(initSize.width, initSize.height);
						rootElement.removeAttribute("style");
						Document.get().getBody().removeClassName("fullscreen");
						graphicsFullSize = false;
					}

				}
			});
		}

		Loon.self.addHandler(new OrientationChangedHandler() {
			@Override
			public void onChanged(Orientation newOrientation) {
				final int clientWidth = rootElement.getClientWidth();
				final int clientHeight = rootElement.getClientHeight();

				if (clientWidth <= 0 || clientHeight <= 0) {
					return;
				}
				game.log().info("update screen size width :" + clientWidth + " height :" + clientHeight);
				setSize(clientWidth, clientHeight);
			}
		});
		Window.addWindowClosingHandler(new ClosingHandler() {

			@Override
			public void onWindowClosing(ClosingEvent event) {
				game.shutdown();
			}
		});
	}

	public void restoreSize() {
		setSize(initSize.width, initSize.height);
	}

	public boolean isGraphicsFullSize() {
		return graphicsFullSize;
	}

	public CanvasElement getCanvas() {
		return canvas;
	}

	private native void addEventListeners() /*-{
		var self = this;
		var eventName = null;
		if ("hidden" in $doc) {
			eventName = "visibilitychange"
		} else if ("webkitHidden" in $doc) {
			eventName = "webkitvisibilitychange"
		} else if ("mozHidden" in $doc) {
			eventName = "mozvisibilitychange"
		} else if ("msHidden" in $doc) {
			eventName = "msvisibilitychange"
		}
		if (eventName !== null) {
			$doc
					.addEventListener(
							eventName,
							function(e) {
								self.@loon.html5.gwt.GWTGraphics::onVisibilityChange(Z)($doc['hidden'] !== true);
							});
		}
	}-*/;

	private void onVisibilityChange(boolean visible) {
		if (visible) {
			game.resume();
		} else {
			game.pause();
		}
	}

	private boolean isFullscreen() {
		return Loon.self.isFullscreen();
	}

	private native int getScreenWidthJSNI() /*-{
		return $wnd.screen.width;
	}-*/;

	private native int getScreenHeightJSNI() /*-{
		return $wnd.screen.height;
	}-*/;

	public void setSize(int width, int height) {
		rootElement.getStyle().setWidth(width, Unit.PX);
		rootElement.getStyle().setHeight(height, Unit.PX);
		canvas.setWidth(scale().scaledCeil(width));
		canvas.setHeight(scale().scaledCeil(height));
		canvas.getStyle().setWidth(width, Style.Unit.PX);
		canvas.getStyle().setHeight(height, Style.Unit.PX);
		int viewWidth = canvas.getWidth();
		int viewHeight = canvas.getHeight();
		if (!isAllowResize(viewWidth, viewHeight)) {
			return;
		}
		viewportChanged(scale(), viewWidth, viewHeight);
	}

	public void registerFontMetrics(String name, Font font, float lineHeight) {
		GWTFontMetrics metrics = getFontMetrics(font);
		fontMetrics.put(font, new GWTFontMetrics(font, lineHeight, metrics.emwidth));
	}

	@Override
	public Dimension screenSize() {
		screenSize.setSize(Document.get().getDocumentElement().getClientWidth(),
				Document.get().getDocumentElement().getClientHeight());
		return screenSize;
	}

	@Override
	public TextLayout layoutText(String text, TextFormat format) {
		return GWTTextLayout.layoutText(this, dummyCtx, text, format);
	}

	@Override
	public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
		return GWTTextLayout.layoutText(this, dummyCtx, text, format, wrap);
	}

	@Override
	protected Canvas createCanvasImpl(Scale scale, int pixelWidth, int pixelHeight) {
		CanvasElement elem = Document.get().createCanvasElement();
		elem.setWidth(pixelWidth);
		elem.setHeight(pixelHeight);
		return new GWTCanvas(this, new GWTImage(this, scale, elem, TextureSource.RenderCanvas));
	}

	void updateTexture(int tex, ImageElement img) {
		GLUtils.bindTexture(gl, tex);
		((GWTGL20) gl).glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, img);
	}

	GWTFontMetrics getFontMetrics(Font font) {
		GWTFontMetrics metrics = fontMetrics.get(font);
		if (metrics == null) {
			final String fontName = font.name;
			measureElement.getStyle().setFontSize(font.size, Unit.PX);
			measureElement.getStyle().setFontWeight(Style.FontWeight.NORMAL);
			measureElement.getStyle().setFontStyle(Style.FontStyle.NORMAL);
			final String ext = PathUtils.getExtension(fontName).trim().toLowerCase();
			if ((game instanceof GWTGame) && ("ttf".equals(ext) || "otf".equals(ext))) {
				measureElement.getStyle().setProperty("src", ((GWTAssets) game.assets()).getURLPath(fontName));
				measureElement.getStyle().setProperty("fontFamily", PathUtils.getBaseFileName(fontName));
			} else {
				measureElement.getStyle().setProperty("fontFamily", GWTFont.getFontName(fontName)
						+ ",'Microsoft YaHei','STHeiti','PingFang SC','WenQuanYi Micro Hei',monospace,sans-serif");
			}
			measureElement.setInnerText(HEIGHT_TEXT);
			switch (font.style) {
			case BOLD:
				measureElement.getStyle().setFontWeight(Style.FontWeight.BOLD);
				break;
			case ITALIC:
				measureElement.getStyle().setFontStyle(Style.FontStyle.ITALIC);
				break;
			case BOLD_ITALIC:
				measureElement.getStyle().setFontWeight(Style.FontWeight.BOLD);
				measureElement.getStyle().setFontStyle(Style.FontStyle.ITALIC);
				break;
			default:
				break;
			}
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
			metrics = new GWTFontMetrics(font, height, emwidth);
			fontMetrics.put(font, metrics);
		}
		return metrics;
	}

}
