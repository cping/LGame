namespace Loon.Action.Avg
{
    using System.IO;
    using Loon.Core;
    using Loon.Core.Graphics.Opengl;
    using Loon.Action.Sprite.Effect;
    using Loon.Action.Sprite;

    public class AVGChara : LRelease
    {

        private LTexture characterCG;

        private int width;

        private int height;

        internal int x;

        internal int y;

        internal int flag = -1;

        internal float time;

        internal float currentFrame;

        internal float opacity;

        protected internal bool isMove, isAnimation, isVisible = true;

        internal int maxWidth, maxHeight;

        private int moveX;

        private int direction;

        private int moveSleep = 10;

        private bool moving;

        protected internal AVGAnm anm;

        /// <summary>
        /// 构造函数，初始化角色图
        /// </summary>
        ///
        /// <param name="image"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="width"></param>
        /// <param name="height"></param>
        public AVGChara(LTexture image, int x, int y, int width,
                int height)
        {
            this.Load(image, x, y, width, height, LSystem.screenRect.width,
                    LSystem.screenRect.height);
        }

        public AVGChara(LTexture image, int x, int y)
        {
            this.Load(image, x, y);
        }

        public AVGChara(string resName, int x, int y):this(resName, x, y, LSystem.screenRect.width, LSystem.screenRect.height)
        {
            
        }

        public AVGChara(string resName, int x, int y,
                int w, int h)
        {
            string path = resName;
            if (path.StartsWith("\""))
            {
                path = resName.Replace("\"", "");
            }
            if (path.EndsWith(".an"))
            {
                this.x = x;
                this.y = y;
                this.isAnimation = true;
                try
                {
                    this.anm = new AVGAnm(path);
                }
                catch (IOException e)
                {
                    Loon.Utils.Debug.Log.Exception(e.StackTrace);
                }
                this.maxWidth = w;
                this.maxHeight = h;
            }
            else
            {
                this.Load(LTextures.LoadTexture(path), x, y);
            }
        }

        internal string tmp_path;

        internal void Update(string path)
        {
            this.tmp_path = path;
        }

        private void Load(LTexture image, int x, int y)
        {
            this.Load(image, x, y, image.GetWidth(), image.GetHeight(),
                    LSystem.screenRect.width, LSystem.screenRect.height);
        }

        private void Load(LTexture image, int x, int y, int width,
                int height, int w, int h)
        {
            this.maxWidth = w;
            this.maxHeight = h;
            this.isAnimation = false;
            this.characterCG = image;
            this.isMove = true;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.moveX = 0;
            this.direction = GetDirection();
            if (direction == 0)
            {
                this.moveX = -(width / 2);
            }
            else
            {
                this.moveX = maxWidth;
            }
        }

        public void SetFlag(int f, float delay)
        {
            this.flag = f;
            this.time = delay;
            if (flag == ISprite_Constants.TYPE_FADE_IN)
            {
                this.currentFrame = this.time;
            }
            else
            {
                this.currentFrame = 0;
            }
        }

        public int GetScreenWidth()
        {
            return maxWidth;
        }

        public int GetScreenHeight()
        {
            return maxHeight;
        }

        private int GetDirection()
        {
            int offsetX = maxWidth / 2;
            if (x < offsetX)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }

        public void SetMove(bool move)
        {
            isMove = move;
        }

        public void Flush()
        {
            characterCG = null;
            x = 0;
            y = 0;
        }

        public int GetNext()
        {
            return moveX;
        }

        public int GetMaxNext()
        {
            return x;
        }

        public bool Next()
        {
            moving = false;
            if (moveX != x)
            {
                for (int sleep = 0; sleep < moveSleep; sleep++)
                {
                    if (direction == 0)
                    {
                        moving = (x > moveX);
                    }
                    else
                    {
                        moving = (x < moveX);
                    }
                    if (moving)
                    {
                        switch (direction)
                        {
                            case 0:
                                moveX += 1;
                                break;
                            case 1:
                                moveX -= 1;
                                break;
                            default:
                                moveX = x;
                                break;
                        }
                    }
                    else
                    {
                        moveX = x;
                    }
                }
            }
            return moving;
        }

        internal void Update(long t)
        {

        }

        internal void draw(GLEx g)
        {
            g.DrawTexture(characterCG, moveX, y);
        }

        public int GetX()
        {
            return x;
        }

        public void SetX(int x)
        {
            if (isMove)
            {
                int move = x - this.moveX;
                if (move < 0)
                {
                    this.moveX = this.x;
                    this.x = x;
                    direction = 1;
                }
                else
                {
                    this.moveX = move;
                    this.x = x;
                }
            }
            else
            {
                this.moveX = x;
                this.x = x;
            }

        }

        public int GetY()
        {
            return y;
        }

        public void SetY(int y)
        {
            this.y = y;
        }

        public int GetHeight()
        {
            return height;
        }

        public void SetHeight(int height)
        {
            this.height = height;
        }

        public int GetWidth()
        {
            return width;
        }

        public void SetWidth(int width)
        {
            this.width = width;
        }

        public int GetMoveSleep()
        {
            return moveSleep;
        }

        public void SetMoveSleep(int moveSleep)
        {
            this.moveSleep = moveSleep;
        }

        public int GetMoveX()
        {
            return moveX;
        }

        public bool IsAnimation()
        {
            return isAnimation;
        }

        internal void SetAnimation(bool isAnimation)
        {
            this.isAnimation = isAnimation;
        }

        public bool IsVisible()
        {
            return isVisible;
        }

        public void SetVisible(bool isVisible)
        {
            this.isVisible = isVisible;
        }

        public LTexture GetTexture()
        {
            return characterCG;
        }

        public void Dispose()
        {
            this.isVisible = false;
            if (characterCG != null)
            {
                characterCG.Destroy();
                characterCG = null;
            }
            if (anm != null)
            {
                anm.Dispose();
                anm = null;
                isAnimation = false;
            }
        }

    }
}
