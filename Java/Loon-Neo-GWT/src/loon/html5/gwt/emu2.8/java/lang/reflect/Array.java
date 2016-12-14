/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang.reflect;

/**
 * This class provides static methods to create and access arrays dynamically.
 */
public final class Array {

    /**
     * Prevent this class from being instantiated.
     */
    private Array(){
        //do nothing
    }

    /**
     * Returns the length of the array. This reproduces the effect of {@code
     * array.length}
     *
     * @param array
     *            the array
     * @return the length of the array
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array
     */
        public static int getLength(Object array) {
            if (array == null) {
                throw new NullPointerException();
            }
            if (!array.getClass().isArray()) {
                throw new IllegalArgumentException(array.getClass()+" is not an array");
            }
            return getLength2(array);
        }

        private static native int getLength2(Object array) /*-{
            return array.length;
          }-*/;

        /**
         * Returns a new array of the specified component type and length. This
         * reproduces the effect of {@code new componentType[size]}.
         *
         * @param componentType
         *            the component type of the new array
         * @param size
         *            the length of the new array
         * @return the new array
         * @throws NullPointerException
         *             if the component type is null
         * @throws NegativeArraySizeException
         *             if {@code size < 0}
         */
      public static Object newInstance(Class<?> componentType, int length)
              throws NegativeArraySizeException {
          if (componentType == null) {
              throw new NullPointerException();
          }
          if (length < 0) {
              throw new NegativeArraySizeException(String.valueOf(length));
          }
          if (componentType == boolean.class) {
            return new boolean[length];
          }
          if (componentType == byte.class) {
            return new byte[length];
          }
          if (componentType == char.class) {
            return new char[length];
          }
          if (componentType == short.class) {
            return new short[length];
          }
          if (componentType == int.class) {
            return new int[length];
          }
          if (componentType == long.class) {
            return new long[length];
          }
          if (componentType == float.class) {
            return new float[length];
          }
          if (componentType == double.class) {
            return new double[length];
          }
          return new Object[length];
        }

