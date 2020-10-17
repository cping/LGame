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
package org.test;

import loon.utils.StringUtils;
import loon.utils.UIntArray;
import loon.utils.UIntArray.UIntMode;
import loon.utils.UNInt;

public class TestByte {
	
	public static void main(String[]args){
		UIntArray array = new UIntArray(UIntMode.UINT32, true);
		array.writeUInt(0x0D0C0B0A);
		array.writeUInt(0x0D0C0B0A);
		array.position(0);
		System.out.println(StringUtils.toHex((int) array.readUInt()));
		System.out.println(array);
	}

}
