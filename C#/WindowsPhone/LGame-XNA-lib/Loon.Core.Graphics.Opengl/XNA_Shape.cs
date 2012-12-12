using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System.Collections.ObjectModel;
using Loon.Utils;

//查询MSDN后发现，Opengl中的SetLineWidth函数在XNA并没有对应实现，只能通过SpriteBatch初始参数设置来扩展图形。
//所以单独建立新类，以满足此需求。
namespace Loon.Core.Graphics.Opengl
{
    public interface IType
    {

    }

    public abstract class Brush : IType
    {
    }

    public class StopColor
    {
        public Color Color { get; set; }
        public float Offset { get; set; }
    }

    public interface IGLType : IType
    {
        void Draw(SpriteBatch spriteBatch);
        void Update();
        bool Intersects(Vector2 point);
    }

    public class ColourBrush : Brush
    {
        public Color Colour { get; set; }
    }

    public abstract class XNAGradient : Brush
    {
        protected bool changed;
        protected Texture2D texture;
        protected GraphicsDevice pdevice;
        protected List<Color> stopColors;
        protected List<float> stopOffsets;
        protected Point dimensions;

        public Point Dimensions
        {
            get { return dimensions; }
            set { changed = true; dimensions = value; }
        }

        public Texture2D Texture
        {
            get
            {
                if (texture == null || changed) Update();
                return texture;
            }
        }

        public XNAGradient(GraphicsDevice device)
        {
            stopColors = new List<Color>();
            stopOffsets = new List<float>();
            pdevice = device;
            changed = true;
        }

        private float Unlerp(float value, float min, float max)
        {
            if (value <= min) return 0;
            if (value >= max) return 1;
            value -= min;
            max -= min;
            value /= max;
            return value;
        }

        public void AddStop(Color color, float offset)
        {
            changed = true;
            stopColors.Add(color);
            stopOffsets.Add(offset);
        }

        public void ClearStops()
        {
            changed = true;
            stopColors.Clear();
            stopOffsets.Clear();
        }

        protected Color GetColour(float lerp)
        {
            int offsetCount = stopOffsets.Count - 1;
            for (int c = 0; c < offsetCount; c++)
            {
                float colorStart = stopOffsets[c];
                float colorEnd = stopOffsets[c + 1];
                if (lerp >= colorStart && lerp <= colorEnd)
                {
                    lerp = Unlerp(lerp, colorStart, colorEnd);
                    return Color.Lerp(stopColors[c], stopColors[c + 1], lerp);
                }
            }
            if (stopColors.Count == 0)
            {
                return stopColors[0];
            }
            return stopColors.Last();
        }

        public abstract void Update();

    }

    public class XNALine : IGLType
    {
        private Vector2 pstart;
        private Vector2 pend;
        private float pstrokeWidth;
        private float pangle;
        private Vector2 pdirection;
        private float plength;
        private Vector2 pcentre;
        private bool pchanged;

        public XNALine()
        {
            pchanged = true;
        }

        public Vector2 Start
        {
            get { return pstart; }
            set { pstart = value; pchanged = true; }
        }

        public Vector2 End
        {
            get { return pend; }
            set { pend = value; pchanged = true; }
        }

        public Color Stroke { get; set; }

        public float StrokeWidth { get { return pstrokeWidth; } set { pstrokeWidth = value; pchanged = true; } }

        public void Update()
        {
            pdirection = End - Start;
            pdirection.Normalize();

            pangle = MathUtils.Atan2(End.Y - Start.Y, End.X - Start.X);
            plength = MathUtils.Ceil(Vector2.Distance(Start, End));
            pcentre = (Start + End) / 2;
            pchanged = false;
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            if (pchanged)
            {
                Update();
            }
            if (pstrokeWidth > 0)
            {
                spriteBatch.Draw(GLEx.WhitePixel, pcentre, new Microsoft.Xna.Framework.Rectangle(0, 0, (int)plength, (int)MathUtils.Floor(StrokeWidth)), Stroke, pangle, new Vector2(plength / 2f, (float)StrokeWidth / 2), 1, SpriteEffects.None, 0);
            }
        }

