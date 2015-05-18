namespace Loon
{
	public abstract class UnityActivity :UnityContext
	{
		private UnityGLSurfaceView m_view;
		
		public UnityActivity(int width, int height):base(width,height)
		{
			
		}
		
		public virtual void Finish()
		{
		}
		
		public virtual void OnCreate(UnityBundle state)
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
		
		public void SetContentView(UnityGLSurfaceView _view)
		{
			this.m_view = _view;
			this.m_view.Create();
		}
		
		public UnityGLSurfaceView GetView()
		{
			return this.m_view;
		}
		
	}
}
