using loon.monogame;
using Microsoft.Xna.Framework.Content.Pipeline;
using Microsoft.Xna.Framework.Content.Pipeline.Serialization.Compiler;

namespace Loon_Pipeline
{
  [ContentTypeWriter]
    class LoonFileWriter : ContentTypeWriter<LoonFileContent>
{
    public override string GetRuntimeReader(TargetPlatform targetPlatform)
    {
        return typeof(LoonFileReader).AssemblyQualifiedName;
    }

    public override string GetRuntimeType(TargetPlatform targetPlatform)
    {
        return typeof(LoonFileContent).AssemblyQualifiedName;
    }

    protected override void Write(ContentWriter output, LoonFileContent value)
    {
        output.Write(value.content);
    }
}
}
