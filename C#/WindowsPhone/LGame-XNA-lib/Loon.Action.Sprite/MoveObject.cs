using Loon.Action.Map;
using Loon.Core.Timer;
using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Utils;
using Loon.Core;
using Loon.Core.Input;
namespace Loon.Action.Sprite
{

    public class MoveObject : SpriteBatchObject
    {

        private bool allDirection;

        private List<Vector2f> findPath = new List<Vector2f>();

        private int startX, startY, endX, endY, moveX, moveY;

        private int speed, touchX, touchY;

        private int direction = Config.EMPTY;

        protected internal const int BLOCK_SIZE = 32;

        private bool isComplete;

        private LTimer timer;

        private AStarFindHeuristic heuristic;

        private int movingLength;

        public MoveObject(float x, float y, Animation animation, TileMap map):this(x, y, 0, 0, animation, map)
        {
            
        }

        public MoveObject(float x, float y, float dw, float dh,
                Animation animation, TileMap map): base(x, y, dw, dh, animation, map)
        {
           
            this.timer = new LTimer(0);
            this.isComplete = false;
            this.allDirection = false;
            this.speed = 4;
        }

        public void UpdateMove()
        {
            lock (typeof(MoveObject))
            {
                if (!GetCollisionArea().Contains(touchX, touchY))
                {

                    if (findPath != null)
                    {
                        CollectionUtils.Clear(findPath);
                    }
                    findPath = AStarFinder
                            .Find(heuristic,
                                    tiles.GetField(),
                                    tiles.PixelsToTilesWidth(X()),
                                    tiles.PixelsToTilesHeight(Y()),
                                    tiles.PixelsToTilesWidth(touchX
                                            - tiles.GetOffset().x),
                                    tiles.PixelsToTilesHeight(touchY
                                            - tiles.GetOffset().y), allDirection);
                }
                else if (findPath != null)
                {
                    CollectionUtils.Clear(findPath);
                }
            }
        }

        public void PressedLeft()
        {
            direction = Config.LEFT;
        }

        public void PressedRight()
        {
            direction = Config.RIGHT;
        }

        public void PressedDown()
        {
            direction = Config.DOWN;
        }

        public void PressedUp()
        {
            direction = Config.UP;
        }

        public void ReleaseDirection()
        {
            this.direction = Config.EMPTY;
        }

