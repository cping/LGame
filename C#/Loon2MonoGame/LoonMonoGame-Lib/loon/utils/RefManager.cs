namespace loon.utils
{
    public class RefManager : LRelease
    {

        public TArray<LRelease> objects = new TArray<LRelease>();

        public RefManager()
        {
        }

        public RefManager(params LRelease[] res)
        {
            objects.AddAll(res);
        }

        public RefManager(TArray<LRelease> res)
        {
            objects.AddAll(res);
        }

        public bool Add(LRelease res)
        {
            return objects.Add(res);
        }

        public void Close()
        {
            foreach (LRelease release in objects)
            {
                if (release != null)
                {
                    release.Close();
                }
            }
            objects.Clear();
        }

    }

}
