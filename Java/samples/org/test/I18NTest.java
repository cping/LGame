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
import loon.utils.I18N;

public class I18NTest extends Stage {

	@Override
	public void create() {
		// 加载i18n配置文件(可以配置多个,也可以写成一个)
		I18N i18 = new I18N("assets/i18nt.txt");
		// 添加语言为cn的pwd标记内容(cn仅仅是文本中$后面标记,写成什么都可以,只要自己能明白意思,还有pwd之类标记也一样)
		addLabel(i18.loadConfig("cn").getValue("pwd"), 100, 100);
		// 添加语言为en的pwd标记内容
		addLabel(i18.loadConfig("en").getValue("pwd"), 100, 150);

		// 绑定简体中文为当前语言
		I18N.bindCurrentLanguage(i18, "cn");
		// 显示当前用语言内容
		addLabel(I18N.getCurrentLanguage().getText("pwd"), 100, 200);

		// 绑定英文为当前语言
		I18N.bindCurrentLanguage(i18, "en");
		// 显示当前用语言内容
		addLabel(I18N.getCurrentLanguage().getText("pwd"), 100, 250);

		add(MultiScreenTest.getBackButton(this, 1));
	}

}
