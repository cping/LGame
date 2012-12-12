using System.Collections.Generic;
using Loon.Core.Graphics.Opengl;
using System;
namespace Loon.Action.Sprite.Node
{

    public class LNAnimationTexture
    {

        protected internal float _duration;

        private List<LTexture> _asList;

        private List<Single> _timeList;

        private string _name;

        protected internal float _totalDuration;

        public LNAnimationTexture()
        {
            this._asList = new List<LTexture>();
            this._timeList = new List<Single>();
            this._totalDuration = 0f;
        }

        public LNAnimationTexture(string fileName, int width, int height)
            : this(fileName, fileName, -1, width, height, 3f)
        {

        }

        public LNAnimationTexture(string fileName, int maxFrame, int width,
                int height)
            : this(fileName, fileName, maxFrame, width, height, 3f)
        {

        }

        public LNAnimationTexture(string fileName, int maxFrame, int width,
                int height, float duration)
            : this(fileName, fileName, maxFrame, width, height, duration)
        {

        }

        public LNAnimationTexture(string aName, string fileName, int maxFrame,
                int width, int height, float duration)
            : this(aName, duration, Animation.GetDefaultAnimation(fileName, maxFrame,
                width, height, 0))
        {

        }

        public LNAnimationTexture(string aName, float duration, Animation al)
        {
            this._asList = new List<LTexture>(al.GetTotalFrames());
            this._timeList = new List<Single>(al.GetTotalFrames());
            this._name = aName;
            this._duration = duration;
            for (int i = 0; i < al.GetTotalFrames(); i++)
            {
                addAnimation(al.GetSpriteImage(i), _duration);
            }
        }

        public LNAnimationTexture(string aName, float duration, params string[] pathList)
        {
            this._asList = new List<LTexture>(pathList.Length);
            this._timeList = new List<Single>();
            this._name = aName;
            this._duration = duration;
            for (int i = 0; i < pathList.Length; i++)
            {
                this.AddAnimation(pathList[i]);
            }
        }

        public void AddAnimation(string path)
        {
            this.AddAnimation(path, this._duration);
        }

        public void addAnimation(LTexture tex2d, float time)
        {
            this._asList.Add(tex2d);
            this._timeList.Add(time);
            this._totalDuration += time;
        }

        public void AddAnimation(string path, float time)
        {
            this._asList.Add(LTextures.LoadTexture(path, Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR));
            this._timeList.Add(time);
            this._totalDuration += time;
        }

        public int FrameCount()
        {
            return this._asList.Count;
        }

        public LTexture GetFrame(int idx)
        {
            return this._asList[idx];
        }

        public LTexture GetFrameByTime(float time)
        {
            if (time == 0f)
            {
                return this._asList[0];
            }
            time *= this._totalDuration;
            for (int i = 0; i < this._timeList.Count; i++)
            {
                float num2 = this._timeList[i];
                if (time > num2)
                {
                    time -= num2;
                }
                else
                {
                    return this._asList[i];
                }
            }
            return this._asList[this._asList.Count - 1];
        }

        public float GetFrameTime(int idx)
        {
            return this._timeList[idx];
        }

        public void SetAnimationTime(float totalTime)
        {
            int count = this._timeList.Count;
            if (count > 0)
            {
                float item = totalTime / ((float)count);
                this._timeList.Clear();
                for (int i = 0; i < count; i++)
                {
                    this._timeList.Add(item);
                }
            }
        }

        public float GetDuration()
        {
            return this._duration;
        }

        public string GetName()
        {
            return this._name;
        }

        public void Dispose()
        {
            if (_asList != null)
            {
                foreach (LTexture tex2d in _asList)
                {
                    tex2d.Dispose();
                }
                _asList.Clear();
            }
        }
    }
}
