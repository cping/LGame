/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite.node;

import java.util.ArrayList;

import loon.utils.StringUtils;

public class DefAnimation extends DefinitionObject {

	public String uniqueID;

	private LNAnimation animation;

	public LNAnimation get() {
		return animation;
	}

	@Override
	public void definitionObjectDidFinishParsing() {
		super.definitionObjectDidFinishParsing();
		if (animation != null) {
			LNDataCache.setAnimation(this, this.uniqueID);
		}
	}

	@Override
	public void definitionObjectDidReceiveString(String v) {
		super.definitionObjectDidReceiveString(v);
		ArrayList<String> result = getResult(v);
		float time = 3f;
		for (String list : result) {
			if (list.length() > 2) {
				String[] values = StringUtils.split(list, "=");
				String name = values[0];
				String value = values[1];
				if ("id".equalsIgnoreCase(name)) {
					this.uniqueID = value;
				} else if ("animationid".equalsIgnoreCase(name)) {
					this.uniqueID = value;
				} else if ("time".equalsIgnoreCase(name)) {
					time = Float.parseFloat(value);
				} else if ("duration".equalsIgnoreCase(name)) {
					time = Float.parseFloat(value);
				} else if ("list".equalsIgnoreCase(name)) {
					if (animation == null) {
						animation = new LNAnimation();
					}
					animation._name = uniqueID;
					String[] lists = StringUtils.split(list, ",");
					for (int i = 0; i < lists.length; i++) {
						animation.addFrameStruct(lists[i], time);
					}
				}
			}
		}
		result.clear();
	}

}
