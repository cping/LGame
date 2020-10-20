namespace loon.geom
{
   public class Affine2f
{
		/* default generality */
		protected int GENERALITY = 4;
		/* x scale */
		public float m00 = 1.0f;
		/* y skew */
		public float m01 = 0.0f;
		/* x skew */
		public float m10 = 0.0f;
		/* y scale */
		public float m11 = 1.0f;
		/* x translation */
		public float tx = 0.0f;
		/* y translation */
		public float ty = 0.0f;
		/* convert Affine to Matrix3 */
		private float[] matrix3f = new float[9];
		/* one 4x4 matrix temp object */
		private Matrix4 projectionMatrix = null;
	}
}
