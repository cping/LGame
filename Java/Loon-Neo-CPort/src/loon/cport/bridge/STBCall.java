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

import org.teavm.backend.c.intrinsic.RuntimeInclude;
import org.teavm.interop.Import;

public final class STBCall {

	private STBCall() {
		importInclude();
	}

	@RuntimeInclude("STBSupport.h")
	@Import(name = "ImportSTBInclude")
	public final static native void importInclude();

	@Import(name = "Load_STB_Image_LoadBytes")
	public final static native long loadBytesToImage(byte[] buffer, int len);

	@Import(name = "Load_STB_Image_LoadPath")
	public final static native long loadPathToImage(String path);

	@Import(name = "Load_STB_Image_LoadPathToSDLSurface")
	public final static native long loadPathToSDLSurface(String path);

	@Import(name = "Load_STB_Image_LoadSDLSurfaceARGB32")
	public final static native long loadSDLSurfaceARGB32(String path);

	@Import(name = "Load_STB_TempSurfaceFree")
	public final static native void freeSurface();

	@Import(name = "Load_STB_Image_Free")
	public final static native void freeImage(long handle);

	@Import(name = "Load_STB_Image_GetPixels")
	public final static native byte[] getImagePixels(long handle);

	@Import(name = "Load_STB_Image_GetDefaultPixels32")
	public final static native int[] getImagePixels32(long handle);

	@Import(name = "Load_STB_Image_GetPixels32")
	public final static native int[] getImagePixels32(long handle, int format);

	@Import(name = "Load_STB_Image_GetWidth")
	public final static native int getImageWidth(long handle);

	@Import(name = "Load_STB_Image_GetHeight")
	public final static native int getImageHeight(long handle);

	@Import(name = "Load_STB_Image_GetFormat")
	public final static native int getImageFormat(long handle);

	@Import(name = "Load_STB_Image_FailureReason")
	public final static native String getImageFailureReason();

	@Import(name = "Load_STB_LoadFontInfo")
	public final static native long loadFontInfo(String path);

	@Import(name = "Load_STB_LoadFontStyleInfo")
	public final static native long loadFontStyleInfo(String path, String fontName, int style);

	@Import(name = "Load_STB_GetCodepointBitmapBox")
	public final static native int[] getCodepointBitmapBox(long handle, float fontsize, int point);

	@Import(name = "Load_STB_GetFontVMetrics")
	public final static native int[] getFontVMetrics(long handle, float fontsize);

	@Import(name = "Load_STB_GetCodepointHMetrics")
	public final static native int getCodepointHMetrics(long handle, int point);

	@Import(name = "Load_STB_GetCharsSize")
	public final static native int[] getCharsSize(long handle, float fontSize, String text);

	@Import(name = "Load_STB_GetCharSize")
	public final static native int[] getCharSize(long handle, float fontSize, int point);

	@Import(name = "Load_STB_MakeCodepointBitmap")
	public final static native byte[] makeCodepointBitmap(long handle, int point, float fontScale, int width,
			int height);

	@Import(name = "Load_STB_MakeDrawTextToBitmap")
	public final static native byte[] makeDrawTextToBitmap(long handle, String text, float fontScale, int width,
			int height);

	@Import(name = "Load_STB_MakeCodepointBitmap32")
	public final static native int[] makeCodepointBitmap32(long handle, int point, float fontScale, int width,
			int height);

	@Import(name = "Load_STB_MakeDrawTextToBitmap32")
	public final static native int[] makeDrawTextToBitmap32(long handle, String text, float fontScale, int width,
			int height);

	@Import(name = "Load_STB_CloseFontInfo")
	public final static native void closeFontInfo(long handle);

	@Import(name = "Call_STB_GetCodepointBitmapBox")
	public final static native int[] getCodepointBitmapBox(float fontSize, int point);

	@Import(name = "Call_STB_GetFontVMetrics")
	public final static native int[] getFontVMetrics(float fontSize);

	@Import(name = "Call_STB_GetCodepointHMetrics")
	public final static native int[] getCodepointHMetrics(int point);

	@Import(name = "Call_STB_MakeCodepointBitmap")
	public final static native int[] makeCodepointBitmap(int point, float fontScale, int width, int height);

	@Import(name = "Call_STB_MakeDrawTextToBitmap")
	public final static native int[] makeDrawTextToBitmap(String text, float fontscale, int width, int height);

	@Import(name = "Call_STB_CloseFontInfo")
	public final static native void closeFontInfo();
}
