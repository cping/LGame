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
package loon.teavm;

import loon.Assets;
import loon.Asyn;
import loon.Sound;
import loon.canvas.ImageImpl;
import loon.canvas.ImageImpl.Data;
import loon.utils.StringUtils;

public class TeaAssets extends Assets {

	protected TeaAssets(Asyn s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	protected String getURLPath(String fileName) {
		return "url('" + StringUtils.replace(getPathPrefix(), "\\", "/") + fileName + "')";
	}

	@Override
	public Sound getSound(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTextSync(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Data load(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ImageImpl createImage(boolean async, int rawWidth, int rawHeight, String source) {
		// TODO Auto-generated method stub
		return null;
	}

}
