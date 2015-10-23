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
package loon.utils.reply;

import java.util.Map;

public class Functions
{
    public static Function<Boolean, Boolean> NOT = new Function<Boolean, Boolean>() {
        public Boolean apply (Boolean value) {
            return !value;
        }
    };

    public static Function<Object, String> TO_STRING = new Function<Object, String>() {
        public String apply (Object value) {
            return String.valueOf(value);
        }
    };

    public static Function<Object, Boolean> IS_NULL = new Function<Object, Boolean>() {
        public Boolean apply (Object value) {
            return (value == null);
        }
    };

    public static Function<Object, Boolean> NON_NULL = new Function<Object, Boolean>() {
        public Boolean apply (Object value) {
            return (value != null);
        }
    };

    public static Function<Number,Float> FLOAT_VALUE = new Function<Number,Float>() {
        public Float apply (Number value) {
            return value.floatValue();
        }
    };

    public static Function<Number,Integer> INT_VALUE = new Function<Number,Integer>() {
        public Integer apply (Number value) {
            return value.intValue();
        }
    };

    public static <E> Function<Object,E> constant (final E constant) {
        return new Function<Object,E>() {
            public E apply (Object value) {
                return constant;
            }
        };
    }

    public static Function<Integer,Boolean> greaterThan (final int target) {
        return new Function<Integer,Boolean>() {
            public Boolean apply (Integer value) {
                return value > target;
            }
        };
    }

    public static Function<Integer,Boolean> greaterThanEqual (final int target) {
        return new Function<Integer,Boolean>() {
            public Boolean apply (Integer value) {
                return value >= target;
            }
        };
    }

    public static Function<Integer,Boolean> lessThan (final int target) {
        return new Function<Integer,Boolean>() {
            public Boolean apply (Integer value) {
                return value < target;
            }
        };
    }

    public static Function<Integer,Boolean> lessThanEqual (final int target) {
        return new Function<Integer,Boolean>() {
            public Boolean apply (Integer value) {
                return value <= target;
            }
        };
    }

    public static <K, V> Function<K, V> forMap (final Map<K, ? extends V> map, final V defaultValue)
    {
        return new Function<K, V>() {
            public V apply (K key) {
                V value = map.get(key);
                return (value != null || map.containsKey(key)) ? value : defaultValue;
            }
        };
    }

    public static <T> Function<T,String> prefix (final String prefix) {
        return new Function<T,String>() {
            public String apply (T value) {
                return prefix + value;
            }
        };
    }

    public static <T> Function<T,String> suffix (final String suffix) {
        return new Function<T,String>() {
            public String apply (T value) {
                return value + suffix;
            }
        };
    }

    public static <T> Function<T, T> identity () {
        @SuppressWarnings("unchecked") Function<T, T> ident = (Function<T, T>)IDENT;
        return ident;
    }

    protected static final Function<Object, Object> IDENT = new Function<Object, Object>() {
        public Object apply (Object value) {
            return value;
        }
    };
}
