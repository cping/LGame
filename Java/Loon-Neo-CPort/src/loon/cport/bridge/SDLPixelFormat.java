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

public final class SDLPixelFormat {

	public static final int SDL_PIXELFORMAT_UNKNOWN = 0;
	public static final int SDL_PIXELFORMAT_INDEX1LSB = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_INDEX1,
			SDLBitmapOrder.SDL_BITMAPORDER_4321, 0, 1, 0);
	public static final int SDL_PIXELFORMAT_INDEX1MSB = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_INDEX1,
			SDLBitmapOrder.SDL_BITMAPORDER_1234, 0, 1, 0);
	public static final int SDL_PIXELFORMAT_INDEX4LSB = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_INDEX4,
			SDLBitmapOrder.SDL_BITMAPORDER_4321, 0, 4, 0);
	public static final int SDL_PIXELFORMAT_INDEX4MSB = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_INDEX4,
			SDLBitmapOrder.SDL_BITMAPORDER_1234, 0, 4, 0);
	public static final int SDL_PIXELFORMAT_INDEX8 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_INDEX8, 0, 0, 8,
			1);
	public static final int SDL_PIXELFORMAT_RGB332 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED8,
			SDLPackedOrder.SDL_PACKEDORDER_XRGB, SDLPackedLayout.SDL_PACKEDLAYOUT_332, 8, 1);
	public static final int SDL_PIXELFORMAT_XRGB4444 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_XRGB, SDLPackedLayout.SDL_PACKEDLAYOUT_4444, 12, 2);
	public static final int SDL_PIXELFORMAT_RGB444 = SDL_PIXELFORMAT_XRGB4444;
	public static final int SDL_PIXELFORMAT_XBGR4444 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_XBGR, SDLPackedLayout.SDL_PACKEDLAYOUT_4444, 12, 2);
	public static final int SDL_PIXELFORMAT_BGR444 = SDL_PIXELFORMAT_XBGR4444;
	public static final int SDL_PIXELFORMAT_XRGB1555 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_XRGB, SDLPackedLayout.SDL_PACKEDLAYOUT_1555, 15, 2);
	public static final int SDL_PIXELFORMAT_RGB555 = SDL_PIXELFORMAT_XRGB1555;
	public static final int SDL_PIXELFORMAT_XBGR1555 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_XBGR, SDLPackedLayout.SDL_PACKEDLAYOUT_1555, 15, 2);
	public static final int SDL_PIXELFORMAT_BGR555 = SDL_PIXELFORMAT_XBGR1555;
	public static final int SDL_PIXELFORMAT_ARGB4444 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_ARGB, SDLPackedLayout.SDL_PACKEDLAYOUT_4444, 16, 2);
	public static final int SDL_PIXELFORMAT_RGBA4444 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_RGBA, SDLPackedLayout.SDL_PACKEDLAYOUT_4444, 16, 2);
	public static final int SDL_PIXELFORMAT_ABGR4444 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_ABGR, SDLPackedLayout.SDL_PACKEDLAYOUT_4444, 16, 2);
	public static final int SDL_PIXELFORMAT_BGRA4444 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_BGRA, SDLPackedLayout.SDL_PACKEDLAYOUT_4444, 16, 2);
	public static final int SDL_PIXELFORMAT_ARGB1555 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_ARGB, SDLPackedLayout.SDL_PACKEDLAYOUT_1555, 16, 2);
	public static final int SDL_PIXELFORMAT_RGBA5551 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_RGBA, SDLPackedLayout.SDL_PACKEDLAYOUT_5551, 16, 2);
	public static final int SDL_PIXELFORMAT_ABGR1555 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_ABGR, SDLPackedLayout.SDL_PACKEDLAYOUT_1555, 16, 2);
	public static final int SDL_PIXELFORMAT_BGRA5551 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_BGRA, SDLPackedLayout.SDL_PACKEDLAYOUT_5551, 16, 2);
	public static final int SDL_PIXELFORMAT_RGB565 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_XRGB, SDLPackedLayout.SDL_PACKEDLAYOUT_565, 16, 2);
	public static final int SDL_PIXELFORMAT_BGR565 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED16,
			SDLPackedOrder.SDL_PACKEDORDER_XBGR, SDLPackedLayout.SDL_PACKEDLAYOUT_565, 16, 2);
	public static final int SDL_PIXELFORMAT_RGB24 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_ARRAYU8,
			SDLArrayOrder.SDL_ARRAYORDER_RGB, 0, 24, 3);
	public static final int SDL_PIXELFORMAT_BGR24 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_ARRAYU8,
			SDLArrayOrder.SDL_ARRAYORDER_BGR, 0, 24, 3);
	public static final int SDL_PIXELFORMAT_XRGB8888 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_XRGB, SDLPackedLayout.SDL_PACKEDLAYOUT_8888, 24, 4);
	public static final int SDL_PIXELFORMAT_RGB888 = SDL_PIXELFORMAT_XRGB8888;
	public static final int SDL_PIXELFORMAT_RGBX8888 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_RGBX, SDLPackedLayout.SDL_PACKEDLAYOUT_8888, 24, 4);
	public static final int SDL_PIXELFORMAT_XBGR8888 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_XBGR, SDLPackedLayout.SDL_PACKEDLAYOUT_8888, 24, 4);
	public static final int SDL_PIXELFORMAT_BGR888 = SDL_PIXELFORMAT_XBGR8888;
	public static final int SDL_PIXELFORMAT_BGRX8888 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_BGRX, SDLPackedLayout.SDL_PACKEDLAYOUT_8888, 24, 4);
	public static final int SDL_PIXELFORMAT_ARGB8888 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_ARGB, SDLPackedLayout.SDL_PACKEDLAYOUT_8888, 32, 4);
	public static final int SDL_PIXELFORMAT_RGBA8888 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_RGBA, SDLPackedLayout.SDL_PACKEDLAYOUT_8888, 32, 4);
	public static final int SDL_PIXELFORMAT_ABGR8888 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_ABGR, SDLPackedLayout.SDL_PACKEDLAYOUT_8888, 32, 4);
	public static final int SDL_PIXELFORMAT_BGRA8888 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_BGRA, SDLPackedLayout.SDL_PACKEDLAYOUT_8888, 32, 4);
	public static final int SDL_PIXELFORMAT_ARGB2101010 = SDL_DEFINE_PIXELFORMAT(SDLPixelType.SDL_PIXELTYPE_PACKED32,
			SDLPackedOrder.SDL_PACKEDORDER_ARGB, SDLPackedLayout.SDL_PACKEDLAYOUT_2101010, 32, 4);

	private SDLPixelFormat() {
	}

	public static int SDL_DEFINE_PIXELFORMAT(int type, int order, int layout, int bits, int bytes) {
		return (1 << 28) | (type << 24) | (order << 20) | (layout << 16) | (bits << 8) | bytes;
	}

	public static int SDL_PIXELFLAG(int x) {
		return (x >> 28) & 0x0F;
	}

	public static int SDL_PIXELTYPE(int x) {
		return (x >> 24) & 0x0F;
	}

	public static int SDL_PIXELORDER(final int x) {
		return (x >> 20) & 0x0F;
	}

	public static int SDL_PIXELLAYOUT(int x) {
		return (x >> 16) & 0x0F;
	}

	public static int SDL_BITSPERPIXEL(final int x) {
		return (x >> 8) & 0xFF;
	}

	public static boolean SDL_ISPIXELFORMAT_FOURCC(int format) {
		return (format != 0) && (SDL_PIXELFLAG(format) != 1);
	}

	public static String toString(int value) {
		if (value == SDL_PIXELFORMAT_UNKNOWN) {
			return "SDL_PIXELFORMAT_UNKNOWN";
		} else if (value == SDL_PIXELFORMAT_INDEX1LSB) {
			return "SDL_PIXELFORMAT_INDEX1LSB";
		} else if (value == SDL_PIXELFORMAT_INDEX1MSB) {
			return "SDL_PIXELFORMAT_INDEX1MSB";
		} else if (value == SDL_PIXELFORMAT_INDEX4LSB) {
			return "SDL_PIXELFORMAT_INDEX4LSB";
		} else if (value == SDL_PIXELFORMAT_INDEX4MSB) {
			return "SDL_PIXELFORMAT_INDEX4MSB";
		} else if (value == SDL_PIXELFORMAT_INDEX8) {
			return "SDL_PIXELFORMAT_INDEX8";
		} else if (value == SDL_PIXELFORMAT_RGB332) {
			return "SDL_PIXELFORMAT_RGB332";
		} else if (value == SDL_PIXELFORMAT_XRGB4444) {
			return "SDL_PIXELFORMAT_XRGB4444";
		} else if (value == SDL_PIXELFORMAT_RGB444) {
			return "SDL_PIXELFORMAT_RGB444";
		} else if (value == SDL_PIXELFORMAT_XBGR4444) {
			return "SDL_PIXELFORMAT_XBGR4444";
		} else if (value == SDL_PIXELFORMAT_BGR444) {
			return "SDL_PIXELFORMAT_BGR444";
		} else if (value == SDL_PIXELFORMAT_XRGB1555) {
			return "SDL_PIXELFORMAT_XRGB1555";
		} else if (value == SDL_PIXELFORMAT_RGB555) {
			return "SDL_PIXELFORMAT_RGB555";
		} else if (value == SDL_PIXELFORMAT_XBGR1555) {
			return "SDL_PIXELFORMAT_XBGR1555";
		} else if (value == SDL_PIXELFORMAT_BGR555) {
			return "SDL_PIXELFORMAT_BGR555";
		} else if (value == SDL_PIXELFORMAT_ARGB4444) {
			return "SDL_PIXELFORMAT_ARGB4444";
		} else if (value == SDL_PIXELFORMAT_RGBA4444) {
			return "SDL_PIXELFORMAT_RGBA4444";
		} else if (value == SDL_PIXELFORMAT_ABGR4444) {
			return "SDL_PIXELFORMAT_ABGR4444";
		} else if (value == SDL_PIXELFORMAT_BGRA4444) {
			return "SDL_PIXELFORMAT_BGRA4444";
		} else if (value == SDL_PIXELFORMAT_ARGB1555) {
			return "SDL_PIXELFORMAT_ARGB1555";
		} else if (value == SDL_PIXELFORMAT_RGBA5551) {
			return "SDL_PIXELFORMAT_RGBA5551";
		} else if (value == SDL_PIXELFORMAT_ABGR1555) {
			return "SDL_PIXELFORMAT_ABGR1555";
		} else if (value == SDL_PIXELFORMAT_BGRA5551) {
			return "SDL_PIXELFORMAT_BGRA5551";
		} else if (value == SDL_PIXELFORMAT_RGB565) {
			return "SDL_PIXELFORMAT_RGB565";
		} else if (value == SDL_PIXELFORMAT_BGR565) {
			return "SDL_PIXELFORMAT_BGR565";
		} else if (value == SDL_PIXELFORMAT_RGB24) {
			return "SDL_PIXELFORMAT_RGB24";
		} else if (value == SDL_PIXELFORMAT_BGR24) {
			return "SDL_PIXELFORMAT_BGR24";
		} else if (value == SDL_PIXELFORMAT_XRGB8888) {
			return "SDL_PIXELFORMAT_XRGB8888";
		} else if (value == SDL_PIXELFORMAT_RGB888) {
			return "SDL_PIXELFORMAT_RGB888";
		} else if (value == SDL_PIXELFORMAT_RGBX8888) {
			return "SDL_PIXELFORMAT_RGBX8888";
		} else if (value == SDL_PIXELFORMAT_XBGR8888) {
			return "SDL_PIXELFORMAT_XBGR8888";
		} else if (value == SDL_PIXELFORMAT_BGR888) {
			return "SDL_PIXELFORMAT_BGR888";
		} else if (value == SDL_PIXELFORMAT_BGRX8888) {
			return "SDL_PIXELFORMAT_BGRX8888";
		} else if (value == SDL_PIXELFORMAT_ARGB8888) {
			return "SDL_PIXELFORMAT_ARGB8888";
		} else if (value == SDL_PIXELFORMAT_RGBA8888) {
			return "SDL_PIXELFORMAT_RGBA8888";
		} else if (value == SDL_PIXELFORMAT_ABGR8888) {
			return "SDL_PIXELFORMAT_ABGR8888";
		} else if (value == SDL_PIXELFORMAT_BGRA8888) {
			return "SDL_PIXELFORMAT_BGRA8888";
		} else if (value == SDL_PIXELFORMAT_ARGB2101010) {
			return "SDL_PIXELFORMAT_ARGB2101010";
		} else {
			return "UNKNOWN(" + value + ")";
		}
	}

}
