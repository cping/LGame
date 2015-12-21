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

import loon.html5.gwt.preloader.Preloader;

import com.google.gwt.storage.client.Storage;

public class GWTResources {
	
	public static enum FileType {

		Classpath,

		Internal,

		External,

		Absolute,

		Local;
	}
	
	public static final Storage LocalStorage = Storage.getLocalStorageIfSupported();

	final Preloader preloader;

	public GWTResources (Preloader preloader) {
		this.preloader = preloader;
	}

	public GWTResourcesLoader getFileHandle (String path, FileType type) {
		return new GWTResourcesLoader(preloader, path, type);
	}

	public GWTResourcesLoader classpath (String path) {
		return new GWTResourcesLoader(preloader, path, FileType.Classpath);
	}

	public GWTResourcesLoader internal (String path) {
		return new GWTResourcesLoader(preloader, path, FileType.Internal);
	}

}
