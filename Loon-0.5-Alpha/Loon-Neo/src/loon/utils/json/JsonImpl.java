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

public class JsonImpl implements Json {

  @Override
  public Json.Writer newWriter() {
    return new JsonStringWriter();
  }

  @Override
  public Array createArray() {
    return new JsonArray();
  }
  
  @Override
  public Object createObject() {
    return new JsonObject();
  }
  
  @Override
  public boolean isArray(java.lang.Object o) {
    return JsonTypes.isArray(o);
  }
  
  @Override
  public boolean isObject(java.lang.Object o) {
    return JsonTypes.isObject(o);
  }
  
  @Override
  public Object parse(String json) throws JsonParserException {
    return JsonParser.object().from(json);
  }

  @Override
  public Array parseArray(String json) throws JsonParserException {
    return JsonParser.array().from(json);
  }
  
  static void checkJsonType(java.lang.Object value) {
    if (value == null || value instanceof String || value instanceof Json.Object || value instanceof Json.Array || value instanceof Boolean || value instanceof Number)
      return;
    
    throw new IllegalArgumentException("Invalid JSON type [value=" + value + ", class=" + value.getClass() + "]");
  }

}
