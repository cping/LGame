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
package loon.robovm;

import loon.canvas.Gradient;

import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGGradient;
import org.robovm.apple.coregraphics.CGGradientDrawingOptions;
import org.robovm.apple.coregraphics.CGPoint;

public abstract class RoboVMGradient extends Gradient {

  private static final CGGradientDrawingOptions gdOptions = new CGGradientDrawingOptions(
    CGGradientDrawingOptions.BeforeStartLocation.value() |
    CGGradientDrawingOptions.AfterEndLocation.value());

  final CGGradient cgGradient;

  public static class Linear extends RoboVMGradient {
    final CGPoint start, end;

    public Linear(Gradient.Linear cfg) {
      super(cfg);
      this.start = new CGPoint(cfg.x0, cfg.y0);
      this.end = new CGPoint(cfg.x1, cfg.y1);
    }

    @Override
    void fill(CGBitmapContext bctx) {
      bctx.drawLinearGradient(cgGradient, start, end, gdOptions);
    }
  }

  public static class Radial extends RoboVMGradient {
    final CGPoint center;
    final float r;

    public Radial(Gradient.Radial cfg) {
      super(cfg);
      this.center = new CGPoint(cfg.x, cfg.y);
      this.r = cfg.r;
    }

    @Override
    void fill(CGBitmapContext bctx) {
      bctx.drawRadialGradient(cgGradient, center, 0, center, r, gdOptions);
    }
  }

  abstract void fill(CGBitmapContext bctx);

  protected RoboVMGradient(Config cfg) {
    float[] comps = new float[cfg.colors.length*4];
    int cc = 0;
    for (int color : cfg.colors) {
      comps[cc++] = ((color >> 16) & 0xFF) / 255f;
      comps[cc++] = ((color >>  8) & 0xFF) / 255f;
      comps[cc++] = ((color >>  0) & 0xFF) / 255f;
      comps[cc++] = ((color >> 24) & 0xFF) / 255f;
    }
    cgGradient = CGGradient.create(RoboVMGraphics.colorSpace, comps, cfg.positions);
  }

  @Override
  protected void finalize () {
    cgGradient.dispose();
  }
}
