package loon.build.packer;

public abstract class OutputArchiveIterable implements ArchiveIterable {
    public abstract OutputArchiveIterator iterator();
    public abstract String getArchiveType();
    public boolean isInputArchive() {
        return false;
    }
    public boolean isOutputArchive() {
        return true;
    }
}
