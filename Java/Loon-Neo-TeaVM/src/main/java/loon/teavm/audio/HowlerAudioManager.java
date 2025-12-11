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
package loon.teavm.audio;

import loon.teavm.TeaResourceLoader;
import loon.teavm.assets.AssetData;

public class HowlerAudioManager {

	public HowlSound createSound(AssetData asset) {
		return new HowlSound(asset);
	}

	public HowlMusic createMusic(AssetData asset) {
		return new HowlMusic(asset);
	}

	public HowlSound createSound(TeaResourceLoader res) {
		return new HowlSound(res);
	}

	public HowlMusic createMusic(TeaResourceLoader res) {
		return new HowlMusic(res);
	}
}
