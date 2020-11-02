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

import java.util.HashMap;

import loon.core.LRelease;

public class LNDataCache implements LRelease {

	private static boolean initCache;

	private static HashMap<String, DefImage> imageDictionary;

	private static HashMap<String, DefAnimation> animationDictionary;

	public static DefAnimation animationByKey(String key) {
		if (!initCache) {
			initDataDefinitions();
		}
		return animationDictionary.get(key);
	}

	public static LNFrameStruct getFrameStruct(String key) {
		if (!initCache) {
			initDataDefinitions();
		}
		DefImage img = imageByKey(key);
		if (img != null) {
			return LNFrameStruct.InitWithImage(img);
		}
		return null;
	}

	public static DefImage imageByKey(String key) {
		if (!initCache) {
			initDataDefinitions();
		}
		return imageDictionary.get(key.toLowerCase());
	}

	public static void initDataDefinitions() {
		if (!initCache) {
			imageDictionary = new HashMap<String, DefImage>();
			animationDictionary = new HashMap<String, DefAnimation>();
			initCache = true;
		}
	}

	public static void setAnimation(DefAnimation anim, String key) {
		if (!initCache) {
			initDataDefinitions();
		}
		animationDictionary.put(key, anim);
	}

	public static void setImage(DefImage img, String key) {
		if (!initCache) {
			initDataDefinitions();
		}
		imageDictionary.put(key.toLowerCase(), img);
	}

	@Override
	public void dispose() {
		initCache = false;
		if (imageDictionary != null) {
			imageDictionary.clear();
			imageDictionary = null;
		}
		if (animationDictionary != null) {
			animationDictionary.clear();
			animationDictionary = null;
		}
	}
}
