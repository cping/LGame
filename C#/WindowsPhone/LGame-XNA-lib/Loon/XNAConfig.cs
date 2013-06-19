using Loon.Core.Graphics.Opengl;
using System.Collections.Generic;
using Loon.Core.Graphics;
using Loon.Utils;
using System.IO;
using System;
using Microsoft.Xna.Framework.Graphics;
namespace Loon
{
    public class XNAConfig
    {
        private static System.Resources.ResourceManager resourceMan;

        private static bool isActive = false;

        private static Dictionary<string, LTexture> texCaches = new Dictionary<string, LTexture>(CollectionUtils.INITIAL_CAPACITY);

        public static bool IsActive()
        {
            return isActive;
        }

        internal static System.Resources.ResourceManager ResourceManager
        {
            get
            {
                if (object.ReferenceEquals(resourceMan, null))
                {
                    System.Resources.ResourceManager resMan = new System.Resources.ResourceManager("Loon-backend-XNA.g", typeof(LGameXNA2DActivity).Assembly);
                    resourceMan = resMan;
                    resourceMan.IgnoreCase = true;
                }
                return resourceMan;
            }
        }

        public static void Load()
        {
            if (ResourceManager != null)
            {
                isActive = true;
            }
        }

        private static void VaildLoon()
        {
            if (!isActive)
            {
                throw new NotImplementedException("Has not been loaded config !");
            }
        }

        public static LTexture LoadTex(string name)
        {
            VaildLoon();
            LTexture texture = (LTexture)CollectionUtils.Get(texCaches, name);
            if (texture == null || texture.isClose)
            {
                try
                {
                    LTextureData data = GLLoader.GetTextureData(ResourceManager.GetStream(name));
                    data.fileName = name;
                    texture = new LTexture(data);
                    texture.isExt = true;
                }
                catch (Exception ex)
                {
                    Loon.Utils.Debugging.Log.Exception(ex);
                }
                texCaches.Add(name, texture);
            }
            return texture;
        }

        public static Stream LoadStream(string name)
        {
            VaildLoon();
            return ResourceManager.GetStream(name);
        }

        public static void Dispose()
        {
            if (texCaches != null)
            {
                foreach (LTexture tex in texCaches.Values)
                {
                    if (tex != null)
                    {
                        tex.Destroy();
                    }
                }
                texCaches.Clear();
            }
        }
    }
}
