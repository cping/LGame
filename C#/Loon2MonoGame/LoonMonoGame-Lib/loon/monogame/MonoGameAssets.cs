using java.lang;
using java.util;
using loon.utils;
using Microsoft.Xna.Framework;
using System.IO;

namespace loon.monogame
{
    public class MonoGameAssets : Assets
    {
        private MonoGameGame game;
        public MonoGameAssets(MonoGameGame game) : base(game.Asyn())
        {
            this.game = game;
        }

        protected internal static sbyte[] OpenEmbeddedResourceBytes(string path)
        {
            var assembly = typeof(LGame).Assembly;
            using (Stream stream = assembly.GetManifestResourceStream(path))
            {
                return ToByteArray(stream);
            }
        }

        internal static sbyte[] OpenMonoGameEmbeddedResourceBytes(string path)
        {
            var assembly = typeof(MathHelper).Assembly;
#if FNA
			name = name.Replace( ".ogl.mgfxo", ".fxb" );
#else
            if (StringUtils.Contains(path, assembly.GetManifestResourceNames()))
            {
                path = path.Replace(".Framework", ".Framework.Platform");
            }
#endif

            using (Stream stream = assembly.GetManifestResourceStream(path))
            {
                return ToByteArray(stream);
            }
        }

        public virtual sbyte[] OpenFileResourceBytes(string path)
        {
#if FNA
			path = path.Replace( ".mgfxo", ".fxb" );
#endif

            byte[] buffer;
            try
            {
                using (Stream stream = OpenStream(path))
                {
                    if (stream.CanSeek)
                    {
                        buffer = new byte[stream.Length];
                        stream.Read(buffer, 0, buffer.Length);
                    }
                    else
                    {
                        using (var ms = new MemoryStream())
                        {
                            stream.CopyTo(ms);
                            buffer = ms.ToArray();
                        }
                    }
                }
            }
            catch (System.Exception)
            {
                throw new LSysException(string.Format(
                    "OpenStream failed to find file at path: {0}. Did you add it to the Content folder and set its properties to copy to output directory?",
                    path));
            }
            return (sbyte[])(System.Array)buffer;
        }

        protected internal static sbyte[] ToByteArray(Stream ins)
        {
            try
            {
                byte[] buffer = new byte[512];
                int size = 0, read = 0;
                while ((read = ins.Read(buffer, size, buffer.Length - size)) > 0)
                {
                    size += read;
                    if (size == buffer.Length)
                        buffer = Arrays.CopyOf(buffer, size * 2);
                }
                if (size < buffer.Length)
                {
                    buffer = Arrays.CopyOf(buffer, size);
                }
                return (sbyte[])(System.Array)buffer;
            }
            finally
            {
                ins.Close();
            }
        }

        public override sbyte[] GetBytesSync(string path)
        {
            try
            {
                return OpenFileResourceBytes(path);
            }
            catch (System.Exception ex)
            {
                LSystem.E("file :" + path + " exception:" + ex.Message);
            }
            return null;
        }

        protected internal override Stream OpenStream(string path)
        {
            return TitleContainer.OpenStream(GetPath(path));
        }

        public override string GetTextSync(string path)
        {
            System.Text.StringBuilder sbr = new System.Text.StringBuilder(1024);
            try
            {
                StreamReader reader = new StreamReader(OpenStream(path), JavaSystem.GetEncoding(LSystem.ENCODING));
                string record = null;
                for (; (record = reader.ReadLine()) != null;)
                {
                    sbr.Append(record);
                }
                reader.Close();
            }
            catch (System.Exception ex)
            {
                LSystem.E("file :" + path + " exception:" + ex.Message);
            }
            return sbr.ToString();
        }

    }
}
