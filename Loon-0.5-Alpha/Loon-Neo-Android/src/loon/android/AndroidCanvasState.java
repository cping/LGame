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
package loon.android;

import loon.canvas.Canvas;
import loon.canvas.Canvas.Composite;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

class AndroidCanvasState {

  static PorterDuffXfermode[] xfermodes;

  static int PAINT_FLAGS = Paint.ANTI_ALIAS_FLAG|Paint.SUBPIXEL_TEXT_FLAG;

  Paint paint;
  int fillColor;
  int strokeColor;
  AndroidGradient gradient;
  AndroidPattern pattern;
  float alpha;
  Composite composite;

  static {
    xfermodes = new PorterDuffXfermode[Canvas.Composite.values().length];
    for (Canvas.Composite composite : Canvas.Composite.values()) {
      xfermodes[composite.ordinal()] = new PorterDuffXfermode(
          PorterDuff.Mode.valueOf(composite.name()));
    }
  }

  AndroidCanvasState() {
    this(new Paint(PAINT_FLAGS), 0xff000000, 0xffffffff, null, null, Composite.SRC_OVER, 1f);
  }

  AndroidCanvasState(AndroidCanvasState toCopy) {
    this(copy(toCopy.paint), toCopy.fillColor, toCopy.strokeColor,
         toCopy.gradient, toCopy.pattern, toCopy.composite, toCopy.alpha);
  }

  AndroidCanvasState(Paint paint, int fillColor, int strokeColor, AndroidGradient gradient,
      AndroidPattern pattern, Composite composite, float alpha) {
    this.paint = paint;
    this.fillColor = fillColor;
    this.strokeColor = strokeColor;
    this.gradient = gradient;
    this.pattern = pattern;
    this.composite = composite;
    this.alpha = alpha;
  }

  void setFillColor(int color) {
    this.fillColor = color;
  }

  void setFillGradient(AndroidGradient gradient) {
    this.gradient = gradient;
  }

  void setFillPattern(AndroidPattern pattern) {
    this.pattern = pattern;
  }

  void setLineCap(Canvas.LineCap cap) {
    paint.setStrokeCap(convertCap(cap));
  }

  void setLineJoin(Canvas.LineJoin join) {
    paint.setStrokeJoin(convertJoin(join));
  }

  void setMiterLimit(float miter) {
    paint.setStrokeMiter(miter);
  }

  void setStrokeColor(int color) {
    this.strokeColor = color;
  }

  void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  void setCompositeOperation(Canvas.Composite composite) {
    this.composite = composite;
  }

  void setStrokeWidth(float strokeWidth) {
    paint.setStrokeWidth(strokeWidth);
  }

  Paint prepareFill() {
    paint.setStyle(Style.FILL);
    paint.setXfermode(convertComposite(composite));
    if (gradient != null) {
      paint.setShader(gradient.shader);
    } else if (pattern != null) {
      paint.setShader(pattern.shader);
    } else {
      paint.setShader(null);
      paint.setColor(fillColor);
      if (alpha < 1){
        paint.setAlpha((int) (alpha * (fillColor >>> 24)));
      }
    }
    return paint;
  }

  Paint prepareStroke() {
    paint.setShader(null);
    paint.setStyle(Style.STROKE);
    paint.setColor(strokeColor);
    if (alpha < 1){
      paint.setAlpha((int) (alpha * (strokeColor >>> 24)));
    }
    paint.setXfermode(convertComposite(composite));
    return paint;
  }

  Paint prepareImage() {
    paint.setAlpha((int) (alpha * 255));
    paint.setXfermode(convertComposite(composite));
    return paint;
  }

  private Cap convertCap(Canvas.LineCap cap) {
    switch (cap) {
      case BUTT:
        return Cap.BUTT;
      case ROUND:
        return Cap.ROUND;
      case SQUARE:
        return Cap.SQUARE;
    }
    return Cap.BUTT;
  }

  private Xfermode convertComposite(Canvas.Composite composite) {
    return xfermodes[composite.ordinal()];
  }

  private Join convertJoin(Canvas.LineJoin join) {
    switch (join) {
      case BEVEL:
        return Join.BEVEL;
      case MITER:
        return Join.MITER;
      case ROUND:
        return Join.ROUND;
    }
    return Join.MITER;
  }

  private static Paint copy(Paint source) {
    Paint clone = new Paint(PAINT_FLAGS);
    clone.set(source);
    return clone;
  }
}
