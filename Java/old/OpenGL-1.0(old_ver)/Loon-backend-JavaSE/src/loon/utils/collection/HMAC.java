/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.utils.collection;

public class HMAC {
	
	private SHA512[] _baseHash;

	private SHA512 _resultHash;
	
	private boolean _updated = false;

	public HMAC(LongArray key) {
		int i = 0;
		int bs = SHA512.blockSize / 32;
		long[][] exKey = new long[2][bs];
		this._baseHash = new SHA512[] { new SHA512(), new SHA512() };
		if (key.length > bs) {
			key = SHA512.hash(key);
		}
		for (i = 0; i < bs; i++) {
			exKey[0][i] = (key.get(i) ^ 0x36363636);
			exKey[1][i] = (key.get(i) ^ 0x5C5C5C5C);
		}
		this._baseHash[0].update(exKey[0]);
		this._baseHash[1].update(exKey[1]);
		this._resultHash = this._baseHash[0];
	}
	
	public LongArray mac(Object data) {
		return encrypt(data);
	}
	
	public LongArray encrypt(Object data) {
		if (!this._updated) {
			this.update(data);
			return this.digest();
		} else {
			throw new RuntimeException(
					"encrypt on already updated hmac called!");
		}
	}

	public void reset() {
		this._updated = false;
		this._resultHash = this._baseHash[0];
	}

	public void update(Object d) {
		this._updated = true;
		this._resultHash.update(d);
	}

	public LongArray digest() {
		LongArray w = this._resultHash._finalize();
		LongArray result = this._baseHash[1].update(w)._finalize();
		this.reset();
		return result;
	}
}
