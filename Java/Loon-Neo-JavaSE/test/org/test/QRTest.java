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

import java.util.Arrays;

import loon.utils.qrcode.QRAlphaNum;
import loon.utils.qrcode.QRCode;
import loon.utils.qrcode.QRData;
import loon.utils.qrcode.QRErrorLevel;
import loon.utils.qrcode.QRKANJI;
import loon.utils.qrcode.QRNumber;

public class QRTest {

	public static void test1(){
		 QRNumber data = new QRNumber("0123");
		 QRCode code = new QRCode(1, QRErrorLevel.H);
		    byte[] act = QRCode.createData(1, QRErrorLevel.H, data );
		    byte[] exp = new byte[]{16,16,12,48,-20,17,-20,17,-20,-50,-20,-24,66,-27,44,-31,-124,-111,13,-69,-37,15,-16,36,-69,104};	
	        System.out.println(Arrays.equals(exp, act));  
	}

	public static void test2(){
	    QRAlphaNum data = new QRAlphaNum("AB01");
	    byte[] act = QRCode.createData(1, QRErrorLevel.H, new QRData[]{data} );
	    byte[] exp = new byte[]{32,33,-51,0,32,-20,17,-20,17,105,-125,-85,106,65,-91,54,-123,-112,-11,-73,21,-13,-106,-89,114,-25};
	        System.out.println(Arrays.equals(exp, act));  
	}
	

	public static void test3(){
		   QRKANJI data = new QRKANJI("漢字");
		    byte[] act = QRCode.createData(1, QRErrorLevel.H, new QRData[]{data} );
		    byte[] exp = new byte[]{-128,35,-97,-88,104,0,-20,17,-20,-11,-82,108,-126,119,-6,118,-128,99,-41,-105,117,-68,-107,-120,47,-5};
	        System.out.println(Arrays.equals(exp, act));  
	}
	
	public static void main(String[]args){
		test2();
	}
	
}
