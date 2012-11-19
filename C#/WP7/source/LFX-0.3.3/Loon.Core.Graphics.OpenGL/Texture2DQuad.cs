using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Loon.Java;
using Loon.Java.Collections;
using Loon.Utils;

namespace Loon.Core.Graphics.OpenGL
{
    public delegate T InterpolationEquation<T>(T startValue, T endValue, T currentValue, float currentTime, float endTime, ref bool valid);

    public interface IInterpolator
    {
        void Update(GameTime gameTime);
        bool IsValid();
    }

    public static class Interpolator
    {
        private static List<IInterpolator> pinterpolators = new List<IInterpolator>();

        public static void Update(GameTime gameTime)
        {
            List<IInterpolator> list = new List<IInterpolator>();
            for (int c = 0; c < pinterpolators.Count; c++)
            {
                if (pinterpolators[c].IsValid())
                {
                    list.Add(pinterpolators[c]);
                    pinterpolators[c].Update(gameTime);
                }
            }
            pinterpolators = list;
        }

        public static void Add(IInterpolator interpolator)
        {
            pinterpolators.Add(interpolator);
        }

        public static Vector3 Linear(Vector3 start, Vector3 end, Vector3 current, float time, float duration, ref bool valid)
        {
            valid = (current != end);
            return Vector3.Lerp(start, end, time / duration);
        }

        public static float Linear(float start, float end, float current, float time, float duration, ref bool valid)
        {
            float retVal = current + (end - start) / duration;
            valid = false;
            if (start < end) { if (current < end) valid = true; } else { if (current > end) valid = true; }
            return retVal;
        }

        public static Color Linear(Color start, Color end, Color current, float time, float duration, ref bool valid)
        {
            valid = (time != duration);
            return Color.Lerp(start, end, time / duration);
        }

        public static IInterpolator GetUnusedInstance<T>()
        {
            foreach (IInterpolator i in pinterpolators)
            {
                if (!i.IsValid() && i.GetType() == typeof(Interpolator<T>))
                {
                    return i;
                }
            }
            return null;
        }
    }

    public class Interpolator<T> : IInterpolator
    {
        private bool pvalid;
        private InterpolationEquation<T> pequation;
        private Action<Interpolator<T>> pvalueChanged;
        private Action<Interpolator<T>> pcompletedOrStopped;
        private float ptimer;

        public T StartValue { get; private set; }
        public T EndValue { get; private set; }
        public T Value { get; private set; }
        public T Speed { get; private set; }
        public float Length { get; private set; }
        public object Tag { get; set; }

        private Interpolator()
        {
            pvalid = true;
            ptimer = 0f;
        }

        public void Stop()
        {
            pvalid = false;

            if (pcompletedOrStopped != null)
                pcompletedOrStopped(this);
        }

        public void Update(GameTime gameTime)
        {
            if (!pvalid)
                return;
            ptimer += (float)gameTime.ElapsedGameTime.TotalSeconds;
            Value = pequation(StartValue, EndValue, Value, ptimer, Length, ref pvalid);
            if (ptimer >= Length)
                pvalid = false;
            if (!pvalid)
                Value = EndValue;
            if (pvalueChanged != null)
                pvalueChanged(this);
            if (!pvalid && pcompletedOrStopped != null)
                pcompletedOrStopped(this);
        }

        public bool IsValid()
        {
            return pvalid;
        }

        public static Interpolator<T> Create(
            T startValue,
            T endValue,
            float length,
            InterpolationEquation<T> equation,
            Action<Interpolator<T>> valueChanged,
            Action<Interpolator<T>> completedOrStopped)
        {
            Interpolator<T> i = Interpolator.GetUnusedInstance<T>() as Interpolator<T>;
            if (i == null) i = new Interpolator<T>();
            i.StartValue = startValue;
            i.EndValue = endValue;
            i.Length = length;
            i.pequation = equation;
            i.pvalueChanged = valueChanged;
            i.pcompletedOrStopped = completedOrStopped;
            i.Value = startValue;
            Interpolator.Add(i);
            return i;
        }
    }

    public class Light
    {
        public float Intensity { get; set; }
        public Vector3 Position { get; set; }
        public Color Colour { get; set; }
        public float Distance { get; set; }

        public Light(float intensity, Vector3 position, Color colour, float distance)
        {
            Intensity = intensity;
            Position = position;
            Colour = colour;
            Distance = distance;
        }
    }

