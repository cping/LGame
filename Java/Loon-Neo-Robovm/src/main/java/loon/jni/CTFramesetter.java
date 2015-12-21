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

import org.robovm.rt.bro.*;
import org.robovm.rt.bro.annotation.*;
import org.robovm.rt.bro.ptr.*;
import org.robovm.apple.foundation.*;
import org.robovm.apple.corefoundation.*;
import org.robovm.apple.coregraphics.*;
import org.robovm.apple.coretext.CTFrameAttributes;
import org.robovm.apple.coretext.CTTypesetter;

@Library("CoreText")
public class CTFramesetter extends CFType {

	public static class CTFramesetterPtr extends
			Ptr<CTFramesetter, CTFramesetterPtr> {
	}

	static {
		Bro.bind(CTFramesetter.class);
	}

	protected CTFramesetter() {
	}

	@Bridge(symbol = "CTFramesetterGetTypeID", optional = true)
	public static native @MachineSizedUInt long getClassTypeID();

	@Bridge(symbol = "CTFramesetterCreateWithAttributedString", optional = true)
	public static native CTFramesetter create(NSAttributedString string);

	@Bridge(symbol = "CTFramesetterCreateFrame", optional = true)
	public native CTFrame createFrame(@ByVal CFRange stringRange, CGPath path,
			CTFrameAttributes frameAttributes);

	@Bridge(symbol = "CTFramesetterGetTypesetter", optional = true)
	public native CTTypesetter getTypesetter();

	@Bridge(symbol = "CTFramesetterSuggestFrameSizeWithConstraints", optional = true)
	public native @ByVal CGSize suggestFrameSize(@ByVal CFRange stringRange,
			CTFrameAttributes frameAttributes, @ByVal CGSize constraints,
			CFRange fitRange);

}
