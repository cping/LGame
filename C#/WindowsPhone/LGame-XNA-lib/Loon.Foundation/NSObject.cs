using Loon.Utils;
using System.Text;
using Loon.Core;
namespace Loon.Foundation
{

    //不完全仿制cocoa库，用于xml化对象数据，便于非Java环境下的数据保存
    public abstract class NSObject : LRelease
    {

        protected internal const bool YES = true;
        protected internal const bool NO = false;

        internal NSObject()
        {
            if (NSAutoreleasePool._instance != null
                    && NSAutoreleasePool._instance._enable && IsArray())
            {
                NSAutoreleasePool._instance.AddObject(this);
            }
        }

        public bool IsEqual(NSObject o)
        {
            return base.Equals(o);
        }

        public bool IsArray()
        {
            return typeof(NSArray).IsInstanceOfType(this) || typeof(NSDictionary).IsInstanceOfType(this);
        }

        protected abstract internal void AddSequence(StringBuilder sbr, string indent);

        public string ToSequence()
        {
            StringBuilder sbr = new StringBuilder(512);
            sbr.Append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sbr.Append(LSystem.LS);
            sbr.Append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
            sbr.Append(LSystem.LS);
            sbr.Append("<plist version=\"1.0\">");
            sbr.Append(LSystem.LS);
            AddSequence(sbr, "");
            sbr.Append(LSystem.LS);
            sbr.Append("</plist>");
            return sbr.ToString();
        }

        public override string ToString()
        {
            return ToSequence();
        }

        public virtual void Dispose()
        {
            if (NSAutoreleasePool._instance != null && NSAutoreleasePool._instance._enable && IsArray())
            {
                NSAutoreleasePool._instance.RemoveObject(this);
            }
        }
    }
}
