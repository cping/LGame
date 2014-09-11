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
package org.test.traintilesgles;


public class Button {
    private boolean m_disabled;
    public int m_flags;
    private int m_padding;
    private Sprite m_sprite;
    private boolean m_toggle;
    private EButtonTypes m_type;

    public Button(EButtonTypes aType, String aFile) 
    {
    	this(aType, aFile, 0x12, 0, false);
    }

    public Button(EButtonTypes aType, String aFile, int align, int aPadding) 
    {
        this(aType, aFile, align, aPadding, false);
    }

    public Button(EButtonTypes aType, String aFile, int align, int aPadding, boolean disabled)
    {
        this.m_type = aType;
        this.m_padding = aPadding;
        this.m_disabled = disabled;
        if (aType == EButtonTypes.ENormal)
        {
            this.m_sprite = new Sprite(aFile, 2, 1, align, false);
        }
        else if (aType == EButtonTypes.ESwitch)
        {
            this.m_sprite = new Sprite(aFile, 2, 1, align, false);
        }
    }

    public Button(EButtonTypes aType, Sprite sprite, int align, int aPadding, boolean disabled)
    {
        this.m_type = aType;
        this.m_padding = aPadding;
        this.m_sprite = sprite;
        this.m_disabled = disabled;
    }

    public void disable(boolean aDisable)
    {
        this.m_disabled = aDisable;
    }

    public int getH()
    {
        return this.m_sprite.getHeight();
    }

    public int getW()
    {
        return this.m_sprite.getWidth();
    }

    public boolean isDisabled()
    {
        return this.m_disabled;
    }

    public boolean isToggled()
    {
        return this.m_toggle;
    }

    public boolean paint(Painter painter, GameCore game, int x, int y)
    {
        int num = this.m_sprite.getWidth();
        int num2 = this.m_sprite.getHeight();
        int num3 = game.getMouseX();
        int num4 = game.getMouseY();
        boolean flag = false;
        int frame = 0;
        boolean flag2 = false;
        int xoff = this.m_sprite.xoff;
        int yoff = this.m_sprite.yoff;
        boolean flag3 = (((num3 > ((x - this.m_padding) + xoff)) && (num3 < (((x + num) + this.m_padding) + xoff))) && (num4 > ((y - this.m_padding) + yoff))) && (num4 < (((y + num2) + this.m_padding) + yoff));
        if (this.m_type == EButtonTypes.ENormal)
        {
            if ((!this.m_disabled && (game.isMouseUp() || game.isMouseDown())) && flag3)
            {
                if (game.isMouseUp())
                {
                    flag = true;
                    game.clearMouseStatus();
                }
                frame = 1;
            }
            this.m_sprite.Paint(painter, (float) x, (float) y, frame);
            return flag;
        }
        if (this.m_type != EButtonTypes.ESwitch)
        {
            return false;
        }
        if ((!this.m_disabled && game.isMouseUp()) && flag3)
        {
            this.m_toggle = !this.m_toggle;
            flag2 = true;
            game.clearMouseStatus();
        }
        this.m_sprite.Paint(painter, (float) x, (float) y, this.m_toggle ? 0 : 1);
        return flag2;
    }

    public void toggle(boolean aState)
    {
        this.m_toggle = aState;
    }
}
