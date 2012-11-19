namespace Loon.Action.Sprite
{
    using Loon.Action.Map;

    public abstract class SimpleObject : SpriteBatchObject
    {

        public SimpleObject(float x, float y, float w, float h,
                Animation animation, TileMap tiles):base(x, y, w, h, animation, tiles)
        {
            
        }

        public override void Update(long elapsedTime)
        {
            animation.Update(elapsedTime);
        }
    }
}