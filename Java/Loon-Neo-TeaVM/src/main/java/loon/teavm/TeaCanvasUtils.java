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

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.canvas.ImageData;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLImageElement;

import loon.canvas.LColor;
import loon.geom.RectI;
import loon.geom.Vector2f;
import loon.teavm.dom.HTMLDocumentExt;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class TeaCanvasUtils {

	public final static String DIV_NAME = "div";

	public final static String IMAGE_NAME = "img";

	public final static String CANVAS_NAME = "canvas";

	public final static String CANVAS_METHOD = "2d";

	public final static String WEBGL_METHOD = "webgl";

	public final static int ALIGN_CENTER = 0;

	public final static int ALIGN_LEFT = 1;

	public final static int ALIGN_RIGHT = 2;

	public final static int VALIGN_MIDDLE = 0;

	public final static int VALIGN_TOP = 1;

	public final static int VALIGN_BOTTOM = 2;

	public static HTMLCanvasElement createCanvas(HTMLCanvasElement canvas, int w, int h) {
		if (canvas != null) {
			canvas.setWidth(w);
			canvas.setHeight(h);
			canvas.getStyle().setProperty("width", w + "px");
			canvas.getStyle().setProperty("height", h + "px");
		}
		return canvas;
	}

	public static HTMLCanvasElement setCoordinateSpace(HTMLCanvasElement canvas, int w, int h) {
		if (canvas != null) {
			canvas.setWidth(w);
			canvas.setHeight(h);
		}
		return canvas;
	}

	public static HTMLCanvasElement setSizeCanvas(HTMLCanvasElement canvas, int w, int h) {
		if (canvas != null) {
			canvas.getStyle().setProperty("width", w + "px");
			canvas.getStyle().setProperty("height", h + "px");
		}
		return canvas;
	}

	public static JSObject getContextWebGL(HTMLCanvasElement canvas, JSObject attributes) {
		return canvas.getContext(WEBGL_METHOD, attributes);
	}

	public static HTMLElement createDiv(HTMLDocumentExt doc) {
		return doc.createElement(DIV_NAME);
	}

	public static HTMLImageElement createImage() {
		return createImage(IMAGE_NAME);
	}

	public static HTMLImageElement createImage(String imageName) {
		return (HTMLImageElement) TeaBase.get().getDocument().createElement(imageName);
	}

	public static HTMLCanvasElement createCanvas(String methodName) {
		return (HTMLCanvasElement) TeaBase.get().getDocument().createElement(methodName);
	}

	public static HTMLCanvasElement createCanvas(HTMLImageElement img) {
		return createCanvas(img.getOwnerDocument());
	}

	public static HTMLCanvasElement createCanvas(HTMLDocument doc) {
		return (HTMLCanvasElement) doc.createElement(CANVAS_NAME);
	}

	public static HTMLCanvasElement createCanvas() {
		return createCanvas(CANVAS_NAME);
	}

	public static CanvasRenderingContext2D getContext2d(HTMLCanvasElement canvas) {
		return getContext2d(canvas, CANVAS_METHOD);
	}

	public static CanvasRenderingContext2D getContext2d(HTMLCanvasElement canvas, String methodName) {
		return (CanvasRenderingContext2D) canvas.getContext(methodName);
	}

	public static HTMLCanvasElement createCanvas(int w, int h) {
		HTMLCanvasElement canvas = createCanvas();
		return createCanvas(canvas, w, h);
	}

	public static void clip(HTMLCanvasElement canvas, int x, int y, int width, int height) {
		CanvasRenderingContext2D g = getContext2d(canvas);
		g.beginPath();
		g.moveTo(x, y);
		g.lineTo(x + width, y);
		g.lineTo(x + width, y + height);
		g.lineTo(x, y + height);
		g.clip();
	}

	public static HTMLCanvasElement createCanvas(HTMLCanvasElement canvas, org.teavm.jso.canvas.ImageData data) {
		if (canvas == null) {
			canvas = createCanvas();
		}
		canvas.setWidth(data.getWidth());
		canvas.setHeight(data.getHeight());
		getContext2d(canvas).putImageData(data, 0, 0);
		return canvas;
	}

	public static void setPosition(HTMLElement root, float x, float y) {
		if (root != null) {
			root.setAttribute("style", "position:absolute; left:" + x + "px; top:" + y + "px");
		}
	}

	public static void setPosition(HTMLElement root, float x, float y, float w, float h) {
		if (root != null) {
			root.setAttribute("style", "width:" + w + "px; " + "height:" + h + "px; " + "position:absolute; left:" + x
					+ "px; top:" + y + "px");
		}
	}

	public static Vector2f getStylePosition(HTMLElement el) {
		if (el != null) {
			CSSStyleDeclaration style = el.getStyle();
			String left = style.getPropertyValue("left");
			String top = style.getPropertyValue("top");
			if (StringUtils.isEmpty(left)) {
				left = "0";
			}
			if (StringUtils.isEmpty(top)) {
				top = "0";
			}
			return Vector2f.at(
					Float.parseFloat(left.replace("em", "").replace("px", "").replace("pt", "").replace("cm", "")
							.replace("in", "").replace("%", "")),
					Float.parseFloat(top.replace("em", "").replace("px", "").replace("pt", "").replace("cm", "")
							.replace("in", "").replace("%", "")));
		}
		return Vector2f.ZERO();
	}

	public static Vector2f getPosition(HTMLElement el) {
		if (el != null) {
			return Vector2f.at(el.getAbsoluteLeft(), el.getAbsoluteTop());
		}
		return Vector2f.ZERO();
	}

	public static void drawCenter(HTMLCanvasElement canvas, HTMLImageElement image, int offsetX, int offsetY,
			float scaleX, float scaleY, float angle, float alpha) {
		CanvasRenderingContext2D g = getContext2d(canvas);
		g.save();
		float rx = (canvas.getWidth()) / 2;
		float ry = (canvas.getHeight()) / 2;
		g.translate(rx, ry);
		float rotate = (MathUtils.PI / 180) * angle;
		g.rotate(rotate);
		g.translate(-rx, -ry);
		g.scale(scaleX, scaleY);
		float px = (canvas.getWidth() / scaleX - image.getWidth()) / 2;
		float py = (canvas.getHeight() / scaleY - image.getHeight()) / 2;
		int ox = (int) (offsetX / scaleX);
		int oy = (int) (offsetY / scaleY);

		float x = ox;
		float y = oy;

		float nx = px + x * MathUtils.cos(-rotate) - y * MathUtils.sin(-rotate);
		float ny = py + x * MathUtils.sin(-rotate) + y * MathUtils.cos(-rotate);

		g.translate(nx, ny);
		g.setGlobalAlpha(alpha);
		g.drawImage(image, 0, 0);
		g.restore();
	}

	public static void drawCenter(HTMLCanvasElement canvas, HTMLCanvasElement canvasImage, int offsetX, int offsetY,
			float scaleX, float scaleY, float angle, float alpha) {
		CanvasRenderingContext2D g = getContext2d(canvas);
		g.save();
		float rx = (canvas.getWidth()) / 2;
		float ry = (canvas.getHeight()) / 2;

		g.translate(rx, ry);
		float rotate = (MathUtils.PI / 180) * angle;
		g.rotate(rotate);
		g.translate(-rx, -ry);

		g.scale(scaleX, scaleY);

		float px = (canvas.getWidth() / scaleX - canvasImage.getWidth()) / 2;
		float py = (canvas.getHeight() / scaleY - canvasImage.getHeight()) / 2;

		int ox = (int) (offsetX / scaleX);
		int oy = (int) (offsetY / scaleY);

		float x = ox;
		float y = oy;

		// offset is effect on angle,but scroll no need do it
		float nx = px + x * MathUtils.cos(-rotate) - y * MathUtils.sin(-rotate);
		float ny = py + x * MathUtils.sin(-rotate) + y * MathUtils.cos(-rotate);

		g.translate(nx, ny);
		g.setGlobalAlpha(alpha);
		g.drawImage(canvasImage, 0, 0);
		g.restore();
	}

	public static void clear(HTMLCanvasElement canvas) {
		getContext2d(canvas).clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public static void fillRect(HTMLCanvasElement canvas, LColor c) {
		CanvasRenderingContext2D g = getContext2d(canvas);
		g.setFillStyle("rgba(" + c.r + "," + c.g + "," + c.b + "," + c.a + ")");
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public static void fillRect(HTMLCanvasElement canvas, String style) {
		getContext2d(canvas).setFillStyle(style);
		getContext2d(canvas).fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public static void fillRect(HTMLCanvasElement canvas, String style, int x, int y, int w, int h) {
		getContext2d(canvas).setFillStyle(style);
		getContext2d(canvas).fillRect(x, y, w, h);
	}

	@JSBody(params = "element", script = "return element.toDataURL();")
	public native static String toDataUrl(HTMLCanvasElement element);

	public static String createColorRectImageDataUrl(int r, int g, int b, float opacity, int w, int h) {
		HTMLCanvasElement canvas = createCanvas(w, h);
		CanvasRenderingContext2D context = getContext2d(canvas);
		context.setFillStyle("rgba(" + r + "," + g + "," + b + "," + opacity + ")");
		context.fillRect(0, 0, w, h);
		String image1 = toDataUrl(canvas);
		return image1;
	}

	public static HTMLCanvasElement createCircleImageCanvas(int r, int g, int b, float opacity, float radius,
			float lineWidth, boolean stroke) {
		float center = radius + lineWidth;
		HTMLCanvasElement canvas = createCanvas((int) center * 2, (int) center * 2);
		CanvasRenderingContext2D context = getContext2d(canvas);
		if (stroke) {
			context.setStrokeStyle("rgba(" + r + "," + g + "," + b + "," + opacity + ")");
			context.setLineWidth(lineWidth);
		} else {
			context.setFillStyle("rgba(" + r + "," + g + "," + b + "," + opacity + ")");
		}
		context.beginPath();
		context.arc(center, center, radius, 0, 360);
		context.closePath();
		if (stroke) {
			context.stroke();
		} else {
			context.fill();
		}
		return canvas;
	}

	public static String createCircleImageDataUrl(int r, int g, int b, float opacity, float radius, float lineWidth,
			boolean stroke) {
		float center = radius + lineWidth;
		HTMLCanvasElement canvas = createCanvas((int) center * 2, (int) center * 2);
		CanvasRenderingContext2D context = getContext2d(canvas);
		if (stroke) {
			context.setStrokeStyle("rgba(" + r + "," + g + "," + b + "," + opacity + ")");
			context.setLineWidth(lineWidth);
		} else {
			context.setFillStyle("rgba(" + r + "," + g + "," + b + "," + opacity + ")");
		}
		context.beginPath();
		context.arc(center, center, radius, 0, 360);
		context.closePath();
		if (stroke) {
			context.stroke();
		} else {
			context.fill();
		}
		String image1 = toDataUrl(canvas);
		return image1;
	}

	public static void drawImageByCordinate(HTMLCanvasElement target, HTMLCanvasElement canvas, int sx1, int sy1,
			int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) {
		getContext2d(target).drawImage(canvas, sx1, sy1, sx2 - sx1, sy2 - sy1, dx1, dy1, dx2 - dx1, dy2 - dy1);
	}

	public static void drawFitCenter(HTMLCanvasElement canvas, HTMLImageElement img) {
		drawFitImage(canvas, img, ALIGN_CENTER, VALIGN_MIDDLE);
	}

	public static void drawExpandCenter(HTMLCanvasElement canvas, HTMLImageElement img) {
		drawExpandImage(canvas, img, ALIGN_CENTER, VALIGN_MIDDLE);
	}

	public static void drawFitImage(HTMLCanvasElement canvas, HTMLImageElement img, int align, int valign) {
		int cw = canvas.getWidth();
		int ch = canvas.getHeight();
		float[] newImageSize = calcurateFitSize(canvas.getWidth(), canvas.getHeight(), img.getWidth(), img.getHeight());
		float dx = 0;
		float dy = 0;
		if (align == ALIGN_CENTER) {
			dx = (cw - newImageSize[0]) / 2;
		} else if (align == ALIGN_RIGHT) {
			dx = cw - newImageSize[0];
		}
		if (valign == VALIGN_MIDDLE) {
			dy = (ch - newImageSize[1]) / 2;
		} else if (valign == VALIGN_BOTTOM) {
			dy = ch - newImageSize[1];
		}
		getContext2d(canvas).drawImage(img, dx, dy, newImageSize[0], newImageSize[1]);
	}

	public static void drawFitImage(HTMLCanvasElement canvas, HTMLImageElement img, RectI rect, int align, int valign) {
		int cw = rect.width;
		int ch = rect.height;
		float[] newImageSize = calculateFitSize(rect.width, rect.height, img.getWidth(), img.getHeight());

		float dx = rect.x;
		float dy = rect.y;
		if (align == ALIGN_CENTER) {
			dx = dx + (cw - newImageSize[0]) / 2;
		} else if (align == ALIGN_RIGHT) {
			dx = dx + cw - newImageSize[0];
		}
		if (valign == VALIGN_MIDDLE) {
			dy = dy + (ch - newImageSize[1]) / 2;
		} else if (valign == VALIGN_BOTTOM) {
			dy = dy + ch - newImageSize[1];
		}
		getContext2d(canvas).drawImage(img, dx, dy, newImageSize[0], newImageSize[1]);
	}

	public static void drawExpandImage(HTMLCanvasElement canvas, HTMLImageElement img, int align, int valign) {
		int cw = canvas.getWidth();
		int ch = canvas.getHeight();

		float[] newImageSize = calcurateExpandSize(canvas.getWidth(), canvas.getHeight(), img.getWidth(),
				img.getHeight());

		float dx = 0;
		float dy = 0;
		if (align == ALIGN_CENTER) {
			dx = (cw - newImageSize[0]) / 2;
		} else if (align == ALIGN_RIGHT) {
			dx = cw - newImageSize[0];
		}
		if (valign == VALIGN_MIDDLE) {
			dy = (ch - newImageSize[1]) / 2;
		} else if (valign == VALIGN_BOTTOM) {
			dy = ch - newImageSize[1];
		}
		getContext2d(canvas).drawImage(img, dx, dy, newImageSize[0], newImageSize[1]);
	}

	public static float[] calcurateFitSize(int canvasWidth, int canvasHeight, int imageWidth, int imageHeight) {
		return calculateFitSize(canvasWidth, canvasHeight, imageWidth, imageHeight);
	}

	public static float[] calculateFitSize(int canvasWidth, int canvasHeight, int imageWidth, int imageHeight) {
		float rw = (float) canvasWidth / imageWidth;
		float rh = (float) canvasHeight / imageHeight;

		float[] result = new float[2];
		if (rw < rh) {
			result[0] = canvasWidth;
			result[1] = rw * imageHeight;
		} else {
			result[0] = rh * imageWidth;
			result[1] = canvasHeight;
		}

		return result;
	}

	public static float[] calcurateExpandSize(int canvasWidth, int canvasHeight, int imageWidth, int imageHeight) {
		return calculateExpandSize(canvasWidth, canvasHeight, imageWidth, imageHeight);
	}

	public static float[] calculateExpandSize(int canvasWidth, int canvasHeight, int imageWidth, int imageHeight) {
		float rw = (float) canvasWidth / imageWidth;
		float rh = (float) canvasHeight / imageHeight;

		float[] result = new float[2];
		if (rw < rh) {
			result[0] = rh * imageWidth;
			result[1] = canvasHeight;

		} else {
			result[0] = canvasWidth;
			result[1] = rw * imageHeight;
		}

		return result;
	}

	public static void drawCenter(HTMLCanvasElement canvas, HTMLImageElement img) {
		int cw = canvas.getWidth();
		int ch = canvas.getHeight();
		int dx = (cw - img.getWidth()) / 2;
		int dy = (ch - img.getHeight()) / 2;
		getContext2d(canvas).drawImage(img, dx, dy, img.getWidth(), img.getHeight());
	}

	public static ImageData getImageData(HTMLCanvasElement canvas, boolean copy) {
		if (copy) {
			return getContext2d(canvas).getImageData(0, 0, canvas.getWidth(), canvas.getHeight());
		} else {
			return getContext2d(canvas).createImageData(canvas.getWidth(), canvas.getHeight());
		}
	}

	public static HTMLCanvasElement copyTo(HTMLCanvasElement imageCanvas, HTMLCanvasElement canvas) {
		return copyTo(imageCanvas, canvas, true);
	}

	public static HTMLCanvasElement copyToSizeOnly(HTMLCanvasElement imageCanvas, HTMLCanvasElement canvas) {
		return copyTo(imageCanvas, canvas, false);
	}

	public static HTMLCanvasElement copyToFlipHorizontal(HTMLCanvasElement imageCanvas, HTMLCanvasElement canvas) {
		if (canvas == null) {
			canvas = createCanvas();
		}
		canvas.getStyle().setProperty("width", imageCanvas.getWidth() + "px");
		canvas.getStyle().setProperty("height", imageCanvas.getHeight() + "px");
		canvas.setWidth(imageCanvas.getWidth());
		canvas.setHeight(imageCanvas.getHeight());
		CanvasRenderingContext2D context = getContext2d(canvas);
		context.save();
		context.translate(imageCanvas.getWidth(), 0);
		context.scale(-1, 1);
		context.drawImage(imageCanvas, 0, 0);
		context.restore();
		return canvas;
	}

	public static HTMLCanvasElement copyTo(HTMLCanvasElement imageCanvas, HTMLCanvasElement canvas, boolean drawImage) {
		if (canvas == null) {
			canvas = createCanvas();
		}
		canvas.getStyle().setProperty("width", imageCanvas.getWidth() + "px");
		canvas.getStyle().setProperty("height", imageCanvas.getHeight() + "px");
		canvas.setWidth(imageCanvas.getWidth());
		canvas.setHeight(imageCanvas.getHeight());
		if (drawImage) {
			getContext2d(canvas).drawImage(imageCanvas, 0, 0);
		}
		return canvas;
	}

	public static void drawImage(HTMLCanvasElement sharedCanvas, HTMLCanvasElement element) {
		getContext2d(sharedCanvas).drawImage(element, 0, 0);
	}

	public static void drawImage(HTMLCanvasElement sharedCanvas, HTMLImageElement element, int x, int y) {
		getContext2d(sharedCanvas).drawImage(element, x, y);
	}

	public static void drawLine(HTMLCanvasElement canvas, float x1, float y1, float x2, float y2) {
		CanvasRenderingContext2D c2d = getContext2d(canvas);
		c2d.beginPath();
		c2d.moveTo(x1, y1);
		c2d.lineTo(x2, y2);
		c2d.closePath();
		c2d.stroke();
	}

	public static void setSize(HTMLCanvasElement canvas, int w, int h) {
		if (canvas != null) {
			canvas.setWidth(w);
			canvas.setHeight(h);
			canvas.getStyle().setProperty("width", w + "px");
			canvas.getStyle().setProperty("height", h + "px");
		}
	}

	public static void clearBackgroundImage(HTMLCanvasElement canvas) {
		String w = canvas.getStyle().getPropertyValue("width");
		String h = canvas.getStyle().getPropertyValue("height");
		canvas.setAttribute("style", "width:" + w + ";height:" + h + ";");
	}

	public static void setBackgroundImage(HTMLCanvasElement canvas, String imageUrl) {
		String w = canvas.getStyle().getPropertyValue("width");
		String h = canvas.getStyle().getPropertyValue("height");
		canvas.setAttribute("style",
				"width:" + w + ";height:" + h + ";background-image:" + "url(\"" + imageUrl + "\");");

	}

	public static void setBackgroundImage(HTMLCanvasElement canvas, String imageUrl, int iw, int ih) {
		String w = canvas.getStyle().getPropertyValue("width");
		String h = canvas.getStyle().getPropertyValue("height");
		canvas.setAttribute("style", "width:" + w + ";height:" + h + ";background-image:" + "url(\"" + imageUrl
				+ "\");background-size:" + iw + "px " + ih + "px;");

	}
}
