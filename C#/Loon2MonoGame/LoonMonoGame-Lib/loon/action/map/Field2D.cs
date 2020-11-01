using loon.geom;

namespace loon.action.map
{
    public class Field2D : Config
    {

        public static int GetDirection(int x, int y)
        {
            return GetDirection(x, y, Config.EMPTY);
        }

        public static int GetDirection(int x, int y, int value)
        {
            int newX = 0;
            int newY = 0;
            if (x > 0)
            {
                newX = 1;
            }
            else if (x < 0)
            {
                newX = -1;
            }
            if (y > 0)
            {
                newY = 1;
            }
            else if (y < 0)
            {
                newY = -1;
            }
            int dir = GetDirectionImpl(newX, newY);
            return Config.EMPTY == dir ? value : dir;
        }

        private static int GetDirectionImpl(int x, int y)
        {
            if (x == 0 && y == 0)
            {
                return Config.EMPTY;
            }
            else if (x == 1 && y == -1)
            {
                return Config.UP;
            }
            else if (x == 0 && y == -1)
            {
                return Config.TUP;
            }
            else if (x == -1 && y == -1)
            {
                return Config.LEFT;
            }
            else if (x == 1 && y == 1)
            {
                return Config.RIGHT;
            }
            else if (x == -1 && y == 1)
            {
                return Config.DOWN;
            }
            else if (x == -1 && y == 0)
            {
                return Config.TLEFT;
            }
            else if (x == 1 && y == 0)
            {
                return Config.TRIGHT;
            }
            else if (x == 0 && y == 1)
            {
                return Config.TDOWN;
            }
            return Config.EMPTY;
        }

        public static Vector2f GetDirection(int type)
        {
            if (type > Config.TDOWN)
            {
                type = Config.TDOWN;
            }
            return GetDirectionToPoint(type, 1).Cpy();
        }

        public static Vector2f GetDirectionToPoint(int dir, int value)
        {
            Vector2f direction;
            switch (dir)
            {
                case Config.UP:
                    direction = new Vector2f(value, -value);
                    break;
                case Config.LEFT:
                    direction = new Vector2f(-value, -value);
                    break;
                case Config.RIGHT:
                    direction = new Vector2f(value, value);
                    break;
                case Config.DOWN:
                    direction = new Vector2f(-value, value);
                    break;
                case Config.TUP:
                    direction = new Vector2f(0, -value);
                    break;
                case Config.TLEFT:
                    direction = new Vector2f(-value, 0);
                    break;
                case Config.TRIGHT:
                    direction = new Vector2f(value, 0);
                    break;
                case Config.TDOWN:
                    direction = new Vector2f(0, value);
                    break;
                default:
                    direction = new Vector2f(0, 0);
                    break;
            }
            return direction;
        }
    }
}
