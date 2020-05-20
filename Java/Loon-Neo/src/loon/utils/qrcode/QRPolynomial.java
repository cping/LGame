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
package loon.utils.qrcode;

import loon.utils.StrBuilder;

public class QRPolynomial {

	private final int[] num;

	public QRPolynomial(int[] num) {
		this(num, 0);
	}

	public QRPolynomial(int[] num, int shift) {
		int offset = 0;
		while (offset < num.length && num[offset] == 0) {
			offset++;
		}
		this.num = new int[num.length - offset + shift];
		System.arraycopy(num, offset, this.num, 0, num.length - offset);
	}

	public int get(int index) {
		return num[index];
	}

	public int getLength() {
		return num.length;
	}

	public QRPolynomial multiply(QRPolynomial e) {

		int[] num = new int[getLength() + e.getLength() - 1];
		for (int i = 0; i < getLength(); i++) {
			for (int j = 0; j < e.getLength(); j++) {
				num[i + j] ^= QRMath.gexp(QRMath.glog(get(i)) + QRMath.glog(e.get(j)));
			}
		}

		return new QRPolynomial(num);
	}

	public QRPolynomial mod(QRPolynomial e) {

		if (getLength() - e.getLength() < 0) {
			return this;
		}
		int ratio = QRMath.glog(get(0)) - QRMath.glog(e.get(0));
		int[] num = new int[getLength()];
		for (int i = 0; i < getLength(); i++) {
			num[i] = get(i);
		}

		for (int i = 0; i < e.getLength(); i++) {
			num[i] ^= QRMath.gexp(QRMath.glog(e.get(i)) + ratio);
		}

		return new QRPolynomial(num).mod(e);
	}

	public String toLogString() {
		StrBuilder buffer = new StrBuilder();
		for (int i = 0; i < getLength(); i++) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(QRMath.glog(get(i)));
		}
		return buffer.toString();
	}

	@Override
	public String toString() {
		StrBuilder buffer = new StrBuilder();
		for (int i = 0; i < getLength(); i++) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(get(i));
		}
		return buffer.toString();
	}
}
