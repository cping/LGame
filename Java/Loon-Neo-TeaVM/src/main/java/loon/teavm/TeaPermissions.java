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

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class TeaPermissions {
	/**
	 * Returns the user permission status for a given API via the
	 * <a href="https://w3c.github.io/permissions/">Permissions API</a>
	 * 
	 * @param permission the permission, see <a href=
	 *                   "https://w3c.github.io/permissions/#permission-registry">w3c</a>
	 *                   for all permissions
	 * @param result     handler of permission result
	 */
	@JSBody(params = { "permission", "result" }, script = "if (\"permissions\" in navigator) {\n"
			+ "    navigator.permissions.query({\n" + "        name: permission\n"
			+ "    }).then(function (permissionStatus) {\n" + "        if (permissionStatus.state === 'granted') {\n"
			+ "            result.granted();\n" + "        } else if (permissionStatus.state === 'denied') {\n"
			+ "            result.denied();\n" + "        } else if (permissionStatus.state === 'prompt') {\n"
			+ "            result.prompt();\n" + "        }\n" + "        permissionStatus.onchange = function() {\n"
			+ "            if (permissionStatus.state === 'granted') {\n" + "                result.granted();\n"
			+ "            } else if (permissionStatus.state === 'denied') {\n" + "                result.denied();\n"
			+ "            } else if (permissionStatus.state === 'prompt') {\n" + "                result.prompt();\n"
			+ "            }\n" + "       };\n" + "    });\n" + "} else {\n" + "    result.granted();\n" + "}")
	public native static void queryPermission(String permission, TeaPermissionResult result);

	/**
	 * See <a href=
	 * "https://w3c.github.io/permissions/#status-of-a-permission">status-of-a-permission</a>
	 * for more information
	 */
	public interface TeaPermissionResult extends JSObject {
		/** the permission to access the feature is granted without asking the user */
		void granted();

		/** accessing the feature is not allowed */
		void denied();

		/** the user will be asked on permission if the feature is tried to access */
		void prompt();
	}
}
