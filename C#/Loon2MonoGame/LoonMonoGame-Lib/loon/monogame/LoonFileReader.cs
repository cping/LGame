using Microsoft.Xna.Framework.Content;
using System;
using System.Collections.Generic;
using System.Text;

namespace loon.monogame
{
    public class LoonFileReader : ContentTypeReader<LoonFileContent>
    {
        protected override LoonFileContent Read(ContentReader input, LoonFileContent existingInstance)
        {
            var content = new LoonFileContent()
            {
                content = input.ReadBytes((int)input.BaseStream.Length)
            };
            return content;
        }
    }
}
