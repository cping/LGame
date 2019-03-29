/**
 * 
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.action.collision;

public class CollisionBaseQuery implements CollisionQuery {

	private String flag;

	private CollisionObject compareObject;

	public void init(String flag, CollisionObject actor) {
		this.flag = flag;
		this.compareObject = actor;
	}

	public boolean checkOnlyCollision(CollisionObject other) {
		return (this.compareObject == null ? true : other
				.intersects(this.compareObject));
	}

	@Override
	public boolean checkCollision(CollisionObject other) {
		return this.flag != null && !flag.equals(other.getObjectFlag()) ? false
				: (this.compareObject == null ? true : other
						.intersects(this.compareObject));
	}
}
