using loon.utils;

namespace loon.geom
{
    public class Clip
    {

        private Clip _parent;

        private int _displayWidth;

        private int _displayHeight;

        private float _offX, _offY;

        private float _widthRatio, _heightRatio;

        private float _factor;

        private int _regionWidth, _regionHeight;

        public Clip(float x, float y, float w, float h) : this(x, y, w, h, false)
        {

        }

        public Clip(float x, float y, float w, float h, bool updateSize) : this(null, 1f, (int)x, (int)y, (int)w, (int)h, updateSize)
        {

        }

        public Clip(int x, int y, int w, int h, bool updateSize) : this(null, 1f, x, y, w, h, updateSize)
        {

        }

        public Clip(Clip parent, float x, float y, float w, float h, bool updateSize) : this(parent, 1f, (int)x, (int)y, (int)w, (int)h, updateSize)
        {

        }

        public Clip(Clip parent, int x, int y, int w, int h, bool updateSize) : this(parent, 1f, x, y, w, h, updateSize)
        {

        }

        public Clip(Clip parent, float factor, int x, int y, int w, int h, bool updateSize)
        {
            this._parent = parent;
            this._factor = factor;
            if (parent == null)
            {
                this._displayWidth = w;
                this._displayHeight = h;
                this.SetRegion(x, y, w, h);
            }
            else
            {
                this.SetRegion(parent, x, y, w, h, updateSize);
            }
        }

        public Clip GetParent()
        {
            return this._parent;
        }

        public Clip SetRegion(Clip region, bool updateSize)
        {
            if (region != null)
            {
                if (updateSize)
                {
                    this._displayWidth = region.GetRegionWidth();
                    this._displayHeight = region.GetRegionHeight();
                }
                else
                {
                    this._displayWidth = region._displayWidth;
                    this._displayHeight = region._displayHeight;
                }
                SetRegion(region._offX, region._offY, region._widthRatio, region._heightRatio);
            }
            return this;
        }

        public Clip SetRegion(Clip region, int x, int y, int width, int height, bool updateSize)
        {
            if (region != null)
            {
                if (updateSize)
                {
                    this._displayWidth = region.GetRegionWidth();
                    this._displayHeight = region.GetRegionHeight();
                }
                else
                {
                    this._displayWidth = region._displayWidth;
                    this._displayHeight = region._displayHeight;
                }
                SetRegion(region.GetRegionX() + x, region.GetRegionY() + y, width, height);
            }
            else
            {
                SetRegion(x, y, width, height);
            }
            return this;
        }

        public Clip SetRegion(int x, int y, int width, int height)
        {
            float invTexWidth = _factor / _displayWidth;
            float invTexHeight = _factor / _displayHeight;
            SetRegion(x * invTexWidth, y * invTexHeight, (x + width) * invTexWidth, (y + height) * invTexHeight);
            _regionWidth = MathUtils.Abs(width);
            _regionHeight = MathUtils.Abs(height);
            return this;
        }

        public Clip SetRegion(float u, float v, float u2, float v2)
        {
            int texWidth = _displayWidth, texHeight = _displayHeight;
            _regionWidth = MathUtils.Round(MathUtils.Abs(u2 - u) * texWidth);
            _regionHeight = MathUtils.Round(MathUtils.Abs(v2 - v) * texHeight);
            if (_regionWidth == _factor && _regionHeight == _factor)
            {
                float adjustX = 0.25f / texWidth;
                u += adjustX;
                u2 -= adjustX;
                float adjustY = 0.25f / texHeight;
                v += adjustY;
                v2 -= adjustY;
            }
            this._offX = u;
            this._offY = v;
            this._widthRatio = u2;
            this._heightRatio = v2;
            return this;
        }

        public float GetDisplayWidth()
        {
            return _displayWidth;
        }

        public float GetDisplayHeight()
        {
            return _displayHeight;
        }

        public float Sx()
        {
            return _offX;
        }

        public float Sy()
        {
            return _offY;
        }

        public float Tx()
        {
            return _widthRatio;
        }

        public float Ty()
        {
            return _heightRatio;
        }

        public float XOff()
        {
            return _offX;
        }

        public float YOff()
        {
            return _offY;
        }

        public float WidthRatio()
        {
            return _widthRatio;
        }

        public float HeightRatio()
        {
            return _heightRatio;
        }

        public Clip SetU(float offX)
        {
            this._offX = offX;
            _regionWidth = MathUtils.Round(MathUtils.Abs(_widthRatio - _offX) * _displayWidth);
            return this;
        }

        public float GetV()
        {
            return _offY;
        }

        public Clip SetV(float offY)
        {
            this._offY = offY;
            _regionHeight = MathUtils.Round(MathUtils.Abs(_heightRatio - _offY) * _displayHeight);
            return this;
        }

        public float GetU2()
        {
            return _widthRatio;
        }

        public Clip SetU2(float ratio)
        {
            this._widthRatio = ratio;
            _regionWidth = MathUtils.Round(MathUtils.Abs(_widthRatio - _offX) * _displayWidth);
            return this;
        }

        public float GetV2()
        {
            return _heightRatio;
        }

        public Clip SetV2(float ratio)
        {
            this._heightRatio = ratio;
            _regionHeight = MathUtils.Round(MathUtils.Abs(_heightRatio - _offY) * _displayHeight);
            return this;
        }

        public int GetRegionX()
        {
            return MathUtils.Round(_offX * _displayWidth);
        }

        public Clip SetRegionX(int x)
        {
            SetU(x / (float)_displayWidth);
            return this;
        }

        public int GetRegionY()
        {
            return MathUtils.Round(_offY * _displayHeight);
        }

        public Clip SetRegionY(int y)
        {
            SetV(y / (float)_displayHeight);
            return this;
        }

        public int GetRegionWidth()
        {
            return _regionWidth;
        }

        public Clip SetRegionWidth(int width)
        {
            if (IsFlipX())
            {
                SetU(_widthRatio + width / (float)_displayWidth);
            }
            else
            {
                SetU2(_offX + width / (float)_displayWidth);
            }
            return this;
        }

        public int GetRegionHeight()
        {
            return _regionHeight;
        }

        public Clip SetRegionHeight(int height)
        {
            if (IsFlipY())
            {
                SetV(_heightRatio + height / (float)_displayHeight);
            }
            else
            {
                SetV2(_offY + height / (float)_displayHeight);
            }
            return this;
        }

        public Clip Flip(bool x, bool y)
        {
            if (x)
            {
                float temp = _offX;
                _offX = _widthRatio;
                _widthRatio = temp;
            }
            if (y)
            {
                float temp = _offY;
                _offY = _heightRatio;
                _heightRatio = temp;
            }
            return this;
        }

        public Clip Offset(float xAmount, float yAmount)
        {
            if (xAmount != 0)
            {
                float width = (_widthRatio - _offX) * _displayWidth;
                _offX = (_offX + xAmount) % 1;
                _widthRatio = _offX + width / _displayWidth;
            }
            if (yAmount != 0)
            {
                float height = (_heightRatio - _offY) * _displayHeight;
                _offY = (_offY + yAmount) % 1;
                _heightRatio = _offY + height / _displayHeight;
            }
            return this;
        }

        public bool IsFlipX()
        {
            return _offX > _widthRatio;
        }

        public bool IsFlipY()
        {
            return _offY > _heightRatio;
        }

        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("Clip");
            builder.Kv("x", GetRegionX()).Comma().Kv("y", GetRegionY()).Comma().Kv("width", GetRegionWidth()).Comma()
                    .Kv("height", GetRegionHeight());
            return builder.ToString();
        }
    }
}
