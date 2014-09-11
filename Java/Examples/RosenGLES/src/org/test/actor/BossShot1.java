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
package org.test.actor;

import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

import org.test.base.BaseSprite;

public class BossShot1 extends BaseSprite
{
    private float vx;
    private float vy;

    public BossShot1()
    {
        super.Load("assets/bossShot1", 1, 0f, true);
        this.Origin.x = super.getWidth() / 2f;
        this.Origin.y = super.getHeight() / 2f;
        super.visible = false;
    }

    public void setV(Vector2f p, float v)
    {
        super.Rotation = MathUtils.acos((p.x / p.length()));
        this.vx = (v / p.length()) * p.x;
        this.vy = (v / p.length()) * p.y;
    }

    protected void specificUpdate(GameTime gameTime)
    {
        this.Pos.x += this.vx;
        this.Pos.y += this.vy;
    }
}
