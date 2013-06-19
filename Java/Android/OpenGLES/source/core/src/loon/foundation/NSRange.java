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
package loon.foundation;

import loon.core.LSystem;

public class NSRange extends NSObject {
	public int start = 0;
	public int end = 0;

	public NSRange(int start, int end) {
		if (start < end) {
			this.start = start;
			this.end = end;
		}
	}

	@Override
	protected void addSequence(StringBuilder sbr, String indent) {
		sbr.append(indent);
		sbr.append("<range>");
		sbr.append(LSystem.LS);
		sbr.append("<start>");
		sbr.append(start);
		sbr.append("</start>");
		sbr.append("<end>");
		sbr.append(end);
		sbr.append("</end>");
		sbr.append(LSystem.LS);
		sbr.append("</range>");
	}

}
