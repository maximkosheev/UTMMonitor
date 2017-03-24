/**
 * 
 */
package madmax.UTMMonitor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Описывает архивный файл
 * @author MadMax
 *
 */
public class ArchiveFile {
	// ссылка на файл
	File link;
	// версия файла
	int version;
	// время создания файла в милисекндах от 01.01.1970
	long time;
	
	public ArchiveFile(File file)
	{
		link = file;
		version = -1;
		time = -1;
		try {
			DataInputStream is = new DataInputStream(new FileInputStream(link));
			
			version = is.readInt();
			time = is.readLong();
			is.close();
		}
		catch (Exception e) {
			link = null;
		}
	}
	
	public int getVersion()
	{
		return version;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public ArchiveReader getReader()
	{
		try {
			ArchiveReader reader = new ArchiveReader10(this);
			return reader;
		}
		catch(Exception e) {
			return null;
		}
	}
}

final class ArchiveReader10 implements ArchiveReader {
	protected ArchiveFile file;
	protected DataInputStream is;
	
	public ArchiveReader10(ArchiveFile file) throws FileNotFoundException, IOException
	{
		this.file = file;
		is = new DataInputStream(new FileInputStream(file.link));
		// пропускаем заголовок файла
		is.skip(12);
		
	}
	
	@Override
	public ArchiveHeader readHeader()
	{
		ArchiveHeader header = new ArchiveHeader();
		header.version = file.getVersion();
		header.time = file.getTime();
		return header;
	}
	
	@Override
	public ArchivePoint readPoint()
	{
		try {
			int paramId = is.readInt();
			long time = is.readLong();
			int value = is.readInt();
			return new ArchivePoint(paramId, value, time);
		}
		catch (IOException e) {
			return null;
		
		}
	}
	
	@Override
	public void close()
	{
		try {
			is.close();
		}
		catch(IOException e) {
			System.out.println("Failed to close file: " + file.link.getName());
		}
	}
}