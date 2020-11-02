package loon.srpg.field;

/**
 * Copyright 2008 - 2011
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
public class SRPGFieldElement {

	public int atk;

	public int def;

	public int state;

	public String depict;

	public int imgId;

	public String name;

	public int index;

	public int mv;

	SRPGFieldElement() {

	}

	public SRPGFieldElement(int id, String name, String depict, int mv,
			int atk, int def, int state) {
		this.imgId = id;
		this.name = name;
		this.mv = mv;
		this.atk = atk;
		this.def = def;
		this.depict = depict;
		this.state = state;
	}

}
