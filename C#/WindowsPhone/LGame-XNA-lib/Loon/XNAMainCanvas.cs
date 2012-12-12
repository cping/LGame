using System;
using Loon.Java;
using Loon.Core.Graphics.Opengl;
using Microsoft.Xna.Framework;

namespace Loon
{
    public abstract class XNAMainCanvas : GLSurfaceView
    {
        private static int m_drawFlag;
        private XNAHandler m_handler;
    
        private static bool m_pause;
  
        private InnerRunnable m_runnableDraw;
   
        private DrawingThread mThread;

        public XNAMainCanvas(XNAContext context)
            : base(context)
        {
            this.m_handler = new XNAHandler();
            this.Init();
            m_drawFlag = 0;
            this.m_runnableDraw = new InnerRunnable(this);
        }

        public void CallDraw()
        {
            if (!m_pause)
            {
                this.PostHandler(this.m_runnableDraw);
            }
        }

        protected virtual void FreamCreate(GL gl, int width, int height)
        {
        }

        protected virtual void FreamDraw(GL gl)
        {
        }

        protected virtual void FreamInit(GL gl)
        {
        }

        public virtual void FreamProc()
        {
        }

        public static int GetDrawFlag()
        {
            return m_drawFlag;
        }

        public DrawingThread GetThreadInstance()
        {
            return this.mThread;
        }

        protected internal virtual void Init()
        {

        }

        public override void OnDrawFrame(GL gl)
        {
            m_drawFlag = 3;
            this.FreamDraw(gl);
            m_drawFlag = 0;
        }

        public override void OnSurfaceChanged(GL gl, int width, int height)
        {
            this.FreamCreate(gl, width, height);
        }

        public override void OnSurfaceCreated(GL gl)
        {
            this.FreamInit(gl);
            this.mThread = new DrawingThread(this);
            this.mThread.Start();
        }

        public void PostHandler(Runnable runnable)
        {
            this.m_handler.Post(runnable);
        }

        public virtual void Repaint()
        {
            m_drawFlag = 2;
            base.RequestRender();
        }

        public void SetPause(bool pause)
        {
            m_pause = pause;
        }

        public override void SurfaceDestroyed()
        {
           
        }

        public class DrawingThread : Thread
        {
            private XNAMainCanvas canvas;

            public DrawingThread(XNAMainCanvas c)
            {
                this.canvas = c;
            }

            public override void Run()
            {
                try
                {
                    this.canvas.FreamProc();
                }
                catch (Exception)
                {
                }
            }

            public void WaitForExit()
            {
                while (XNAMainCanvas.m_drawFlag != 0)
                {
                }
                try
                {
                    base.Join();
                }
                catch (Exception)
                {
                }
            }
        }

        private class InnerRunnable : Runnable
        {
            private XNAMainCanvas canvas;

            public InnerRunnable(XNAMainCanvas c)
            {
                this.canvas = c;
            }

            public virtual void Run()
            {
                this.canvas.Repaint();
            }
        }
    }
}
