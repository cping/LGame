package loon.core.resource;

import java.io.Serializable;

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class LPKTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileName;

	private long fileSize = 0L;

	private long offSet = 0L;

	public LPKTable() {
		this.fileName = null;
	}

	public LPKTable(byte[] fileName, long fileSize, long offSet) {
		this.fileName = new String(fileName).trim();
		this.fileSize = fileSize;
		this.offSet = offSet;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(byte[] bytes) {
		this.fileName = new String(bytes).trim();
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getOffSet() {
		return offSet;
	}

	public void setOffSet(long offSet) {
		this.offSet = offSet;
	}

	public static int size() {
		return LPKHeader.LF_FILE_LENGTH + 4 + 4;
	}

}
