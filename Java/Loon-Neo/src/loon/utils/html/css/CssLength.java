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
package loon.utils.html.css;


public class CssLength extends CssKeyword {

	protected float value = 0.0f;
	
	protected CssUnit unit;

	public CssLength() {
		super("");
	}

	public CssLength(float value, CssUnit unit) {
		super(value + unit.toString());
		this.value = value;
		this.unit = unit;
	}

	public void setLength(CssLength length) {
		this.value = length.value;
		this.unit = length.unit;
	}

	public float toPx() {
		return this.value;

	}

	@Override
	public void setKeyword(CssKeyword keyword) {

	}

	@Override
	public void setColor(CssColor color) {


	}

}
