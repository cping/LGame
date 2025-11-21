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
package loon.teavm.dom;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.events.EventTarget;
import org.teavm.jso.file.File;
import org.teavm.jso.typedarrays.ArrayBuffer;

public abstract class FileReaderWrapper implements EventTarget, JSObject {

    @JSMethod
    public abstract void readAsDataURL(File file);

    @JSMethod
    public abstract void readAsArrayBuffer(File file);

    @JSMethod
    public abstract void readAsText(File file);

    @JSProperty("result")
    public abstract ArrayBuffer getResultAsArrayBuffer();

    @JSProperty("result")
    public abstract String getResultAsString();

    @JSBody(script = "return new FileReader();")
    public static native FileReaderWrapper create();
}