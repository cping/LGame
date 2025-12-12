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

import loon.teavm.assets.AssetFile;

public class DefaultWebApp extends WebBaseApp {

	@Override
	public void setup(TeaClassLoader classLoader, TeaBuildConfiguration config) {
		InputStream indexSteam = classLoader.getResourceAsStream("webapp/index.html");
		InputStream webXMLStream = classLoader.getResourceAsStream("webapp/WEB-INF/web.xml");
		InputStream scriptSteam = classLoader.getResourceAsStream("webapp/scripts/howler.js");
		InputStream logoSteam = classLoader.getResourceAsStream("webapp/logo.png");
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
		if (scriptSteam != null) {
			try {
				byte[] bytes = scriptSteam.readAllBytes();
				audioScript = new String(bytes);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		if (logoSteam != null) {
			try {
				byte[] bytes = logoSteam.readAllBytes();
				logoImage = bytes;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		if (mainHtml == null) {
			mainHtml = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "    <head>\r\n" + "        <title>%TITLE%</title>\r\n"
					+ "        <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">\r\n"
					+ "        <style>\r\n" + "            body {\r\n" + "                display: flex;\r\n"
					+ "                justify-content: center;\r\n" + "                align-items: center;\r\n"
					+ "                background: #000;\r\n" + "                height: 100vh;\r\n"
					+ "                margin: 0;\r\n" + "                padding: 0;\r\n"
					+ "                overflow: hidden;\r\n" + "            }\r\n" + "            #canvas {\r\n"
					+ "                border: none;\r\n" + "            }\r\n" + "        </style>\r\n"
					+ "    </head>\r\n" + "    <body oncontextmenu=\"return false\">\r\n" + "        <div>\r\n"
					+ "            <canvas id=\"maincanvas\"></canvas>\r\n" + "        </div>\r\n"
					+ "        <script>\r\n" + "            async function start() {\r\n" + "                %MODE%\r\n"
					+ "            }\r\n" + "            window.addEventListener(\"load\", start);\r\n"
					+ "        </script>\r\n" + "        %JS_SCRIPT%\r\n" + "    </body>\r\n" + "</html>";
		}
		if (webXML == null) {
			webXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
					+ "    xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\"\r\n"
					+ "    version=\"3.0\">\r\n" + "\r\n" + "</web-app>";
		}
		if (audioScript == null) {
			AssetFile file = new AssetFile("webapp/scripts/howler.js");
			if (file.exists()) {
				try {
					byte[] bytes = file.read().readAllBytes();
					audioScript = new String(bytes);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		if (logoImage == null) {
			AssetFile file = new AssetFile("webapp/logo.png");
			if (file.exists()) {
				try {
					byte[] bytes = file.read().readAllBytes();
					logoImage = bytes;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
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
