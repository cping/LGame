using static loon.monogame.Loon;

namespace loon.monogame
{
    public enum LMode
    {
        Defalut, Max, Fill, FitFill, Ratio, MaxRatio
    }

    public class MonoGameSetting : LSetting
    {

        // 屏幕显示模式
        public LMode showMode = LMode.Fill;
        public bool useRatioScaleFactor = false;
        public bool allowUserResizing = false;
        public bool isMouseVisible = true;
        public bool isFixedTimeStep = true;
        public bool synchronizeVerticalRetrace = true;
        public bool preferMultiSampling = true;
    }
}
