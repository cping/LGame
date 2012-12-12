package loon.action.sprite.effect;

import loon.core.LRelease;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;


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
public interface IKernel extends LRelease {

	public int id();

	public void draw(GLEx g);

	public void update();

	public LTexture get();

	public float getHeight();

	public float getWidth();

}
