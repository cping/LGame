using Loon.Utils;
using Loon.Java;
using System;
using Loon.Core.Event;

namespace Loon.Core.Graphics.Opengl
{
    public class LTextures
    {
        private static System.Collections.Generic.Dictionary<string, LTexture> lazyTextures = new System.Collections.Generic.Dictionary<string, LTexture>(
            100);

        public static int Count()
        {
            return lazyTextures.Count;
        }

        public static LTexture LoadTexture(string path)
        {
            return LoadTexture(path, Loon.Core.Graphics.Opengl.LTexture.Format.DEFAULT);
        }

        public static bool ContainsValue(LTexture texture)
        {
            return lazyTextures.ContainsValue(texture);
        }

        public static int GetRefCount(LTexture texture)
        {
            return GetRefCount(texture.lazyName);
        }

        public static int GetRefCount(string fileName)
        {
            string key = fileName.Trim().ToLower();
            LTexture texture = (LTexture)CollectionUtils.Get(lazyTextures, key);
            if (texture != null)
            {
                return texture.refCount;
            }
            return 0;
        }

        public static LTexture LoadTexture(string fileName, Loon.Core.Graphics.Opengl.LTexture.Format format)
        {
            if (fileName == null)
            {
                return null;
            }
            lock (lazyTextures)
            {
                string key = fileName.Trim().ToLower();
                LTexture texture = (LTexture)CollectionUtils.Get(lazyTextures, key);
                if (texture != null && !texture.isClose)
                {
                    texture.refCount++;
                    return texture;
                }
                texture = new LTexture(fileName, format);
                texture.lazyName = fileName;
                CollectionUtils.Put(lazyTextures, key, texture);
                return texture;
            }
        }

        public static LTexture LoadTexture(LTexture texture)
        {
            return LoadTexture(JavaRuntime.CurrentTimeMillis(), texture);
        }

        public static LTexture LoadTexture(long id, LTexture tex2d)
        {
            if (tex2d == null)
            {
                return null;
            }
            lock (lazyTextures)
            {
                string key = tex2d.lazyName == null ? Convert.ToString(id)
                        : tex2d.lazyName;
                LTexture texture = (LTexture)CollectionUtils.Get(lazyTextures, key);
                if (texture != null && !texture.isClose)
                {
                    texture.refCount++;
                    return texture;
                }
                texture = tex2d;
                texture.lazyName = key;
                CollectionUtils.Put(lazyTextures, key, texture);
                return texture;
            }
        }

        private class DeleteUpdate : Updateable
        {

            public LTexture texture;

            public DeleteUpdate(LTexture tex)
            {
                this.texture = tex;
            }

            public void Action()
            {
                lock (texture)
                {
                    if (texture.textureID > 0)
                    {
                        if (texture.parent == null)
                        {
                            GLEx.DeleteTexture(texture.textureID);
                        }
                        texture.textureID = -1;
                        texture.bufferID = -1;
                    }
                    texture.isLoaded = false;
                    texture.isClose = true;
                    LTextureBatch.isBatchCacheDitry = true;
                }
            }
        }

        public static int RemoveTexture(string name, bool remove)
        {
            LTexture texture = (LTexture)CollectionUtils.Get(lazyTextures, name);
            if (texture != null)
            {
                if (texture.refCount <= 0)
                {
                    if (remove)
                    {
                        lock (lazyTextures)
                        {
                            CollectionUtils.Remove(lazyTextures, name);
                        }
                    }
                    if (!texture.isClose)
                    {
                        LSystem.Load(new DeleteUpdate(texture));
                        if (texture.imageData != null && texture.parent == null)
                        {
                            //XNA环境无需分别处理
                            //if (texture.imageData.fileName == null)
                            //{
                                if (texture.imageData.buffer != null)
                                {
                                    texture.imageData.buffer.Dispose();
                                    texture.imageData.buffer = null;
                                }
                                texture.imageData = null;
                           // }
                        }
                        if (texture.childs != null)
                        {
                            texture.childs.Clear();
                            texture.childs = null;
                        }
                    }
                }
                else
                {
                    texture.refCount--;
                }
                return texture.refCount;
            }
            return -1;
        }

        public static int RemoveTexture(LTexture texture, bool remove)
        {
            return RemoveTexture(texture.lazyName, remove);
        }

        private class ReloadUpdateable : Updateable
        {

            private LTexture texture;

            public ReloadUpdateable(LTexture tex2d)
            {
                this.texture = tex2d;
            }

            public void Action()
            {
                texture.LoadTexture();
                for (int i = 0; i < texture.childs.Count; i++)
                {
                    LTexture child = texture.childs[i];
                    if (child != null)
                    {
                        child.textureID = texture.textureID;
                        child.isLoaded = texture.isLoaded;
                        child.reload = texture.reload;
                    }
                }
                LTextureBatch.isBatchCacheDitry = true;
            }

        }

        /// <summary>
        /// 此函数在标准XNA环境下无需调用（因为标准XNA【暂停】时不会回收纹理资源），除非进行全面的纹理重制
        /// </summary>
        public static void Reload()
        {
            lock (lazyTextures)
            {
                if (lazyTextures.Count > 0)
                {
                    foreach (LTexture texture in lazyTextures.Values)
                    {
                        if (texture != null)
                        {
                            texture.isLoaded = false;
                            texture.reload = true;
                            texture._hashCode = 1;
                            if (texture.childs != null)
                            {

                                LSystem.Load(new ReloadUpdateable(texture));
                            }
                        }
                    }
                    LTextureBatch.isBatchCacheDitry = true;
                }
                GLUtils.Reload();
            }
        }

        public static void DisposeAll()
        {
            if (lazyTextures.Count > 0)
            {
                foreach (LTexture tex2d in lazyTextures.Values)
                {
                    if (tex2d != null && !tex2d.isClose)
                    {
                        tex2d.refCount = 0;
                        tex2d.Dispose(false);
                    }
                }
                lazyTextures.Clear();
            }
            LSTRDictionary.Dispose();
        }

        public static void DestroyAll()
        {
            if (lazyTextures.Count > 0)
            {
                foreach (LTexture tex2d in lazyTextures.Values)
                {
                    if (tex2d != null && !tex2d.isClose)
                    {
                        tex2d.refCount = 0;
                        tex2d.Destroy(false);
                    }
                }
                lazyTextures.Clear();
            }
            LSTRDictionary.Dispose();
        }
    }
}
