package loon.html5.gwt;

import loon.geom.RectI;
import loon.utils.MathUtils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;

public class GWTCanvasUtils {

	public static int ALIGN_CENTER = 0;
	public static int ALIGN_LEFT = 1;
	public static int ALIGN_RIGHT = 2;

	public static int VALIGN_MIDDLE = 0;
	public static int VALIGN_TOP = 1;
	public static int VALIGN_BOTTOM = 2;

	public static Canvas createCanvas(Canvas canvas, int w, int h) {
		if (canvas != null) {
			canvas.setCoordinateSpaceWidth(w);
			canvas.setCoordinateSpaceHeight(h);
			canvas.setWidth(w + "px");
			canvas.setHeight(h + "px");
		}
		return canvas;
	}

	public static Canvas setCoordinateSpace(Canvas canvas, int w, int h) {
		if (canvas != null) {
			canvas.setCoordinateSpaceWidth(w);
			canvas.setCoordinateSpaceHeight(h);
		}
		return canvas;
	}

	public static Canvas setSizeCanvas(Canvas canvas, int w, int h) {
		if (canvas != null) {
			canvas.setWidth(w + "px");
			canvas.setHeight(h + "px");
		}
		return canvas;
	}

	public static Canvas createCanvas(int w, int h) {
		Canvas canvas = Canvas.createIfSupported();
		return createCanvas(canvas, w, h);
	}

	public static void clip(Canvas canvas, int x, int y, int width, int height) {
		canvas.getContext2d().beginPath();
		canvas.getContext2d().moveTo(x, y);
		canvas.getContext2d().lineTo(x + width, y);
		canvas.getContext2d().lineTo(x + width, y + height);
		canvas.getContext2d().lineTo(x, y + height);
		canvas.getContext2d().clip();
	}

	public static Canvas createCanvas(Canvas canvas, ImageData data) {
		if (canvas == null) {
			canvas = Canvas.createIfSupported();
		}
		canvas.setCoordinateSpaceWidth(data.getWidth());
		canvas.setCoordinateSpaceHeight(data.getHeight());
		canvas.getContext2d().putImageData(data, 0, 0);
		return canvas;
	}

	public static void drawCenter(Canvas canvas, ImageElement image,
			int offsetX, int offsetY, float scaleX, float scaleY, float angle,
			float alpha) {
		canvas.getContext2d().save();
		float rx = (canvas.getCoordinateSpaceWidth()) / 2;
		float ry = (canvas.getCoordinateSpaceHeight()) / 2;
		canvas.getContext2d().translate(rx, ry);
		float rotate = (MathUtils.PI / 180) * angle;
		canvas.getContext2d().rotate(rotate);
		canvas.getContext2d().translate(-rx, -ry);
		canvas.getContext2d().scale(scaleX, scaleY);
		float px = (canvas.getCoordinateSpaceWidth() / scaleX - image
				.getWidth()) / 2;
		float py = (canvas.getCoordinateSpaceHeight() / scaleY - image
				.getHeight()) / 2;
		int ox = (int) (offsetX / scaleX);
		int oy = (int) (offsetY / scaleY);

		float x = ox;
		float y = oy;

		float nx = px + x * MathUtils.cos(-rotate) - y * MathUtils.sin(-rotate);
		float ny = py + x * MathUtils.sin(-rotate) + y * MathUtils.cos(-rotate);

		canvas.getContext2d().translate(nx, ny);
		canvas.getContext2d().setGlobalAlpha(alpha);
		canvas.getContext2d().drawImage(image, 0, 0);
		canvas.getContext2d().restore();
	}

