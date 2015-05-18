using UnityEngine;
using System.Collections.Generic;

namespace Loon{

	public class UnityGUI 
	{
		private Font FontReplace = null;
		private float PixelWidth = 480.0f;
		private float PixelHeight = 320.0f;
		private float DisplayWidth = 480.0f;
		private float DisplayHeight = 320.0f;
		private Rect DrawRect;

		/// <summary>
		/// 默认字体替换
		/// </summary>
		/// <param name="fontReplace">Font replace.</param>
		public void GetFontReplace(ref Font fontReplace)
		{
			fontReplace = FontReplace;
		}

		/// <summary>
		/// 获得当前默认字体
		/// </summary>
		/// <param name="fontReplace">Font replace.</param>
		public void SetFontReplace(Font fontReplace)
		{
			FontReplace = fontReplace;
		}

		/// <summary>
		/// 调整分辨率
		/// </summary>
		/// <param name="pixelWidth">Pixel width.</param>
		/// <param name="pixelHeight">Pixel height.</param>
		public void GetPixelResolution(ref float pixelWidth, ref float pixelHeight)
		{
			pixelWidth = PixelWidth;
			pixelHeight = PixelHeight;
		}

		/// <summary>
		/// 调整分辨率
		/// </summary>
		/// <param name="pixelWidth">Pixel width.</param>
		/// <param name="pixelHeight">Pixel height.</param>
		public void SetPixelResolution(float pixelWidth, float pixelHeight)
		{
			PixelWidth = pixelWidth;
			PixelHeight = pixelHeight;
		}

		/// <summary>
		/// 调整分辨率
		/// </summary>
		/// <param name="Width">Width.</param>
		/// <param name="Height">Height.</param>
		public void GetDisplayResolution(ref float Width, ref float Height)
		{
			Width = DisplayWidth;
			Height = DisplayHeight;
		}

		public void SetDisplayResolution(float Width, float Height)
		{
			DisplayWidth = Width;
			DisplayHeight = Height;
		}

		public void DrawString(object label, float x, float y, int fontSize, Color fontColor) {
			GUIStyle style = new GUIStyle(GUI.skin.GetStyle("label"));
			if(GUI.skin.font != FontReplace) {
				GUI.skin.font = FontReplace;
			}
			style.fontSize = fontSize;
			style.onFocused.textColor =
				style.onActive.textColor =
					style.onHover.textColor =
					style.onNormal.textColor =
					style.normal.textColor = fontColor;
			GUI.Label (new Rect(x, y, Screen.width, Screen.height), label.ToString (), style);
		}

		public void StereoBox(int X, int Y, int wX, int hY, ref string text, Color color)
		{
			Font prevFont = GUI.skin.font;
			GUI.color = color;
			if(GUI.skin.font != FontReplace) {
				GUI.skin.font = FontReplace;
			}
			float s = PixelWidth / DisplayWidth;
			CalcPositionAndSize(X * s, Y * s, wX * s, hY * s, ref DrawRect);
			GUI.Box(DrawRect, text);
			GUI.skin.font = prevFont;
		}

		public void StereoBox(float X, float Y, float wX, float hY, ref string text, Color color)
		{
			StereoBox ((int)(X * PixelWidth),
			           (int)(Y * PixelHeight),
			           (int)(wX * PixelWidth),
			           (int)(hY * PixelHeight),
			           ref text, color);
		}
	
		public void StereoDrawTexture(int X, int Y, int wX, int hY, ref Texture image, Color color)
		{
			GUI.color = color;
			if(GUI.skin.font != FontReplace){
				GUI.skin.font = FontReplace;
			}
			float s = PixelWidth / DisplayWidth;
			CalcPositionAndSize(X * s, Y * s, wX * s, hY * s, ref DrawRect);
			GUI.DrawTexture(DrawRect, image);
		}
	
		public void StereoDrawTexture(float X, float Y, float wX, float hY, ref Texture image, Color color)
		{
			StereoDrawTexture ((int)(X * PixelWidth),
			                   (int)(Y * PixelHeight),
			                   (int)(wX * PixelWidth),
			                   (int)(hY * PixelHeight),
			                   ref image, color);
		}

		private void CalcPositionAndSize(float X, float Y, float wX, float hY,
		                                 ref Rect calcPosSize)
		{
			float sSX = (float)Screen.width / PixelWidth;
			float sSY = (float)Screen.height / PixelHeight;
			calcPosSize.x = X * sSX;
			calcPosSize.width = wX * sSX;
			calcPosSize.y = Y * sSY;
			calcPosSize.height = hY * sSY;
		}
	}

}
