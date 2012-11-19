namespace Loon.Action.Sprite
{
    using System;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Loon.Core;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Utils;

    public class Animation : LRelease
    {

        public interface AnimationListener
        {
            void OnComplete(Animation animation);
        }

        public AnimationListener Listener
        {
            set;
            get;
        }

        public void SetAnimationListener(AnimationListener l)
        {
            this.Listener = l;
        }

        public AnimationListener GetAnimationListener()
        {
            return this.Listener;
        }

        internal bool isRunning;

        private List<AnimationFrame> frames;

        internal int loopCount, loopPlay;

        internal int currentFrameIndex;

        internal long animTime, totalDuration;

        internal int size;

        public Animation()
            : this(new List<AnimationFrame>(CollectionUtils.INITIAL_CAPACITY), 0)
        {

        }

        public Animation(Animation a)
        {
            this.isRunning = a.isRunning;
            this.frames = new List<Animation.AnimationFrame>(a.frames);
            this.loopCount = a.loopCount;
            this.loopPlay = a.loopPlay;
            this.currentFrameIndex = a.currentFrameIndex;
            this.animTime = a.animTime;
            this.totalDuration = a.totalDuration;
            this.size = frames.Count;
        }

        private Animation(List<AnimationFrame> frames, long totalDuration)
        {
            this.loopCount = -1;
            this.frames = frames;
            this.size = frames.Count;
            this.totalDuration = totalDuration;
            this.isRunning = true;
            Start();
        }

        public static Animation GetDefaultAnimation(String fileName, int width,
                int height, int timer)
        {
            return Animation.GetDefaultAnimation(
                    TextureUtils.GetSplitTextures(fileName, width, height), -1,
                    timer);
        }

        public static Animation GetDefaultAnimation(String fileName, int maxFrame,
                int width, int height, int timer)
        {
            return Animation.GetDefaultAnimation(
                    TextureUtils.GetSplitTextures(fileName, width, height),
                    maxFrame, timer);
        }

        public static Animation GetDefaultAnimation(LTexture[] images,
                int maxFrame, int timer)
        {
            if (images == null)
            {
                return new Animation();
            }
            Animation animation = new Animation();
            if (maxFrame != -1)
            {
                for (int i = 0; i < maxFrame; i++)
                {
                    animation.AddFrame(images[i], timer);
                }
            }
            else
            {
                int size = images.Length;
                for (int i = 0; i < size; i++)
                {
                    animation.AddFrame(images[i], timer);
                }
            }
            return animation;
        }

        public virtual object Clone()
        {
            return new Animation(frames, totalDuration);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddFrame(LTexture image, long timer)
        {
            totalDuration += timer;
            frames.Add(new AnimationFrame(image, totalDuration));
            size++;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddFrame(String fileName, long timer)
        {
            AddFrame(LTextures.LoadTexture(fileName), timer);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Start()
        {
            animTime = 0;
            if (size > 0)
            {
                currentFrameIndex = 0;
            }
        }

        public virtual void Reset()
        {
            animTime = 0;
            currentFrameIndex = 0;
            loopPlay = 0;
            loopCount = -1;
            isRunning = true;
        }


        [MethodImpl(MethodImplOptions.Synchronized)]
        public virtual void Update(long timer)
        {
            if (loopCount != -1 && loopPlay > loopCount)
            {
                return;
            }
            if (isRunning)
            {
                if (size > 0)
                {
                    animTime += timer;
                    if (animTime > totalDuration)
                    {
                        if (Listener != null)
                        {
                            Listener.OnComplete(this);
                        }
                        animTime = animTime % totalDuration;
                        currentFrameIndex = 0;
                        loopPlay++;
                    }
                    for (; animTime > GetFrame(currentFrameIndex).endTimer; )
                    {
                        currentFrameIndex++;
                    }
                }
            }
        }

        public virtual LTexture GetSpriteImage()
        {
            if (size == 0)
            {
                return null;
            }
            else
            {
                return GetFrame(currentFrameIndex).image;
            }
        }

        public virtual LTexture GetSpriteImage(int index)
        {
            if (index < 0 || index >= size)
            {
                return null;
            }
            else
            {
                return GetFrame(index).image;
            }
        }

        private AnimationFrame GetFrame(int index)
        {
            if (index < 0)
            {
                return (AnimationFrame)frames[0];
            }
            else if (index >= size)
            {
                return (AnimationFrame)frames[size - 1];
            }
            return (AnimationFrame)frames[index];
        }


        public void SetRunning(bool runing)
        {
            this.isRunning = runing;
        }

        public bool IsRunning()
        {
            return this.isRunning;
        }

        public int GetCurrentFrameIndex()
        {
            return this.currentFrameIndex;
        }

        public void SetCurrentFrameIndex(int index)
        {
            this.currentFrameIndex = index;
        }

        public int GetTotalFrames()
        {
            return size;
        }

        public int GetLoopCount()
        {
            return loopCount;
        }

        public void SetLoopCount(int loopCount)
        {
            this.loopCount = loopCount;
        }

        private class AnimationFrame : LRelease
        {

            internal LTexture image;

            internal long endTimer;

            public AnimationFrame(LTexture image, long endTimer)
            {
                this.image = image;
                this.endTimer = endTimer;
            }

            public void Dispose()
            {
                if (image != null)
                {
                    LTexture father = image.GetParent();
                    if (father != null && !father.isClose)
                    {
                        father.Destroy();
                    }
                    else if (image != null && !image.isClose)
                    {
                        image.Destroy();
                    }
                }
            }
        }

        public void Dispose()
        {
            if (frames != null)
            {
                foreach (AnimationFrame frame in frames)
                {
                    if (frame != null)
                    {
                        frame.Dispose();
                    }
                }
                frames.Clear();
            }
            this.size = 0;
        }
    }
}
