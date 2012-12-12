namespace Loon.Core.Geom {
	
	public class AABB {
	
		public float minX, minY;
	
		public float maxX, maxY;
	
		public AABB() :this(0.0F, 0.0F, 0.0F, 0.0F){
			
		}
	
		public AABB(float minX_0, float minY_1, float maxX_2, float maxY_3) {
			this.minX = minX_0;
			this.minY = minY_1;
			this.maxX = maxX_2;
			this.maxY = maxY_3;
		}
	
		public virtual AABB Clone() {
			return new AABB(this.minX, this.minY, this.maxX, this.maxY);
		}
	
		public bool IsHit(AABB b) {
			return this.minX < b.maxX && b.minX < this.maxX && this.minY < b.maxY
					&& b.minY < this.maxY;
		}
	
		public void Set(float minX_0, float minY_1, float maxX_2, float maxY_3) {
			this.minX = minX_0;
			this.minY = minY_1;
			this.maxX = maxX_2;
			this.maxY = maxY_3;
		}
	
	}
}
