/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package org.test;

import loon.geom.Vector2f;
import loon.utils.TArray;

public class Path {

	String name;
	
	float x,y;
	
	TArray<Vector2f> curves = new TArray<Vector2f>();

	
	TArray<Vector2f> cacheLengths = new TArray<Vector2f>();
	
	boolean autoClose = false;
	
	Vector2f startPoint = new Vector2f();
	
	Vector2f _tmpVec2A = new Vector2f();
	
	Vector2f _tmpVec2B = new Vector2f();
	 
	public Path(float x,float y){
		this.x = x;
		this.y = y;
	    this.startPoint.set(x, y);
	}
	
	public Path add (Vector2f curve)
	    {
	        this.curves.add(curve);

	        return this;
	    }
	
}
