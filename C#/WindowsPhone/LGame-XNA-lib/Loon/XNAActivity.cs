using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;

namespace Loon
{
    public abstract class XNAActivity :XNAContext
    {
        private GLSurfaceView m_view;

        public XNAActivity(int width, int height):base(width,height)
        {

        }

        public virtual void Finish()
        {
        }

        public virtual void OnCreate(XNABundle state)
        {
        }

        protected virtual void OnDestroy()
        {
        }

        protected virtual void OnPause()
        {
        }

        protected virtual void OnRestart()
        {
        }

        protected virtual void OnResume()
        {
        }

        protected virtual void OnStart()
        {
        }

        protected virtual void OnStop()
        {
        }

        public void RequestRender()
        {
            this.m_view.RequestRender();
        }

        public void SetContentView(GLSurfaceView _view)
        {
            this.m_view = _view;
            this.m_view.Create();
        }

        public GLSurfaceView GetView()
        {
            return this.m_view;
        }

    }
}
