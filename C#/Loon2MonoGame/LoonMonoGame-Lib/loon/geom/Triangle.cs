
namespace loon.geom
{
    public interface Triangle
    {
        int GetTriangleCount();

        float[] GetTrianglePoint(int t, int i);

        void AddPolyPoint(float x, float y);

        void StartHole();

        bool Triangulate();
    }

}
