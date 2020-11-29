using java.lang;
using java.util;
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
                return ToByteArray(OpenStream(path));
            }
            catch (System.Exception ex)
            {
                LSystem.E("file :" + path + " exception:" + ex.Message);
            }
            return null;
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

        protected internal override Stream OpenStream(string path)
        {
            return TitleContainer.OpenStream(GetPath(path));
        }
    }
}