        private static Vector2 GetIntersectPoint(Vector2 start, Vector2 end, Vector2 point)
        {
            Vector2 gradientVector = end - start;
            Vector2 currentVector = point - start;
            float multiplier = MathHelper.Clamp(Vector2.Dot(gradientVector, currentVector) / gradientVector.LengthSquared(), 0, 1);
            return start + (gradientVector * multiplier);
        }

        private static bool Intersects(Vector2 start, Vector2 end, Vector2 point)
        {
            return Vector2.Distance(GetIntersectPoint(start, end, point), point) < 1f;
        }

        public bool Intersects(Vector2 point)
        {
            return Intersects(Start, End, point);
        }

    }

    public class XNAPolyline : IGLType
    {
        private bool pboundingBoxInitialized;
        private float pstrokeWidth;
        protected List<Vector2> ppoints;

        protected Vector2 pmax;
        protected Vector2 pmin;
        protected List<XNALine> plines;
        protected bool pchanged;
        protected GraphicsDevice pdevice;
        protected Texture2D pendPointTexture;

        public void Dispose()
        {
            pboundingBoxInitialized = false;
            pchanged = false;
            if (pendPointTexture != null)
            {
                pendPointTexture.Dispose();
                pendPointTexture = null;
            }
            if (plines != null)
            {
                plines.Clear();
            }
            if (ppoints != null)
            {
                ppoints.Clear();
            }
        }

        public ReadOnlyCollection<Vector2> Points
        {
            get
            {
                ReadOnlyCollection<Vector2> points = new ReadOnlyCollection<Vector2>(ppoints);
                return points;
            }
        }
        public Color Stroke { get; set; }
        public Microsoft.Xna.Framework.Rectangle BoundingRectangle { get; protected set; }
        public float StrokeWidth
        {
            get { return pstrokeWidth; }
            set { pstrokeWidth = value; pchanged = true; }
        }

        public XNAPolyline(GraphicsDevice device)
        {
            pdevice = device;
            pboundingBoxInitialized = false;
            plines = new List<XNALine>();
            ppoints = new List<Vector2>();
            pstrokeWidth = 1;
            Stroke = Color.White;
        }

        public bool Intersects(Vector2 point)
        {
            foreach (XNALine line in plines)
            {
                if (line.Intersects(point)) return true;
            }
            return false;
        }

        public XNALine IntersectingLine(Vector2 point)
        {
            foreach (XNALine line in plines)
            {
                if (line.Intersects(point)) return line;
            }
            return null;
        }

        public void ClearPoints()
        {
            ppoints.Clear();
        }

        public virtual void AddPoint(Vector2 point)
        {
            ppoints.Add(new Vector2(point.X, point.Y));
            if (!pboundingBoxInitialized)
            {
                pmax = point;
                pmin = point;
                pboundingBoxInitialized = true;
            }
            else
            {
                if (point.X < pmin.X) pmin.X = (int)point.X;
                if (point.Y < pmin.Y) pmin.Y = (int)point.Y;
                if (point.X > pmax.X) pmax.X = (int)point.X;
                if (point.Y > pmax.Y) pmax.Y = (int)point.Y;
                BoundingRectangle = new Microsoft.Xna.Framework.Rectangle((int)pmin.X, (int)pmin.Y, (int)pmax.X - (int)pmin.X + 1, (int)pmax.Y - (int)pmin.Y + 1);
            }
            pchanged = true;
        }

        public virtual void Update()
        {
            plines.Clear();
            for (int c = 0; c < ppoints.Count - 1; c++)
            {
                Vector2 currentPoint = ppoints[c];

                XNALine line = new XNALine
                {
                    Start = currentPoint,
                    End = ppoints[c + 1],
                    Stroke = Stroke,
                    StrokeWidth = StrokeWidth
                };
                plines.Add(line);
            }
            pendPointTexture = XNACircle.CreateTexture(pdevice, (int)MathUtils.Floor(pstrokeWidth));
            pchanged = false;
        }

        public virtual void Draw(SpriteBatch spriteBatch)
        {
            if (pchanged) Update();
            for (int c = 0; c < plines.Count; c++)
            {
                plines[c].Draw(spriteBatch);
                if (pendPointTexture == null) return;
                if (c < plines.Count - 1)
                {
                    float half = pendPointTexture.Width / 2f;
                    spriteBatch.Draw(pendPointTexture, plines[c].End - new Vector2(half, half), Stroke);
                }
            }
        }

