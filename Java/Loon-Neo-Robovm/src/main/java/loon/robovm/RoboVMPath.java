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

import loon.canvas.Path;

import org.robovm.apple.coregraphics.CGMutablePath;

public class RoboVMPath implements Path {

  CGMutablePath cgPath = CGMutablePath.createMutable();

  @Override
  public Path reset() {
    cgPath.dispose();
    cgPath = CGMutablePath.createMutable();
    return this;
  }

  @Override
  public Path close() {
    cgPath.closeSubpath();
    return this;
  }

  @Override
  public Path moveTo(float x, float y) {
    cgPath.moveToPoint(null, x, y);
    return this;
  }

  @Override
  public Path lineTo(float x, float y) {
    cgPath.addLineToPoint(null, x, y);
    return this;
  }

  @Override
  public Path quadraticCurveTo(float cpx, float cpy, float x, float y) {
    cgPath.addQuadCurveToPoint(null, cpx, cpy, x, y);
    return this;
  }

  @Override
  public Path bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y) {
    cgPath.addCurveToPoint(null, c1x, c1y, c2x, c2y, x, y);
    return this;
  }

  @Override
  protected void finalize() {
    cgPath.dispose(); 
  }
}
