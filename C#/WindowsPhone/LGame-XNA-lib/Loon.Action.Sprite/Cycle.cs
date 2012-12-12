namespace Loon.Action.Sprite
{

    using System;
    using System.Collections.Generic;
    using Loon.Core;
    using Loon.Utils;
    using Loon.Core.Timer;
    using Loon.Core.Geom;
    using Loon.Core.Graphics;
    using Loon.Core.Graphics.Opengl;
    using Microsoft.Xna.Framework;

    public class Cycle : LObject, ISprite
    {

     
		public static Cycle GetSample(int type, float srcWidth,
				float srcHeight, float width, float height, float offset,
				int padding) {
	
			Cycle cycle = new Cycle();
			float s = 1;
			if (srcWidth > srcHeight) {
				s = MathUtils.Max(srcWidth / width, srcHeight / height);
			} else {
				s = MathUtils.Min(srcWidth / width, srcHeight / height);
			}
			float scale = s;
			switch (type) {
			case 0:
				cycle = new Anonymous_C1(scale);
				cycle.SetLineWidth(5);
				cycle.SetDelay(45);
				cycle.SetColor(0xFF2E82);
				cycle.SetStepType(4);
				cycle.SetStepsPerFrame(1);
				cycle.SetTrailLength(1);
				cycle.SetPointDistance(0.05f);
				cycle.AddPath(Cycle.ARC, 50, 50, 40, 0, 360);
				break;
			case 1:
				cycle.SetColor(0xFF7B24);
				cycle.SetStepsPerFrame(1);
				cycle.SetTrailLength(1);
				cycle.SetPointDistance(0.10f);
				cycle.SetMultiplier(2);
				cycle.AddPath(Cycle.ARC, 10 * scale, 10 * scale, 10 * scale, -270,
						-90);
				cycle.AddPath(Cycle.BEZIER, 10 * scale, 0 * scale, 40 * scale,
						20 * scale, 20 * scale, 0, 30 * scale, 20 * scale);
				cycle.AddPath(Cycle.ARC, 40 * scale, 10 * scale, 10 * scale, 90,
						-90);
				cycle.AddPath(Cycle.BEZIER, 40 * scale, 0 * scale, 10 * scale,
						20 * scale, 30 * scale, 0, 20 * scale, 20 * scale);
				break;
			case 2:
				cycle.SetColor(0xD4FF00);
				cycle.SetStepType(1);
				cycle.SetDelay(55);
				cycle.SetStepsPerFrame(2);
				cycle.SetTrailLength(0.3f);
				cycle.SetPointDistance(0.1f);
				cycle.AddPath(Cycle.LINE, 0, 0, 30 * scale, 0);
				cycle.AddPath(Cycle.LINE, 30 * scale, 0 * scale, 30 * scale,
						30 * scale);
				cycle.AddPath(Cycle.LINE, 30 * scale, 30 * scale, 0, 30 * scale);
				cycle.AddPath(Cycle.LINE, 0, 30 * scale, 0, 0);
				break;
			case 3:
	
				cycle = new Anonymous_C0(scale);
				cycle.SetColor(0x05E2FF);
				cycle.SetLineWidth(2);
				cycle.SetStepType(4);
				cycle.SetStepsPerFrame(1);
				cycle.SetTrailLength(1);
				cycle.SetPointDistance(0.025f);
				cycle.AddPath(Cycle.ARC, 50, 50, 40, 0, 360);
				break;
			case 4:
                cycle.SetColor(0xFFA50000);
				cycle.SetStepsPerFrame(1);
				cycle.SetTrailLength(1);
				cycle.SetPointDistance(0.025f);
				cycle.AddPath(Cycle.ARC, 50 * scale, 50 * scale, 40 * scale, 0, 360);
				break;
			case 5:
				cycle.SetColor(0xFF2E82);
				cycle.SetDelay(60);
				cycle.SetStepType(1);
				cycle.SetStepsPerFrame(1);
				cycle.SetTrailLength(1);
				cycle.SetPointDistance(0.1f);
				cycle.AddPath(Cycle.LINE, 0, 20 * scale, 100 * scale, 20 * scale);
				cycle.AddPath(Cycle.LINE, 100 * scale, 20 * scale, 0, 20 * scale);
				break;
			case 6:
				cycle.SetStepsPerFrame(7);
				cycle.SetTrailLength(0.7f);
				cycle.SetPointDistance(0.01f);
				cycle.SetDelay(35);
				cycle.SetLineWidth(10);
				cycle.AddPath(Cycle.LINE, 20 * scale, 70 * scale, 50 * scale,
						20 * scale);
				cycle.AddPath(Cycle.LINE, 50 * scale, 20 * scale, 80 * scale,
						70 * scale);
				cycle.AddPath(Cycle.LINE, 80 * scale, 70 * scale, 20 * scale,
						70 * scale);
				break;
			case 7:
				cycle.SetColor(0xD4FF00);
				cycle.SetStepsPerFrame(3);
				cycle.SetTrailLength(1);
				cycle.SetPointDistance(0.01f);
				cycle.SetLineWidth(6);
				cycle.SetPadding(0);
				cycle.AddPath(Cycle.ARC, 50 * scale, 50 * scale, 20 * scale, 360, 0);
				break;
			case 8:
				cycle.SetColor(0x05E2FF);
				cycle.SetStepsPerFrame(1);
				cycle.SetTrailLength(1);
				cycle.SetPointDistance(0.02f);
				cycle.AddPath(Cycle.ARC, 50 * scale, 50 * scale, 30 * scale, 0, 360);
				break;
			case 9:
				cycle.SetStepType(1);
				cycle.SetColor(LColor.yellow);
				cycle.AddPath(Cycle.LINE, 10 * scale, 10 * scale, 90 * scale,
						10 * scale);
				cycle.AddPath(Cycle.LINE, 90 * scale, 10 * scale, 90 * scale,
						90 * scale);
				cycle.AddPath(Cycle.LINE, 90 * scale, 90 * scale, 10 * scale,
						90 * scale);
				cycle.AddPath(Cycle.LINE, 10 * scale, 90 * scale, 10 * scale,
						10 * scale);
				break;
			}
			float size = MathUtils.Min(srcWidth / (1 / cycle.GetPointDistance()),
					srcHeight / (1 / cycle.GetPointDistance()));
			cycle.SetPadding(padding);
			cycle.SetBlockWidth(size + offset);
			cycle.SetBlockHeight(size + offset);
			cycle.SetWidth(width * scale);
			cycle.SetHeight(height * scale);
			return cycle;
		}
	
		private const long serialVersionUID = -4197405628446701982L;
	
		public const int OTHER = 0, DIM = 1, DEGREE = 2, RADIUS = 3;
	
		public const int BEZIER = 0, ARC = 1, LINE = 2;
	
		protected internal float pointDistance;
	
		protected internal float multiplier;
	
		protected internal int frame, padding;
	
		protected internal int stepType, lineWidth;
	
		protected internal float trailLength, stepsPerFrame;
	
		protected internal bool isUpdate, isVisible, stopped;
	
		protected internal List<object[]> data;
	
		protected static internal Dictionary<Int32, float[]> signatures;
	
		protected internal List<Progress> points;
	
		private LTimer timer;
	
		private Polygon poly;
	
		private LColor color;
	
		private Progress last;
	
		protected internal float scaleX, scaleY;
	
		protected internal float blockWidth, blockHeight, blockHalfWidth, blockHalfHeight;
	
		protected internal float width, height;
	
		public sealed class Anonymous_C1 : Cycle {
			private readonly float scale;
			private Path path;
	
			public Anonymous_C1(float scale) {
				this.scale = scale;
			}
	
			public override void Step(GLEx g, float x, float y, float progress,
					int index, int frame, LColor color, float alpha) {
				float cx = this.padding + 50, cy = this.padding + 50, angle = (MathUtils.PI / 180)
						* (progress * 360), innerRadius = (index == 1) ? 10
						: 25;
				if (path == null) {
					path = new Path(GetX() + x * scale, GetY() + y * scale);
				} else {
					path.Clear();
					path.Set(GetX() + x * scale, GetY() + y * scale);
				}
				path.LineTo(GetX()
						+ ((MathUtils.Cos(angle) * innerRadius) + cx)
						* scale, GetY()
						+ ((MathUtils.Sin(angle) * innerRadius) + cy)
						* scale);
				path.Close();
				g.Draw(path);
			}
		}
	
		public sealed class Anonymous_C0 : Cycle {
			private readonly float scale;
	
			private Path path;
	
			public Anonymous_C0(float scale) {
				this.scale = scale;
			}

            public override void Step(GLEx g, float x, float y, float progress,
					int index, int frame, LColor color, float alpha) {
	
				float cx = this.padding + 50, cy = this.padding + 50, angle = (MathUtils.PI / 180)
						* (progress * 360);
				alpha = MathUtils.Max(0.5f, alpha);
				g.SetAlpha(alpha);
				if (path == null) {
					path = new Path(GetX() + x * scale, GetY() + y * scale);
				} else {
					path.Clear();
					path.Set(GetX() + x * scale, GetY() + y * scale);
				}
				path.LineTo(GetX() + ((MathUtils.Cos(angle) * 35) + cx)
						* scale, GetY()
						+ ((MathUtils.Sin(angle) * 35) + cy) * scale);
				path.Close();
				g.Draw(path);
				if (path == null) {
					path = new Path(GetX()
							+ ((MathUtils.Cos(-angle) * 32) + cx) * scale,
							GetY() + ((MathUtils.Sin(-angle) * 32) + cy)
									* scale);
				} else {
					path.Clear();
					path.Set(GetX() + ((MathUtils.Cos(-angle) * 32) + cx)
							* scale, GetY()
							+ ((MathUtils.Sin(-angle) * 32) + cy) * scale);
				}
				path.LineTo(GetX() + ((MathUtils.Cos(-angle) * 27) + cx)
						* scale, GetY()
						+ ((MathUtils.Sin(-angle) * 27) + cy) * scale);
				path.Close();
				g.Draw(path);
				g.SetAlpha(1);
			}
		}
	
		public class Progress {
	
			internal float x;
	
			internal float y;
	
			internal float progress;
	
			public Progress(float x, float y, float p) {
				this.x = x;
				this.y = y;
				this.progress = p;
			}
		}
	
		public Cycle():	this(0, 0) {
		
		}
	
		public Cycle(int x, int y):this(x, y, 6, 6) {
			
		}
	
		public Cycle(int x, int y, int w, int h):this(null, x, y, w, h) {
			
		}
	
		public Cycle(List<object[]> path, int x, int y, int w, int h) {
	
			if (path != null) {
                CollectionUtils.Add(data, CollectionUtils.ToArray(path));
				isUpdate = true;
			} else {
				data = new List<object[]>(10);
			}
	
			this.SetLocation(x, y);
			this.timer = new LTimer(25);
			this.color = LColor.white;
			this.points = new List<Progress>();
			this.multiplier = 1;
			this.pointDistance = 0.05f;
			this.padding = 0;
			this.stepType = 0;
			this.stepsPerFrame = 1;
			this.trailLength = 1;
			this.scaleX = 1;
			this.scaleY = 1;
			this.alpha = 1;
			this.blockWidth = w;
			this.blockHeight = h;
			this.blockHalfWidth = w / 2;
			this.blockHalfHeight = h / 2;
			if (signatures == null) {
				signatures = new Dictionary<Int32, float[]>(3);
				CollectionUtils.Put(signatures,ARC,new float[] { 1, 1, 3, 2, 2, 0 });
				CollectionUtils.Put(signatures,BEZIER,new float[] { 1, 1, 1, 1, 1, 1, 1, 1 });
				CollectionUtils.Put(signatures,LINE,new float[] { 1, 1, 1, 1 });
			}
			this.Setup();
			this.isVisible = true;
	
		}
	
		public void Play() {
			this.stopped = false;
		}
	
		public void IterateFrame() {
			this.frame += (int)this.stepsPerFrame;
	
			if (this.frame >= this.points.Count) {
				this.frame = 0;
			}
		}
	
		public void Stop() {
			this.stopped = true;
		}
	
		public void SetDelay(long delay) {
			timer.SetDelay(delay);
		}
	
		public long GetDelay() {
			return timer.GetDelay();
		}
	
		public void AddPath(int type, params float[] f) {
			object[] o = new object[2];
			o[0] = type;
			o[1] = f;
            CollectionUtils.Add(data, o);
			isUpdate = true;
		}
	
		private void Setup() {
	
			if (!isUpdate) {
				return;
			}
	
			float[] args;
			float value_ren;
			int index;
			foreach (object[] o  in  data) {
				Int32 type = (Int32) o[0];
				args = (float[]) o[1];
	
				for (int a = -1, al = args.Length; ++a < al;) {

                    index = (int)((float[])CollectionUtils.Get(signatures, type))[a];
					value_ren = args[a];
					switch (index) {
					case RADIUS:
						value_ren *= this.multiplier;
						break;
					case DIM:
						value_ren *= this.multiplier;
						value_ren += this.padding;
						break;
					case DEGREE:
						value_ren *= MathUtils.PI / 180;
						break;
					}
	
					args[a] = value_ren;
				}
				CallMethod(type, args);
	
			}
			this.isUpdate = false;
		}
	
		private  void Step(GLEx g, Progress e, int index, int frame,
				LColor color, float alpha) {
			switch (stepType) {
			case 0:
				g.FillOval(X() + e.x - blockHalfWidth, Y() + e.y - blockHalfHeight,
						blockWidth, blockHeight);
				break;
			case 1:
				g.FillRect(X() + e.x - blockHalfWidth, Y() + e.y - blockHalfHeight,
						blockWidth, blockHeight);
				break;
			case 2:
				if (last != null) {
					float[] xs = { X() + last.x, X() + e.x };
					float[] ys = { Y() + last.y, Y() + e.y };
					g.DrawPolygon(xs, ys, 2);
				}
				last = e;
				break;
			case 3:
				if (last != null) {
					g.DrawLine(X() + last.x, Y() + last.y, X() + e.x, Y() + e.y);
				}
				last = e;
				break;
			case 4:
				Step(g, e.x, e.y, e.progress, index, frame, color, alpha);
				break;
			}
		}
	
		public virtual void Step(GLEx g, float x, float y, float progress, int index,
				int frame, LColor color, float alpha) {
	
		}
	
		public override void Update(long elapsedTime) {
			if (timer.Action(elapsedTime)) {
				this.IterateFrame();
			}
		}
	
		private void CallMethod(int index, params float[] f) {
	
			float[] result;
	
			for (float pd = this.pointDistance, t = pd; t <= 1; t += pd) {
	
				t = MathUtils.Round(t * 1f / pd) / (1f / pd);
				switch (index) {
				case BEZIER:
					result = Bezier(t, f[0], f[1], f[2], f[3], f[4], f[5], f[6],
							f[7]);
					break;
				case ARC:
					result = Arc(t, f[0], f[1], f[2], f[3], f[4]);
					break;
				case LINE:
					result = Line(t, f[0], f[1], f[2], f[3]);
					break;
				default:
					result = new float[] { 0f, 0f };
					break;
				}
	
				CollectionUtils.Add(points,new Progress(result[0], result[1], t));
	
			}
	
		}
	
		private float[] Bezier(float t, float p0x, float p0y, float p1x,
				float p1y, float c0x, float c0y, float c1x, float c1y) {
	
			t = 1 - t;
	
			float i = 1 - t, x = t * t, y = i * i, a = x * t, b = 3 * x * i, c = 3
					* t * y, d = y * i;
	
			return new float[] { a * p0x + b * c0x + c * c1x + d * p1x,
					a * p0y + b * c0y + c * c1y + d * p1y };
		}
	
		private float[] Arc(float t, float cx, float cy, float radius,
				float start, float end) {
			float point = (end - start) * t + start;
	
			return new float[] { (MathUtils.Cos(point) * radius) + cx,
					(MathUtils.Sin(point) * radius) + cy };
	
		}
	
		private float[] Line(float t, float sx, float sy, float ex, float ey) {
			return new float[] { (ex - sx) * t + sx, (ey - sy) * t + sy };
		}
	
		public void CreateUI(GLEx g) {
			if (!isVisible) {
				return;
			}
	
			this.Setup();
	
			int pointsLength = points.Count;
	
			Progress point;
			int index;
			int frameD;
			int indexD;
	
			float size = (pointsLength * this.trailLength);
	
			for (float i = -1, l = size; ++i < l && !this.stopped;) {
	
				index = (int) (frame + i);
				if (index < pointsLength) {
					point = points[index];
				} else {
					point = points[index - pointsLength];
				}
				this.alpha = (i / (l - 1));
				frameD = frame / (pointsLength - 1);
				indexD = (int) alpha;
				if (lineWidth > 0) {
					g.SetLineWidth(lineWidth);
				}
				if (scaleX != 1 || scaleY != 1) {
					g.Scale(scaleX, scaleY);
				}
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(alpha);
				}
				g.SetColor(color);
				Step(g, point, indexD, frameD, color, alpha);
				g.ResetColor();
				if (alpha > 0 && alpha < 1) {
					g.SetAlpha(1);
				}
				if (lineWidth > 0) {
					g.ResetLineWidth();
				}
				if (scaleX != 1 || scaleY != 1) {
					g.Restore();
				}
			}
	
		}
	
		public LColor GetColor() {
			return color;
		}
	
		public void SetColor(LColor color) {
			this.color = color;
		}
	
		public void SetColor(uint pixel) {
			this.color.SetARGB(pixel);
		}
	
		public List<object[]> GetData() {
			return data;
		}
	
		public void SetData(List<object[]> data) {
			this.data = data;
		}
	
		public int GetFrame() {
			return frame;
		}
	
		public void SetFrame(int frame) {
			this.frame = frame;
		}
	
		public bool IsUpdate() {
			return isUpdate;
		}
	
		public void SetUpdate(bool isUpdate) {
			this.isUpdate = isUpdate;
		}
	
		public Progress GetLast() {
			return last;
		}
	
		public void SetLast(Progress last) {
			this.last = last;
		}
	
		public int GetLineWidth() {
			return lineWidth;
		}
	
		public void SetLineWidth(int lineWidth) {
			this.lineWidth = lineWidth;
		}
	
		public float GetMultiplier() {
			return multiplier;
		}
	
		public void SetMultiplier(float multiplier) {
			this.multiplier = multiplier;
		}
	
		public int GetPadding() {
			return padding;
		}
	
		public void SetPadding(int padding) {
			this.padding = padding;
		}
	
		public float GetPointDistance() {
			return pointDistance;
		}
	
		public void SetPointDistance(float pointDistance) {
			this.pointDistance = pointDistance;
		}
	
		public List<Progress> GetPoints() {
			return points;
		}
	
		public void SetPoints(List<Progress> points) {
			this.points = points;
		}
	
		public float GetScaleX() {
			return scaleX;
		}
	
		public void SetScaleX(float scaleX) {
			this.scaleX = scaleX;
		}
	
		public float GetScaleY() {
			return scaleY;
		}
	
		public void SetScaleY(float scaleY) {
			this.scaleY = scaleY;
		}
	
		public float GetStepsPerFrame() {
			return stepsPerFrame;
		}
	
		public void SetStepsPerFrame(float stepsPerFrame) {
			this.stepsPerFrame = stepsPerFrame;
		}
	
		public int GetStepType() {
			return stepType;
		}
	
		public void SetStepType(int stepType) {
			this.stepType = stepType;
		}
	
		public bool IsStopped() {
			return stopped;
		}
	
		public float GetTrailLength() {
			return trailLength;
		}
	
		public void SetTrailLength(float trailLength) {
			this.trailLength = trailLength;
		}
	
		public int GetBlockHeight() {
			return (int) blockHeight;
		}
	
		public void SetBlockHeight(float blockHeight) {
			this.blockHeight = blockHeight;
			this.blockHalfHeight = blockHeight / 2;
		}
	
		public int GetBlockWidth() {
			return (int) blockWidth;
		}
	
		public void SetBlockWidth(float blockWidth) {
			this.blockWidth = blockWidth;
			this.blockHalfWidth = blockWidth / 2;
		}
	
		public LTexture GetBitmap() {
			return null;
		}
	
		public Shape GetShape() {
			if (isUpdate) {
				Setup();
				poly = new Polygon();
				foreach (Progress point  in  points) {
					poly.AddPoint(point.x, point.y);
				}
			}
			return poly;
		}
	
		public RectBox GetCollisionBox() {
			Shape shape = GetShape();
			return GetRect(shape.GetX(), shape.GetY(), shape.GetWidth(),
					shape.GetHeight());
		}
	
		public void SetWidth(float w) {
			this.width = w;
		}
	
		public void SetHeight(float h) {
			this.height = h;
		}
	
		public override int GetWidth() {
			return (int) height;
		}

        public override int GetHeight()
        {
			return (int) width;
		}
	
		public bool IsVisible() {
			return isVisible;
		}
	
		public void SetVisible(bool visible) {
			this.isVisible = visible;
		}
	
		public void Dispose() {
	
		}
    }
}
