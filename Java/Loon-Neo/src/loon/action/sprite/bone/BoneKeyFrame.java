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

import loon.utils.TArray;

public class BoneKeyFrame {
	
	public int frameNumber;
	public TArray<Bone> bones;
	public String trigger = "";
	public boolean flipVertically;
	public boolean flipHorizontally;

	public float frameTime;
	public TArray<Bone> updateOrderBones;

	public BoneKeyFrame() {
		updateOrderBones = new TArray<Bone>();
	}

	public BoneKeyFrame cpy(int frameNumber) {
		BoneKeyFrame k = new BoneKeyFrame();
		k.frameNumber = frameNumber;
		k.flipVertically = flipVertically;
		k.flipHorizontally = flipHorizontally;
		k.trigger = "";
		for (Bone b : bones) {
			k.bones.add(b.cpy());
		}
		return k;
	}

	public void sortBones() {
		updateOrderBones.clear();
		for (Bone bone : bones) {
			boneSortAdd(bone);
		}
	}

	protected void boneSortAdd(Bone b) {
		if (updateOrderBones.contains(b)) {
			return;
		}
		if (b.parentIndex != -1) {
			boneSortAdd(bones.get(b.parentIndex));
		}
		updateOrderBones.add(b);
		b.updateIndex = updateOrderBones.size() - 1;
	}
}
