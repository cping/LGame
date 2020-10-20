using loon.utils;

namespace loon
{
  public  abstract class LTrans
{
		public readonly static  float ANGLE_90 = MathUtils.PI / 2;

		public readonly static float ANGLE_270 = MathUtils.PI * 3 / 2;

		public readonly static float ANGLE_360 = MathUtils.PI * 4 / 2;

		public readonly static int SOLID = 0;

		public readonly static int DOTTED = 1;

		public readonly static int TRANS_NONE = 0;

		public readonly static int TRANS_ROT90 = 5;

		public readonly static int TRANS_ROT180 = 3;

		public readonly static int TRANS_ROT270 = 6;

		public readonly static int TRANS_MIRROR = 2;

		public readonly static int TRANS_MIRROR_ROT90 = 7;

		public readonly static int TRANS_MIRROR_ROT180 = 1;

		public readonly static int TRANS_MIRROR_ROT270 = 4;

		public readonly static int HCENTER = 1;

		public readonly static int VCENTER = 2;

		public readonly static int LEFT = 4;

		public readonly static int RIGHT = 8;

		public readonly static int TOP = 16;

		public readonly static int BOTTOM = 32;

		public readonly static int BASELINE = 64;
	}
}
