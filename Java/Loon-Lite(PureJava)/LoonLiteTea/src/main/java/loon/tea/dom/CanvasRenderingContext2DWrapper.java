package loon.tea.dom;

import org.teavm.jso.canvas.CanvasGradient;
import org.teavm.jso.canvas.CanvasPattern;
import org.teavm.jso.canvas.TextMetrics;
import org.teavm.jso.dom.html.HTMLImageElement;

public interface CanvasRenderingContext2DWrapper {

    HTMLCanvasElementWrapper getCanvas();

    void save();

    void restore();

    void scale(double x, double y);

    void rotate(double angle);

    void translate(double x, double y);

    void transform(double a, double b, double c, double d, double e, double f);

    void setTransform(double a, double b, double c, double d, double e, double f);

    double getGlobalAlpha();

    void setGlobalAlpha(double globalAlpha);

    String getGlobalCompositeOperation();

    void setGlobalCompositeOperation(String globalCompositeOperation);

    String getStrokeStyle();

    void setStrokeStyle(String strokeStyle);

    String getFillStyle();

    void setFillStyle(String fillStyle);

	CanvasGradient createLinearGradient(double x0, double y0, double x1, double y1);

	CanvasGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1);

	CanvasPattern createPattern(HTMLImageElement image, String repetition);

	CanvasPattern createPattern(HTMLCanvasElementWrapper image, String repetition);

	CanvasPattern createPattern(HTMLVideoElementWrapper image, String repetition);

    double getLineWidth();

    void setLineWidth(double lineWidth);

    String getLineCap();

    void setLineCap(String lineCap);

    String getLineJoin();

    void setLineJoin(String lineJoin);

    double getMiterLimit();

    void setMiterLimit(double miterLimit);

    double getShadowOffsetX();

    void setShadowOffsetX(double shadowOffsetX);

    double getShadowOffsetY();

    void setShadowOffsetY(double shadowOffsetY);

    double getShadowBlur();

    void setShadowBlur(double shadowBlur);

    String getShadowColor();

    void setShadowColor(String shadowColor);

    void clearRect(double x, double y, double w, double h);

    void fillRect(double x, double y, double w, double h);

    void strokeRect(double x, double y, double w, double h);

    void beginPath();

    void closePath();

    void moveTo(double x, double y);

    void lineTo(double x, double y);

    void quadraticCurveTo(double cpx, double cpy, double x, double y);

    void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y);

    void arcTo(double x1, double y1, double x2, double y2, double radius);

    void rect(double x, double y, double w, double h);

    void arc(double x, double y, double radius, double startAngle, double endAngle);

    void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise);

    void fill();

    void stroke();

    void drawSystemFocusRing(ElementWrapper element);

    boolean drawCustomFocusRing(ElementWrapper element);

    void scrollPathIntoView();

    void clip();

    boolean isPointInPath(double x, double y);

    String getFont();

    void setFont(String font);

    String getTextAlign();

    void setTextAlign(String textAlign);

    String getTextBaseline();

    void setTextBaseline(String textBaseline);

    void fillText(String text, double x, double y);

    void fillText(String text, double x, double y, double maxWidth);

    void strokeText(String text, double x, double y);

    void strokeText(String text, double x, double y, double maxWidth);

	TextMetrics measureText(String text);

    void drawImage(HTMLImageElementWrapper image, double dx, double dy);

    void drawImage(HTMLImageElementWrapper image, double dx, double dy, double dw, double dh);

    void drawImage(HTMLImageElementWrapper image, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh);

    void drawImage(HTMLCanvasElementWrapper image, double dx, double dy);

    void drawImage(HTMLCanvasElementWrapper image, double dx, double dy, double dw, double dh);

    void drawImage(HTMLCanvasElementWrapper image, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh);

    void drawImage(HTMLVideoElementWrapper image, double dx, double dy);

    void drawImage(HTMLVideoElementWrapper image, double dx, double dy, double dw, double dh);

    void drawImage(HTMLVideoElementWrapper image, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh);

    ImageDataWrapper createImageData(double sw, double sh);

    ImageDataWrapper createImageData(ImageDataWrapper imagedata);

    ImageDataWrapper getImageData(double sx, double sy, double sw, double sh);

    void putImageData(ImageDataWrapper imagedata, double dx, double dy);

    void putImageData(ImageDataWrapper imagedata, double dx, double dy, double dirtyX, double dirtyY, double dirtyWidth, double dirtyHeight);
}
