using Loon.Action.Sprite;
using Loon.Action.Sprite.Node;
using Loon.Core.Input;
namespace NodeSample
{
    public class Test : SpriteBatchScreen
    {

        public override LTransition OnTransition()
        {
            return LTransition.NewFadeIn();
        }

        public override void Create()
        {

            // 直接载入图片到节点(直接加载大图)
            LNSprite sprite = LNSprite.GInitWithFilename("assets/ccc.png");
            // 支持拖拽
            sprite.SetLocked(false);
            sprite.SetLimitMove(false);
            Add(sprite);

            LNLabel label = new LNLabel();
            label.SetString("测试中");
            label.SetRotation(60);
            Add(label);

            sprite.RunAction(LNSequence.Action(LNEase.Action(Easing.BACK_IN_OUT,
                    LNMoveBy.Action(1f, 225, 125)), LNEnd.Action()));

            // 载入节点配置文件（大图切分为精灵）
            LoadNodeDef("assets/thunder.Image.txt");

            LNSprite t1 = new LNSprite("thunder_02");
            t1.SetLocation(145, 180);
            Add(t1);

            LNSprite t3 = new LNSprite("thunder_03");
            t3.SetLocation(199, 99);
            Add(t3);

            LNSprite t2 = new LNSprite("thunder_04");
            t2.SetLocation(99, 99);
            Add(t2);

            t1.SetAlpha(0f);
            // 动作执行
            t1.RunAction(LNSequence.Action(LNDelay.Action(2f), LNFadeIn.Action(1f),
                    LNFadeOut.Action(1f), LNFadeIn.Action(1f), LNFadeOut.Action(1f)));

            t2.SetAlpha(0f);
            t2.RunAction(LNSequence.Action(LNFadeIn.Action(1f),
                    LNFadeOut.Action(1f), LNRotateTo.Action(3f, 90),
                    LNFadeIn.Action(1f), LNFadeOut.Action(1f)));

            t3.SetAlpha(0f);
            t3.RunAction(LNSequence.Action(LNDelay.Action(1f), LNFadeIn.Action(1f),
                    LNScaleTo.Action(3f, 2f), LNFadeOut.Action(1f),
                    LNScaleTo.Action(3f, 1f), LNFadeIn.Action(1f),
                    LNFadeOut.Action(1f)));
        }

        public override void After(SpriteBatch batch)
        {

        }

        public override void Before(SpriteBatch batch)
        {

        }

        public override void Press(Loon.Core.Input.LKey e)
        {

        }

        public override void Release(Loon.Core.Input.LKey e)
        {

        }

        public override void Update(long elapsedTime)
        {

        }

        public override void Close()
        {

        }

        public override void TouchDown(Loon.Core.Input.LTouch e)
        {

        }

        public override void TouchUp(Loon.Core.Input.LTouch e)
        {

        }

        public override void TouchMove(Loon.Core.Input.LTouch e)
        {

        }

        public override void TouchDrag(Loon.Core.Input.LTouch e)
        {

        }
    }
}
