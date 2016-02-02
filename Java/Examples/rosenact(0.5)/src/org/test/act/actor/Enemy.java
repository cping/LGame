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
package org.test.act.actor;

import org.test.act.base.BaseSprite;
import org.test.act.item.DeadEffect;
import org.test.act.item.SoundControl;

public class Enemy extends BaseSprite
{
    public float actHeight;
    public float actWidth;
    public DeadEffect de;

    public int life = 5;

    public void hitted()
    {
        this.life--;
        if (SoundControl.on)
        {
        
        }
        this.color.a = 1f;
        this.color.r = 1f;
        this.color.g = 0;
        this.color.b = 0;
        if (this.life <= 0)
        {
            for (BaseSprite sprite : this.de.sps)
            {
                sprite.Pos = (super.Pos.sub(super.Origin)).add(this.actWidth / 2f, this.actHeight / 2f);
            }
            if (SoundControl.on)
            {
         
            }
            this.de.shoot();
            super.visible = false;
        }
    }

    public void loadSe(String asset, float dis)
    {
        this.de = new DeadEffect();
        this.de.init(asset, dis);
    }
}
