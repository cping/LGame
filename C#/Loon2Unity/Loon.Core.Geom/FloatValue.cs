namespace Loon.Core.Geom {
	
	public class FloatValue {
	
		private float value_ren;
	
		public FloatValue(float v) {
			this.value_ren = v;
		}
	
		public float Get() {
			return value_ren;
		}
	
		public void Set(float v) {
			this.value_ren = v;
		}
	
		public override string ToString() {
			return value_ren.ToString();
		}
	
	}
}
