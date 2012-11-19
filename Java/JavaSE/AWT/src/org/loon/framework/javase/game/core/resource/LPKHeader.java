package org.loon.framework.javase.game.core.resource;

import org.loon.framework.javase.game.utils.NumberUtils;


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
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class LPKHeader {

	public static final int LF_PAK_ID = (('L' << 24) + ('G' << 16) + ('P' << 8) + 'K');
	
	public static final int LF_PASSWORD_LENGTH = 10;

	public static final int LF_FILE_LENGTH = 30;
	
	private int identity;

	private byte[] password;

	private float version = 1.0F;

	private long tables = 0;

	public LPKHeader() {
		this.password = new byte[LPKHeader.LF_PASSWORD_LENGTH];
	}

	public LPKHeader(byte[] password, float version, long tables) {
		this.password = new byte[LPKHeader.LF_PASSWORD_LENGTH];
		for (int i = 0; i < LPKHeader.LF_PASSWORD_LENGTH; this.password[i] = password[i], i++)
			;
		this.version = version;
		this.tables = tables;
	}

	public long getTables() {
		return tables;
	}

	public void setTables(long tables) {
		this.tables = tables;
	}

	public float getVersion() {
		return version;
	}

	public void setVersion(float version) {
		this.version = version;
	}

	public int getPAKIdentity() {
		return LPKHeader.LF_PAK_ID;
	}

	public void setPAKIdentity(int id) {
		this.identity = id;
	}

	public boolean validatePAK() {
		return identity == LPKHeader.LF_PAK_ID;
	}

	public byte[] getPassword() {
		return password;
	}

	public void setPassword(long pass) {
		this.password = NumberUtils.addZeros(pass, LPKHeader.LF_PASSWORD_LENGTH)
				.getBytes();
	}

	public void setPassword(byte[] password) {
		for (int i = 0; i < LPKHeader.LF_PASSWORD_LENGTH; password[i] = password[i], i++)
			;
	}

	public static int size() {
		return 4 + LPKHeader.LF_PASSWORD_LENGTH + 4 + 4 + 1;
	}

}
