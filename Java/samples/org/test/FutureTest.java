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

import loon.Counter;
import loon.Stage;
import loon.utils.reply.ObjRef;

/**
 * Future异步加载示例(此示例不能加载Texture和GLEx渲染相关类,否则可能跳出渲染线程而报错, 安全的异步加载请参考PreloadTest示例)
 */
public class FutureTest extends Stage {

	@Override
	public void create() {

		// 构建反馈对象
		ObjRef<String> result = ObjRef.of("未加载");

		// 计数器
		final Counter count = newCounter();
		// 开局加载10000数据
		for (int i = 0; i < 10000; i++) {
			// 异步预加载数据
			loadFuture(() -> {
				result.set("已加载" + count.increment());
				return result;
				// 成功返回
			}).onSuccess(v -> {
				System.out.println(v);
				// 加载失败
			}).onFailure(v -> {
				System.out.println(v);
			});
		}
		println("程序此时已经运行");

	}

}
