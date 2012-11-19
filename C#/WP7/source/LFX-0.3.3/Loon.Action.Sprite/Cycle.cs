namespace Loon.Action.Sprite
{

    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Loon.Core;
    using Loon.Utils;
    using Loon.Core.Timer;
    using Loon.Core.Geom;
    using Loon.Core.Graphics;
    using Loon.Core.Graphics.OpenGL;

    public class Cycle : LObject, ISprite
    {

        internal class CycleProgress
        {

            public float x;

            public float y;

            public float progress;

            public CycleProgress(float x1, float y1, float p)
            {
                this.x = x1;
                this.y = y1;
                this.progress = p;
            }
        }

        public static Cycle GetSample(int type, float srcWidth,
                float srcHeight, float width, float height, float offset,
                int padding)
        {

            Cycle cycle = new Cycle();
            float s = 1;
            if (srcWidth > srcHeight)
            {
                s = MathUtils.Max(srcWidth / width, srcHeight / height);
            }
            else
            {
                s = MathUtils.Min(srcWidth / width, srcHeight / height);
            }
            float scale = s;
            switch (type)
            {
                case 0:
                    cycle = new Cycle._Anonymous(scale);
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

                    cycle = new Cycle.Anonymous_C0(scale);
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

        internal List<CycleProgress> points;

        private LTimer timer;

        private Polygon poly;

        private LColor color;

        private CycleProgress last;

        protected internal float scaleX, scaleY;

        protected internal float blockWidth, blockHeight, blockHalfWidth, blockHalfHeight;

        protected internal float width, height;

        public sealed class _Anonymous : Cycle
        {
            private readonly float scale;
            private const long serialVersionUID = 1L;
            private Path path;

            public _Anonymous(float scale_0)
            {
                this.scale = scale_0;
            }

            public override void Step(GLEx g, float x, float y, float progress,
                    int index, int frame_0, LColor color_1, float alpha_2)
            {
                float cx = this.padding + 50, cy = this.padding + 50, angle = (MathUtils.PI / 180)
                        * (progress * 360), innerRadius = (index == 1) ? 10
                        : 25;
                if (path == null)
                {
                    path = new Path(GetX() + x * scale, GetY() + y * scale);
                }
                else
                {
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

        public sealed class Anonymous_C0 : Cycle
        {
            private readonly float scale;
            private const long serialVersionUID = 1L;
            private Path path;

            public Anonymous_C0(float scale_0)
            {
                this.scale = scale_0;
            }

            public override void Step(GLEx g, float x, float y, float progress,
                    int index, int frame_0, LColor color_1, float alpha_2)
            {

                float cx = this.padding + 50, cy = this.padding + 50, angle = (MathUtils.PI / 180)
                        * (progress * 360);
                alpha_2 = MathUtils.Max(0.5f, alpha_2);
                g.SetAlpha(alpha_2);
                if (path == null)
                {
                    path = new Path(GetX() + x * scale, GetY() + y * scale);
                }
                else
                {
                    path.Clear();
                    path.Set(GetX() + x * scale, GetY() + y * scale);
                }
                path.LineTo(GetX() + ((MathUtils.Cos(angle) * 35) + cx)
                        * scale, GetY()
                        + ((MathUtils.Sin(angle) * 35) + cy) * scale);
                path.Close();
                g.Draw(path);
                if (path == null)
                {
                    path = new Path(GetX()
                            + ((MathUtils.Cos(-angle) * 32) + cx) * scale,
                            GetY() + ((MathUtils.Sin(-angle) * 32) + cy)
                                    * scale);
                }
                else
                {
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


        public Cycle()
            : this(0, 0)
        {

        }

        public Cycle(int x_0, int y_1)
            : this(x_0, y_1, 6, 6)
        {

        }

        public Cycle(int x_0, int y_1, int w, int h)
            : this(null, x_0, y_1, w, h)
        {

        }

        public Cycle(List<object[]> path_0, int x_1, int y_2, int w, int h)
        {

            if (path_0 != null)
            {
                CollectionUtils.Add(data, CollectionUtils.ToArray(path_0));
                isUpdate = true;
            }
            else
            {
                data = new List<object[]>(10);
            }

            this.SetLocation(x_1, y_2);
            this.timer = new LTimer(25);
            this.color = LColor.white;
            this.points = new List<CycleProgress>();
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
            if (signatures == null)
            {
                signatures = new Dictionary<Int32, float[]>(3);
                CollectionUtils.Put(signatures, ARC, new float[] { 1, 1, 3, 2, 2, 0 });
                CollectionUtils.Put(signatures, BEZIER, new float[] { 1, 1, 1, 1, 1, 1, 1, 1 });
                CollectionUtils.Put(signatures, LINE, new float[] { 1, 1, 1, 1 });
            }
            this.Setup();
            this.isVisible = true;

        }

        public void Play()
        {
            this.stopped = false;
        }

        public void IterateFrame()
        {
            this.frame += (int)this.stepsPerFrame;

            if (this.frame >= this.points.Count)
            {
                this.frame = 0;
            }
        }

        public void Stop()
        {
            this.stopped = true;
        }

        public void SetDelay(long delay)
        {
            timer.SetDelay(delay);
        }

        public long GetDelay()
        {
            return timer.GetDelay();
        }

        public void AddPath(int type, params float[] f)
        {
            object[] o = new object[2];
            o[0] = type;
            o[1] = f;
            CollectionUtils.Add(data, o);
            isUpdate = true;
        }

        private void Setup()
        {

            if (!isUpdate)
            {
                return;
            }

            float[] args;
            float value_ren;
            int index;

            foreach (object[] o in data)
            {
                Int32 type = (Int32)o[0];
                args = (float[])o[1];

                for (int a = -1, al = args.Length; ++a < al; )
                {
                    index = (int)(((float[])CollectionUtils.Get(signatures, type))[a]);
                    value_ren = args[a];
                    switch (index)
                    {
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
                CallMethod((type), args);

            }
            this.isUpdate = false;
        }

        private void Step(GLEx g, CycleProgress e, int index, int frame_0,
                LColor color_1, float alpha_2)
        {
            switch (stepType)
            {
                case 0:
                    g.FillOval(X() + e.x - blockHalfWidth, Y() + e.y - blockHalfHeight,
                            blockWidth, blockHeight);
                    break;
                case 1:
                    g.FillRect(X() + e.x - blockHalfWidth, Y() + e.y - blockHalfHeight,
                            blockWidth, blockHeight);
                    break;
                case 2:
                    if (last != null)
                    {
                        float[] xs = { X() + last.x, X() + e.x };
                        float[] ys = { Y() + last.y, Y() + e.y };
                        g.DrawPolygon(xs, ys, 2);
                    }
                    last = e;
                    break;
                case 3:
                    if (last != null)
                    {
                        g.DrawLine(X() + last.x, Y() + last.y, X() + e.x, Y() + e.y);
                    }
                    last = e;
                    break;
                case 4:
                    Step(g, e.x, e.y, e.progress, index, frame_0, color_1, alpha_2);
                    break;
            }
        }

        public virtual void Step(GLEx g, float x_0, float y_1, float progress_2, int index,
                int frame_3, LColor color_4, float alpha_5)
        {

        }

        public override void Update(long elapsedTime)
        {
            if (timer.Action(elapsedTime))
            {
                this.IterateFrame();
            }
        }

        private void CallMethod(int index, params float[] f)
        {

            float[] result;

            for (float pd = this.pointDistance, t = pd; t <= 1; t += pd)
            {

                t = MathUtils.Round(t * 1f / pd) / (1f / pd);
                switch (index)
                {
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

                CollectionUtils.Add(points, new CycleProgress(result[0], result[1], t));

            }

        }

        private float[] Bezier(float t, float p0x, float p0y, float p1x,
                float p1y, float c0x, float c0y, float c1x, float c1y)
        {

            t = 1 - t;

            float i = 1 - t, x_0 = t * t, y_1 = i * i, a = x_0 * t, b = 3 * x_0 * i, c = 3
                    * t * y_1, d = y_1 * i;

            return new float[] { a * p0x + b * c0x + c * c1x + d * p1x,
					a * p0y + b * c0y + c * c1y + d * p1y };
        }

        private float[] Arc(float t, float cx, float cy, float radius,
                float start, float end)
        {
            float point = (end - start) * t + start;

            return new float[] { (MathUtils.Cos(point) * radius) + cx,
					(MathUtils.Sin(point) * radius) + cy };

        }

        private float[] Line(float t, float sx, float sy, float ex, float ey)
        {
            return new float[] { (ex - sx) * t + sx, (ey - sy) * t + sy };
        }

        public virtual void CreateUI(GLEx g)
        {
            if (!isVisible)
            {
                return;
            }

            this.Setup();

            int pointsLength = points.Count;

            CycleProgress point;
            int index;
            int frameD;
            int indexD;

            float size = (pointsLength * this.trailLength);

            for (float i = -1, l = size; ++i < l && !this.stopped; )
            {
                index = (int)(frame + i);
                if (index < pointsLength)
                {
                    point = points[index];
                }
                else
                {
                    point = points[index - pointsLength];
                }
                this.alpha = (i / (l - 1));
                frameD = frame / (pointsLength - 1);
                indexD = (int)alpha;
                if (lineWidth > 0)
                {
                    g.SetLineWidth(lineWidth);
                }
                if (scaleX != 1 || scaleY != 1)
                {
                    g.Scale(scaleX, scaleY);
                }
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(alpha);
                }
                g.SetColor(color);
                Step(g, point, indexD, frameD, color, alpha);
                g.ResetColor();
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(1);
                }
                if (lineWidth > 0)
                {
                    g.ResetLineWidth();
                }
                if (scaleX != 1 || scaleY != 1)
                {
                    g.Restore();
                }
            }

        }

        public LColor GetColor()
        {
            return color;
        }

        public void SetColor(LColor color_0)
        {
            this.color = color_0;
        }

        public void SetColor(uint pixel)
        {
            this.color = new LColor(pixel);
        }

        public List<object[]> GetData()
        {
            return data;
        }

        public void SetData(List<object[]> data_0)
        {
            this.data = data_0;
        }

        public int GetFrame()
        {
            return frame;
        }

        public void SetFrame(int frame_0)
        {
            this.frame = frame_0;
        }

        public bool IsUpdate()
        {
            return isUpdate;
        }

        public void SetUpdate(bool isUpdate_0)
        {
            this.isUpdate = isUpdate_0;
        }

        public int GetLineWidth()
        {
            return lineWidth;
        }

        public void SetLineWidth(int lineWidth_0)
        {
            this.lineWidth = lineWidth_0;
        }

        public float GetMultiplier()
        {
            return multiplier;
        }

        public void SetMultiplier(float multiplier_0)
        {
            this.multiplier = multiplier_0;
        }

        public int GetPadding()
        {
            return padding;
        }

        public void SetPadding(int padding_0)
        {
            this.padding = padding_0;
        }

        public float GetPointDistance()
        {
            return pointDistance;
        }

        public void SetPointDistance(float pointDistance_0)
        {
            this.pointDistance = pointDistance_0;
        }

        public float GetScaleX()
        {
            return scaleX;
        }

        public void SetScaleX(float scaleX_0)
        {
            this.scaleX = scaleX_0;
        }

        public float GetScaleY()
        {
            return scaleY;
        }

        public void SetScaleY(float scaleY_0)
        {
            this.scaleY = scaleY_0;
        }

        public float GetStepsPerFrame()
        {
            return stepsPerFrame;
        }

        public void SetStepsPerFrame(float stepsPerFrame_0)
        {
            this.stepsPerFrame = stepsPerFrame_0;
        }

        public int GetStepType()
        {
            return stepType;
        }

        public void SetStepType(int stepType_0)
        {
            this.stepType = stepType_0;
        }

        public bool IsStopped()
        {
            return stopped;
        }

        public float GetTrailLength()
        {
            return trailLength;
        }

        public void SetTrailLength(float trailLength_0)
        {
            this.trailLength = trailLength_0;
        }

        public int GetBlockHeight()
        {
            return (int)blockHeight;
        }

        public void SetBlockHeight(float blockHeight_0)
        {
            this.blockHeight = blockHeight_0;
            this.blockHalfHeight = blockHeight_0 / 2;
        }

        public int GetBlockWidth()
        {
            return (int)blockWidth;
        }

        public void SetBlockWidth(float blockWidth_0)
        {
            this.blockWidth = blockWidth_0;
            this.blockHalfWidth = blockWidth_0 / 2;
        }

        public virtual LTexture GetBitmap()
        {
            return null;
        }

        public Shape GetShape()
        {
            if (isUpdate)
            {
                Setup();
                poly = new Polygon();
                /* foreach */
                foreach (CycleProgress point in points)
                {
                    poly.AddPoint(point.x, point.y);
                }
            }
            return poly;
        }

        public virtual RectBox GetCollisionBox()
        {
            Shape shape = GetShape();
            return GetRect(shape.GetX(), shape.GetY(), shape.GetWidth(),
                    shape.GetHeight());
        }

        public void SetWidth(float w)
        {
            this.width = w;
        }

        public void SetHeight(float h)
        {
            this.height = h;
        }

        public override int GetWidth()
        {
            return (int)height;
        }

        public override int GetHeight()
        {
            return (int)width;
        }

        public virtual bool IsVisible()
        {
            return isVisible;
        }

        public virtual void SetVisible(bool visible)
        {
            this.isVisible = visible;
        }

        public virtual void Dispose()
        {

        }

    }
}