	public static void drawCenter(Canvas canvas, CanvasElement canvasImage,
			int offsetX, int offsetY, float scaleX, float scaleY, float angle,
			float alpha) {
		canvas.getContext2d().save();
		float rx = (canvas.getCoordinateSpaceWidth()) / 2;
		float ry = (canvas.getCoordinateSpaceHeight()) / 2;

		canvas.getContext2d().translate(rx, ry);
		float rotate = (MathUtils.PI / 180) * angle;
		canvas.getContext2d().rotate(rotate);
		canvas.getContext2d().translate(-rx, -ry);

		canvas.getContext2d().scale(scaleX, scaleY);

		float px = (canvas.getCoordinateSpaceWidth() / scaleX - canvasImage
				.getWidth()) / 2;
		float py = (canvas.getCoordinateSpaceHeight() / scaleY - canvasImage
				.getHeight()) / 2;

		int ox = (int) (offsetX / scaleX);
		int oy = (int) (offsetY / scaleY);

		float x = ox;
		float y = oy;

		// offset is effect on angle,but scroll no need do it
		float nx = px + x * MathUtils.cos(-rotate) - y * MathUtils.sin(-rotate);
		float ny = py + x * MathUtils.sin(-rotate) + y * MathUtils.cos(-rotate);

		canvas.getContext2d().translate(nx, ny);
		canvas.getContext2d().setGlobalAlpha(alpha);
		canvas.getContext2d().drawImage(canvasImage, 0, 0);
		canvas.getContext2d().restore();
	}

