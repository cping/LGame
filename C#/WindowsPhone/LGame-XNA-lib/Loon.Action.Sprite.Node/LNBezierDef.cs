using Loon.Core.Geom;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	public class LNBezierDef {
	
		public Vector2f endPosition;
	
		public Vector2f controlPoint_1;
	
		public Vector2f controlPoint_2;
	
		public LNBezierDef() {
	
		}
	
		public static float BezierAt(float a, float b, float c, float d, float t) {
			return (MathUtils.Pow(1 - t, 3) * a + 3 * t * (MathUtils.Pow(1 - t, 2))
					* b + 3 * MathUtils.Pow(t, 2) * (1 - t) * c + MathUtils.Pow(t,
					3) * d);
		}
	
	}
}
