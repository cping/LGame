using Loon.Core.Geom;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Input;
using Loon.Utils;
using System.Collections.Generic;
namespace Loon.Core.Graphics.Component {

	public class LGesture : LComponent {
	
		private float mX;
		private float mY;
	
		private float curveEndX;
		private float curveEndY;
	
		private bool resetGesture;
	
		private bool autoClear;
	
		private Path goalPath;
	
		private LColor color = LColor.orange;
	
		private int lineWidth;
	
		public LGesture(int x, int y, int w, int h, bool c):base(x, y, w, h){
			this.autoClear = c;
			this.lineWidth = 5;
		}
	
		public LGesture(int x, int y, int w, int h) :this(x, y, w, h, true){
			
		}
	
		public LGesture():this(0, 0, LSystem.screenRect.width, LSystem.screenRect.height, true) {
			
		}
	
		public LGesture(bool flag):	this(0, 0, LSystem.screenRect.width, LSystem.screenRect.height, flag) {
		
		}
	
		public override void CreateUI(GLEx g, int x, int y, LComponent component,
				LTexture[] buttonImage) {
			if (visible && goalPath != null) {
				g.SetLineWidth(lineWidth);
				g.SetColor(color);
				g.Draw(goalPath);
				g.ResetLineWidth();
				g.ResetColor();
			}
		}
	
		protected internal override void ProcessTouchPressed() {
			int x = Touch.X();
			int y = Touch.Y();
			if (GetCollisionBox().Contains(x, y)) {
				mX = x;
				mY = y;
				if (resetGesture) {
					resetGesture = false;
					if (goalPath != null) {
						goalPath.Clear();
					}
				}
				if (goalPath == null) {
					goalPath = new Path(x, y);
	
				} else {
					goalPath.Set(x, y);
				}
				curveEndX = x;
				curveEndY = y;
				DownClick();
			}
		}
	
		protected internal override void ProcessTouchReleased() {
			if (autoClear) {
				Clear();
			}
			UpClick();
		}
	
		protected internal override void ProcessTouchDragged() {
			if (input.IsMoving()) {
				int x = Touch.X();
				int y = Touch.Y();
				if (GetCollisionBox().Contains(x, y)) {
					float previousX = mX;
					float previousY = mY;
	
					float dx = MathUtils.Abs(x - previousX);
					float dy = MathUtils.Abs(y - previousY);
	
					if (dx >= 3 || dy >= 3) {
						float cX = curveEndX = (x + previousX) / 2;
						float cY = curveEndY = (y + previousY) / 2;
						if (goalPath != null) {
							goalPath.QuadTo(previousX, previousY, cX, cY);
						}
						mX = x;
						mY = y;
					}
					DragClick();
				}
			}
		}
	
		public float[] GetPoints() {
			if (goalPath != null) {
				return goalPath.GetPoints();
			}
			return null;
		}
	
		public List<Vector2f> GetList() {
			if (goalPath != null) {
				float[] points = goalPath.GetPoints();
				int size = points.Length;
                List<Vector2f> result = new List<Vector2f>(size);
				for (int i = 0; i < size; i++) {
					result.Add(new Vector2f(points[i], points[i + 1]));
				}
				return result;
			}
			return null;
		}
	
		private static float Distance(float x1, float y1, float x2, float y2) {
			float deltaX = x1 - x2;
			float deltaY = y1 - y2;
			return MathUtils.Sqrt(deltaX * deltaX + deltaY * deltaY);
		}
	
		public float GetLength() {
			if (goalPath != null) {
				float length = 0;
				float[] points = goalPath.GetPoints();
				int size = points.Length;
				for (int i = 0; i < size;) {
					if (i < size - 3) {
						length += Distance(points[0 + i], points[1 + i],
								points[2 + i], points[3 + i]);
					}
					i += 4;
				}
				return length;
			}
			return 0;
		}

        public virtual float[] GetCenter()
        {
			if (goalPath != null) {
				return goalPath.GetCenter();
			}
			return new float[] { 0, 0 };
		}

        public virtual void DragClick()
        {
			if (Click != null) {
				Click.DragClick(this, input.GetTouchX(), input.GetTouchY());
			}
		}

        public virtual void DownClick()
        {
			if (Click != null) {
				Click.DownClick(this, input.GetTouchX(), input.GetTouchY());
			}
		}

        public virtual void UpClick()
        {
			if (Click != null) {
				Click.UpClick(this, input.GetTouchX(), input.GetTouchY());
			}
		}

        public virtual void Clear()
        {
			if (goalPath != null) {
				goalPath.Clear();
			}
		}

        public virtual float GetCurveEndX()
        {
			return curveEndX;
		}

        public virtual void SetCurveEndX(float curveEndX)
        {
			this.curveEndX = curveEndX;
		}

        public virtual float GetCurveEndY()
        {
			return curveEndY;
		}

        public virtual void SetCurveEndY(float curveEndY)
        {
			this.curveEndY = curveEndY;
		}

        public virtual Path GetPath()
        {
			return goalPath;
		}

        public virtual LColor GetColor()
        {
			return color;
		}

        public virtual void SetColor(LColor color)
        {
			this.color = color;
		}

        public virtual int GetLineWidth()
        {
			return lineWidth;
		}

        public virtual void SetLineWidth(int lineWidth)
        {
			this.lineWidth = lineWidth;
		}

        public virtual bool IsAutoClear()
        {
			return autoClear;
		}

        public virtual void SetAutoClear(bool autoClear)
        {
			this.autoClear = autoClear;
		}
	
		public override string GetUIName() {
			return "Gesture";
		}
	
	}
}