        public int Count
        {
            get
            {
                return ppoints.Count;
            }
        }
    }

    public class XNAPolygon : XNAPolyline
    {
        private Brush pFill;

        protected Texture2D pFillTexture;

        protected Color[] pFillTextureData;

        private bool pclockwise;

        public XNAPolygon(GraphicsDevice device)
            : base(device)
        {
            Clockwise = true;
        }

        public Brush Fill
        {
            get { return pFill; }
            set
            {
                pchanged = true; pFill = value;
            }
        }
        public bool Clockwise
        {
            get { return pclockwise; }
            set
            {
                pclockwise = value;
                pchanged = true;
            }
        }

        public static Vector3 ToIntegerVector(Vector3 vector)
        {
            return new Vector3((int)vector.X, (int)vector.Y, (int)vector.Z);
        }

        public static float GetAngleBetween(Vector3 first, Vector3 second)
        {
            first.Normalize();
            second.Normalize();
            return MathUtils.Acos(Vector3.Dot(first, second));
        }

        public static float GetSignedAngleBetween(Vector3 first, Vector3 second, Vector3 right)
        {
            first.Normalize();
            second.Normalize();

            float angle1 = GetAngleBetween(Vector3.Down, first);
            if (first.X < 0) angle1 = MathHelper.ToRadians(360) - angle1;

            float angle2 = GetAngleBetween(Vector3.Down, second);
            if (second.X < 0) angle2 = MathHelper.ToRadians(360) - angle2;

            float output = angle2 - angle1;
            if (output > MathHelper.Pi)
            {
                output = output - MathHelper.TwoPi;
            }
            else if (output < -MathHelper.TwoPi)
            {
                output = MathHelper.TwoPi + output;
            }
            return output;
        }

        public override void Update()
        {
            plines.Clear();
            for (int c = 0; c < ppoints.Count; c++)
            {
                XNALine line = new XNALine
                {
                    Start = ppoints[c],
                    End = (c == ppoints.Count - 1 ? ppoints[0] : ppoints[c + 1]),
                    Stroke = Stroke,
                    StrokeWidth = StrokeWidth
                };
                plines.Add(line);
            }
            GetFillTexture();
            if (StrokeWidth > 0) pendPointTexture = XNACircle.CreateTexture(pdevice, (int)MathUtils.Floor(StrokeWidth));
            pchanged = false;
        }

        protected virtual void GetFillTexture()
        {
            XNAGradient gradient = pFill as XNAGradient;
            if (gradient == null) return;
            gradient.Dimensions = new Point(BoundingRectangle.Width, BoundingRectangle.Height);
            gradient.Update();
            pFillTexture = gradient.Texture;

            int height = pFillTexture.Height;
            int width = pFillTexture.Width;
            pFillTextureData = new Color[width * height];
            pFillTexture.GetData(pFillTextureData);

            for (int y = 0; y < height; y++)
            {
                XNALine lastLine = null;
                bool paint = false;
                bool paintNext = false;
                for (int x = 0; x < width; x++)
                {
                    Vector2 point = new Vector2((float)x + BoundingRectangle.X, (float)y + BoundingRectangle.Y);
                    XNALine line = IntersectingLine(point);
                    if (line != null)
                    {
                        if (line != lastLine) paintNext = !paint;
                        lastLine = line;
                    }
                    if (!paint) pFillTextureData[x + (y * width)].A = 0;
                    paint = paintNext;
                }
            }
            pFillTexture.SetData(pFillTextureData);
        }

        public virtual bool Contains(Vector2 point)
        {
            if (BoundingRectangle.Contains((int)point.X, (int)point.Y))
            {
                point = new Vector2(point.X - BoundingRectangle.X, point.Y - BoundingRectangle.Y);
                return pFillTextureData[(int)point.X + ((int)point.Y * pFillTexture.Width)].A > 0;
            }
            return false;
        }

