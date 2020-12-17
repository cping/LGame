using loon.geom;
using loon.utils.reply;
using System;

namespace loon.utils
{
	public class Scale
	{

		public enum Mode
		{
			NONE,
			FILL,
			FILL_X,
			FILL_Y,
			FIT,
			STRETCH
		}

		public class ScaledResource
		{

			public readonly Scale scale;

			public readonly string path;

			public ScaledResource(Scale scale, string path)
			{
				this.scale = scale;
				this.path = path;
			}

			public override string ToString()
			{
				return scale + ": " + path;
			}
		}

		public static readonly Scale ONE = new Scale(1f);

		public readonly float factor;

		public Scale(float factor)
		{
			this.factor = factor;
		}

		public virtual float Scaled(float length)
		{
			return factor * length;
		}

		/// <summary>
		/// 成比例的缩放目标大小为指定大小
		/// </summary>
		/// <param name="scaling"> </param>
		/// <param name="srcWidth"> </param>
		/// <param name="srcHeight"> </param>
		/// <param name="tarWidth"> </param>
		/// <param name="tarHeight">
		/// @return </param>
		public virtual Pair<Vector2f, Vector2f> ScaledSize(Mode scaling, float srcWidth, float srcHeight, float tarWidth, float tarHeight)
		{
			return ScaledSize(scaling, srcWidth, srcHeight, tarWidth, tarHeight);
		}

		/// <summary>
		/// 成比例的缩放目标大小为指定大小
		/// </summary>
		/// <param name="mode"> </param>
		/// <param name="powerOfTwo"> </param>
		/// <param name="srcWidth"> </param>
		/// <param name="srcHeight"> </param>
		/// <param name="tarWidth"> </param>
		/// <param name="tarHeight">
		/// @return </param>
		public virtual Pair<Vector2f, Vector2f> ScaledSize(Mode mode, bool powerOfTwo, float srcWidth, float srcHeight, float tarWidth, float tarHeight)
		{
			return ScaledSize(mode, new Vector2f(), new Vector2f(), powerOfTwo, srcWidth, srcHeight, tarWidth, tarHeight);
		}

		/// <summary>
		/// 成比例的缩放目标大小为指定大小
		/// </summary>
		/// <param name="mode"> </param>
		/// <param name="sizeResult"> </param>
		/// <param name="scaleResult"> </param>
		/// <param name="powerOfTwo"> </param>
		/// <param name="srcWidth"> </param>
		/// <param name="srcHeight"> </param>
		/// <param name="tarWidth"> </param>
		/// <param name="tarHeight">
		/// @return </param>
		public virtual Pair<Vector2f, Vector2f> ScaledSize(Mode mode, Vector2f sizeResult, Vector2f scaleResult, bool powerOfTwo, float srcWidth, float srcHeight, float tarWidth, float tarHeight)
		{



			if (mode == default)
			{
				mode = Mode.NONE;
			}

			float targetRatio = this.factor;
			float sourceRatio = this.factor;
			float scaleValue = this.factor;

			switch (mode)
			{
				case Mode.FILL:
					targetRatio = tarHeight / tarWidth;
					sourceRatio = srcHeight / srcWidth;
					scaleValue = targetRatio < sourceRatio ? tarWidth / srcWidth : tarHeight / srcHeight;
					if (powerOfTwo)
					{
						scaleValue = MathUtils.PreviousPowerOfTwo(MathUtils.Ceil(scaleValue));
					}
					sizeResult.Set(srcWidth * scaleValue, srcHeight * scaleValue);
					scaleResult.Set(scaleValue, scaleValue);
					break;
				case Mode.FILL_X:
					scaleValue = tarWidth / srcWidth;
					if (powerOfTwo)
					{
						scaleValue = MathUtils.PreviousPowerOfTwo(MathUtils.Ceil(scaleValue));
					}
					sizeResult.Set(srcWidth * scaleValue, srcHeight * scaleValue);
					scaleResult.Set(scaleValue, scaleValue);
					break;
				case Mode.FILL_Y:
					scaleValue = tarHeight / srcHeight;
					if (powerOfTwo)
					{
						scaleValue = MathUtils.PreviousPowerOfTwo(MathUtils.Ceil(scaleValue));
					}
					sizeResult.Set(srcWidth * scaleValue, srcHeight * scaleValue);
					scaleResult.Set(scaleValue, scaleValue);
					break;
				case Mode.FIT:
					targetRatio = tarHeight / tarWidth;
					sourceRatio = srcHeight / srcWidth;
					scaleValue = targetRatio > sourceRatio ? tarWidth / srcWidth : tarHeight / srcHeight;
					if (powerOfTwo)
					{
						scaleValue = MathUtils.PreviousPowerOfTwo(MathUtils.Floor(scaleValue));
					}
					sizeResult.Set(srcWidth * scaleValue, srcHeight * scaleValue);
					scaleResult.Set(scaleValue, scaleValue);
					break;
				case Mode.STRETCH:
					float scaleX = tarWidth / srcWidth;
					float scaleY = tarHeight / srcHeight;
					if (powerOfTwo)
					{
						scaleX = MathUtils.PreviousPowerOfTwo(MathUtils.Ceil(scaleX));
						scaleY = MathUtils.PreviousPowerOfTwo(MathUtils.Ceil(scaleY));
					}
					sizeResult.Set(tarWidth, tarHeight);
					scaleResult.Set(scaleX, scaleY);
					break;
				case Mode.NONE:
				default:
					sizeResult.Set(srcWidth, srcHeight);
					scaleResult.Set(1f);
					break;
			}
			return Pair<Vector2f, Vector2f>.Get(sizeResult, scaleResult);

		}

		public virtual int ScaledCeil(float length)
		{
			return MathUtils.Iceil(Scaled(length));
		}

		public virtual int ScaledFloor(float length)
		{
			return MathUtils.Ifloor(Scaled(length));
		}

		public virtual float InvScaled(float length)
		{
			return length / factor;
		}

		public virtual int InvScaledFloor(float length)
		{
			return MathUtils.Ifloor(InvScaled(length));
		}

		public virtual int InvScaledCeil(float length)
		{
			return MathUtils.Iceil(InvScaled(length));
		}

		public virtual TArray<ScaledResource> GetScaledResources(string path)
		{
			TArray<ScaledResource> rsrcs = new TArray<ScaledResource>();
			rsrcs.Add(new ScaledResource(this, ComputePath(path, factor)));
			for (float rscale = MathUtils.Ifloor(factor); rscale > 1; rscale -= 1)
			{
				if (rscale != factor)
				{
					rsrcs.Add(new ScaledResource(new Scale(rscale), ComputePath(path, rscale)));
				}
			}
			rsrcs.Add(new ScaledResource(ONE, path));
			return rsrcs;
		}

		private string ComputePath(string path, float scale)
		{
			if (scale <= 1f)
			{
				return path;
			}
			int scaleFactor = (int)(scale * 10);
			if (scaleFactor % 10 == 0)
			{
				scaleFactor /= 10;
			}
			int didx = path.LastIndexOf(".", StringComparison.Ordinal);
			if (didx == -1)
			{
				return path;
			}
			else
			{
				return path.Substring(0, didx) + "@" + scaleFactor + "x" + path.Substring(didx);
			}
		}

		public override string ToString()
		{
			return "x" + factor;
		}
	}
}
