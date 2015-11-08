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
import loon.LSystem;
import loon.Platform.Orientation;
import loon.canvas.Canvas;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.geom.Vector2f;
import loon.html5.gwt.GWTGame.GWTSetting;
import loon.html5.gwt.Loon.OrientationChangedHandler;
import loon.opengl.GL20;
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
	private final CanvasElement canvas;
	private final Vector2f mousePoint = new Vector2f();
	private final Dimension screenSize = new Dimension();
	private final float mouseScale;

	private static final String HEIGHT_TEXT = "THEQUICKBROWNFOXJUMPEDOVERTHELAZYDOGthequickbrownfoxjumpedoverthelazydog_-+!.,[]0123456789";
	private static final String EMWIDTH_TEXT = "m";

	static float experimentalScale = 1;

	public GWTGraphics(final Panel panel, final LGame game, final GWTSetting cfg) {
		super(game, new GWTGL20(), new Scale(cfg.scaleFactor));

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

		mouseScale = config.scaleFactor / Loon.devicePixelRatio();

		canvas = Document.get().createCanvasElement();
		root.appendChild(canvas);
		if (config.scaling()) {
			setSize(config.width_zoom > 0 ? config.width_zoom
					: root.getOffsetWidth(),
					config.height_zoom > 0 ? config.height_zoom : root
							.getOffsetHeight());
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

		WebGLRenderingContext glc = WebGLRenderingContext.getContext(canvas,
				attrs);
		if (glc == null) {
			throw new RuntimeException("Unable to create GL context");
		}

		((GWTGL20) gl).init(glc);

		if (config.scaling()) {
			glc.viewport(0, 0, config.width_zoom, config.height_zoom);
		} else {
			glc.viewport(0, 0, config.width, config.height);
		}

		if (config.fullscreen) {
			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					if (getScreenWidthJSNI() == event.getWidth()
							&& getScreenHeightJSNI() == event.getHeight()) {
						float width = LSystem.viewSize.width(), height = LSystem.viewSize
								.height();
						experimentalScale = Math.min(getScreenWidthJSNI()
								/ width, getScreenHeightJSNI() / height);

						int yOfs = (int) ((getScreenHeightJSNI() - height
								* experimentalScale) / 3.f);
						int xOfs = (int) ((getScreenWidthJSNI() - width
								* experimentalScale) / 2.f);
						rootElement.setAttribute("style", "width:"
								+ experimentalScale * width + "px; "
								+ "height:" + experimentalScale * height
								+ "px; " + "position:absolute; left:" + xOfs
								+ "px; top:" + yOfs);
						Document.get().getBody().addClassName("fullscreen");
					} else {
						experimentalScale = 1;
						rootElement.removeAttribute("style");
						Document.get().getBody().removeClassName("fullscreen");
					}
				}
			});
		}

		Loon.self.addHandler(new OrientationChangedHandler() {
			@Override
			public void onChanged(Orientation newOrientation) {
				int width = Loon.self.getContainerWidth();
				int height = Loon.self.getContainerHeight();
				game.log().info(
						"update screen size width :" + width + " height :"
								+ height);
				setSize(width, height);
			}
		});
	}

	private boolean isFullscreen() {
		return isFullscreenJSNI();
	}

	private native int getScreenWidthJSNI() /*-{
		return $wnd.screen.width;
	}-*/;

	private native int getScreenHeightJSNI() /*-{
		return $wnd.screen.height;
	}-*/;

	private native boolean isFullscreenJSNI() /*-{
		if ("webkitIsFullScreen" in $doc) {
			return $doc.webkitIsFullScreen;
		}
		if ("mozFullScreen" in $doc) {
			return $doc.mozFullScreen;
		}
		return false
	}-*/;

	public void setSize(int width, int height) {
		rootElement.getStyle().setWidth(width, Unit.PX);
		rootElement.getStyle().setHeight(height, Unit.PX);
		canvas.setWidth(scale().scaledCeil(width));
		canvas.setHeight(scale().scaledCeil(height));
		canvas.getStyle().setWidth(width, Style.Unit.PX);
		canvas.getStyle().setHeight(height, Style.Unit.PX);
		viewportChanged(scale(), canvas.getWidth(), canvas.getHeight());
	}

	public void registerFontMetrics(String name, Font font, float lineHeight) {
		GWTFontMetrics metrics = getFontMetrics(font);
		fontMetrics.put(font, new GWTFontMetrics(font, lineHeight,
				metrics.emwidth));
	}

	@Override
	public Dimension screenSize() {
		screenSize.setSize(
				Document.get().getDocumentElement().getClientWidth(), Document
						.get().getDocumentElement().getClientHeight());
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
	protected Canvas createCanvasImpl(Scale scale, int pixelWidth,
			int pixelHeight) {
		CanvasElement elem = Document.get().createCanvasElement();
		elem.setWidth(pixelWidth);
		elem.setHeight(pixelHeight);
		return new GWTCanvas(this, new GWTImage(this, scale, elem, "<canvas>"));
	}

	void updateTexture(int tex, ImageElement img) {
		gl.glBindTexture(GL20.GL_TEXTURE_2D, tex);
		((GWTGL20) gl).glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA,
				GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, img);
	}

	GWTFontMetrics getFontMetrics(Font font) {
		GWTFontMetrics metrics = fontMetrics.get(font);
		if (metrics == null) {
			measureElement.getStyle().setFontSize(font.size, Unit.PX);
			measureElement.getStyle().setFontWeight(Style.FontWeight.NORMAL);
			measureElement.getStyle().setFontStyle(Style.FontStyle.NORMAL);
			measureElement.getStyle().setProperty("fontFamily", font.name);
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
			float height = measureElement.getOffsetHeight();
			measureElement.setInnerText(EMWIDTH_TEXT);
			float emwidth = measureElement.getOffsetWidth();
			metrics = new GWTFontMetrics(font, height, emwidth);
			fontMetrics.put(font, metrics);
		}
		return metrics;
	}

	Vector2f transformMouse(float x, float y) {
		return mousePoint.set(x / mouseScale, y / mouseScale);
	}

}
