/// <summary>
/// Copyright 2008 - 2012
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
/// @project loon
/// @email£ºjavachenpeng@yahoo.com
namespace Loon.Action {
	
    using Loon.Action.Map;
    using Loon.Core.Geom;
	
	public interface Event {
	
		Field2D GetField2D();
	
		int X();
	
		int Y();
	
		float GetX();
	
		float GetY();
	
		float GetScaleX();
	
		float GetScaleY();
	
		void SetScale(float sx, float sy);
	
		float GetRotation();
	
		void SetRotation(float r);
	
		int GetWidth();
	
		int GetHeight();
	
		float GetAlpha();
	
		void SetAlpha(float a);
	
		void SetLocation(float x, float y);
	
		bool IsBounded();
	
		bool IsContainer();
	
		bool InContains(int x, int y, int w, int h);
	
		RectBox GetRectBox();
	
		int GetContainerWidth();
	
		int GetContainerHeight();
	}
}
