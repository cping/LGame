using loon.monogame;
using Microsoft.Xna.Framework.Content.Pipeline;

using TInput = System.String;
using TOutput = System.String;

namespace Loon_Pipeline
{
    [ContentProcessor(DisplayName = "File Processor - Loon")]
    public class mini2DxContentProcessor : ContentProcessor<LoonFileContent, LoonFileContent>
    {
        public override LoonFileContent Process(LoonFileContent input, ContentProcessorContext context)
        {
            return input;
        }
    }
}
