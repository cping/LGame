using loon.geom;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.Graphics;

namespace loon.monogame
{
    public class MonoGameShader
    {
        private Vector2 tmpVector2 = new Vector2();
        private Vector3 tmpVector3 = new Vector3();
        private Vector4 tmpVector4 = new Vector4();
        private Matrix tmpMatrix = new Matrix();

        public Effect shader;

        public MonoGameShader(Loon game,string name)
        {
            if (!name.EndsWith(".fx"))
            {
                name += ".fx";
            }
            ContentManager contentManager = game.GetContentManager();
            shader = contentManager.Load<Effect>(name);
            shader.CurrentTechnique = shader.Techniques[0];
        }

        public MonoGameShader(Effect effect)
        {
            shader = effect;
            if (shader != null)
            {
                shader.CurrentTechnique = shader.Techniques[0];
            }
        }

        public void Dispose()
        {
            shader.Dispose();
        }

        public bool HasParameter(string key)
        {
            try
            {
                return shader.Parameters[key] != null;
            }
            catch
            {
            }
            return false;
        }

        public void SetParameter(string key, Texture2D t)
        {
            shader.Parameters[key].SetValue(t);
        }

        public void SetParameterf(string key, float f)
        {
            shader.Parameters[key].SetValue(f);
        }

        public void SetParameterf(string key, float f1, float f2)
        {
            tmpVector2.X = f1;
            tmpVector2.Y = f2;
            shader.Parameters[key].SetValue(tmpVector2);
        }

        public void SetParameterf(string key, float f1, float f2, float f3)
        {
            tmpVector3.X = f1;
            tmpVector3.Y = f2;
            tmpVector3.Z = f3;
            shader.Parameters[key].SetValue(tmpVector3);
        }

        public void SetParameterf(string key, float f1, float f2, float f3, float f4)
        {
            tmpVector4.X = f1;
            tmpVector4.Y = f2;
            tmpVector4.Z = f3;
            tmpVector4.W = f4;
            shader.Parameters[key].SetValue(tmpVector4);
        }

        public void SetParameterf(string key, Vector2f v)
        {
            tmpVector2.X = v.x;
            tmpVector2.Y = v.y;
            shader.Parameters[key].SetValue(tmpVector2);
        }

        public void SetParameterf(string key, Vector3f v)
        {
            tmpVector3.X = v.x;
            tmpVector3.Y = v.y;
            tmpVector3.Z = v.z;
            shader.Parameters[key].SetValue(tmpVector3);
        }

        public void SetParameteri(string key, int i)
        {
            shader.Parameters[key].SetValue(i);
        }

        public void SetParameterMatrix(string key, Matrix4 m)
        {
            SetParameterMatrix(key, m, false);
        }

        public void SetParameterMatrix(string key, Matrix4 m, bool b)
        {
            m.SetMatrix(this.tmpMatrix);
            if (b)
            {
                shader.Parameters[key].SetValueTranspose(tmpMatrix);
            }
            else
            {
                shader.Parameters[key].SetValue(tmpMatrix);
            }
        }

        public Effect GetShader()
        {
            return this.shader;
        }

        public bool IsCompiled()
        {
            return true;
        }

    }
}
