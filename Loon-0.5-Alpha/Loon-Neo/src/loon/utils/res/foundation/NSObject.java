/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.utils.res.foundation;

import loon.LRelease;
import loon.LSystem;
import loon.utils.res.foundation.NSArray;
import loon.utils.res.foundation.NSAutoreleasePool;
import loon.utils.res.foundation.NSDictionary;
import loon.utils.res.foundation.NSObject;

//不完全的仿制cocoa库，可将相关对象序列化为xml文档，便于非标准Java环境保存
public abstract class NSObject implements LRelease {

	protected static final boolean YES = true;
	protected static final boolean NO = false;

	NSObject() {
		if (NSAutoreleasePool._instance != null
				&& NSAutoreleasePool._instance._enable && isArray()) {
			NSAutoreleasePool._instance.addObject(this);
		}
	}

	public boolean isEqual(NSObject o) {
		return super.equals(o);
	}

	public boolean isArray() {
		return this instanceof NSArray
				|| this instanceof NSDictionary;
	}

	protected abstract void addSequence(StringBuilder sbr, String indent);

	public String toSequence() {
		StringBuilder sbr = new StringBuilder(512);
		sbr.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sbr.append(LSystem.LS);
		sbr.append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
		sbr.append(LSystem.LS);
		sbr.append("<plist version=\"1.0\">");
		sbr.append(LSystem.LS);
		addSequence(sbr, "");
		sbr.append(LSystem.LS);
		sbr.append("</plist>");
		return sbr.toString();
	}

	@Override
	public String toString() {
		return toSequence();
	}

	@Override
	public void close() {
		if (NSAutoreleasePool._instance != null
				&& NSAutoreleasePool._instance._enable && isArray()) {
			NSAutoreleasePool._instance.removeObject(this);
		}
	}
}
