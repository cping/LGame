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
import org.teavm.interop.StaticInit;
import org.teavm.interop.Unmanaged;

@Unmanaged
@StaticInit
public final class STBCall {

	private STBCall() {
		importInclude();
	}

	@RuntimeInclude("STBSupport.h")
	@Import(name = "ImportSTBInclude")
	public final static native void importInclude();

	@Import(name = "Load_STB_InputDialog")
	public final static native void inputDialog(long handle, int dialogType, int width, int height, String title,
			String text, String textA, String textB);

	@Import(name = "Load_STB_Dialog_YesOrNO")
	public final static native boolean getDialogYesOrNO();

	@Import(name = "Load_STB_Dialog_InputText")
	public final static native String getDialogInputText();

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
	public final static native void getImagePixels(long handle, byte[] pixels);

	@Import(name = "Load_STB_Image_GetDefaultPixels32")
	public final static native void getImagePixels32(long handle, int w, int h, int[] pixels);

	@Import(name = "Load_STB_Image_GetPixels32")
	public final static native void getImagePixels32(long handle, int format, int[] pixels);

	@Import(name = "Load_STB_Image_GetWidth")
	public final static native int getImageWidth(long handle);

	@Import(name = "Load_STB_Image_GetHeight")
	public final static native int getImageHeight(long handle);

	@Import(name = "Load_STB_Image_GetFormat")
	public final static native int getImageFormat(long handle);

	@Import(name = "Load_STB_Image_GetSizeFormat")
	public final static native void getSizeFormat(long handle, int[] rect);

	@Import(name = "Load_STB_Image_FailureReason")
	public final static native String getImageFailureReason();

	@Import(name = "Load_STB_LoadFontInfo")
	public final static native long loadFontInfo(String path);

	@Import(name = "Load_STB_LoadFontStyleInfo")
	public final static native long loadFontStyleInfo(String path, String fontName, int style);

	@Import(name = "Load_STB_LoadSystemFontStyleInfo")
	public final static native long loadSystemFontStyleInfo(String sysfontName, String path, String fontName,
			int style);

	@Import(name = "Load_STB_GetTextLinesSize")
	public final static native void getTextLinesSize(long handle, String text, float fontScale, int align, int[] rect);

	@Import(name = "Load_STB_GetCodepointBitmapBox")
	public final static native void getCodepointBitmapBox(long handle, float fontsize, int point, int[] rect);

	@Import(name = "Load_STB_GetFontVMetrics")
	public final static native void getFontVMetrics(long handle, float fontsize, int[] rect);

	@Import(name = "Load_STB_GetCodepointHMetrics")
	public final static native int getCodepointHMetrics(long handle, int point);

	@Import(name = "Load_STB_GetCharsSize")
	public final static native void getCharsSize(long handle, float fontSize, String text, int[] rect);

	@Import(name = "Load_STB_GetCharSize")
	public final static native void getCharSize(long handle, float fontSize, int point, int[] rect);

	@Import(name = "Load_STB_MakeCodepointBitmap")
	public final static native void makeCodepointBitmap(long handle, int point, float fontScale, int width, int height,
			byte[] bytes);

	@Import(name = "Load_STB_MakeDrawTextToBitmap")
	public final static native void makeDrawTextToBitmap(long handle, String text, float fontScale, int width,
			int height, byte[] bytes);

	@Import(name = "Load_STB_MakeCodepointBitmap32")
	public final static native void makeCodepointBitmap32(long handle, int point, float fontScale, int width,
			int height, int r, int g, int b, int[] pixels);

	@Import(name = "Load_STB_MakeDrawTextToBitmap32")
	public final static native void makeDrawTextToBitmap32(long handle, String text, float fontScale, int width,
			int height, int r, int g, int b, int[] pixels);

	@Import(name = "Load_STB_CloseFontInfo")
	public final static native void closeFontInfo(long handle);

	@Import(name = "Call_STB_GetCodepointBitmapBox")
	public final static native void getCodepointBitmapBox(float fontSize, int point, int[] rect);

	@Import(name = "Call_STB_GetFontVMetrics")
	public final static native void getFontVMetrics(float fontSize, int[] rect);

	@Import(name = "Call_STB_GetCodepointHMetrics")
	public final static native void getCodepointHMetrics(int point, int[] rect);

	@Import(name = "Call_STB_MakeCodepointBitmap")
	public final static native void makeCodepointBitmap(int point, float fontScale, int width, int height,
			byte[] bytes);

	@Import(name = "Call_STB_MakeDrawTextToBitmap")
	public final static native void makeDrawTextToBitmap(String text, float fontScale, int width, int height,
			byte[] bytes);

	@Import(name = "Load_STB_MeasureTextWidth")
	public final static native int measureTextWidth(long handle, String text, float fontScale);

	@Import(name = "Load_STB_MeasureTextHieght")
	public final static native int measureTextHieght(long handle, String text, float fontScale);

	@Import(name = "Load_STB_DrawTextLinesToBytes")
	public final static native void drawTextLinesToBytes(long handle, String text, float fontScale, int align,
			int[] outDims, byte[] pixels);

	@Import(name = "Load_STB_DrawTextLinesToInt32")
	public final static native void drawTextLinesToInt32(long handle, String text, float fontScale, int align, int r,
			int g, int b, int bgr, int bgg, int bgb, int bgA, int[] outDims, int[] pixels);

	@Import(name = "Load_STB_DrawChar")
	public final static native void drawChar(long handle, int codepoint, float fontScale, int color, int[] outsize,
			int[] outPixels);

	@Import(name = "Load_STB_DrawString")
	public final static native void drawString(long handle, String text, float fontScale, int color, int[] outsize,
			int[] outPixels);

	@Import(name = "Call_STB_SaveArgbToPng")
	public final static native void saveArgbToPngFile(String filename, int[] pixels, int w, int h);

	@Import(name = "Call_STB_CloseFontInfo")
	public final static native void closeFontInfo();
}
