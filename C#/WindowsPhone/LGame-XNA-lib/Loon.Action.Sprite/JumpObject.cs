using Loon.Action.Map;
using Loon.Core.Geom;
namespace Loon.Action.Sprite
{
    public class JumpObject : SpriteBatchObject
    {

        public interface JumpListener
        {

            void Update(long elapsedTime);

            void Check(int x, int y);

        }

        public JumpListener listener;

        public float GRAVITY;

        protected internal float vx;

        protected internal float vy;

        private float speed;

        private float jumpSpeed;

        private bool onGround;

        private bool forceJump;

        private bool jumperTwo;

        private bool canJumperTwo;

        public JumpObject(float x, float y, Animation animation, TileMap map):  base(x, y, 0, 0, animation, map)
        {
            this.GRAVITY = 0.6f;
            vx = 0;
            vy = 0;
            speed = 6f;
            jumpSpeed = 12f;
            onGround = false;
            forceJump = false;
            jumperTwo = false;
            canJumperTwo = true;
        }

        public JumpObject(float x, float y, float dw, float dh,
                Animation animation, TileMap map)
            : base(x, y, dw, dh, animation, map)
        {

            this.GRAVITY = 0.6f;
            vx = 0;
            vy = 0;
            speed = 6f;
            jumpSpeed = 12f;
            onGround = false;
            forceJump = false;
            jumperTwo = false;
            canJumperTwo = true;
        }

        public void Stop()
        {
            vx = 0;
        }

        public void AccelerateLeft()
        {
            vx = -speed;
        }

        public void AccelerateRight()
        {
            vx = speed;
        }

        public void AccelerateUp()
        {
            vy = speed;
        }

        public void AccelerateDown()
        {
            vy = -speed;
        }

        public void Jump()
        {
            if (onGround || forceJump)
            {
                vy = -jumpSpeed;
                onGround = false;
                forceJump = false;
            }
            else if (jumperTwo && canJumperTwo)
            {
                vy = -jumpSpeed;
                canJumperTwo = false;
            }
        }

        public void SetForceJump(bool forceJump_0)
        {
            this.forceJump = forceJump_0;
        }

        public float GetSpeed()
        {
            return speed;
        }

        public void SetSpeed(float speed_0)
        {
            this.speed = speed_0;
        }

        public void SetJumperTwo(bool jumperTwo_0)
        {
            this.jumperTwo = jumperTwo_0;
        }

        public override void Update(long elapsedTime) {
	
			if (animation != null) {
				animation.Update(elapsedTime);
			}
			TileMap map = tiles;
			float x = GetX();
			float y = GetY();
	
			vy += GRAVITY;
	
			float newX = x + vx;
	
			Vector2f tile = map.GetTileCollision(this, newX, y);
			if (tile == null) {
				x = newX;
			} else {
				if (vx > 0) {
					x = map.TilesToPixelsX(tile.x) - GetWidth();
				} else if (vx < 0) {
					x = map.TilesToPixelsY(tile.x + 1);
				}
				vx = 0;
			}
	
			float newY = y + vy;
			tile = map.GetTileCollision(this, x, newY);
			if (tile == null) {
				y = newY;
				onGround = false;
			} else {
				if (vy > 0) {
					y = map.TilesToPixelsY(tile.y) - GetHeight();
					vy = 0;
					onGround = true;
					canJumperTwo = true;
				} else if (vy < 0) {
					y = map.TilesToPixelsY(tile.y + 1);
					vy = 0;
					IsCheck(tile.X(), tile.Y());
				}
			}
	
			SetLocation(x, y);
			if (listener != null) {
				listener.Update(elapsedTime);
			}
		}

        public void IsCheck(int x, int y)
        {
            if (listener != null)
            {
                listener.Check(x, y);
            }
        }

        public JumpListener GetJumpListener()
        {
            return listener;
        }

        public void SetJumpListener(JumpListener l)
        {
            this.listener = l;
        }

    }
}