    public class Quad
    {
        public static int QuadComparer(Quad x, Quad y)
        {
            return (x.plowerLeft.Y.CompareTo(y.plowerLeft.Y));
        }

        private Vector3 pupperLeft;
        private Vector3 plowerLeft;
        private Vector3 pupperRight;
        private Vector3 plowerRight;
        private Vector3 pnormal;

        private BoundingBox pboundingBox;
        private Plane pplane;
        private VertexPositionNormalTexture[] pvertices;
        private int[] pindexes;
        private Color[] pcolourInfo;
        private Interpolator<Color> colourInterpolator;


        public Color Colour { get; private set; }
        public bool Active { get; set; }
        public Texture2D Texture { get; private set; }
        public Effect Effect { get; set; }
        public GraphicsDevice Device { get; set; }

        public Quad(GraphicsDevice device, Effect effect, Texture2D texture, Vector3 normal, float width, Vector3 start, Vector3 end)
            : this(device, effect, texture)
        {
            pnormal = normal;
            SetDimensions(normal, width, start, end);
        }

        public Quad(GraphicsDevice device, Effect effect, Texture2D texture, Vector3 normal, Vector3 origin, Vector3 up, float width, float height)
            : this(device, effect, texture)
        {
            pnormal = normal;
            SetDimensions(normal, origin, up, width, height);
        }

        public Quad(GraphicsDevice device, Effect effect, Texture2D texture)
            : this()
        {
            Effect = effect;
            Texture = texture;
            Device = device;
        }

        public void SetTexture(Texture2D texture)
        {
            Active = true;
            Texture = texture;
            Colour = Color.White;
            pcolourInfo = new Color[Texture.Width * Texture.Height];
            Texture.GetData<Color>(pcolourInfo);
        }

        public Quad()
        {
            pvertices = new VertexPositionNormalTexture[4];
            pindexes = new int[6];
        }

        public void SetDimensions(Vector3 normal, Vector3 centre, Vector3 up, float width, float height)
        {
            Vector3 left = Vector3.Cross(normal, up);
            Vector3 uppercenter = (up * height / 2) + centre;
            pupperLeft = uppercenter + (left * width / 2);
            pupperRight = uppercenter - (left * width / 2);
            plowerLeft = pupperLeft - (up * height);
            plowerRight = pupperRight - (up * height);
            pnormal = normal;

            pboundingBox = new BoundingBox(pupperLeft, plowerRight);
            pplane = new Plane(pupperLeft, pupperRight, plowerLeft);
            FillVertices();
        }

        public void SetDimensions(Vector3 normal, float width, Vector3 start, Vector3 end)
        {
            Vector3 direction = Vector3.Normalize(end - start);
            float angle = MathUtils.Acos(Vector3.Dot(Vector3.Backward, direction));
            if (direction.X < 0) angle = MathHelper.ToRadians(360) - angle;
            float length = Vector3.Distance(start, end);
            Vector3 centre = (start + end) / 2;

            SetDimensions(normal, centre, Vector3.Backward, width, length);
            Rotate(angle);
        }

        public void Rotate(float angle)
        {
            Rotate(angle, Vector3.Zero);
        }

        private void Rotate(float angle, Vector3 origin)
        {
            Vector3 centre = (pupperRight + pupperLeft + plowerRight + plowerLeft) / 4;
            Matrix matrix = Matrix.CreateRotationY(angle) * Matrix.CreateTranslation(centre + origin);

            pupperLeft = Vector3.Transform(pupperLeft - centre - origin, matrix);
            pupperRight = Vector3.Transform(pupperRight - centre - origin, matrix);
            plowerRight = Vector3.Transform(plowerRight - centre - origin, matrix);
            plowerLeft = Vector3.Transform(plowerLeft - centre - origin, matrix);
            FillVertices();
        }

        public void SetColour(Color colour)
        {
            if (Colour == colour) return;

            if (colourInterpolator != null)
                colourInterpolator.Stop();
            colourInterpolator = Interpolator<Color>.Create(
                Colour,
                colour,
                0.5f,
                Interpolator.Linear,
                i => Colour = i.Value,
                i => colourInterpolator = null);
        }