        private bool MoveState()
        {
            movingLength = 0;
            switch (direction)
            {
                case Config.LEFT:
                    if (MoveLeft())
                    {
                        return true;
                    }
                    break;
                case Config.RIGHT:
                    if (MoveRight())
                    {
                        return true;
                    }
                    break;
                case Config.UP:
                    if (MoveUp())
                    {
                        return true;
                    }
                    break;
                case Config.DOWN:
                    if (MoveDown())
                    {
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }

        private bool MoveLeft()
        {
            int px = X();
            int py = Y();
            int x = tiles.PixelsToTilesWidth(px);
            int y = tiles.PixelsToTilesHeight(py);
            int nextX = x - 1;
            int nextY = y;
            if (nextX < 0)
            {
                nextX = 0;
            }
            if (tiles.IsHit(nextX, nextY))
            {
                px -= speed;
                if (px < 0)
                {
                    px = 0;
                }
                movingLength += speed;
                SetLocation(px, py);
                if (movingLength >= tiles.GetTileWidth())
                {
                    x--;
                    px = x * tiles.GetTileWidth();
                    SetLocation(px, py);
                    return true;
                }
            }
            else
            {
                px = x * tiles.GetTileWidth();
                py = y * tiles.GetTileHeight();
                SetLocation(px, py);
            }

            return false;
        }

        private bool MoveRight()
        {
            int px = X();
            int py = Y();
            int x = tiles.PixelsToTilesWidth(px);
            int y = tiles.PixelsToTilesHeight(py);
            int nextX = x + 1;
            int nextY = y;

            if (nextX > tiles.GetRow() - 1)
            {
                nextX = tiles.GetRow() - 1;
            }
            if (tiles.IsHit(nextX, nextY))
            {
                px += speed;
                if (px > tiles.GetWidth() - tiles.GetTileWidth())
                {
                    px = tiles.GetWidth() - tiles.GetTileWidth();
                }
                movingLength += speed;
                SetLocation(px, py);
                if (movingLength >= tiles.GetTileWidth())
                {
                    x++;
                    px = x * tiles.GetTileWidth();
                    SetLocation(px, py);
                    return true;
                }
            }
            else
            {
                px = x * tiles.GetTileWidth();
                py = y * tiles.GetTileHeight();
                SetLocation(px, py);
            }

            return false;
        }

        private bool MoveUp()
        {
            int px = X();
            int py = Y();
            int x = tiles.PixelsToTilesWidth(px);
            int y = tiles.PixelsToTilesHeight(py);
            int nextX = x;
            int nextY = y - 1;
            if (nextY < 0)
            {
                nextY = 0;
            }
            if (tiles.IsHit(nextX, nextY))
            {
                py -= speed;
                if (py < 0)
                {
                    py = 0;
                }
                movingLength += speed;
                SetLocation(px, py);
                if (movingLength >= tiles.GetTileHeight())
                {
                    y--;
                    py = y * tiles.GetTileHeight();
                    SetLocation(px, py);
                    return true;
                }
            }
            else
            {
                px = x * tiles.GetTileWidth();
                py = y * tiles.GetTileHeight();
                SetLocation(px, py);
            }

            return false;
        }

        private bool MoveDown()
        {
            int px = X();
            int py = Y();
            int x = tiles.PixelsToTilesWidth(px);
            int y = tiles.PixelsToTilesHeight(py);
            int nextX = x;
            int nextY = y + 1;
            if (nextY > tiles.GetCol() - 1)
            {
                nextY = tiles.GetCol() - 1;
            }
            if (tiles.IsHit(nextX, nextY))
            {
                py += speed;
                if (py > tiles.GetHeight() - tiles.GetTileHeight())
                {
                    py = tiles.GetHeight() - tiles.GetTileHeight();
                }
                movingLength += speed;
                SetLocation(px, py);
                if (movingLength >= tiles.GetTileHeight())
                {
                    y++;
                    py = y * tiles.GetTileHeight();
                    SetLocation(px, py);
                    return true;
                }
            }
            else
            {
                px = x * tiles.GetTileWidth();
                py = y * tiles.GetTileHeight();
                SetLocation(px, py);
            }
            return false;
        }

        public override int GetHashCode()
        {
            if (tiles == null)
            {
                return base.GetHashCode();
            }
            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, allDirection);
            hashCode = LSystem.Unite(hashCode, tiles.PixelsToTilesWidth(X()));
            hashCode = LSystem.Unite(hashCode, tiles.PixelsToTilesHeight(Y()));
            hashCode = LSystem.Unite(hashCode,
                    tiles.PixelsToTilesWidth(touchX - tiles.GetOffset().x));
            hashCode = LSystem.Unite(hashCode,
                    tiles.PixelsToTilesHeight(touchY - tiles.GetOffset().y));
            hashCode = LSystem.Unite(hashCode, tiles.GetWidth());
            hashCode = LSystem.Unite(hashCode, tiles.GetHeight());
            hashCode = LSystem.Unite(hashCode, tiles.GetTileWidth());
            hashCode = LSystem.Unite(hashCode, tiles.GetTileHeight());
            hashCode = LSystem.Unite(hashCode,
                    CollectionUtils.HashCode(tiles.GetMap()));
            return hashCode;
        }

        public void OnTouch(LTouch e)
        {
            this.OnTouch(e.X(), e.Y());
        }

        public void OnTouch(int x, int y)
        {
            this.touchX = x;
            this.touchY = y;
            this.UpdateMove();
        }

        public int GetTouchX()
        {
            return touchX;
        }

        public int GetTouchY()
        {
            return touchY;
        }

        public void OnPosition(LTouch e)
        {
            this.OnPosition(e.GetX(), e.GetY());
        }

        public void OnPosition(float x, float y)
        {
            if (findPath == null)
            {
                return;
            }
            lock (findPath)
            {
                if (findPath != null)
                {
                    CollectionUtils.Clear(findPath);
                }
            }
            this.SetLocation(x, y);
        }

        private bool isMoving;

        public override void Update(long elapsedTime)
        {
            if (timer.Action(elapsedTime))
            {

                isMoving = MoveState();

                if (tiles == null || findPath == null)
                {
                    return;
                }
                if (IsComplete())
                {
                    return;
                }

                lock (findPath)
                {
                    if (endX == startX && endY == startY)
                    {
                        if (findPath != null)
                        {
                            if (findPath.Count > 1)
                            {
                                Vector2f moveStart = findPath[0];
                                Vector2f moveEnd = findPath[1];
                                startX = tiles.TilesToPixelsX(moveStart.X());
                                startY = tiles.TilesToPixelsY(moveStart.Y());
                                endX = moveEnd.X() * tiles.GetTileWidth();
                                endY = moveEnd.Y() * tiles.GetTileHeight();
                                moveX = moveEnd.X() - moveStart.X();
                                moveY = moveEnd.Y() - moveStart.Y();
                                direction = Field2D.GetDirection(moveX, moveY);
                                findPath.RemoveAt(0);
                            }
                            else
                            {
                                findPath.Clear();
                            }
                        }
                    }
                    switch (direction)
                    {
                        case Field2D.TUP:
                            startY -= speed;
                            if (startY < endY)
                            {
                                startY = endY;
                            }
                            break;
                        case Field2D.TDOWN:
                            startY += speed;
                            if (startY > endY)
                            {
                                startY = endY;
                            }
                            break;
                        case Field2D.TLEFT:
                            startX -= speed;
                            if (startX < endX)
                            {
                                startX = endX;
                            }
                            break;
                        case Field2D.TRIGHT:
                            startX += speed;
                            if (startX > endX)
                            {
                                startX = endX;
                            }
                            break;
                        case Field2D.UP:
                            startX += speed;
                            startY -= speed;
                            if (startX > endX)
                            {
                                startX = endX;
                            }
                            if (startY < endY)
                            {
                                startY = endY;
                            }
                            break;
                        case Field2D.DOWN:
                            startX -= speed;
                            startY += speed;
                            if (startX < endX)
                            {
                                startX = endX;
                            }
                            if (startY > endY)
                            {
                                startY = endY;
                            }
                            break;
                        case Field2D.LEFT:
                            startX -= speed;
                            startY -= speed;
                            if (startX < endX)
                            {
                                startX = endX;
                            }
                            if (startY < endY)
                            {
                                startY = endY;
                            }
                            break;
                        case Field2D.RIGHT:
                            startX += speed;
                            startY += speed;
                            if (startX > endX)
                            {
                                startX = endX;
                            }
                            if (startY > endY)
                            {
                                startY = endY;
                            }
                            break;
                    }

                    Vector2f tile = tiles.GetTileCollision(this, startX, startY);

                    if (tile != null)
                    {
                        int sx = tiles.TilesToPixelsX(tile.x);
                        int sy = tiles.TilesToPixelsY(tile.y);
                        if (sx > 0)
                        {
                            sx = sx - GetWidth();
                        }
                        else if (sx < 0)
                        {
                            sx = tiles.TilesToPixelsX(tile.x);
                        }
                        if (sy > 0)
                        {
                            sy = sy - GetHeight();
                        }
                        else if (sy < 0)
                        {
                            sy = tiles.TilesToPixelsY(tile.y);
                        }
                    }
                    else
                    {
                        SetLocation(startX, startY);
                    }

                }
            }
        }

        public long GetDelay()
        {
            return timer.GetDelay();
        }

        public void SetDelay(long d)
        {
            timer.SetDelay(d);
        }

        public int GetDirection()
        {
            return direction;
        }

        public int GetSpeed()
        {
            return speed;
        }

        public void SetSpeed(int speed)
        {
            this.speed = speed;
        }

        public bool IsComplete()
        {
            return findPath == null || findPath.Count == 0 || isComplete;
        }

        public void SetComplete(bool c)
        {
            this.isComplete = true;
        }

        public float GetRotationTo(float x, float y)
        {
            float r = MathUtils.Atan2(x - X(), y - Y());
            return ShapeUtils.GetAngleDiff(rotation, r);
        }

        public override void Dispose()
        {
            base.Dispose();
            if (findPath != null)
            {
                findPath.Clear();
                findPath = null;
            }
        }

        public AStarFindHeuristic GetHeuristic()
        {
            return heuristic;
        }

        public void setHeuristic(AStarFindHeuristic heuristic)
        {
            this.heuristic = heuristic;
        }

        public bool IsAllDirection()
        {
            return allDirection;
        }

        public void SetAllDirection(bool allDirection)
        {
            this.allDirection = allDirection;
        }

        public bool IsMoving()
        {
            return isMoving;
        }

    }
}
