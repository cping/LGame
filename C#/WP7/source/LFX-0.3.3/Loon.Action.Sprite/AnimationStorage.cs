namespace Loon.Action.Sprite
{
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Loon.Utils;
    using Loon.Core.Graphics.OpenGL;

    public class AnimationStorage : Animation
    {
        private int animationIndexLocked = -1;

        private class AnimationStorageListener : AnimationListener
        {

            private AnimationStorage store;

            public AnimationStorageListener(AnimationStorage s)
            {
                this.store = s;
            }

            public void OnComplete(Animation animation)
            {
                if (store.animationIndexLocked != -1)
                {
                    store.currentFrameIndex = store.animationIndexLocked;
                }
                else
                {
                    if (store.loopOverToRemove)
                    {
                        if (store.Listener != null)
                        {
                            store.Listener.OnComplete(store);
                        }
                        store.playAnimations.Remove(animation);
                        store.size = store.playAnimations.Count;
                        store.loopPlay++;
                    }
                    else
                    {
                        if (store.currentFrameIndex < store.size - 1)
                        {
                            if (store.Listener != null)
                            {
                                store.Listener.OnComplete(store);
                            }
                            store.currentFrameIndex++;
                            store.loopPlay++;
                        }
                        else
                        {
                            if (store.loopOverToPlay)
                            {
                                store.currentFrameIndex = 0;
                            }
                            else
                            {
                                store.currentFrameIndex = 0;
                                store.isRunning = false;
                            }
                        }
                    }
                }
            }

        }

        private bool loopOverToPlay;

        private bool loopOverToRemove;

        private AnimationStorageListener asl;

        private List<Animation> playAnimations;

        public AnimationStorage(List<Animation> f)
        {
            this.asl = new AnimationStorageListener(this);
            if (f != null)
            {
                playAnimations = f;
            }
            else
            {
                playAnimations = new List<Animation>(
                        CollectionUtils.INITIAL_CAPACITY);
            }
            foreach (Animation a in playAnimations)
            {
                if (a != null)
                {
                    a.Listener = asl;
                }
            }
            this.size = playAnimations.Count;
            this.loopOverToPlay = true;
            this.loopOverToRemove = false;
        }

        public AnimationStorage()
            : this(new List<Animation>(CollectionUtils.INITIAL_CAPACITY))
        {

        }

        public override object Clone()
        {
            return new AnimationStorage(playAnimations);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddAnimation(Animation anm)
        {
            if (anm != null)
            {
                anm.Listener = asl;
                playAnimations.Add(anm);
                isRunning = true;
                size++;
            }
        }


        [MethodImpl(MethodImplOptions.Synchronized)]
        public override void Update(long timer)
        {
            if (loopCount != -1 && loopPlay > loopCount)
            {
                return;
            }
            if (isRunning)
            {
                if (currentFrameIndex > -1 && currentFrameIndex < size)
                {
                    Animation animation = playAnimations[currentFrameIndex];
                    if (animation != null)
                    {
                        if (animation.isRunning)
                        {
                            animation.Update(timer);
                        }
                    }
                }
            }
        }


        public Animation GetAnimation(int idx)
        {
            if (currentFrameIndex > -1 && currentFrameIndex < size)
            {
                return playAnimations[idx];
            }
            else
            {
                return null;
            }
        }

        public void PlayIndex(int idx)
        {
            if (currentFrameIndex > -1 && currentFrameIndex < size)
            {
                currentFrameIndex = idx;
                Animation animation = playAnimations[currentFrameIndex];
                if (animation != null)
                {
                    animation.Reset();
                }
            }
        }

        public override LTexture GetSpriteImage()
        {
            if (currentFrameIndex > -1 && currentFrameIndex < size)
            {
                Animation animation = playAnimations[currentFrameIndex];
                return animation.GetSpriteImage(animation.currentFrameIndex);
            }
            else
            {
                return null;
            }
        }

        public override LTexture GetSpriteImage(int idx)
        {
            if (currentFrameIndex > -1 && currentFrameIndex < size)
            {
                Animation animation = playAnimations[currentFrameIndex];
                return animation.GetSpriteImage(idx);
            }
            else
            {
                return null;
            }
        }

        public LTexture GetSpriteImage(int animation, int idx)
        {
            if (currentFrameIndex > -1 && currentFrameIndex < size)
            {
                return playAnimations[animation].GetSpriteImage(idx);
            }
            else
            {
                return null;
            }
        }

        public int GetIndexLocked()
        {
            return animationIndexLocked;
        }

        public void IndexLocked(int idx)
        {
            this.animationIndexLocked = idx;
            if (animationIndexLocked > -1 && animationIndexLocked < size)
            {
                this.currentFrameIndex = animationIndexLocked;
                Animation animation = playAnimations[currentFrameIndex];
                if (animation != null)
                {
                    animation.Reset();
                }
            }
        }

        public override void Reset()
        {
            base.Reset();
            loopOverToPlay = true;
            loopOverToRemove = false;
        }

        public bool IsLoopOverToRemove()
        {
            return loopOverToRemove;
        }

        public void LoopOverToRemove(bool l)
        {
            loopOverToRemove = l;
        }

        public bool IsLoopPlay()
        {
            return loopOverToPlay;
        }

        public void SetLoopPlay(bool l)
        {
            this.loopOverToPlay = l;
        }

    }

}
