namespace Loon.Core
{

    using System;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Loon.Core.Graphics;
    using Loon.Core.Geom;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Utils.Debug;

    public class EmulatorButton
    {

        private bool disabled;

        private bool click, onClick;

        private RectBox bounds;

        private LTexture bitmap, bitmap1;

        private LColor color;

        private int id;

        public EmulatorButton(String fileName, int w, int h, int x, int y): this(LTextures.LoadTexture(fileName), w, h, x, y, true)
        {
           
        }

        public EmulatorButton(LTexture img, int w, int h, int x, int y):this(img, w, h, x, y, true)
        {
            
        }

        public EmulatorButton(String fileName, int x, int y): this(LTextures.LoadTexture(fileName), 0, 0, x, y, false)
        {
           
        }

        public EmulatorButton(LTexture img, int x, int y):this(img, 0, 0, x, y, false)
        {
            
        }

        public EmulatorButton(LTexture img, int w, int h, int x, int y, bool flag):this(img, w, h, x, y, flag, img.GetWidth(), img.GetHeight())
        {
            
        }

        public EmulatorButton(LTexture img, int w, int h, int x, int y,
                bool flag, int sizew, int sizeh)
        {
            this.color = new LColor(LColor.gray.R, LColor.gray.G,
                            LColor.gray.B, 125);
            img.LoadTexture();
            if (flag)
           {
                this.bitmap = img.GetSubTexture(x, y, w, h);
            }
            else
            {
                this.bitmap = img;
            }
            if (bitmap.GetWidth() != sizew || bitmap.GetHeight() != sizeh)
            {
           
                LTexture tmp = bitmap;
                this.bitmap = bitmap.Scale(sizew, sizeh);
     
                if (tmp != null)
                {
                    tmp.Dispose();
                    tmp = null;
                }
            }
            this.bounds = new RectBox(0, 0, bitmap.GetWidth(), bitmap.GetHeight());
        }

        public bool IsClick()
        {
            return click;
        }

        public void Hit(int nid, int x, int y)
        {
            onClick = bounds.Contains(x, y)
                    || bounds.Intersects(x, y, bounds.width / 2, bounds.height / 4);
            if (nid == id)
            {
                click = false;
            }
            if (!disabled && !click)
            {
                SetPointerId(nid);
                click = onClick;
            }
        }

        public void Hit(int x, int y)
        {
            onClick = bounds.Contains(x, y)
                    || bounds.Intersects(x, y, bounds.width / 2, bounds.height / 4);
            if (!disabled && !click)
            {
                click = onClick;
            }
        }

        public void Unhit(int nid)
        {
            if (id == nid)
            {
                click = false;
                onClick = false;
            }
        }

        public void Unhit()
        {
            click = false;
            onClick = false;
        }

        public void SetX(int x)
        {
            this.bounds.SetX(x);
        }

        public void SetY(int y)
        {
            this.bounds.SetY(y);
        }

        public int GetX()
        {
            return (int)bounds.GetX();
        }

        public int GetY()
        {
            return (int)bounds.GetY();
        }

        public void SetLocation(int x, int y)
        {
            this.bounds.SetX(x);
            this.bounds.SetY(y);
        }

        public void SetPointerId(int i)
        {
            this.id = i;
        }

        public RectBox GetBounds()
        {
            return bounds;
        }

        public int GetPointerId()
        {
            return this.id;
        }

        public bool IsEnabled()
        {
            return disabled;
        }

        public void Disable(bool flag)
        {
            this.disabled = flag;
        }

        public int GetHeight()
        {
            return bounds.height;
        }

        public int GetWidth()
        {
            return bounds.width;
        }

        public void SetSize(int w, int h)
        {
            this.bounds.SetWidth(w);
            this.bounds.SetHeight(h);
        }

        public void SetBounds(int x, int y, int w, int h)
        {
            this.bounds.SetBounds(x, y, w, h);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void SetClickImage(LTexture i)
        {
            SetClickImage(null, i);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void SetClickImage(LTexture on, LTexture un)
        {
            if (un == null)
            {
                return;
            }
            if (bitmap != null)
            {
                bitmap.Dispose();
                bitmap = null;
            }
            if (bitmap1 != null)
            {
                bitmap1.Dispose();
                bitmap1 = null;
            }
            this.bitmap = (un == null) ? on : un;
            this.bitmap1 = (on == null) ? un : on;
            this.SetSize(un.GetWidth(), un.GetHeight());
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void SetOnClickImage(LTexture img)
        {
            if (bitmap1 != null)
            {
                bitmap1.Dispose();
                bitmap1 = null;
            }
            this.bitmap1 = img;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void SetUnClickImage(LTexture img)
        {
            if (bitmap != null)
            {
                bitmap.Dispose();
                bitmap = null;
            }
            this.bitmap = img;
        }

        public void Draw(GLEx g)
        {
            if (!disabled)
            {
                if (click && onClick)
                {
                    if (bitmap1 != null)
                    {
                
                        g.DrawBatch(bitmap1, bounds.x, bounds.y);
                    }
                    else
                    {
                        g.DrawBatch(bitmap, bounds.x, bounds.y, color.Color);
                    }
                }
                else
                {
                    g.DrawBatch(bitmap, bounds.x, bounds.y);
                }
            }
        }
	

    }
}
