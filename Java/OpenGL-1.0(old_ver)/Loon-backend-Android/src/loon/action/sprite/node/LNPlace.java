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

import loon.core.geom.Vector2f;

public class LNPlace extends LNAction
{
	LNPlace(){
		
	}
	
    protected Vector2f _pos;

    public static LNPlace Action(Vector2f pos)
    {
    	LNPlace place = new LNPlace();
        place._pos = pos;
        return place;
    }

    @Override
	public void step(float dt)
    {
        super._target.setPosition(this._pos);
        super._isEnd = true;
    }

	@Override
	public LNAction copy() {
		return Action(_pos);
	}
}
