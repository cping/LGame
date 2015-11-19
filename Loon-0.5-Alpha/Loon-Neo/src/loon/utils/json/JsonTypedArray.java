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

import java.util.Iterator;
import java.util.NoSuchElementException;

import loon.Json;
import loon.utils.ObjectMap;

public class JsonTypedArray<T> implements Json.TypedArray<T> {
  private final Json.Array array;
  private Getter<T> getter;

  private interface Getter<T> {
    T get(Json.Array array, int index, T dflt);
  }

  private static ObjectMap<Class<?>, Getter<?>> getters = new ObjectMap<Class<?>, Getter<?>>();
  static {
    getters.put(Boolean.class, new Getter<Boolean>() {
      @Override
      public Boolean get(Json.Array array, int index, Boolean dflt) {
        return dflt == null ? array.getBoolean(index) : array.getBoolean(index, dflt);
      }
    });
    getters.put(Integer.class, new Getter<Integer>() {
      @Override
      public Integer get(Json.Array array, int index, Integer dflt) {
        return dflt == null ? array.getInt(index) : array.getInt(index, dflt);
      }
    });
    getters.put(Double.class, new Getter<Double>() {
      @Override
      public Double get(Json.Array array, int index, Double dflt) {
        return dflt == null ? array.getDouble(index) : array.getDouble(index, dflt);
      }
    });
    getters.put(Float.class, new Getter<Float>() {
      @Override
      public Float get(Json.Array array, int index, Float dflt) {
        return dflt == null ? array.getNumber(index) : array.getNumber(index, dflt);
      }
    });
    getters.put(String.class, new Getter<String>() {
      @Override
      public String get(Json.Array array, int index, String dflt) {
        return array.getString(index, dflt);
      }
    });
    getters.put(Json.Array.class, new Getter<Json.Array>() {
      @Override
      public Json.Array get(Json.Array array, int index, Json.Array dflt) {
        return array.getArray(index, dflt);
      }
    });
    getters.put(Json.Object.class, new Getter<Json.Object>() {
      @Override
      public Json.Object get(Json.Array array, int index, Json.Object dflt) {
        return array.getObject(index, dflt);
      }
    });
  }

  public JsonTypedArray(Json.Array array, Class<T> type) {
    this.array = array;

    @SuppressWarnings("unchecked")
    final Getter<T> getter = (Getter<T>) getters.get(type);
    if (getter == null) {
      throw new IllegalArgumentException("Only json types may be used for TypedArray, not '"
          + type.getName() + "'");
    }
    this.getter = getter;
  }

  @Override
  public int length() {
    return array.length();
  }

  @Override
  public T get(int index) {
    return getter.get(array, index, null);
  }

  @Override
  public T get(int index, T dflt) {
    return getter.get(array, index, dflt);
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      @Override
      public boolean hasNext() {
        return index < length();
      }

      @Override
      public T next() {
        if (index >= length()) {
          throw new NoSuchElementException();
        }
        return get(index++);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      private int index;
    };
  }
}
