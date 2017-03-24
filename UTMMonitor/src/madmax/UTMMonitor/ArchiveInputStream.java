package madmax.UTMMonitor;

public class ArchiveInputStream {
	protected ArchiveFile file;
	protected ArchiveReader reader;
	
	public ArchiveInputStream(ArchiveFile file)
	{
		this.file = file;
		reader = file.getReader();
	}
	
	public ArchiveHeader readHeader()
	{
		if (reader != null)
			return reader.readHeader();
		else
			return null;
	}
	
	public ArchivePoint readPoint()
	{
		if (reader != null)
			return reader.readPoint();
		else
			return null;
	}
	
	public void close()
	{
		reader.close();
	}
}
