using loon.component;
using loon.utils;

namespace loon.events
{
    public class TouchedClick : ClickListener
    {

        private Touched _downTouch;

        private Touched _upTouch;

        private Touched _dragTouch;

        private Touched _allTouch;

        private bool _enabled = true, _downClick = false;

        private TArray<ClickListener> clicks;

        public virtual TouchedClick AddClickListener(ClickListener c)
        {
            if (c == null)
            {
                return this;
            }
            if (c == this)
            {
                return this;
            }
            if (clicks == null)
            {
                clicks = new TArray<ClickListener>(8);
            }
            if (!clicks.Contains(c))
            {
                clicks.Add(c);
            }
            return this;
        }

        public virtual void DoClick(LComponent comp)
        {
            if (!_enabled)
            {
                return;
            }
            if (_allTouch != null)
            {
                _allTouch.On(SysTouch.GetX(), SysTouch.GetY());
            }
            if (clicks != null)
            {
                for (int i = 0, size = clicks.size; i < size; i++)
                {
                    ClickListener listener = clicks.Get(i);
                    if (listener != null && listener != this)
                    {
                        listener.DoClick(comp);
                    }
                }
            }
        }


        public virtual void DownClick(LComponent comp, float x, float y)
        {
            if (!_enabled)
            {
                return;
            }
            if (_downTouch != null)
            {
                _downTouch.On(x, y);
            }
            if (clicks != null)
            {
                for (int i = 0, size = clicks.size; i < size; i++)
                {
                    ClickListener listener = clicks.Get(i);
                    if (listener != null && listener != this)
                    {
                        listener.DownClick(comp, x, y);
                    }
                }
            }
            _downClick = true;
        }

        public virtual void UpClick(LComponent comp, float x, float y)
        {
            if (!_enabled)
            {
                return;
            }
            if (_downClick)
            {
                if (_upTouch != null)
                {
                    _upTouch.On(x, y);
                }
                if (clicks != null)
                {
                    for (int i = 0, size = clicks.size; i < size; i++)
                    {
                        ClickListener listener = clicks.Get(i);
                        if (listener != null && listener != this)
                        {
                            listener.UpClick(comp, x, y);
                        }
                    }
                }
                _downClick = false;
            }
        }

        public virtual void DragClick(LComponent comp, float x, float y)
        {
            if (!_enabled)
            {
                return;
            }
            if (_dragTouch != null)
            {
                _dragTouch.On(x, y);
            }
            if (clicks != null)
            {
                for (int i = 0, size = clicks.size; i < size; i++)
                {
                    ClickListener listener = clicks.Get(i);
                    if (listener != null && listener != this)
                    {
                        listener.DragClick(comp, x, y);
                    }
                }
            }
        }

        public virtual Touched GetDownTouch()
        {
            return _downTouch;
        }

        public virtual void SetDownTouch(Touched downTouch)
        {
            this._downTouch = downTouch;
        }

        public virtual Touched GetUpTouch()
        {
            return _upTouch;
        }

        public virtual void SetUpTouch(Touched upTouch)
        {
            this._upTouch = upTouch;
        }

        public virtual Touched GetDragTouch()
        {
            return _dragTouch;
        }

        public virtual void SetDragTouch(Touched dragTouch)
        {
            this._dragTouch = dragTouch;
        }

        public virtual Touched GetAllTouch()
        {
            return _allTouch;
        }

        public virtual void SetAllTouch(Touched allTouch)
        {
            this._allTouch = allTouch;
        }

        public virtual bool IsEnabled()
        {
            return _enabled;
        }

        public virtual void SetEnabled(bool e)
        {
            _enabled = e;
        }

        public virtual bool IsClicked()
        {
            return _downClick;
        }

        public virtual Touched DownTouch
        {
            get
            {
                return _downTouch;
            }
            set
            {
                this._downTouch = value;
            }
        }


        public virtual Touched UpTouch
        {
            get
            {
                return _upTouch;
            }
            set
            {
                this._upTouch = value;
            }
        }


        public virtual Touched DragTouch
        {
            get
            {
                return _dragTouch;
            }
            set
            {
                this._dragTouch = value;
            }
        }


        public virtual Touched AllTouch
        {
            get
            {
                return _allTouch;
            }
            set
            {
                this._allTouch = value;
            }
        }


        public virtual bool Enabled
        {
            get
            {
                return _enabled;
            }
            set
            {
                _enabled = value;
            }
        }


        public virtual bool Clicked
        {
            get
            {
                return _downClick;
            }
        }

        public virtual void Clear()
        {
            if (clicks != null)
            {
                clicks.Clear();
            }
            _downClick = false;
        }

    }
}
