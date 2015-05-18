namespace Loon.Core.Geom {
	
	
	public class IntValue {
	
		private int value_ren;
	
		public IntValue(int v) {
			this.value_ren = v;
		}
	
		public int Get() {
			return value_ren;
		}
	
		public void Set(int v) {
			this.value_ren = v;
		}
	
		public override string ToString() {
			return value_ren.ToString();
		}
	
	}
}
