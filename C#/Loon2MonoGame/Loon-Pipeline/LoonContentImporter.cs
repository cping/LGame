using loon.monogame;
using Microsoft.Xna.Framework.Content.Pipeline;
using System.IO;
using TImport = System.String;

namespace Loon_Pipeline
{
    [ContentImporter(".txt",".xml", ".json", ".tmx", ".tsx", ".tx", ".atlas", ".fnt", DisplayName = "File Importer - mini2Dx", DefaultProcessor = "LoonContentProcessor")]
    public class LoonContentImporter : ContentImporter<LoonFileContent>
    {
        public override LoonFileContent Import(string filename, ContentImporterContext context)
    {
        return new LoonFileContent
        {
            content = File.ReadAllBytes(filename)
        };
    }

}

}
