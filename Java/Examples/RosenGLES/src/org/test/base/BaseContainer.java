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
package org.test.base;

import java.util.ArrayList;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;

public  class BaseContainer extends BaseSprite
{
    private ArrayList<BaseSprite> sprites;

    public BaseContainer()
    {
        super.Origin = new Vector2f();
        super.Rotation = 0f;
        super.Scale = new Vector2f(1f,1f);
        super.Depth = 0.5f;
        this.sprites = new ArrayList<BaseSprite>();
        super._framecount = 1;
        super._Paused = false;
        super.moveRate = 1f;
    }

    public BaseContainer(Vector2f origin, float rotation, Vector2f scale, float depth)
    {
        super.Origin = origin;
        super.Rotation = rotation;
        super.Scale = scale;
        super.Depth = depth;
        this.sprites = new ArrayList<BaseSprite>();
        super._framecount = 1;
        super._Paused = false;
        super.moveRate = 1f;
    }

    public void addChild(BaseSprite sp)
    {
        this.sprites.add(sp);
    }

    public void addPosX(float x)
    {
        for (BaseSprite sprite : this.sprites)
        {
            if (sprite instanceof BaseContainer)
            {
                ((BaseContainer)sprite).addPosX(x * sprite.moveRate);
            }
            else
            {
                sprite.Pos.x += x * sprite.moveRate;
            }
        }
        this.Pos.x += x * super.moveRate;
    }

    public void addPosY(float y)
    {
        for (BaseSprite sprite : this.sprites)
        {
            if (sprite instanceof BaseContainer)
            {
            	 ((BaseContainer)sprite).addPosY(y * sprite.moveRate);
            }
            else
            {
                sprite.Pos.y += y * sprite.moveRate;
            }
        }
        this.Pos.y += y * super.moveRate;
    }

    public  void DrawFrame(SpriteBatch batch)
    {
        if (super.visible)
        {
            this.DrawFrame(batch, super._Frame);
        }
    }

    public  void DrawFrame(SpriteBatch batch, int frame)
    {
        if (super.visible)
        {
            for (BaseSprite sprite : this.sprites)
            {
                sprite.DrawFrame(batch, sprite.Frame());
            }
        }
    }

    public void removeChild(BaseSprite sp)
    {
        this.sprites.remove(sp);
    }

    public  void UpdateFrame(GameTime gameTime)
    {
        for (BaseSprite sprite : this.sprites)
        {
            sprite.UpdateFrame(gameTime);
        }
        this.specificUpdate(gameTime);
    }
}