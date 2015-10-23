/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.utils.reply;

public class IntValue extends Var<Integer>
{
    public IntValue (int value) {
        super(value);
    }

    public int increment (int amount) {
        return updateInt(get() + amount);
    }

    public int incrementClamp (int amount, int max) {
        return updateInt(Math.min(get() + amount, max));
    }

    public int incrementClamp (int amount, int min, int max) {
        return updateInt(Math.max(min, Math.min(get() + amount, max)));
    }

    public int decrementClamp (int amount, int min) {
        return updateInt(Math.max(get() - amount, min));
    }

    protected int updateInt (int value) {
        update(value);
        return value;
    }
}
