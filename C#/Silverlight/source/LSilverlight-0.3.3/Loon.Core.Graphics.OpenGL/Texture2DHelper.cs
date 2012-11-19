namespace Loon.Core.Graphics.OpenGL
{

    using System;
    using System.Collections.Generic;
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework;
    using Loon.Utils;

    public static class Texture2DHelper
    {

        public static void DrawArc(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 center, float radius, int sides, float startingAngle, float degrees, Color color)
        {
            DrawArc(spriteBatch, brush, center, radius, sides, startingAngle, degrees, color, 1f);
        }

        public static void DrawArc(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 center, float radius, int sides, float startingAngle, float degrees, Color color, float thickness)
        {
            List<Vector2> points = CreateArc(radius, sides, startingAngle, degrees);
            DrawPoints(spriteBatch, brush, center, points, color, thickness);
        }

        public static void DrawCircle(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 center, float radius, int sides, Color color)
        {
            DrawPoints(spriteBatch, brush, center, CreateCircle(radius, sides), color, 1f);
        }

        public static void DrawCircle(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 center, float radius, int sides, Color color, float thickness)
        {
            DrawPoints(spriteBatch, brush, center, CreateCircle(radius, sides), color, thickness);
        }

        public static void DrawLine(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 point1, Vector2 point2, Color color)
        {
            DrawLine(spriteBatch, brush, point1, point2, color, 1f);
        }

        public static void DrawLine(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 point1, Vector2 point2, Color color, float thickness)
        {
            float length = Vector2.Distance(point1, point2);
            float angle = MathUtils.Atan2((point2.Y - point1.Y), point2.X - point1.X);
            DrawLine(spriteBatch, brush, point1, length, angle, color, thickness);
        }

        public static void DrawLine(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 point, float length, float angle, Color color)
        {
            DrawLine(spriteBatch, brush, point, length, angle, color, 1f);
        }

        public static void DrawLine(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 point, float length, float angle, Color color, float thickness)
        {
            spriteBatch.Draw(brush, point, null, color, angle, Vector2.Zero, new Vector2(length, thickness), 0, 0f);
        }

        public static void DrawLine(this SpriteBatch spriteBatch, Texture2D brush,
            float x1, float y1, float x2, float y2, Color color)
        {
            DrawLine(spriteBatch, brush, new Vector2(x1, y1), new Vector2(x2, y2), color, 1f);
        }

        public static void DrawLine(this SpriteBatch spriteBatch, Texture2D brush,
            float x1, float y1, float x2, float y2, Color color, float thickness)
        {
            DrawLine(spriteBatch, brush, new Vector2(x1, y1), new Vector2(x2, y2), color, thickness);
        }

        public static void DrawRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Rectangle rect, Color color)
        {
            DrawRectangle(spriteBatch, brush, rect, color, 1f, 0f, new Vector2((float)rect.X, (float)rect.Y));
        }

        public static void DrawRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Rectangle rect, Color color, float thickness)
        {
            DrawRectangle(spriteBatch, brush, rect, color, thickness, 0f, new Vector2((float)rect.X, (float)rect.Y));
        }

        public static void DrawRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 location, Vector2 size, Color color)
        {
            DrawRectangle(spriteBatch, brush, new Rectangle((int)location.X, (int)location.Y, (int)size.X, (int)size.Y), color, 1f, 0f, location);
        }

        public static void DrawRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Rectangle rect, Color color, float thickness, float angle)
        {
            DrawRectangle(spriteBatch, brush, rect, color, thickness, angle, new Vector2((float)rect.X, (float)rect.Y));
        }

        public static void DrawRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 location, Vector2 size, Color color, float thickness)
        {
            DrawRectangle(spriteBatch, brush, new Rectangle((int)location.X, (int)location.Y, (int)size.X, (int)size.Y), color, thickness, 0f, location);
        }

        public static void DrawRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Rectangle rect, Color color, float thickness, float angle, Vector2 rotateAround)
        {
            DrawLine(spriteBatch, brush, new Vector2((float)rect.X, (float)rect.Y), new Vector2((float)rect.Right, (float)rect.Y), color, thickness);
            DrawLine(spriteBatch, brush, new Vector2(rect.X + 1f, (float)rect.Y), new Vector2(rect.X + 1f, rect.Bottom + 1f), color, thickness);
            DrawLine(spriteBatch, brush, new Vector2((float)rect.X, (float)rect.Bottom), new Vector2((float)rect.Right, (float)rect.Bottom), color, thickness);
            DrawLine(spriteBatch, brush, new Vector2(rect.Right + 1f, (float)rect.Y), new Vector2(rect.Right + 1f, rect.Bottom + 1f), color, thickness);
        }

        public static void DrawRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 location, Vector2 size, Color color, float thickness, float angle)
        {
            DrawRectangle(spriteBatch, brush, new Rectangle((int)location.X, (int)location.Y, (int)size.X, (int)size.Y), color, thickness, angle, location);
        }

        public static void DrawRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 location, Vector2 size, Color color, float thickness, float angle, Vector2 rotateAround)
        {
            DrawRectangle(spriteBatch, brush, new Rectangle((int)location.X, (int)location.Y, (int)size.X, (int)size.Y), color, thickness, angle, rotateAround);
        }

        public static void FillRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Rectangle rect, Color color)
        {
            spriteBatch.Draw(brush, rect, color);
        }

        public static void FillRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Rectangle rect, Color color, float angle)
        {
            spriteBatch.Draw(brush, rect, null, color, angle, Vector2.Zero, 0, 0f);
        }

        public static void FillRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 location, Vector2 size, Color color)
        {
            FillRectangle(spriteBatch, brush, location, size, color, 0f);
        }

        public static void FillRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 location, Vector2 size, Color color, float angle)
        {
            spriteBatch.Draw(brush, location, null, color, angle, Vector2.Zero, size, 0, 0f);
        }

        public static void FillRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            float x1, float y1, float x2, float y2, Color color)
        {
            FillRectangle(spriteBatch, brush, new Vector2(x1, y1), new Vector2(x2, y2), color, 1f);
        }

        public static void FillRectangle(this SpriteBatch spriteBatch, Texture2D brush,
            float x1, float y1, float x2, float y2, Color color, float thickness)
        {
            FillRectangle(spriteBatch, brush, new Vector2(x1, y1), new Vector2(x2, y2), color, thickness);
        }

        public static void DrawPoint(this SpriteBatch spriteBatch, Texture2D brush,
            Vector2 position, Color color)
        {
            spriteBatch.Draw(brush, position, color);
        }

        public static void DrawPoint(this SpriteBatch spriteBatch, Texture2D brush,
            float x, float y, Color color)
        {
            DrawPoint(spriteBatch, brush, new Vector2(x, y), color);
        }

        private static readonly Dictionary<int, List<Vector2>> circleCache = new Dictionary<int, List<Vector2>>();

        private static List<Vector2> CreateArc(float radius, int sides, float startingAngle, float degrees)
        {
            List<Vector2> list = new List<Vector2>();
            list.AddRange(CreateCircle(radius, sides));
            list.RemoveAt(list.Count - 1);
            double num = 0.0;
            double num2 = 360.0 / ((double)sides);
            while ((num + (num2 / 2.0)) < startingAngle)
            {
                num += num2;
                list.Add(list[0]);
                list.RemoveAt(0);
            }
            list.Add(list[0]);
            int num3 = (int)((((double)degrees) / num2) + 0.5);
            list.RemoveRange(num3 + 1, (list.Count - num3) - 1);
            return list;
        }

        public static List<Vector2> CreateCircle(float radius, int sides)
        {
            int key = 1;
            key = LSystem.Unite(key, radius);
            key = LSystem.Unite(key, sides);
            List<Vector2> list = (List<Vector2>)CollectionUtils.Get(circleCache, key);
            if (list != null)
            {
                return list;
            }
            list = new List<Vector2>();
            float num = MathUtils.PI * 2 / sides;
            for (float i = 0f; i < MathUtils.PI * 2; i += num)
            {
                list.Add(new Vector2((radius * MathUtils.Cos(i)), (radius * MathUtils.Sin(i))));
            }
            list.Add(new Vector2((radius * MathUtils.Cos(0f)), (radius * MathUtils.Sin(0f))));
            circleCache.Add(key, list);
            return list;
        }

        public static void DrawPoints(SpriteBatch spriteBatch, Texture2D brush,
            Vector2 position, List<Vector2> points, Color color)
        {
            DrawPoints(spriteBatch, brush, position, points, color, 1f);
        }

        public static void DrawPoints(SpriteBatch spriteBatch, Texture2D brush,
            Vector2 position, List<Vector2> points, Color color, float thickness)
        {
            if (points.Count >= 2)
            {
                for (int i = 1; i < points.Count; i++)
                {
                    DrawLine(spriteBatch, brush, points[i - 1] + position, points[i] + position, color, thickness);
                }
            }
        }

    }
}
