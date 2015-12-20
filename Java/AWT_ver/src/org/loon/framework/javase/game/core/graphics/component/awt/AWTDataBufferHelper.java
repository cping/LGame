package org.loon.framework.javase.game.core.graphics.component.awt;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email��ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class AWTDataBufferHelper {

	public static Object getData(final DataBuffer db) {
		if (db instanceof DataBufferByte) {
			return ((DataBufferByte) db).getData();
		} else if (db instanceof DataBufferUShort) {
			return ((DataBufferUShort) db).getData();
		} else if (db instanceof DataBufferShort) {
			return ((DataBufferShort) db).getData();
		} else if (db instanceof DataBufferInt) {
			return ((DataBufferInt) db).getData();
		} else if (db instanceof DataBufferFloat) {
			return ((DataBufferFloat) db).getData();
		} else if (db instanceof DataBufferDouble) {
			return ((DataBufferDouble) db).getData();
		} else {
			throw new RuntimeException("Not found DataBuffer class !");
		}
	}

	public static int[] getDataInt(final DataBuffer db) {
		return ((DataBufferInt) db).getData();
	}

	public static byte[] getDataByte(final DataBuffer db) {
		return ((DataBufferByte) db).getData();
	}

	public static short[] getDataShort(final DataBuffer db) {
		return ((DataBufferShort) db).getData();
	}

	public static short[] getDataUShort(final DataBuffer db) {
		return ((DataBufferUShort) db).getData();
	}

	public static double[] getDataDouble(final DataBuffer db) {
		return ((DataBufferDouble) db).getData();
	}

	public static float[] getDataFloat(final DataBuffer db) {
		return ((DataBufferFloat) db).getData();
	}

}