        public override void Draw(SpriteBatch spriteBatch)
        {

            if (pchanged) Update();
            if (pFillTexture != null)
            {
                spriteBatch.Draw(pFillTexture, BoundingRectangle, Color.White);
            }
            for (int c = 0; c < plines.Count; c++)
            {
                plines[c].Draw(spriteBatch);
                if (StrokeWidth <= 0) continue;
                float half = pendPointTexture.Width / 2f;
                spriteBatch.Draw(pendPointTexture, plines[c].End - new Vector2(half, half), Stroke);
            }
        }

    }

    public abstract class XNAShape : XNAPolygon
    {
        private Vector2 pposition;

        public Vector2 Position { get { return pposition; } set { pposition = value; pchanged = true; } }

        protected XNAShape(GraphicsDevice device)
            : base(device)
        {

        }
    }

    public class XNACircle : XNAShape
    {

        private static Color TransparentWhite = new Color(255, 255, 255, 0);

        private float pradius;
        public float Radius { get { return pradius; } set { pradius = value; pchanged = true; } }

        public XNACircle(GraphicsDevice device)
            : base(device)
        {
        }

        private static Dictionary<int, Dictionary<float, Texture2D>> ptextures = new Dictionary<int, Dictionary<float, Texture2D>>();

        public static void Free()
        {
            if (ptextures != null)
            {
                ptextures.Clear();
            }

        }

        public static Texture2D CreateTexture(GraphicsDevice device, int width)
        {
            if (width <= 0) return null;
            if (ptextures.ContainsKey(width))
            {
                if (ptextures[width].ContainsKey(360f)) return ptextures[width][360f];
            }

            Texture2D texture = new Texture2D(device, width, width);
            float halfPoint = (float)width / 2;
            Vector2 centre = new Vector2(halfPoint, halfPoint);
            Color[] data = new Color[width * width];
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < width; y++)
                {

                    float lerp = MathHelper.Clamp(halfPoint - Vector2.Distance(centre, new Vector2(x, y)), 0, 1);
                    data[x + (y * width)] = Color.Lerp(TransparentWhite, Color.White, lerp);
                }
            }
            texture.SetData(data);

