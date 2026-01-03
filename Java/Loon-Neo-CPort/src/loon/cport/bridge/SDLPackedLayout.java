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
package loon.cport.bridge;

public final class SDLPackedLayout {
	
    public static final int SDL_PACKEDLAYOUT_NONE = 0;
    public static final int SDL_PACKEDLAYOUT_332 = 1;
    public static final int SDL_PACKEDLAYOUT_4444 = 2;
    public static final int SDL_PACKEDLAYOUT_1555 = 3;
    public static final int SDL_PACKEDLAYOUT_5551 = 4;
    public static final int SDL_PACKEDLAYOUT_565 = 5;
    public static final int SDL_PACKEDLAYOUT_8888 = 6;
    public static final int SDL_PACKEDLAYOUT_2101010 = 7;
    public static final int SDL_PACKEDLAYOUT_1010102 = 8;

    private SDLPackedLayout() {
    }
}
