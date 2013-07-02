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
namespace Loon.Core {

    using System.Collections.Generic;
    using Loon.Core.Geom;
    using Loon.Utils;

	public class Director {
	
		public static bool IsOrientationPortrait() {
			if (LSystem.screenRect.width <= LSystem.screenRect.height) {
				return true;
			} else {
				return false;
			}
		}
	
		public enum Origin {
			CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, LEFT_CENTER, TOP_CENTER, BOTTOM_CENTER, RIGHT_CENTER
		}
	
		public enum Position {
			SAME, CENTER, LEFT, TOP_LEFT, TOP_LEFT_CENTER, TOP_RIGHT, TOP_RIGHT_CENTER, BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_LEFT_CENTER, BOTTOM_RIGHT, BOTTOM_RIGHT_CENTER, RIGHT_CENTER, TOP_CENTER
		}
	
		public static Vector2f MakeOrigin(LObject o, Origin origin) {
            return CreateOrigin(o, origin);
		}

        public static List<Vector2f> MakeOrigins(Origin origin,
                params LObject[] objs)
        {
            List<Vector2f> result = new List<Vector2f>(objs.Length);
            foreach (LObject o in objs)
            {
                CollectionUtils.Add(result, CreateOrigin(o, origin));
            }
            return result;
        }
	
		private static Vector2f CreateOrigin(LObject o, Origin origin) {
			Vector2f v = new Vector2f(o.X(), o.Y());
			switch (origin) {
			case Origin.CENTER:
				v.Set(o.GetWidth() / 2f, o.GetHeight() / 2f);
				return v;
			case Origin.TOP_LEFT:
				v.Set(0.0f, o.GetHeight());
				return v;
			case Origin.TOP_RIGHT:
				v.Set(o.GetWidth(), o.GetHeight());
				return v;
			case Origin.BOTTOM_LEFT:
				v.Set(0.0f, 0.0f);
				return v;
			case Origin.BOTTOM_RIGHT:
				v.Set(o.GetWidth(), 0.0f);
				return v;
			case Origin.LEFT_CENTER:
				v.Set(0.0f, o.GetHeight() / 2f);
				return v;
			case Origin.TOP_CENTER:
				v.Set(o.GetWidth() / 2f, o.GetHeight());
				return v;
			case Origin.BOTTOM_CENTER:
				v.Set(o.GetWidth() / 2f, 0.0f);
				return v;
			case Origin.RIGHT_CENTER:
				v.Set(o.GetWidth(), o.GetHeight() / 2f);
				return v;
			default:
				return v;
			}
		}
	
		public static void SetPoisiton(LObject objToBePositioned,
				LObject objStable, Position position) {
			float atp_W = objToBePositioned.GetWidth();
			float atp_H = objToBePositioned.GetHeight();
			float obj_X = objStable.GetX();
			float obj_Y = objStable.GetY();
			float obj_XW = objStable.GetWidth() + obj_X;
			float obj_YH = objStable.GetHeight() + obj_Y;
			SetLocation(objToBePositioned, atp_W, atp_H, obj_X, obj_Y, obj_XW,
					obj_YH, position);
		}
	
		public static void SetPoisiton(LObject objToBePositioned, float x, float y,
				float width, float height, Position position) {
			float atp_W = objToBePositioned.GetWidth();
			float atp_H = objToBePositioned.GetHeight();
			float obj_X = x;
			float obj_Y = y;
			float obj_XW = width + obj_X;
			float obj_YH = height + obj_Y;
			SetLocation(objToBePositioned, atp_W, atp_H, obj_X, obj_Y, obj_XW,
					obj_YH, position);
		}
	
		private static void SetLocation(LObject objToBePositioned, float atp_W,
				float atp_H, float obj_X, float obj_Y, float obj_XW, float obj_YH,
				Position position) {
			switch (position) {
			case Position.CENTER:
				objToBePositioned.SetX((obj_XW / 2f) - atp_W / 2f);
				objToBePositioned.SetY((obj_YH / 2f) - atp_H / 2f);
				break;
			case Position.SAME:
				objToBePositioned.SetLocation(obj_X, obj_Y);
				break;
			case Position.LEFT:
				objToBePositioned.SetLocation(obj_X, obj_YH / 2f - atp_H / 2f);
				break;
			case Position.TOP_LEFT:
				objToBePositioned.SetLocation(obj_X, obj_YH - atp_H);
				break;
			case Position.TOP_LEFT_CENTER:
				objToBePositioned.SetLocation(obj_X - atp_W / 2f, obj_YH - atp_H
						/ 2f);
				break;
			case Position.TOP_RIGHT:
				objToBePositioned.SetLocation(obj_XW - atp_W, obj_YH - atp_H);
				break;
			case Position.TOP_RIGHT_CENTER:
				objToBePositioned.SetLocation(obj_XW - atp_W / 2f, obj_YH - atp_H
						/ 2f);
				break;
			case Position.TOP_CENTER:
				objToBePositioned.SetLocation(obj_XW / 2f - atp_W / 2f, obj_YH
						- atp_H);
				break;
			case Position.BOTTOM_LEFT:
				objToBePositioned.SetLocation(obj_X, obj_Y);
				break;
			case Position.BOTTOM_LEFT_CENTER:
				objToBePositioned.SetLocation(obj_X - atp_W / 2f, obj_Y - atp_H
						/ 2f);
				break;
			case Position.BOTTOM_RIGHT:
				objToBePositioned.SetLocation(obj_XW - atp_W, obj_Y);
				break;
			case Position.BOTTOM_RIGHT_CENTER:
				objToBePositioned.SetLocation(obj_XW - atp_W / 2f, obj_Y - atp_H
						/ 2f);
				break;
			case Position.BOTTOM_CENTER:
				objToBePositioned.SetLocation(obj_XW / 2f - atp_W / 2f, obj_Y);
				break;
			case Position.RIGHT_CENTER:
				objToBePositioned.SetLocation(obj_XW - atp_W, obj_YH / 2f - atp_H
						/ 2f);
				break;
			default:
				objToBePositioned.SetLocation(objToBePositioned.GetX(),
						objToBePositioned.GetY());
				break;
			}
		}
	}
}
