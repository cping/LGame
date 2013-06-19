namespace Loon.Physics {
	
    using Loon.Core.Geom;
	
	public class PWorldBox {
	
		private PBody northBody, southBody, eastBody, westBody;
	
		private PPhysManager manager;
	
		private bool build;
	
		public readonly FloatValue density = new FloatValue(0);
	
		private float mx, my, mw, mh;
	
		private float thick;
	
		public PWorldBox(PPhysManager world, float x, float y, float w, float h) {
			this.build = false;
			this.manager = world;
			this.Set(x, y, w, h);
			this.thick = 1f;
			this.density.Set(1f);
		}
	
		public void RemoveWorld() {
			if (build) {
				manager.world.RemoveBody(northBody);
				manager.world.RemoveBody(southBody);
				manager.world.RemoveBody(eastBody);
				manager.world.RemoveBody(westBody);
			}
			build = false;
		}
	
		public bool IsBuild() {
			return build;
		}
	
		public void Set(float x, float y, float w, float h) {
			this.mx = x;
			this.my = y;
			this.mw = w;
			this.mh = h;
		}
	
		public void Build() {
			if (build) {
				throw new System.Exception("Build Error !");
			}
			this.manager.AddBox(true, 0f, 0f, mw, thick, 0, density.Get());
			this.manager.AddBox(true, 0f, mh, mw, thick, 0, density.Get());
			this.manager.AddBox(true, 0f, 0f, thick, mh, 0, density.Get());
			this.manager.AddBox(true, mw, 0f, thick, mh, 0, density.Get());
			this.build = true;
		}
	
		public float GetDensity() {
			return density.Get();
		}
	
		public void SetDensity(float d) {
			this.density.Set(d);
		}
	
		public PBody GetEastBody() {
			return eastBody;
		}
	
		public void SetEastBody(PBody eastBody_0) {
			this.eastBody = eastBody_0;
		}
	
		public PBody GetNorthBody() {
			return northBody;
		}
	
		public void SetNorthBody(PBody northBody_0) {
			this.northBody = northBody_0;
		}
	
		public PBody GetSouthBody() {
			return southBody;
		}
	
		public void SetSouthBody(PBody southBody_0) {
			this.southBody = southBody_0;
		}
	
		public PBody GetWestBody() {
			return westBody;
		}
	
		public void SetWestBody(PBody westBody_0) {
			this.westBody = westBody_0;
		}
	
		public float X() {
			return mx;
		}
	
		public void SetX(float mx_0) {
			this.mx = mx_0;
		}
	
		public float Y() {
			return my;
		}
	
		public void SetY(float my_0) {
			this.my = my_0;
		}
	
		public float GetWidth() {
			return mw;
		}
	
		public void SetWidth(float mw_0) {
			this.mw = mw_0;
		}
	
		public float GetHeight() {
			return mh;
		}
	
		public void SetHeight(float mh_0) {
			this.mh = mh_0;
		}
	
		public float GetThick() {
			return thick;
		}
	
		public void SetThick(float thick_0) {
			this.thick = thick_0;
		}
	}
}
