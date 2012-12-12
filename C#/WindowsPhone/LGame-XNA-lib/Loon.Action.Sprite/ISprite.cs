namespace Loon.Action.Sprite
{
    using Loon.Core;
    using Loon.Core.Graphics.Opengl;
    using Loon.Core.Geom;

    public interface ISprite : LRelease
    {
        int GetWidth();

        int GetHeight();

        float GetAlpha();

        int X();

        int Y();

        float GetX();

        float GetY();

        void SetVisible(bool visible);

        bool IsVisible();

        void CreateUI(GLEx g);

        void Update(long elapsedTime);

        int GetLayer();

        void SetLayer(int layer);

        RectBox GetCollisionBox();

        LTexture GetBitmap();
    }
}
