using loon.canvas;
using loon.geom;

namespace loon.opengl
{
    public class TrilateralBatch : BaseBatch
    {

        public TrilateralBatch(GL20 g):base(g)
        {

        }

        public override void AddQuad(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float x1, float y1, float sx1, float sy1, float x2, float y2, float sx2, float sy2, float x3, float y3, float sx3, float sy3, float x4, float y4, float sx4, float sy4)
        {
            throw new System.NotImplementedException();
        }
    }

}
