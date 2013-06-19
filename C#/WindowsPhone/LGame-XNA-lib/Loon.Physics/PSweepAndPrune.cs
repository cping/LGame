/// <summary>
/// Copyright 2013 The Loon Authors
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
namespace Loon.Physics {
	
    using Loon.Utils;
	
	public class PSweepAndPrune {
	
		internal bool checkX;
		internal int numObject;
		private PSortableObject[] objsX;
		private PSortableObject[] objsY;
	
		public PSweepAndPrune() {
			objsX = new PSortableObject[1024];
			objsY = new PSortableObject[1024];
		}
	
		internal void AddObject(PSortableObject ox, PSortableObject oy) {
			if (numObject + 1 >= objsX.Length) {
				objsX = (PSortableObject[]) CollectionUtils.CopyOf(objsX,
						objsX.Length * 2);
				objsY = (PSortableObject[]) CollectionUtils.CopyOf(objsY,
						objsY.Length * 2);
			}
			objsX[numObject] = ox;
			objsY[numObject] = oy;
			numObject++;
		}
	
		internal void RemoveObject(PSortableObject ox, PSortableObject oy) {
			int indexX = -1;
			int indexY = -1;
			for (int i = 0; i < numObject; i++) {
				if (objsX[i] != ox)
					continue;
				indexX = i;
				break;
			}
	
			if (indexX != -1 && indexX != numObject - 1)
				System.Array.Copy((objsX),indexX + 1,(objsX),indexX,numObject
									- indexX - 1);
			for (int i_0 = 0; i_0 < numObject; i_0++) {
				if (objsY[i_0] != oy)
					continue;
				indexY = i_0;
				break;
			}
	
			if (indexY != -1 && indexY != numObject - 1)
				System.Array.Copy((objsY),indexY + 1,(objsY),indexY,numObject
									- indexY - 1);
			numObject--;
		}
	
		internal PSortableObject[] Sort() {
			Physics.PInsertionSorter.Sort(objsX, numObject);
			Physics.PInsertionSorter.Sort(objsY, numObject);
			int stack = 0;
			int overlapX = 0;
			int overlapY = 0;
			for (int i = 0; i < numObject; i++)
				if (objsX[i].begin) {
					stack++;
					overlapX += stack;
				} else {
					stack--;
				}
	
			stack = 0;
			for (int i_0 = 0; i_0 < numObject; i_0++)
				if (objsY[i_0].begin) {
					stack++;
					overlapY += stack;
				} else {
					stack--;
				}
	
			return (checkX = overlapX < overlapY) ? objsX : objsY;
		}
	
	}
}
