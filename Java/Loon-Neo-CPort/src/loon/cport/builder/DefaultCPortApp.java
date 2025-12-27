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
package loon.cport.builder;

import java.io.IOException;
import java.io.InputStream;

import loon.cport.assets.AssetFile;

public class DefaultCPortApp extends CBaseApp {

	@Override
	public void setup(TeaClassLoader classLoader, CBuildConfiguration config) {

		InputStream logoSteam = classLoader.getResourceAsStream("capp/logo.png");
		
		if (logoSteam != null) {
			try {
				byte[] bytes = logoSteam.readAllBytes();
				logoImage = bytes;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	
		if (logoImage == null) {
			AssetFile file = new AssetFile("capp/logo.png");
			if (file.exists()) {
				try {
					byte[] bytes = file.read().readAllBytes();
					logoImage = bytes;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
}
