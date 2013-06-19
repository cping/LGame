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
using Loon.Core.Geom;
using Loon.Core.Graphics;
using Loon.Utils;
namespace Loon.Physics {
	
	public abstract class PShape {
	
		internal AABB _aabb;
		internal float _ang;
		internal float _dens;
		internal float _fric;
		internal float _localAng;
		internal float ii;
		internal float mm;
		internal Vector2f _localPos;
		internal PTransformer _mAng;
		internal PBody _parent;
		internal Vector2f _pos;
		internal bool _rem;
		internal float _rest;
		internal PSortableAABB _sapAABB;
		internal PShapeType _type;
		internal LColor _color;
		internal LColor _strokeColor;
		private bool _rnd;
	
		public PShape():this(true) {
			
		}
	
		public PShape(bool randColor) {
			_fric = 0.5F;
			_rest = 0.5F;
			_localPos = new Vector2f();
			_pos = new Vector2f();
			_mAng = new PTransformer();
			_aabb = new AABB();
			_sapAABB = new PSortableAABB();
			_type = Physics.PShapeType.NULL_SHAPE;
			_rnd = randColor;
			if (randColor) {
				SetColor((int) (MathUtils.Random() * 160F + 96F),
						(int) (MathUtils.Random() * 160F + 96F),
						(int) (MathUtils.Random() * 160F + 96F));
			}
		}
	
		abstract internal void CalcAABB();
	
		public AABB GetAABB() {
			return _aabb;
		}
	
		public float GetAngle() {
			return _ang;
		}
	
		public float GetDensity() {
			return _dens;
		}
	
		public float GetFriction() {
			return _fric;
		}
	
		public float GetLocalAngle() {
			return _localAng;
		}
	
		public Vector2f GetLocalPosition() {
			return _localPos.Clone();
		}
	
		public Vector2f GetPosition() {
			return _pos.Clone();
		}
	
		public float GetRestitution() {
			return _rest;
		}
	
		public PShapeType GetShapeType() {
			return _type;
		}

		public void SetAngle(float angle) {
			_localAng = angle;
		}
	
		public void SetDensity(float density) {
			_dens = density;
			if (_parent != null)
				_parent.CalcMassData();
		}
	
		public void SetFriction(float friction) {
			_fric = friction;
		}
	
		public void SetPosition(float px, float py) {
			_localPos.Set(px, py);
			if (_parent != null) {
				_localPos.SubLocal(_parent.pos);
				_parent.CorrectCenterOfGravity();
				_parent.CalcMassData();
			}
		}
	
		public void SetRestitution(float restitution) {
			_rest = restitution;
		}
	
		abstract internal void Update();
	
		public void SetColor(int r, int g, int b) {
			if (_rnd) {
				_color = new LColor(r, g, b);
				_strokeColor = new LColor((int) ((float) r * 0.375F),
						(int) ((float) g * 0.375F), (int) ((float) b * 0.375F));
			}
		}
	
		public LColor GetColor() {
			if (_rnd) {
				return _color;
			}
			return LColor.black;
		}
	
		public LColor GetStrokeColor() {
			return _strokeColor;
		}
	
	}
}
