using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics.Opengl;
using Loon.Java;
using Loon.Utils.Debugging;

namespace Loon
{
    public abstract class XNAMainActivity : XNAActivity
    {
     
        private XNAHandler m_handler = new XNAHandler();
        private bool m_isBootBrowser = false;
        public int m_licenseCheckResult = -1;
        
        private static XNAActivity mActivity;
        private static XNAMainCanvas mCanvas = null;
        private XNAContext mContext;
  
        protected XNAMainActivity(int w,int h):base(w,h)
        {

        }

        public void SetContent(Microsoft.Xna.Framework.Content.ContentManager content )
        {
            this.GameContent = content;
        }

        public abstract void CreateApp(GL gl, int width, int height);
     
        public abstract void DoMain();

        public abstract void Draw(GL gl);

        public void EndApplication()
        {
            base.Finish();
        }

        public static XNAActivity GetActivity()
        {
            return mActivity;
        }

        public static XNAMainCanvas GetCanvas()
        {
            return mCanvas;
        }

        public static int GetCanvasHeight()
        {
            return mCanvas.GetHeight();
        }

        public static int GetCanvasWidth()
        {
            return mCanvas.GetWidth();
        }

        public int GetLicenceResult()
        {
            return this.m_licenseCheckResult;
        }

        public abstract void InitApp(XNAContext context, GL gl);

        public override void OnCreate(XNABundle state)
        {
            base.OnCreate(state);
            mActivity = this;
            mActivity.GameContent = GameContent;
            this.mContext = this;
            mContext.GameContent = GameContent;
            mCanvas = new ICanvasGL(this);
            base.SetContentView(mCanvas);
        }

        protected override void OnDestroy()
        {
            base.OnDestroy();
        }

        protected override void OnPause()
        {
            mCanvas.SetPause(true);
            base.OnPause();
            this.PauseApp();
            Log.DebugWrite("onPause");
        }

        protected override void OnRestart()
        {
            base.OnRestart();
            Log.DebugWrite("onRestart");
        }

        protected override void OnResume()
        {
            if (!this.m_isBootBrowser)
            {
                mCanvas.SetPause(false);
                this.ResumeApp();
            }
            base.OnResume();
            if (this.m_isBootBrowser)
            {
                this.EndApplication();
            }
            Log.DebugWrite("onResume");
        }

        protected override void OnStart()
        {
            base.OnStart();
            Log.DebugWrite("onStart");
        }

        protected override void OnStop()
        {
            base.OnStop();
            if (this.m_isBootBrowser)
            {
                this.EndApplication();
            }
            Log.DebugWrite("onStop");
        }

        public abstract void PauseApp();
        
        public void PostHandler(Runnable runnable)
        {
            this.m_handler.Post(runnable);
        }

        public abstract void ResumeApp();

        private class ICanvasGL : XNAMainCanvas
        {
            private XNAMainActivity activity;

            public ICanvasGL(XNAMainActivity context)
                : base(context)
            {
                this.activity = context;
            }

            protected override void FreamCreate(GL gl, int width, int height)
            {
                this.activity.CreateApp(gl, width, height);
            }

            protected override void FreamDraw(GL gl)
            {
                this.activity.Draw(gl);
            }

            protected override void FreamInit(GL gl)
            {
                this.activity.InitApp(this.activity.mContext, gl);
            }

            public override void FreamProc()
            {
                this.activity.DoMain();
            }
        }
    }
    
}
