using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;

namespace Loon.Core.Graphics.OpenGL
{
    public class GL
    {
        public const int GL_QUADS = 0;

        public const int GL_POLYGON = 0;

        public const int GL_TRIANGLE_FAN = 0;

        public const int GL_TRIANGLES = 0;

        public const int GL_TRIANGLE_STRIP = 0;

        public const int GL_POINTS = 1;

        public const int GL_LINES = 1;

        public const int GL_LINE_LOOP = 1;

        public const int GL_LINE_STRIP = 1;

        private float sAlpha = 1f;

        private bool sbColorArray = false;

        private bool sbInitViewPort = false;

        private float sBlue = 1f;

        private bool sbTextures = false;

        protected BasicEffect sEffect = null;

        private float sGreen = 1f;

        protected Matrix sMatrix = new Matrix();

        protected List<Matrix> sMatrixStack = new List<Matrix>(10);

        protected Matrix sProjMatrix = new Matrix();

        private float sRed = 1f;

        private int sTextureId = 0;

        protected List<Texture2D> sTextures = new List<Texture2D>();

        private int[] sViewPort = new int[4];

        public GL()
        {
            sEffect = new BasicEffect(GLEx.device);
            sTextures.Add(null);
            SamplerState state2 = new SamplerState();
            state2.AddressU = TextureAddressMode.Clamp;
            state2.AddressV = TextureAddressMode.Clamp;
            GLEx.device.SamplerStates[0] = state2;
            DepthStencilState state3 = new DepthStencilState();
            state3.DepthBufferEnable = false;
            state3.DepthBufferWriteEnable = false;
            state3.DepthBufferFunction = CompareFunction.Always;
            GLEx.device.DepthStencilState = state3;
            EnableTextures();
        }

        public void GLBindTexture(int inTextureId)
        {
            if (inTextureId != sTextureId)
            {
                sTextureId = inTextureId;
                sEffect.Texture = sTextures[sTextureId];
            }
        }

        public int CreateTexture(Texture2D inTexture2d)
        {
            int count = sTextures.Count;
            sTextures.Add(inTexture2d);
            return count;
        }

        public void DisableColorArray()
        {
            if (sbColorArray)
            {
                sbColorArray = false;
                sEffect.VertexColorEnabled = false;
            }
        }

        public void DisableTextures()
        {
            if (sbTextures)
            {
                sbTextures = false;
                sEffect.TextureEnabled = false;
            }
        }

        public void DrawElements(PrimitiveType inType, VertexPositionColor[] inVerts, short[] inIndices)
        {
            sEffect.World = sMatrix;
            sEffect.View = Matrix.CreateLookAt(new Vector3(0f, 0f, 0f), new Vector3(0f, 0f, -1f), new Vector3(0f, 1f, 0f));
            sEffect.Projection = sProjMatrix;
            foreach (EffectPass pass in sEffect.CurrentTechnique.Passes)
            {
                pass.Apply();
                GLEx.device.BlendState = BlendState.AlphaBlend;
                GLEx.device.DrawUserIndexedPrimitives<VertexPositionColor>(inType, inVerts, 0, inVerts.Length, inIndices, 0, inIndices.Length - 2);
            }
        }

        public void DrawElements(PrimitiveType inType, VertexPositionColorTexture[] inVerts, short[] inIndices)
        {
            sEffect.World = sMatrix;
            sEffect.View = Matrix.CreateLookAt(new Vector3(0f, 0f, 0f), new Vector3(0f, 0f, -1f), new Vector3(0f, 1f, 0f));
            sEffect.Projection = sProjMatrix;
            foreach (EffectPass pass in sEffect.CurrentTechnique.Passes)
            {
                pass.Apply();
                GLEx.device.BlendState = BlendState.AlphaBlend;
                GLEx.device.DrawUserIndexedPrimitives<VertexPositionColorTexture>(inType, inVerts, 0, inVerts.Length, inIndices, 0, inIndices.Length - 2);
            }
        }

