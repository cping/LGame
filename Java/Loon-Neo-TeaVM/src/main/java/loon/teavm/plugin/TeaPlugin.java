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
package loon.teavm.plugin;

import org.teavm.backend.javascript.TeaVMJavaScriptHost;
import org.teavm.jso.impl.JSOPlugin;
import org.teavm.vm.spi.Before;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

@Before(JSOPlugin.class)
public class TeaPlugin implements TeaVMPlugin {
	@Override
	public void install(TeaVMHost host) {
		host.add(new OverlayTransformer());
		host.add(new ObjectDependency());
		TeaVMJavaScriptHost jsHost = host.getExtension(TeaVMJavaScriptHost.class);
		if (jsHost != null) {
			jsHost.add(new AssetsCopy());
		}
	}

}
