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
package loon.action.sprite.node;

public class LNRepeatForever extends LNAction
{
	LNRepeatForever(){
		
	}
	
    protected LNAction _action;

    public static LNRepeatForever Action(LNAction action)
    {
    	LNRepeatForever forever = new LNRepeatForever();
        forever._action = action;
        return forever;
    }

    @Override
	public void setTarget(LNNode node)
    {
        super._firstTick = true;
        super._isEnd = false;
        super._target = node;
        this._action.setTarget(super._target);
    }

    @Override
	public void step(float dt)
    {
        if (super._firstTick)
        {
        	super._firstTick = false;
        	super._elapsed = 0f;
        }
        else
        {
        	super._elapsed += dt;
        }
        this._action.step(dt);
        if (this._action.isEnd())
        {
            this._action.start();
            super._elapsed = 0f;
        }
    }

	@Override
	public LNAction copy() {
		return Action(_action);
	}
}
