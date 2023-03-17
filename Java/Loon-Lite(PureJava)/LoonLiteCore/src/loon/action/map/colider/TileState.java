/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.map.colider;

public class TileState {

    public static final int NOT_WALL = 0;

    public static final int WALL = 1;

    private int state = 0;

    public TileState() {
    	this(NOT_WALL);
    }

    public TileState(int v) {
    	this.state = v;
    }

    public TileState setResult(int v) {
    	this.state = v;
    	return this;
    }

    public int getResult() {
    	return this.state;
    }

    public boolean isWalkable() {
        return this.state == NOT_WALL;
    }

    public boolean isNotWalkable() {
        return this.state == WALL;
    }
}
