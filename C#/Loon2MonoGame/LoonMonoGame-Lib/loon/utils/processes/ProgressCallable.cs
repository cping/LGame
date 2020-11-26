namespace loon.utils.processes
{
    public interface ProgressCallable<T>
    {
        T Call(ProgressListener p);

    }

}
