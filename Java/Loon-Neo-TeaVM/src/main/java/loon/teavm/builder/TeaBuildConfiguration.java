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

import java.net.URL;
import java.util.ArrayList;

import loon.teavm.assets.AssetFile;

public class TeaBuildConfiguration {

	public AssetFilter assetFilter = null;
	public ArrayList<AssetFile> assetsPath = new ArrayList<AssetFile>();

	public ArrayList<String> assetsClasspath = new ArrayList<String>();

	public boolean shouldGenerateAssetFile = true;

	public String webappPath = "";

	public final ArrayList<URL> additionalClasspath = new ArrayList<URL>();

	public final ArrayList<String> classesToPreserve = new ArrayList<>();

	public String mainClassArgs = "";

	public String htmlTitle = "loon-teavm";

	public int gameWidth = 480;
	public int gameHeight = 320;

	public boolean useDefaultHtmlIndex = true;

	public boolean showLoadingLogo = true;

	public String logoPath = "logo.png";

	public WebBaseApp webApp;
	public TargetType targetType = TargetType.JavaScript;
	public String targetFileName = "run";

	public BuildReflectionListener reflectionListener;
}
