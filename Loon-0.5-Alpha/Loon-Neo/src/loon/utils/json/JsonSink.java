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
package loon.utils.json;

import loon.Json;
import loon.utils.ArrayMap;
import loon.utils.TArray;

public interface JsonSink<I extends JsonSink<I>> {

  I array(TArray<Object> c);

  I array(Json.Array c);

  I array(String key, TArray<Object> c);

  I array(String key, Json.Array c);

  I object(ArrayMap map);

  I object(Json.Object map);

  I object(String key, ArrayMap map);

  I object(String key, Json.Object map);

  I nul();

  I nul(String key);

  I value(Object o);

  I value(String key, Object o);

  I value(String s);

  I value(boolean b);

  I value(Number n);

  I value(String key, String s);

  I value(String key, boolean b);

  I value(String key, Number n);

  I array();

  I object();

  I array(String key);

  I object(String key);

  I end();
}