            if (!ptextures.ContainsKey(width))
            {
                ptextures.Add(width, new Dictionary<float, Texture2D>());
            }
            ptextures[width].Add(360f, texture);
            return texture;
        }
        protected override void GetFillTexture()
        {
            XNAGradient gradient = Fill as XNAGradient;
            if (gradient == null) return;
            gradient.Dimensions = new Point(BoundingRectangle.Width, BoundingRectangle.Height);
            gradient.Update();
            pFillTexture = gradient.Texture;

            int height = pFillTexture.Height;
            int width = pFillTexture.Width;

            float halfPoint = (width - 1f) / 2f;
            Vector2 centre = new Vector2(halfPoint, halfPoint);

            pFillTextureData = new Color[width * height];
            pFillTexture.GetData(pFillTextureData);
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    float lerp = MathHelper.Clamp(halfPoint - (Vector2.Distance(centre, new Vector2(x, y))) + 1, 0, 1);
                    pFillTextureData[x + (y * width)].A = (byte)(255f * lerp);
                }
            }
            pFillTexture.SetData(pFillTextureData);
        }
        public static Texture2D CreateTexture(GraphicsDevice device, int width, float startAngle, float stopAngle)
        {
            if (ptextures.ContainsKey(width))
            {
                if (ptextures[width].ContainsKey(stopAngle)) return ptextures[width][stopAngle];
            }

            startAngle = MathHelper.ToRadians(startAngle);
            float stopAngleRadians = MathHelper.ToRadians(stopAngle);
            Texture2D texture = new Texture2D(device, width, width);
            float halfPoint = width / 2f;
            Vector2 centre = new Vector2(halfPoint, halfPoint);
            Vector2 topVector = new Vector2(halfPoint, 0) - centre;
            topVector.Normalize();
            Color[] data = new Color[width * width];
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < width; y++)
                {
                    Vector2 currentVector = new Vector2(x, y) - centre;
                    currentVector.Normalize();

                    float angle = MathUtils.Acos(Vector2.Dot(currentVector, topVector));
                    if (x < halfPoint) angle = MathHelper.ToRadians(360f) - angle;
                    float lerp;
                    if (angle > startAngle && angle < stopAngleRadians)
                    {
                        lerp = 0;
                    }
                    else
                    {
                        lerp = MathHelper.Clamp(halfPoint - Vector2.Distance(centre, new Vector2(x, y)), 0, 1);
                    }
                    data[x + (y * width)] = Color.Lerp(TransparentWhite, Color.White, lerp);
                }
            }
            texture.SetData(data);

            if (!ptextures.ContainsKey(width))
            {
                ptextures.Add(width, new Dictionary<float, Texture2D>());
            }
            ptextures[width].Add(stopAngle, texture);

            return texture;
        }
        public override void Update()
        {
            const float steps = 60f;
            float thetaStep = MathHelper.ToRadians(360f / steps);
            float theta = 0;

            ClearPoints();

            for (int c = 0; c < steps; c++)
            {
                float x = pradius * MathUtils.Cos(theta) + Position.X;
                float y = pradius * MathUtils.Sin(theta) + Position.Y;
                AddPoint(new Vector2(x, y));
                theta += thetaStep;
            }

            base.Update();
        }
    }

    public class XNAEllipse : XNAShape
    {
        private float phRadius;
        private float pvRadius;
        public float HorizontalRadius { get { return phRadius; } set { phRadius = value; pchanged = true; } }
        public float VerticalRadius { get { return pvRadius; } set { pvRadius = value; pchanged = true; } }

        public XNAEllipse(GraphicsDevice device)
            : base(device)
        {
        }

        public override void Update()
        {
            const int steps = 60;
            float thetaStep = MathHelper.ToRadians(360f / steps);
            float theta = 0;

            for (int c = 0; c < steps; c++)
            {
                float x = phRadius * MathUtils.Cos(theta) + Position.X;
                float y = pvRadius * MathUtils.Sin(theta) + Position.Y;
                AddPoint(new Vector2(x, y));
                theta += thetaStep;
            }

            base.Update();
        }
    }

    public class XNALinearGradient : XNAGradient
    {
        private Vector2 start;
        private Vector2 end;

        public Vector2 Start
        {
            get { return start; }
            set { changed = true; start = value; }
        }
        public Vector2 End
        {
            get { return end; }
            set { changed = true; end = value; }
        }

        public XNALinearGradient(GraphicsDevice device)
            : base(device)
        {
        }
        public override void Update()
        {
            texture = new Texture2D(pdevice, dimensions.X, dimensions.Y);
            Color[] data = new Color[texture.Height * texture.Width];

            Vector2 gradientVector = End - Start;

            for (int x = 0; x < texture.Width; x++)
            {
                for (int y = 0; y < texture.Height; y++)
                {
                    Vector2 currentPoint = new Vector2((float)(x + 1) / texture.Width, (float)(y + 1) / texture.Height);

                    Vector2 currentVector = currentPoint - start;
                    Vector2 intersect = (gradientVector * (Vector2.Dot(gradientVector, currentVector) / gradientVector.LengthSquared()));

                    float lerp = intersect.Length() / gradientVector.Length();
                    lerp = MathHelper.Clamp(lerp, 0, 1);

                    data[x + (y * texture.Width)] = GetColour(lerp);
                }
            }
            texture.SetData<Color>(data);
            changed = false;
        }
    }

    public class XNAPath : IGLType
    {
        private List<XNAPolyline> lines;
        private GraphicsDevice pdevice;
        private Vector2 lastPosition;

        public bool Closed { get; set; }
        public Color Stroke { get; set; }
        public float StrokeWidth { get; set; }

        public XNAPath(GraphicsDevice device)
        {
            pdevice = device;
            lines = new List<XNAPolyline>();
        }

        public void MoveTo(Vector2 pos)
        {
            XNAPolyline line = new XNAPolyline(pdevice);
            line.Stroke = Stroke;
            line.StrokeWidth = StrokeWidth;
            line.AddPoint(pos);
            lines.Add(line);
            lastPosition = pos;
        }
        public void LineTo(Vector2 pos)
        {
            if (lines.Count == 0) return;
            lines[lines.Count - 1].AddPoint(pos);
            lastPosition = pos;
        }
        public bool Intersects(Vector2 point)
        {
            foreach (XNAPolyline line in lines)
            {
                if (line.Intersects(point)) return true;
            }
            return false;
        }
        public void Update()
        {
            foreach (XNAPolyline line in lines)
            {
                line.Update();
            }
        }
        public void Clear()
        {
            lines.Clear();
        }

        private const int steps = 20;

        public void QuadraticBezier(Vector2 control, Vector2 pos)
        {
            QuadraticBezier(new List<Vector2> { control }, pos);
        }
        public void QuadraticBezier(List<Vector2> controlPoints, Vector2 endPoint)
        {
            for (int step = 1; step <= steps; step++)
            {
                List<Vector2> points = new List<Vector2>();
                points.Add(lastPosition);
                points.AddRange(controlPoints);
                points.Add(endPoint);

                while (points.Count > 1)
                {
                    List<Vector2> newPoints = new List<Vector2>();
                    for (int c = 1; c < points.Count; c++)
                    {
                        Vector2 line = points[c] - points[c - 1];
                        line.Normalize();
                        float distance = Vector2.Distance(points[c - 1], points[c]);
                        float stepDistance = distance / steps;

                        newPoints.Add(points[c - 1] + (line * stepDistance * step));
                    }
                    points = newPoints;
                }
                lines[lines.Count - 1].AddPoint(points[0]);
            }
            lastPosition = endPoint;
        }

        public XNAPolygon ToPolygon()
        {
            XNAPolygon polygon = new XNAPolygon(pdevice)
            {
                StrokeWidth = StrokeWidth,
                Stroke = Stroke
            };
            foreach (var line in lines)
            {
                foreach (var point in line.Points)
                {
                    polygon.AddPoint(point);
                }
            }
            return polygon;
        }
        public void Draw(SpriteBatch spriteBatch)
        {
            foreach (XNAPolyline line in lines)
            {
                line.Draw(spriteBatch);
            }
        }
    }

    public class XNARadialGradient : XNAGradient
    {
        private Vector2 centre;
        private Vector2 focus;
        private float radius;

        public Vector2 Centre
        {
            get { return centre; }
            set { changed = true; centre = value; }
        }
        public Vector2 Focus
        {
            get { return focus; }
            set { changed = true; focus = value; }
        }
        public float Radius
        {
            get { return radius; }
            set { changed = true; radius = value; }
        }

        public XNARadialGradient(GraphicsDevice device)
            : base(device)
        {
        }

        public override void Update()
        {
            texture = new Texture2D(pdevice, dimensions.X, dimensions.Y);
            int width = texture.Width;
            int height = texture.Height;
            Color[] data = new Color[width * height];

            Vector2 centreToFocus = focus - centre;
            float c = centreToFocus.LengthSquared() - (radius * radius);

            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    Vector2 currentPoint = new Vector2((x + 1) / width, (y + 1) / height);
                    float lerp;
                    if (centre == focus)
                    {
                        lerp = Vector2.Distance(centre, currentPoint) / radius;
                    }
                    else
                    {
                        Vector2 centreToPoint = currentPoint - centre;
                        if (centreToPoint.Length() < radius)
                        {
                            Vector2 focusToPoint = currentPoint - focus;
                            float a = focusToPoint.LengthSquared();
                            float b = 2 * Vector2.Dot(centreToFocus, focusToPoint);
                            lerp = (-b + MathUtils.Sqrt((b * b) - 4 * a * c)) / (2 * a);
                            lerp = 1 / lerp;
                        }
                        else
                        {
                            lerp = 1;
                        }
                    }
                    lerp = MathHelper.Clamp(lerp, 0, 1);
                    data[x + (y * width)] = GetColour(lerp);
                }
            }
            texture.SetData<Color>(data);
            changed = false;
        }
    }

    public class XNARectangle : XNAShape
    {
        private float pwidth;
        private float pheight;
        public float Width { get { return pwidth; } set { pwidth = value; pchanged = true; } }
        public float Height { get { return pheight; } set { pheight = value; pchanged = true; } }

        public XNARectangle(GraphicsDevice device)
            : base(device)
        {
        }

        public override void Update()
        {
            AddPoint(Position);
            AddPoint(new Vector2(Position.X + Width, Position.Y));
            AddPoint(new Vector2(Position.X + Width, Position.Y + Height));
            AddPoint(new Vector2(Position.X, Position.Y + Height));

            base.Update();
        }
    }
}
