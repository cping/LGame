namespace Loon
{
	using Loon.Core.Graphics.Opengl;

	public class UnityGLSurfaceView 
	{
		private GL _gl;
		
		private UnityContext _context;
		
		public UnityGLSurfaceView(UnityContext c)
		{
			this._context = c;
			this._gl = new GL();
		}
		
		public int GetHeight()
		{
			return _context.GetWidth();
		}
		
		public int GetWidth()
		{
			return _context.GetHeight();
		}
		
		public void Create()
		{
			this.OnSurfaceCreated(this._gl);
			this.OnSurfaceChanged(this._gl, _context._width, _context._height);
		}
		
		public virtual void OnDrawFrame(GL gl)
		{
		}
		
		public virtual void OnSurfaceChanged(GL gl, int width, int height)
		{
			_context._width = width;
			_context._height = height;
			LSystem.screenRect.SetBounds(0, 0, width, height);
		}
		
		public virtual void OnSurfaceCreated(GL gl)
		{
		}
		
		public void RequestRender()
		{
			this.OnDrawFrame(this._gl);
		}
		
		public virtual void SurfaceDestroyed()
		{
		}
		
		public GL GetGL()
		{
			return _gl;
		}
	}
}
