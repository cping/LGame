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
package loon.teavm.builder;

import java.io.IOException;
import java.io.InputStream;

public class DefaultWebApp extends WebBaseApp {

	@Override
	public void setup(TeaClassLoader classLoader, TeaBuildConfiguration config) {
		InputStream indexSteam = classLoader.getResourceAsStream("webapp/index.html");
		InputStream webXMLStream = classLoader.getResourceAsStream("webapp/WEB-INF/web.xml");

		if (indexSteam != null) {
			try {
				byte[] bytes = indexSteam.readAllBytes();
				mainHtml = new String(bytes);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		if (webXMLStream != null) {
			try {
				byte[] bytes = webXMLStream.readAllBytes();
				webXML = new String(bytes);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		String logo = config.logoPath;

		String mode = "main(%ARGS%)";
		String jsScript = "<script type=\"text/javascript\" charset=\"utf-8\" src=\"" + config.targetFileName
				+ ".js\"></script>";
		if (config.targetType == TargetType.WebAssembly) {
			mode = "let teavm = await TeaVM.wasmGC.load(\"" + config.targetFileName
					+ ".wasm\"); teavm.exports.main([%ARGS%]);";
			String jsName = "wasm-gc-runtime.min.js";
			jsScript = "<script type=\"text/javascript\" charset=\"utf-8\" src=\"" + jsName + "\"></script>";
		}

		mainHtml = mainHtml.replace("%MODE%", mode);
		mainHtml = mainHtml.replace("%JS_SCRIPT%", jsScript);
		mainHtml = mainHtml.replace("%TITLE%", config.htmlTitle);
		mainHtml = mainHtml.replace("%WIDTH%", String.valueOf(config.gameWidth));
		mainHtml = mainHtml.replace("%HEIGHT%", String.valueOf(config.gameHeight));
		mainHtml = mainHtml.replace("%ARGS%", config.mainClassArgs);
		if (config.showLoadingLogo) {
			rootAssets.add(logo);
		}
	}
}
