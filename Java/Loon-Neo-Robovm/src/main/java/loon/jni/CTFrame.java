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
package loon.jni;

import org.robovm.apple.coretext.CTFrameAttributes;
import org.robovm.rt.bro.*;
import org.robovm.rt.bro.annotation.*;
import org.robovm.rt.bro.ptr.*;
import org.robovm.apple.corefoundation.*;
import org.robovm.apple.coregraphics.*;

@Library("CoreText")
public class CTFrame extends CFType {

	public static class CTFramePtr extends Ptr<CTFrame, CTFramePtr> {
	}

	static {
		Bro.bind(CTFrame.class);
	}

	protected CTFrame() {
	}

	@Bridge(symbol = "CTFrameGetTypeID", optional = true)
	public static native @MachineSizedUInt long getClassTypeID();

	@Bridge(symbol = "CTFrameGetStringRange", optional = true)
	public native @ByVal CFRange getStringRange();

	@Bridge(symbol = "CTFrameGetVisibleStringRange", optional = true)
	public native @ByVal CFRange getVisibleStringRange();

	@Bridge(symbol = "CTFrameGetPath", optional = true)
	public native CGPath getPath();

	@Bridge(symbol = "CTFrameGetFrameAttributes", optional = true)
	public native CTFrameAttributes getFrameAttributes();

	@Bridge(symbol = "CTFrameGetLines", optional = true)
	public native CFArray getLines();

	@Bridge(symbol = "CTFrameGetLineOrigins", optional = true)
	public native void getLineOrigins(@ByVal CFRange range, CGPoint origins);

	@Bridge(symbol = "CTFrameDraw", optional = true)
	public native void draw(CGContext context);
}
