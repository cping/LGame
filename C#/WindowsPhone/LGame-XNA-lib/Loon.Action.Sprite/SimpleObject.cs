using Loon.Action.Map;
namespace Loon.Action.Sprite
{
    public abstract class SimpleObject : SpriteBatchObject
    {

        public SimpleObject(float x, float y, float w, float h,
                Animation animation, TileMap tiles): base(x, y, w, h, animation, tiles)
        {
           
        }

        public SimpleObject(float x, float y, Animation animation, TileMap tiles):  base(x, y, animation, tiles)
        {
          
        }

        public override void Update(long elapsedTime)
        {
            animation.Update(elapsedTime);
        }
    }
}
