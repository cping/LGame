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
package loon.tea;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class TeaWebAgent {

    @JSBody(script =
            "var userAgent = navigator.userAgent.toLowerCase();"
                    + "return {"
                    + "firefox : userAgent.indexOf('firefox') != -1,"
                    + "chrome : userAgent.indexOf('chrome') != -1,"
                    + "safari : userAgent.indexOf('safari') != -1,"
                    + "opera : userAgent.indexOf('opera') != -1,"
                    + "IE : userAgent.indexOf('msie') != -1,"
                    + "macOS : userAgent.indexOf('mac') != -1,"
                    + "linux : userAgent.indexOf('linux') != -1,"
                    + "windows : userAgent.indexOf('win') != -1,"
                    + "userAgent : userAgent"
                    + "};")
    private static native JSObject createAgent();

    public static TeaAgentInfo computeAgentInfo() {
        JSObject jsObj = TeaWebAgent.createAgent();
        TeaAgentInfo agent = (TeaAgentInfo)jsObj;
        return agent;
    }
}
