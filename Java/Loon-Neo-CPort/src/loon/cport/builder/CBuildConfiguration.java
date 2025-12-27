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

import java.net.URL;
import java.util.ArrayList;

import loon.cport.assets.AssetFile;

public class CBuildConfiguration {

	public AssetFilter assetFilter = null;
	
	public ArrayList<AssetFile> assetsPath = new ArrayList<AssetFile>();

	public ArrayList<String> assetsClasspath = new ArrayList<String>();

	public boolean shouldGenerateAssetFile = true;

	public String cappPath = "";

	public final ArrayList<URL> additionalClasspath = new ArrayList<URL>();

	public final ArrayList<String> classesToPreserve = new ArrayList<>();

	public String mainClassArgs = "";

	public CBaseApp baseApp;
	
	public TargetType targetType = TargetType.CPort;
	
	public String targetFileName = "run";

	public BuildReflectionListener reflectionListener;
}
