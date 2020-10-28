using loon.utils;

namespace loon
{
    public abstract class LTrans
    {
        public const float ANGLE_90 = MathUtils.PI / 2;

        public const float ANGLE_270 = MathUtils.PI * 3 / 2;

        public const float ANGLE_360 = MathUtils.PI * 4 / 2;

        public const int SOLID = 0;

        public const int DOTTED = 1;

        public const int TRANS_NONE = 0;

        public const int TRANS_ROT90 = 5;

        public const int TRANS_ROT180 = 3;

        public const int TRANS_ROT270 = 6;

        public const int TRANS_MIRROR = 2;

        public const int TRANS_MIRROR_ROT90 = 7;

        public const int TRANS_MIRROR_ROT180 = 1;

        public const int TRANS_MIRROR_ROT270 = 4;

        public const int HCENTER = 1;

        public const int VCENTER = 2;

        public const int LEFT = 4;

        public const int RIGHT = 8;

        public const int TOP = 16;

        public const int BOTTOM = 32;

        public const int BASELINE = 64;
    }
}