        public void DrawElements(PrimitiveType inType, VertexPositionTexture[] inVerts, short[] inIndices)
        {
            sEffect.World = sMatrix;
            sEffect.View = Matrix.CreateLookAt(new Vector3(0f, 0f, 0f), new Vector3(0f, 0f, -1f), new Vector3(0f, 1f, 0f));
            sEffect.Projection = sProjMatrix;
            foreach (EffectPass pass in sEffect.CurrentTechnique.Passes)
            {
                pass.Apply();
                GLEx.device.BlendState = BlendState.AlphaBlend;
                GLEx.device.DrawUserIndexedPrimitives<VertexPositionTexture>(inType, inVerts, 0, inVerts.Length, inIndices, 0, inIndices.Length - 2);
            }
        }

        public void DrawElements(PrimitiveType inType, VertexPositionColor[] inVerts, short[] inIndices, int inIndexCount)
        {
            sEffect.World = sMatrix;
            sEffect.View = Matrix.CreateLookAt(new Vector3(0f, 0f, 0f), new Vector3(0f, 0f, -1f), new Vector3(0f, 1f, 0f));
            sEffect.Projection = sProjMatrix;
            foreach (EffectPass pass in sEffect.CurrentTechnique.Passes)
            {
                pass.Apply();
                GLEx.device.BlendState = BlendState.AlphaBlend;
                GLEx.device.DrawUserIndexedPrimitives<VertexPositionColor>(inType, inVerts, 0, inVerts.Length, inIndices, 0, inIndexCount - 2);
            }
        }

        public void EnableColorArray()
        {
            if (!sbColorArray)
            {
                sbColorArray = true;
                sEffect.VertexColorEnabled = true;
            }
        }

        public void EnableTextures()
        {
            if (!sbTextures)
            {
                sbTextures = true;
                sEffect.TextureEnabled = true;
            }
        }

        public Texture2D GetTexture(int inIndex)
        {
            return sTextures[inIndex];
        }

        public int[] GetViewPort()
        {
            if (!sbInitViewPort)
            {
                sbInitViewPort = true;
                sViewPort[0] = 0;
                sViewPort[1] = 0;
                sViewPort[2] = 320;
                sViewPort[3] = 0x214;
            }
            return sViewPort;
        }


        public void LoadIdentity()
        {
            sMatrix = Matrix.CreateScale((float)1f);
        }

        public void PopMatrix()
        {
            sMatrix = sMatrixStack[sMatrixStack.Count - 1];
            sMatrixStack.Remove(sMatrix);
        }

        public void PushMatrix()
        {
            sMatrixStack.Add(new Matrix(sMatrix.M11, sMatrix.M12, sMatrix.M13, sMatrix.M14, sMatrix.M21, sMatrix.M22, sMatrix.M23, sMatrix.M24, sMatrix.M31, sMatrix.M32, sMatrix.M33, sMatrix.M34, sMatrix.M41, sMatrix.M42, sMatrix.M43, sMatrix.M44));
        }

        public void ReplaceTexture(int inIndex, Texture2D inTexture2d)
        {
            sTextures[inIndex] = inTexture2d;
        }

        public void Rotate(float x, float y, float z)
        {
            if (!(x == 0f))
            {
                sMatrix = Matrix.CreateRotationX((x * 3.141593f) / 180f) * sMatrix;
            }
            if (!(y == 0f))
            {
                sMatrix = Matrix.CreateRotationY((y * 3.141593f) / 180f) * sMatrix;
            }
            if (!(z == 0f))
            {
                sMatrix = Matrix.CreateRotationZ((z * 3.141593f) / 180f) * sMatrix;
            }
        }

        public void Scale(float inXScale, float inYScale, float inZScale)
        {
            if (((inXScale != 1f) || (inYScale != 1f)) || (inZScale != 1f))
            {
                sMatrix = Matrix.CreateScale(inXScale, inYScale, inZScale) * sMatrix;
            }
        }

        public void SetColor(float inRed, float inGreen, float inBlue, float inAlpha)
        {
            if ((((inRed != sRed) || (inGreen != sGreen)) || (inBlue != sBlue)) || (inAlpha != sAlpha))
            {
                sRed = inRed;
                sGreen = inGreen;
                sBlue = inBlue;
                sAlpha = inAlpha;
                sEffect.DiffuseColor = new Vector3(sRed, sGreen, sBlue);
                sEffect.Alpha = sAlpha;
            }
        }

        public void Translate(float x, float y, float z)
        {
            if (((x != 0f) || (y != 0f)) || (z != 0f))
            {
                sMatrix = Matrix.CreateTranslation(x, y, z) * sMatrix;
            }
        }

    }
}
