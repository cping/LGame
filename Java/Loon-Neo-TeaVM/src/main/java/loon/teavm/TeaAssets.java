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

import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;

import loon.Assets;
import loon.Asyn;
import loon.Sound;
import loon.canvas.ImageImpl;
import loon.canvas.ImageImpl.Data;
import loon.html5.gwt.GWTAssets.ImageManifest;
import loon.teavm.TeaGame.TeaSetting;
import loon.utils.Scale;
import loon.utils.StringUtils;

public class TeaAssets extends Assets {

	public interface ImageManifest {
		int[] imageSize(String path);
	}

	public void setImageManifest(ImageManifest manifest) {
		_imageManifest = manifest;
	}

	private TeaSetting _setting;

	private ImageManifest _imageManifest;

	private Scale _assetScale = null;
	
	protected TeaAssets(TeaGame g, Asyn s) {
		super(s);
		_setting = g.getSetting();
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

	private HTMLCanvasElement createEmptyCanvas(int w, int h) {
		HTMLCanvasElement canvasTmp = (HTMLCanvasElement) HTMLDocument.current().createElement(_setting.canvasName);
		canvasTmp.setWidth(w);
		canvasTmp.setHeight(h);
		CanvasRenderingContext2D context = (CanvasRenderingContext2D) canvasTmp.getContext(_setting.canvasMethod);
		context.setFillStyle("rgba(255,255,255,255)");
		context.fillRect(0, 0, w, h);
		return canvasTmp;
	}
}
