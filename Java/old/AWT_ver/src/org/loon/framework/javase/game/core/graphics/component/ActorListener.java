package org.loon.framework.javase.game.core.graphics.component;

import org.loon.framework.javase.game.core.graphics.device.LGraphics;

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
public interface ActorListener {

	public void draw(LGraphics g);

	public void update(long elapsedTime);

	public void downClick(int x, int y);

	public void upClick(int x, int y);

	public void drag(int x, int y);
}
