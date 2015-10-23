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

import loon.Graphics;
import loon.canvas.Canvas;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.geom.Vector2f;
import loon.opengl.GL20;
import loon.utils.Scale;

import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBitmapInfo;
import org.robovm.apple.coregraphics.CGColorSpace;
import org.robovm.apple.coregraphics.CGImageAlphaInfo;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIUserInterfaceIdiom;

public class RoboVMGraphics extends Graphics {

  static final CGColorSpace colorSpace = CGColorSpace.createDeviceRGB();

  final RoboVMGame game;
  private final float touchScale;
  private final Vector2f touchTemp = new Vector2f();
  private final Dimension screenSize = new Dimension();
  private int defaultFramebuffer;

  private static final int S_SIZE = 10;
  final CGBitmapContext scratchCtx = createCGBitmap(S_SIZE, S_SIZE);

  private static boolean useHalfSize (RoboVMGame game) {
    boolean isPad = UIDevice.getCurrentDevice().getUserInterfaceIdiom() == UIUserInterfaceIdiom.Pad;
    return isPad && game.config.iPadLikePhone;
  }
  private static Scale viewScale (RoboVMGame game) {
    float deviceScale = (float)UIScreen.getMainScreen().getScale();
    boolean useHalfSize = useHalfSize(game);
    return new Scale((useHalfSize ? 2 : 1) * deviceScale);
  }

  public RoboVMGraphics(RoboVMGame game, CGRect bounds) {
    super(game, new RoboVMGL20(), viewScale(game));
    this.game = game;
    this.touchScale = useHalfSize(game) ? 2 : 1;
    setSize(bounds);
  }

  @Override public Dimension screenSize() {
    CGRect screenBounds = UIScreen.getMainScreen().getBounds();
    screenSize.width = (int)screenBounds.getWidth();
    screenSize.height = (int)screenBounds.getHeight();
    if (useHalfSize(game)) {
      screenSize.width /= 2;
      screenSize.height /= 2;
    }
    return screenSize;
  }

  @Override public TextLayout layoutText(String text, TextFormat format) {
    return RoboVMTextLayout.layoutText(this, text, format);
  }

  @Override public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
    return RoboVMTextLayout.layoutText(this, text, format, wrap);
  }

  @Override protected int defaultFramebuffer () { return defaultFramebuffer; }

  @Override protected Canvas createCanvasImpl (Scale scale, int pixelWidth, int pixelHeight) {
    return new RoboVMCanvas(this, new RoboVMCanvasImage(this, scale, pixelWidth, pixelHeight,
                                                    game.config.interpolateCanvasDrawing));
  }

  static CGBitmapContext createCGBitmap(int width, int height) {
    return CGBitmapContext.create(width, height, 8, 4 * width, colorSpace, new CGBitmapInfo(
      CGImageAlphaInfo.PremultipliedLast.value()));
  }

  void viewDidInit(CGRect bounds) {
    defaultFramebuffer = gl.glGetInteger(GL20.GL_FRAMEBUFFER_BINDING);
    if (defaultFramebuffer == 0) throw new IllegalStateException(
      "Failed to determine defaultFramebuffer");
    setSize(bounds);
  }

  void setSize(CGRect bounds) {
    int viewWidth = scale.scaledCeil((float)bounds.getWidth());
    int viewHeight = scale.scaledCeil((float)bounds.getHeight());
    viewportChanged(scale, viewWidth, viewHeight);
  }

  Vector2f transformTouch(float x, float y) {
    return touchTemp.set(x/touchScale, y/touchScale);
  }
}
