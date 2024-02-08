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

import loon.Stage;
import loon.utils.ObjectBundle;

/**
 * Screen类及其子类中的ObjectBundle对象是一个全局存储器,位于LProcess类中,高于Screen及其子类而存在.
 * 只要Loon系统不关闭,Screen中ObjectBundle对象全局存在,不会因转换Screen而被清空.
 */
public class InitSettingTest extends Stage {

	
	@Override
	public void setting(ObjectBundle bundle) {
		//传参给系统
		bundle.set("abc", 321);
		bundle.set("cs", 456);
	}

	@Override
	public void create() {
		//获得传递的参数
		println(getBundle().get("abc"));
	}

}
