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

	@Import(name = "Load_STB_TempSurfaceFree")
	public final static native void freeSurfaceFree();

	@Import(name = "Load_STB_Image_Free")
	public final static native void freeImage(long handle);

	@Import(name = "Load_STB_Image_GetPixels")
	public final static native byte[] getImagePixels(long handle);

	@Import(name = "Load_STB_Image_GetPixels32")
	public final static native int[] getImagePixels32(long handle);

	@Import(name = "Load_STB_Image_GetWidth")
	public final static native int getImageWidth(long handle);

	@Import(name = "Load_STB_Image_GetHeight")
	public final static native int getImageHeight(long handle);

	@Import(name = "Load_STB_Image_GetFormat")
	public final static native int getImageFormat(long handle);
}
