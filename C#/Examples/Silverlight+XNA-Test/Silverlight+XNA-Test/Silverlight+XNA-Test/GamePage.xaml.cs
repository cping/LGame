namespace Silverlight_XNA_Test
{
    using System.Windows;
    using System.Windows.Navigation;
    using Loon;
    using Loon.Core.Graphics;
    using Loon.Core.Input;
    using Loon.Core.Timer;
    using Loon.Core.Graphics.OpenGL;
    using Microsoft.Phone.Controls;

    public class ScreenTest : Screen
    {

        public override LTransition OnTransition()
        {
            return LTransition.NewEmpty();
        }

        public override void OnLoad()
        {


        }

        public override void Alter(LTimerContext c)
        {

        }

        public override void Draw(GLEx g)
        {

        }

        public override void TouchDown(LTouch touch)
        {

        }

        public override void TouchDrag(LTouch e)
        {

        }

        public override void TouchMove(LTouch e)
        {

        }

        public override void TouchUp(LTouch touch)
        {

        }

    }

    //微软硬性规定此处的PhoneApplicationPage必须是原始类，所以LGame在使用Silverlight时就只能采取如下加载方式了……
    public partial class GamePage : PhoneApplicationPage
    {

        LSilverlightPlus plus;

        public GamePage()
        {
            InitializeComponent();
            //加载Silverlight数据到LGame
            plus = LSilverlightPlus.Load(this, (Application.Current as App).Content, OnMain);
        }

        /// <summary>
        /// 初始化事件
        /// </summary>
        /// <param name="plus"></param>
        public void OnMain(LSilverlightPlus plus)
        {

            //加载LGame默认资源(不进行此操作，LGame内置的模拟按钮之类功能无法使用)
            XNAConfig.Load("content/loon.def");
            //加载字体文件（此处是预编译好的xnb文件，也可以加载标准Content下的）
            plus.XNAFont = new LFont("content", "black", 0, 20);

            //设定启动参数
            LSetting setting = new LSetting();
            setting.fps = 60;
            setting.width = 480;
            setting.height = 320;
            setting.showFPS = true;
            setting.landscape = false;
            //注册初始Screen
            plus.Register(setting, typeof(ScreenTest));

        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            if (plus != null)
            {
                plus.OnNavigatedTo(e);
                base.OnNavigatedTo(e);
            }
        }

        protected override void OnNavigatedFrom(NavigationEventArgs e)
        {
            if (plus != null)
            {
                plus.OnNavigatedFrom(e);
                base.OnNavigatedFrom(e);
            }
        }

    }
}