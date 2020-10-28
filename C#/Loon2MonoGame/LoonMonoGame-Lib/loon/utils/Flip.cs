namespace loon.utils
{
    public interface Flip<T>
    {

        T SetFlipX(bool x);

        T SetFlipY(bool y);

        T SetFlipXY(bool x, bool y);

        bool IsFlipX();

        bool IsFlipY();
    }

}
