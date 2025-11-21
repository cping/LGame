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
package loon.teavm.assets;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSDate;
import org.teavm.jso.typedarrays.Int8Array;

public abstract class IDBFileData implements JSObject {
    @JSProperty
    public abstract void setContents(byte[] contents);

    @JSProperty
    public abstract Int8Array getContents();

    @JSProperty
    public abstract int getType();

    @JSProperty
    public abstract JSDate getTimestamp();

    @JSBody(params = { "type", "timestamp" }, script = "return {type: type, date: timestamp};")
    public static native IDBFileData create(int type, JSDate timestamp);
}