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

import loon.font.Font;

class GWTFontMetrics {

  public final Font font;

  public final float height;

  public final float emwidth;

  public GWTFontMetrics(Font font, float height, float emwidth) {
    this.font = font;
    this.height = height;
    this.emwidth = emwidth;
  }

  public float ascent() {
    return 0.7f * height; 
  }

  public float descent() {
    return height - ascent();
  }

  public float leading() {
    return 0.1f * height;
  }

  public float adjustWidth(float width) {
    switch (font.style) {
    case ITALIC:      return width + emwidth/8;
    case BOLD_ITALIC: return width + emwidth/6;
    default:          return width; 
    }
  }
}