	public static void clear(Canvas canvas) {
		canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(),
				canvas.getCoordinateSpaceHeight());
	}

	public static void fillRect(Canvas canvas, String style) {
		canvas.getContext2d().setFillStyle(style);
		canvas.getContext2d().fillRect(0, 0, canvas.getCoordinateSpaceWidth(),
				canvas.getCoordinateSpaceHeight());
	}

	public static void fillRect(Canvas canvas, String style, int x, int y,
			int w, int h) {
		canvas.getContext2d().setFillStyle(style);
		canvas.getContext2d().fillRect(x, y, w, h);
	}

	public static String createColorRectImageDataUrl(int r, int g, int b,
			float opacity, int w, int h) {
		Canvas canvas = createCanvas(w, h);
		canvas.getContext2d().setFillStyle(
				"rgba(" + r + "," + g + "," + b + "," + opacity + ")");
		canvas.getContext2d().fillRect(0, 0, w, h);
		String image1 = canvas.toDataUrl();
		return image1;
	}

	public static Canvas createCircleImageCanvas(int r, int g, int b,
			float opacity, float radius, float lineWidth, boolean stroke) {
		float center = radius + lineWidth;
		Canvas canvas = createCanvas((int) center * 2, (int) center * 2);
		if (stroke) {
			canvas.getContext2d().setStrokeStyle(
					"rgba(" + r + "," + g + "," + b + "," + opacity + ")");
			canvas.getContext2d().setLineWidth(lineWidth);
		} else {
			canvas.getContext2d().setFillStyle(
					"rgba(" + r + "," + g + "," + b + "," + opacity + ")");
		}
		canvas.getContext2d().beginPath();

		canvas.getContext2d().arc(center, center, radius, 0, 360);
		canvas.getContext2d().closePath();
		if (stroke) {
			canvas.getContext2d().stroke();
		} else {
			canvas.getContext2d().fill();
		}

		return canvas;
	}

	public static String createCircleImageDataUrl(int r, int g, int b,
			float opacity, float radius, float lineWidth, boolean stroke) {
		float center = radius + lineWidth;
		Canvas canvas = createCanvas((int) center * 2, (int) center * 2);
		if (stroke) {
			canvas.getContext2d().setStrokeStyle(
					"rgba(" + r + "," + g + "," + b + "," + opacity + ")");
			canvas.getContext2d().setLineWidth(lineWidth);
		} else {
			canvas.getContext2d().setFillStyle(
					"rgba(" + r + "," + g + "," + b + "," + opacity + ")");
		}
		canvas.getContext2d().beginPath();

		canvas.getContext2d().arc(center, center, radius, 0, 360);
		canvas.getContext2d().closePath();
		if (stroke) {
			canvas.getContext2d().stroke();
		} else {
			canvas.getContext2d().fill();
		}
		String image1 = canvas.toDataUrl();
		return image1;
	}

	public static void drawImageByCordinate(Canvas target,
			CanvasElement canvas, int sx1, int sy1, int sx2, int sy2, int dx1,
			int dy1, int dx2, int dy2) {

		target.getContext2d().drawImage(canvas, sx1, sy1, sx2 - sx1, sy2 - sy1,
				dx1, dy1, dx2 - dx1, dy2 - dy1);
	}

	public static void drawFitCenter(Canvas canvas, ImageElement img) {
		drawFitImage(canvas, img, ALIGN_CENTER, VALIGN_MIDDLE);
	}

	public static void drawExpandCenter(Canvas canvas, ImageElement img) {
		drawExpandImage(canvas, img, ALIGN_CENTER, VALIGN_MIDDLE);
	}

	public static void drawFitImage(Canvas canvas, ImageElement img, int align,
			int valign) {
		int cw = canvas.getCoordinateSpaceWidth();
		int ch = canvas.getCoordinateSpaceHeight();
		float[] newImageSize = calcurateFitSize(
				canvas.getCoordinateSpaceWidth(),
				canvas.getCoordinateSpaceHeight(), img.getWidth(),
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
		canvas.getContext2d().drawImage(img, dx, dy, newImageSize[0],
				newImageSize[1]);
	}

	public static void drawFitImage(Canvas canvas, ImageElement img,
			RectI rect, int align, int valign) {
		int cw = rect.width;
		int ch = rect.height;
		float[] newImageSize = calculateFitSize(rect.width, rect.height,
				img.getWidth(), img.getHeight());

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
		canvas.getContext2d().drawImage(img, dx, dy, newImageSize[0],
				newImageSize[1]);
	}

	public static void drawExpandImage(Canvas canvas, ImageElement img,
			int align, int valign) {
		int cw = canvas.getCoordinateSpaceWidth();
		int ch = canvas.getCoordinateSpaceHeight();

		float[] newImageSize = calcurateExpandSize(
				canvas.getCoordinateSpaceWidth(),
				canvas.getCoordinateSpaceHeight(), img.getWidth(),
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
		canvas.getContext2d().drawImage(img, dx, dy, newImageSize[0],
				newImageSize[1]);
	}

	public static float[] calcurateFitSize(int canvasWidth, int canvasHeight,
			int imageWidth, int imageHeight) {
		return calculateFitSize(canvasWidth, canvasHeight, imageWidth,
				imageHeight);
	}

	public static float[] calculateFitSize(int canvasWidth, int canvasHeight,
			int imageWidth, int imageHeight) {
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

	public static float[] calcurateExpandSize(int canvasWidth,
			int canvasHeight, int imageWidth, int imageHeight) {
		return calculateExpandSize(canvasWidth, canvasHeight, imageWidth,
				imageHeight);
	}

	public static float[] calculateExpandSize(int canvasWidth,
			int canvasHeight, int imageWidth, int imageHeight) {
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

	public static void drawCenter(Canvas canvas, ImageElement img) {
		int cw = canvas.getCoordinateSpaceWidth();
		int ch = canvas.getCoordinateSpaceHeight();
		int dx = (cw - img.getWidth()) / 2;
		int dy = (ch - img.getHeight()) / 2;
		canvas.getContext2d().drawImage(img, dx, dy, img.getWidth(),
				img.getHeight());
	}

	public static ImageData getImageData(Canvas canvas, boolean copy) {
		if (copy) {
			return canvas.getContext2d().getImageData(0, 0,
					canvas.getCoordinateSpaceWidth(),
					canvas.getCoordinateSpaceHeight());
		} else {
			return canvas.getContext2d().createImageData(
					canvas.getCoordinateSpaceWidth(),
					canvas.getCoordinateSpaceHeight());
		}
	}

	public static Canvas copyTo(Canvas imageCanvas, Canvas canvas) {
		return copyTo(imageCanvas, canvas, true);
	}

	public static Canvas copyToSizeOnly(Canvas imageCanvas, Canvas canvas) {
		return copyTo(imageCanvas, canvas, false);
	}

	public static Canvas copyToFlipHorizontal(Canvas imageCanvas, Canvas canvas) {
		if (canvas == null) {
			canvas = Canvas.createIfSupported();
		}
		canvas.setWidth(imageCanvas.getCoordinateSpaceWidth() + "px");
		canvas.setHeight(imageCanvas.getCoordinateSpaceHeight() + "px");
		canvas.setCoordinateSpaceWidth(imageCanvas.getCoordinateSpaceWidth());
		canvas.setCoordinateSpaceHeight(imageCanvas.getCoordinateSpaceHeight());

		canvas.getContext2d().save();
		canvas.getContext2d().translate(imageCanvas.getCoordinateSpaceWidth(),
				0);
		canvas.getContext2d().scale(-1, 1);
		canvas.getContext2d().drawImage(imageCanvas.getCanvasElement(), 0, 0);
		canvas.getContext2d().restore();
		return canvas;
	}

	public static Canvas copyTo(Canvas imageCanvas, Canvas canvas,
			boolean drawImage) {
		if (canvas == null) {
			canvas = Canvas.createIfSupported();
		}
		canvas.setWidth(imageCanvas.getCoordinateSpaceWidth() + "px");
		canvas.setHeight(imageCanvas.getCoordinateSpaceHeight() + "px");
		canvas.setCoordinateSpaceWidth(imageCanvas.getCoordinateSpaceWidth());
		canvas.setCoordinateSpaceHeight(imageCanvas.getCoordinateSpaceHeight());
		if (drawImage) {
			canvas.getContext2d().drawImage(imageCanvas.getCanvasElement(), 0,
					0);
		}
		return canvas;
	}

	public static void drawImage(Canvas sharedCanvas, ImageElement element) {
		sharedCanvas.getContext2d().drawImage(element, 0, 0);
	}

	public static void drawImage(Canvas sharedCanvas, ImageElement element,
			int x, int y) {
		sharedCanvas.getContext2d().drawImage(element, x, y);
	}

	public static void drawImage(Canvas sharedCanvas, Canvas imageCanvas) {
		sharedCanvas.getContext2d().drawImage(imageCanvas.getCanvasElement(),
				0, 0);
	}

	public static void drawLine(Canvas canvas, float x1, float y1, float x2,
			float y2) {
		Context2d c2d = canvas.getContext2d();
		c2d.beginPath();
		c2d.moveTo(x1, y1);
		c2d.lineTo(x2, y2);
		c2d.closePath();
		c2d.stroke();
	}

	public static void setSize(Canvas canvas, int w, int h) {
		if (canvas != null) {
			canvas.setCoordinateSpaceWidth(w);
			canvas.setCoordinateSpaceHeight(h);
			canvas.setWidth(w + "px");
			canvas.setHeight(h + "px");
		}
	}

	public static void clearBackgroundImage(Canvas canvas) {
		String w = canvas.getElement().getStyle().getWidth();
		String h = canvas.getElement().getStyle().getHeight();
		canvas.getElement().setAttribute("style",
				"width:" + w + ";height:" + h + ";");
	}

	public static void setBackgroundImage(Canvas canvas, String imageUrl) {
		String w = canvas.getElement().getStyle().getWidth();
		String h = canvas.getElement().getStyle().getHeight();
		canvas.getElement().setAttribute(
				"style",
				"width:" + w + ";height:" + h + ";background-image:" + "url(\""
						+ imageUrl + "\");");

	}

	public static void setBackgroundImage(Canvas canvas, String imageUrl,
			int iw, int ih) {
		String w = canvas.getElement().getStyle().getWidth();
		String h = canvas.getElement().getStyle().getHeight();
		canvas.getElement().setAttribute(
				"style",
				"width:" + w + ";height:" + h + ";background-image:" + "url(\""
						+ imageUrl + "\");background-size:" + iw + "px " + ih
						+ "px;");

	}

}
