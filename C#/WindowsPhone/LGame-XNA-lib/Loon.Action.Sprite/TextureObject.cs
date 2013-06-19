using Loon.Core.Graphics.Opengl;
using Loon.Action.Sprite;
namespace Loon.Action.Sprite
{
    public class TextureObject : SimpleObject
    {
        public TextureObject(float x, float y,
        LTexture tex2d):base(x, y, tex2d.GetWidth(), tex2d.GetHeight(), Animation.GetDefaultAnimation(tex2d), null)
        {
            
        }

        public TextureObject(float x, float y,
                string file):  base(x, y, Animation.GetDefaultAnimation(file), null)
        {
          
        }
    }
}
