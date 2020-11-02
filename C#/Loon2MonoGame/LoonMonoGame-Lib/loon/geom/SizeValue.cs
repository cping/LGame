using java.lang;

namespace loon.geom
{
    public class SizeValue
    {

        private const string PIXEL = "px";

        private const string PERCENT = "%";

        private const string WIDTH_SUFFIX = "w";

        private const string HEIGHT_SUFFIX = "h";

        private const string WILDCARD = "*";

        private const float MAX_PERCENT = 100.0f;

        private readonly string value;

        private readonly float percentValue;

        private readonly float pixelValue;

        private readonly bool hasWidthSuffix;

        private readonly bool hasHeightSuffix;

        public SizeValue(int size) : this(size + PIXEL)
        {

        }

        public SizeValue(string valueParam)
        {
            if (valueParam != null)
            {
                if (valueParam.EndsWith(PERCENT + WIDTH_SUFFIX))
                {
                    hasWidthSuffix = true;
                    this.value = valueParam.Substring(0, valueParam.Length() - 1);
                }
                else if (valueParam.EndsWith(PERCENT + HEIGHT_SUFFIX))
                {
                    hasHeightSuffix = true;
                    this.value = valueParam.Substring(0, valueParam.Length() - 1);
                }
                else
                {
                    this.value = valueParam;
                }
            }
            else
            {
                this.value = valueParam;
            }
            this.percentValue = GetPercentValue();
            this.pixelValue = GetPixelValue();
        }

        public bool IsPercentOrPixel()
        {
            return IsPercent() || IsPixel();
        }

        public float GetValue(float range)
        {
            if (IsPercent())
            {
                return (range / MAX_PERCENT) * percentValue;
            }
            else if (IsPixel())
            {
                return pixelValue;
            }
            else
            {
                return -1;
            }
        }

        public int GetValueAsInt(float range)
        {
            return (int)GetValue(range);
        }

        private float GetPercentValue()
        {
            if (IsPercent())
            {
                string percent = value.Substring(0,
                        value.Length() - PERCENT.Length());
                return Float.ParseFloat(percent);
            }
            else
            {
                return 0;
            }
        }

        private int GetPixelValue()
        {
            if (IsPixel())
            {
                if (HasNoSuffix())
                {
                    return Integer.ParseInt(value);
                }
                string pixel = value.Substring(0, value.Length() - PIXEL.Length());
                return Integer.ParseInt(pixel);
            }
            else
            {
                return 0;
            }
        }

        private bool IsPercent()
        {
            if (value == null)
            {
                return false;
            }
            else
            {
                return value.EndsWith(PERCENT);
            }
        }

        public bool IsPixel()
        {
            if (value == null)
            {
                return false;
            }
            else
            {
                return !value.Equals(WILDCARD)
                        && (value.EndsWith(PIXEL) || HasNoSuffix());
            }
        }

        private bool HasNoSuffix()
        {
            if (value == null)
            {
                return false;
            }

            if (value.EndsWith(PIXEL) || value.EndsWith(PERCENT)
                    || value.EndsWith(WIDTH_SUFFIX)
                    || value.EndsWith(HEIGHT_SUFFIX))
            {
                return false;
            }
            return true;
        }

        public bool HasWidthSuffix()
        {
            return hasWidthSuffix;
        }

        public bool HasHeightSuffix()
        {
            return hasHeightSuffix;
        }

        public bool HasWildcard()
        {
            return WILDCARD.Equals(value);
        }

        public override string ToString()
        {
            return value;
        }
    }
}