        public bool Intersects(Ray ray)
        {
            if (ray.Intersects(pboundingBox) != null)
            {
                Vector3 hitPoint = (ray.Position + Vector3.Multiply(ray.Direction, (float)ray.Intersects(pplane))) - pboundingBox.Min;
                return pcolourInfo[Convert.ToInt32(MathUtils.Floor(hitPoint.X)) + (Convert.ToInt32(MathUtils.Floor(hitPoint.Z)) * Texture.Width)].A > 0;
            }
            return false;
        }

        public void Draw(Matrix view, Matrix projection, Light light)
        {
            GLEx.Device.SamplerStates[0] = SamplerState.AnisotropicWrap;
            GLEx.Device.DepthStencilState = DepthStencilState.Default;

            if (Effect is BasicEffect)
            {
                BasicEffect basicEffect = (BasicEffect)Effect;
                basicEffect.Alpha = 1.0f;
                basicEffect.AmbientLightColor = Colour.ToVector3();
                basicEffect.TextureEnabled = true;
                basicEffect.Texture = Texture;
                basicEffect.World = Matrix.Identity;
                basicEffect.View = view;
                basicEffect.Projection = projection;
            }
            else
            {
                Effect.Parameters["WorldViewProjection"].SetValue(view * projection * Matrix.Identity);
                Effect.Parameters["World"].SetValue(Matrix.Identity);
                Effect.Parameters["TargetColor"].SetValue(Colour.ToVector3());
                Effect.Parameters["LightPosition"].SetValue(light.Position);
                Effect.Parameters["LightIntensity"].SetValue(light.Intensity);
                Effect.Parameters["DiffuseTexture"].SetValue(Texture);
                Effect.Parameters["Ambient"].SetValue(.1f);
                Effect.Parameters["MaxLightDistance"].SetValue(light.Distance);
            }
   
            foreach (EffectPass pass in Effect.CurrentTechnique.Passes)
            {
                pass.Apply();
                Device.DrawUserIndexedPrimitives<VertexPositionNormalTexture>(
                    PrimitiveType.TriangleList, pvertices, 0, 4, pindexes, 0, 2);
            }
  
        }

        private void FillVertices()
        {
            Vector2 textureUpperLeft = new Vector2(0.0f, 0.0f);
            Vector2 textureUpperRight = new Vector2(1.0f, 0.0f);
            Vector2 textureLowerLeft = new Vector2(0.0f, 1.0f);
            Vector2 textureLowerRight = new Vector2(1.0f, 1.0f);
            for (int i = 0; i < pvertices.Length; i++)
            {
                pvertices[i].Normal = pnormal;
            }
            pvertices[0].Position = plowerLeft;
            pvertices[0].TextureCoordinate = textureLowerLeft;
            pvertices[1].Position = pupperLeft;
            pvertices[1].TextureCoordinate = textureUpperLeft;
            pvertices[2].Position = plowerRight;
            pvertices[2].TextureCoordinate = textureLowerRight;
            pvertices[3].Position = pupperRight;
            pvertices[3].TextureCoordinate = textureUpperRight;
            pindexes[0] = 0;
            pindexes[1] = 1;
            pindexes[2] = 2;
            pindexes[3] = 2;
            pindexes[4] = 1;
            pindexes[5] = 3;
        }

    }

    public class GameGraph : Singleton<GameGraph>
    {
        public Pool<Quad> Quads { get; set; }
        public GraphicsDevice GraphicsDevice { get; set; }
        public Light Light { get; set; }

        public GameGraph()
        {
            Quads = new Pool<Quad>(1000, 0, i => i.Active)
            {
                Initialize = i =>
                {
                    i.Active = true;
                    i.Device = GraphicsDevice;
                    i.Effect = null;
                },
                Uninitialize = i =>
                {
                    i.Active = true;
                    i.Device = null;
                    i.Effect = null;
                }
            };
        }

        public Quad CreateQuad(Effect effect, Texture2D texture)
        {
            Quad quad = Quads.New();
            quad.Effect = effect;
            quad.SetTexture(texture);
            Quads.Sort(Quad.QuadComparer);
            return quad;
        }

        public Quad CreateQuad(Effect effect)
        {
            Quad quad = Quads.New();
            quad.Effect = effect;
            Quads.Sort(Quad.QuadComparer);
            return quad;
        }

        public void Draw(Matrix view, Matrix projection)
        {
            foreach (Quad quad in Quads)
            {
                if (quad.Active)
                {
                    quad.Draw(view, projection, Light);
                }
            }
            Quads.CleanUp();
        }

    }
}
