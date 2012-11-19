package org.loon.framework.javase.game.core;

/**
 * Copyright 2008 - 2010
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
public interface EmulatorListener {


	public void onUpClick();

	public void onLeftClick();

	public void onRightClick();

	public void onDownClick();

	public void onTriangleClick();

	public void onSquareClick();

	public void onCircleClick();

	public void onCancelClick();
	

	public void unUpClick();

	public void unLeftClick();

	public void unRightClick();

	public void unDownClick();

	public void unTriangleClick();

	public void unSquareClick();

	public void unCircleClick();

	public void unCancelClick();

}
