using System;
using System.Collections.Generic;
using Loon.Utils;
using Loon.Java;
using Loon.Utils.Debug;

namespace Loon.Core.Graphics.OpenGL
{
    public class LTextures
    {

        private static Dictionary<string, LTexture> lazyTextures = new Dictionary<string, LTexture>(100);

        public static int Count()
        {
            return lazyTextures.Count;
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

        public static LTexture LoadTexture(string fileName)
        {
            if (fileName == null)
            {
                return null;
            }
            lock (lazyTextures)
            {
                string key = fileName.Trim().ToLower();
                LTexture texture = (LTexture)CollectionUtils.Get(lazyTextures, key);
                try
                {
                    
                    if (texture != null && !texture.isClose)
                    {
                        texture.refCount++;
                        return texture;
                    }
                    texture = new LTexture(fileName);
                    texture.lazyName = fileName;
                    CollectionUtils.Put(lazyTextures, key, texture);
                }
                catch (Exception ex)
                {
                    Log.Exception(ex);
                }
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
                string key = tex2d.lazyName == null ? Convert.ToString(id) : tex2d.lazyName;
                LTexture texture = (LTexture)CollectionUtils.Get(lazyTextures, key);
                try
                {
                    if (texture != null && !texture.isClose)
                    {
                        texture.refCount++;
                        return texture;
                    }
                    texture = tex2d;
                    texture.lazyName = key;
                    CollectionUtils.Put(lazyTextures, key, texture);
                }
                catch (Exception ex)
                {
                    Log.Exception(ex);
                }
                return texture;
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
                            lazyTextures.Remove(name);
                        }
                    }
                    if (!texture.isClose)
                    {
                        if (texture.childs != null)
                        {
                            CollectionUtils.Clear(texture.childs);
                            texture.childs = null;
                        }
                        if (texture.tex2d != null)
                        {
                            texture.tex2d.Dispose();
                            texture.tex2d = null;
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
                            texture.hashCode = 1;
                            if (texture.childs != null)
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

                            }
                        }
                    }
                }
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
        }
    }

}

