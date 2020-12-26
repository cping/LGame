
using loon.geom;
using loon.opengl;
using Microsoft.Xna.Framework.Graphics;

namespace loon.monogame
{
    public class MonoGameGraphics : Graphics
    {
        protected internal MonoGameGraphics(LGame game, GraphicsDevice d, int w, int h) : base(game, new GL20(d, w, h), loon.utils.Scale.ONE)
        {
            screenSize.width = scale.InvScaled(w);
            screenSize.height = scale.InvScaled(h);
        }

        protected internal void OnSizeChanged(int viewWidth, int viewHeight)
        {
            if (!IsAllowResize(viewWidth, viewHeight))
            {
                return;
            }
            screenSize.width = viewWidth / scale.factor;
            screenSize.height = viewHeight / scale.factor;
            game.Log().Info("Updating size " + viewWidth + "x" + viewHeight + " / " + scale.factor + " -> " + screenSize);
            ViewportChanged(scale, viewWidth, viewHeight);
        }

        private readonly Dimension screenSize = new Dimension();
        public override Dimension ScreenSize()
        {
            return screenSize;
        }
    }
}