      /**
       * Returns the element of the array at the specified index. This reproduces
       * the effect of {@code array[index]}. If the array component is a primitive
       * type, the result is automatically wrapped.
       *
       * @param array
       *            the array
       * @param index
       *            the index
       * @return the requested element, possibly wrapped
       * @throws NullPointerException
       *             if the array is null
       * @throws IllegalArgumentException
       *             if {@code array} is not an array
       * @throws ArrayIndexOutOfBoundsException
       *             if {@code  index < 0 || index >= array.length}
       */
          public static Object get(Object array, int index)
                          throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
              if (array == null) {
                  throw new NullPointerException();
              }
              if (!array.getClass().isArray()) {
                  throw new IllegalArgumentException(array.getClass()+" is not an array");
              }
              if ((index < 0) || (index >= getLength2(array))) {
                  throw new ArrayIndexOutOfBoundsException(String.valueOf(index));
              }
              return get2(array, index);
          }

         private static native Object get2(Object array, int index) /*-{
            return array[index];
         }-*/;

      /**
       * Sets the element of the array at the specified index to the value. This
       * reproduces the effect of {@code array[index] = value}. If the array
       * component is a primitive type, the value is automatically unwrapped.
       *
       * @param array
       *            the array
       * @param index
       *            the index
       * @param value
       *            the new value
       *
       * @throws NullPointerException
       *             if the {@code array} is {@code null}
       * @throws IllegalArgumentException
       *             if {@code array} is not an array or the value cannot be
       *             converted to the array type by a widening conversion
       * @throws ArrayIndexOutOfBoundsException
       *             if {@code  index < 0 || index >= array.length}
       */
          public static void set(Object array, int index, Object value)
                          throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
              if (array == null) {
                  throw new NullPointerException();
              }
              if (!array.getClass().isArray()) {
                  throw new IllegalArgumentException(array.getClass()+" is not an array");
              }
              if ((index < 0) || (index >= getLength2(array))) {
                  throw new ArrayIndexOutOfBoundsException(String.valueOf(index));
              }
              set2(array, index, value);
          }

          private static native void set2(Object array, int index, Object value) /*-{
              array[index] = value;
          }-*/;

    /**
     * Returns the element of the array at the specified index, converted to a
     * {@code boolean}, if possible. This reproduces the effect of {@code
     * array[index]}
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the requested element
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array or the element at the
     *             index position can not be converted to the return type
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code index < 0 || index >= array.length}
     */
	public static boolean getBoolean(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    return ((boolean[]) array)[index];
	}

    /**
     * Returns the element of the array at the specified index, converted to a
     * {@code byte}, if possible. This reproduces the effect of {@code
     * array[index]}
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the requested element
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array or the element at the
     *             index position can not be converted to the return type
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code index < 0 || index >= array.length}
     */
	public static byte getByte(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    return ((byte[]) array)[index];
        }

    /**
     * Returns the element of the array at the specified index, converted to a
     * {@code char}, if possible. This reproduces the effect of {@code
     * array[index]}
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the requested element
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array or the element at the
     *             index position can not be converted to the return type
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code index < 0 || index >= array.length}
     */
	public static char getChar(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    return ((char[]) array)[index];
        }

    /**
     * Returns the element of the array at the specified index, converted to a
     * {@code double}, if possible. This reproduces the effect of {@code
     * array[index]}
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the requested element
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array or the element at the
     *             index position can not be converted to the return type
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code index < 0 || index >= array.length}
     */
	public static double getDouble(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException  {
	    return ((double[]) array)[index];
        }

    /**
     * Returns the element of the array at the specified index, converted to a
     * {@code float}, if possible. This reproduces the effect of {@code
     * array[index]}
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the requested element
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array or the element at the
     *             index position can not be converted to the return type
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code index < 0 || index >= array.length}
     */
	public static float getFloat(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    return ((float[]) array)[index];
        }

    /**
     * Returns the element of the array at the specified index, converted to an
     * {@code int}, if possible. This reproduces the effect of {@code
     * array[index]}
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the requested element
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array or the element at the
     *             index position can not be converted to the return type
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code index < 0 || index >= array.length}
     */
	public static int getInt(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    return ((int[]) array)[index];
	}

    /**
     * Returns the element of the array at the specified index, converted to a
     * {@code long}, if possible. This reproduces the effect of {@code
     * array[index]}
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the requested element
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array or the element at the
     *             index position can not be converted to the return type
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code index < 0 || index >= array.length}
     */
	public static long getLong(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    return ((long[]) array)[index];
	}

    /**
     * Returns the element of the array at the specified index, converted to a
     * {@code short}, if possible. This reproduces the effect of {@code
     * array[index]}
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @return the requested element
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code array} is not an array or the element at the
     *             index position can not be converted to the return type
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code index < 0 || index >= array.length}
     */
	public static short getShort(Object array, int index)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    return ((short[]) array)[index];
	}

    /**
     * Sets the element of the array at the specified index to the {@code
     * boolean} value. This reproduces the effect of {@code array[index] =
     * value}.
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @param value
     *            the new value
     *
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if the {@code array} is not an array or the value cannot be
     *             converted to the array type by a widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code  index < 0 || index >= array.length}
     */
	public static void setBoolean(Object array, int index, boolean value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    ((boolean[]) array)[index] = value;
	}

    /**
     * Sets the element of the array at the specified index to the {@code byte}
     * value. This reproduces the effect of {@code array[index] = value}.
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @param value
     *            the new value
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if the {@code array} is not an array or the value cannot be
     *             converted to the array type by a widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code  index < 0 || index >= array.length}
     */
	public static void setByte(Object array, int index, byte value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    ((byte[]) array)[index] = value;
	}

    /**
     * Set the element of the array at the specified index to the {@code char}
     * value. This reproduces the effect of {@code array[index] = value}.
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @param value
     *            the new value
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if the {@code array} is not an array or the value cannot be
     *             converted to the array type by a widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code  index < 0 || index >= array.length}
     */
	public static void setChar(Object array, int index, char value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    ((char[]) array)[index] = value;
	}

    /**
     * Set the element of the array at the specified index to the {@code double}
     * value. This reproduces the effect of {@code array[index] = value}.
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @param value
     *            the new value
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if the {@code array} is not an array or the value cannot be
     *             converted to the array type by a widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code  index < 0 || index >= array.length}
     */
	public static void setDouble(Object array, int index, double value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    ((double[]) array)[index] = value;
	}

    /**
     * Set the element of the array at the specified index to the {@code float}
     * value. This reproduces the effect of {@code array[index] = value}.
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @param value
     *            the new value
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if the {@code array} is not an array or the value cannot be
     *             converted to the array type by a widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code  index < 0 || index >= array.length}
     */
	public static void setFloat(Object array, int index, float value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    ((float[]) array)[index] = value;
	}

    /**
     * Set the element of the array at the specified index to the {@code int}
     * value. This reproduces the effect of {@code array[index] = value}.
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @param value
     *            the new value
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if the {@code array} is not an array or the value cannot be
     *             converted to the array type by a widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code  index < 0 || index >= array.length}
     */
	public static void setInt(Object array, int index, int value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    ((int[]) array)[index] = value;
	}

    /**
     * Set the element of the array at the specified index to the {@code long}
     * value. This reproduces the effect of {@code array[index] = value}.
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @param value
     *            the new value
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if the {@code array} is not an array or the value cannot be
     *             converted to the array type by a widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code  index < 0 || index >= array.length}
     */
	public static void setLong(Object array, int index, long value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    ((long[]) array)[index] = value;
	}

    /**
     * Set the element of the array at the specified index to the {@code short}
     * value. This reproduces the effect of {@code array[index] = value}.
     *
     * @param array
     *            the array
     * @param index
     *            the index
     * @param value
     *            the new value
     * @throws NullPointerException
     *             if the {@code array} is {@code null}
     * @throws IllegalArgumentException
     *             if the {@code array} is not an array or the value cannot be
     *             converted to the array type by a widening conversion
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code  index < 0 || index >= array.length}
     */
	public static void setShort(Object array, int index, short value)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	    ((short[]) array)[index] = value;
	}

}