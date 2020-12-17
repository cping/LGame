using loon.geom;
using loon.utils;
using loon.utils.reply;

namespace loon
{
    public abstract class Graphics
    {

        protected internal readonly LGame game;
        protected internal readonly Dimension viewSizeM = new Dimension();

        protected internal Scale scale_Conflict = null;
        protected internal int viewPixelWidth, viewPixelHeight;

        private Display display = null;
        private Affine2f affine = null, lastAffine = null;
        private Matrix4 viewMatrix = null;
        private Array<Matrix4> matrixsStack = new Array<Matrix4>();

        private LTexture colorTex;

        private class DisposePort : UnitPort
        {

            internal readonly LRelease _release;

            internal DisposePort(LRelease r)
            {
                this._release = r;
            }


            public override void OnEmit()
            {
                _release.Close();
            }

        }

        public void QueueForDispose(LRelease resource)
        {
            game.frame.Connect(new DisposePort(resource)).Once();
        }

    }
}
