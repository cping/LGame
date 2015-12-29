package loon.build.packer;

public interface ArchiveIterable extends Iterable<ArchiveEntry> {
	public ArchiveIterator iterator();

	public String getArchiveType();

	public boolean isInputArchive();

	public boolean isOutputArchive();
}
