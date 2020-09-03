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
package loon.action.sprite.bone;

import loon.geom.RectBox;
import loon.geom.Vector2f;

public class Bone {
	
	protected String name;
	protected boolean hidden;
	protected int parentIndex;
	protected int textureIndex;
	protected int selfIndex;

	protected int updateIndex;
	protected RectBox bounds;
	protected Vector2f position;
	protected float rotation;
	protected Vector2f scale;
	protected boolean textureFlipHorizontal;
	protected boolean textureFlipVertical;

	public Bone cpy() {
		Bone b = new Bone();
		b.name = name;
		b.hidden = hidden;
		b.parentIndex = parentIndex;
		b.textureIndex = textureIndex;
		b.updateIndex = updateIndex;
		b.position = position.cpy();
		b.rotation = rotation;
		b.scale = scale.cpy();
		b.textureFlipHorizontal = textureFlipHorizontal;
		b.textureFlipVertical = textureFlipVertical;
		return b;
	}
}
