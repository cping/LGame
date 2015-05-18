using System.Collections.Generic;
using Loon.Utils;
using Loon.Java.Collections;
namespace Loon.Core
{

    public class RefManager : LRelease
    {

        public List<LRelease> objects;

        public RefManager()
        {
            this.objects = new List<LRelease>(
                    CollectionUtils.INITIAL_CAPACITY);
        }

        public RefManager(params LRelease[] res)
        {
            this.objects = new List<LRelease>(
                    CollectionUtils.INITIAL_CAPACITY);
            CollectionUtils.AddAll(objects, Arrays.AsList(res));
        }

        public RefManager(List<LRelease> res)
        {
            this.objects = new List<LRelease>(
                    CollectionUtils.INITIAL_CAPACITY);
            objects.AddRange(res);
        }

        public bool Add(LRelease res)
        {
            return CollectionUtils.Add(objects, res);
        }

        public virtual void Dispose()
        {
            foreach (LRelease release in objects)
            {
                if (release != null)
                {
                    release.Dispose();
                }
            }
            CollectionUtils.Clear(objects);
        }

    }
}
