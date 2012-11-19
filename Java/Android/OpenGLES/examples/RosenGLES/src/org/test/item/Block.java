/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package org.test.item;

import org.test.base.BaseSprite;

public class Block extends BaseSprite
{
    public Block()
    {
        super.Load("assets/block", 1, 1, 1f, true);
    }

    public void resize(float w, float h)
    {
        this.Scale.x = w / 160f;
        this.Scale.y = h / 160f;
    }
}
