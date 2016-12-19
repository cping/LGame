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
package loon.html5.gwt;

import loon.html5.gwt.GWTGame.Mode;

import com.google.gwt.user.client.Window;

public interface GWTUrl {

	public static interface Log {
		static final String DEBUG = "DEBUG";
		static final String ERROR = "ERROR";
		static final String FATAL = "FATAL";
		static final String INFO = "INFO";
		static final String PARAM_NAME = "log_level";
		static final String TRACE = "TRACE";
		static final String WARN = "WARN";
	}

	public static class Renderer {
		static final String CANVAS = "canvas";
		static final String GL = "gl";
		static final String PARAM_NAME = "renderer";

		static Mode requestedMode() {
			String renderer = Window.Location.getParameter(PARAM_NAME);
			if (CANVAS.equals(renderer)) {
				return Mode.CANVAS;
			} else if (GL.equals(renderer)) {
				return Mode.WEBGL;
			}
			return Mode.AUTODETECT;
		}
	}

	public static interface Sound {
		static final String FLASH = "flash";
		static final String HTML5 = "html5";
		static final String NATIVE = "native";
		static final String WEBAUDIO = "webaudio";
		static final String PARAM_NAME = "gwt-voices";
	}

	public static boolean checkGLErrors = "check".equals(Window.Location
			.getParameter("glerrors"));

	public static boolean quadShader = "quad".equals(Window.Location
			.getParameter("glshader"));
}
