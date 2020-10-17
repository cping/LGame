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
import loon.component.LLabel;
import loon.events.CacheListener;
import loon.events.Touched;
import loon.utils.cache.CacheObjectManager;
import loon.utils.cache.CacheObjectPool;
import loon.utils.cache.ListenerCache;
import loon.utils.timer.Duration;

public class CachePoolTest extends Stage{

	@Override
	public void create() {

		add(MultiScreenTest.getBackButton(this, 1));
		
		final LLabel label = addLabel("对象缓存池测试中,现有两个测试对象");
		centerOn(label);

		CacheObjectManager manager = new CacheObjectManager();

		// 创建缓存池,多例模式(如果false则为单例,每个对象仅允许引用一次,多次引用会返回null)
		final CacheObjectPool<ListenerCache> pool = manager.createObjectPool("test1", Duration.atSecond(10f), true);

		// 把CacheListener注入ListenerCache缓存对象
		ListenerCache cache = new ListenerCache(new CacheListener() {

			@Override
			public void onUnspawn() {

			}

			@Override
			public void onSpawn() {

			}

			@Override
			public void disposed(boolean close) {

				label.setText("活了10秒,我死了");

			}
		});
		//给cache加个标记
		cache.setTempTag("我是第一个");

		//注册cache到缓存池
		pool.register(cache);

		addButton("点我引用test1", 50, 200, 140, 50, new Touched() {

			@Override
			public void on(float x, float y) {

				ListenerCache cache = pool.onSpawn();
				label.setText(cache == null ? "unkown" : cache.getTempTag().toString());

			}
		});

		// 创建第二个缓存池,单例模式,存活30秒
		final CacheObjectPool<ListenerCache> pool2 = manager.createObjectPool("test2", Duration.atSecond(30f), false);

		// 把CacheListener注入ListenerCache缓存对象
		ListenerCache cache2 = new ListenerCache(new CacheListener() {

			@Override
			public void onUnspawn() {

			}

			@Override
			public void onSpawn() {

			}

			@Override
			public void disposed(boolean close) {

				label.setText("活了30秒,我也死了");

			}
		});
		cache2.setTempTag("我是第二个，只能引用一次");
		//注册cache2到缓存池2
		pool2.register(cache2);

		addButton("点我引用test2", 300, 200, 140, 50, new Touched() {

			@Override
			public void on(float x, float y) {

				ListenerCache cache = pool2.onSpawn();
				label.setText(cache == null ? "unkown" : cache.getTempTag().toString());

			}
		});

		//提交缓存池(否则不会自动循环,需要手动调用update)
		manager.subimit();

	}

}
