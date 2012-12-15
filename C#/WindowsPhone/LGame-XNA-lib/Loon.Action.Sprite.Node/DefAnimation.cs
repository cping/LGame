using System.Collections.Generic;
using Loon.Utils;
using System;
namespace Loon.Action.Sprite.Node
{
    public class DefAnimation : DefinitionObject
    {

        public string uniqueID;

        private LNAnimation animation;

        public DefAnimation()
        {
        }

        public virtual LNAnimation Get()
        {
            return animation;
        }

        public override void DefinitionObjectDidFinishParsing()
        {
            base.DefinitionObjectDidFinishParsing();
            if (animation != null)
            {
                LNDataCache.SetAnimation(this, this.uniqueID);
            }
        }

        public override void DefinitionObjectDidReceiveString(string v)
        {
            base.DefinitionObjectDidReceiveString(v);
            List<string> result = GetResult(v);
            float time = 3f;
            foreach (string list in result)
            {
                if (list.Length > 2)
                {
                    string[] values = StringUtils.Split(list, "=");
                    string name = values[0];
                    string value = values[1];
                    if ("id".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.uniqueID = value;
                    }
                    else if ("animationid".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        this.uniqueID = value;
                    }
                    else if ("time".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        time = Convert.ToSingle(value);
                    }
                    else if ("duration".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        time = Convert.ToSingle(value);
                    }
                    else if ("list".Equals(name, System.StringComparison.InvariantCultureIgnoreCase))
                    {
                        if (animation == null)
                        {
                            animation = new LNAnimation();
                        }
                        animation._name = uniqueID;
                        string[] lists = StringUtils.Split(list, ",");
                        for (int i = 0; i < lists.Length; i++)
                        {
                            animation.AddFrameStruct(lists[i], time);
                        }
                    }
                }
            }
            result.Clear();
        }

    }

}