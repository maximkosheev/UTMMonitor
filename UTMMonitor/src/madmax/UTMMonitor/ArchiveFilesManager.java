/**
 * 
 */
package madmax.UTMMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Облегчает работу с архивными файлами
 * @author MadMax
 */
public class ArchiveFilesManager {

	private static ArchiveFilesManager _instance = null;
	
	// список всех архивных файлов в каталоге
	private List<ArchiveFile> _files;
	// текуший файл в списке
	private int _currentPos;
	
	private ArchiveFilesManager()
	{
		_instance = this;
	}
	
	public void initialize()
	{
		/* Поиск всех архивных файлов */
		_files = new ArrayList<ArchiveFile>();
		
		File dir = new File(Settings.archiveDir);

		// если каталог с архивными файлами не существует, создаем его
		if (!dir.exists())
			dir.mkdirs();
		
		for (File file : dir.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".arch")) {
				_files.add(new ArchiveFile(file));
			}
		}
		
		// сортировка списка архивных файлов по времени
		Collections.sort(_files, new Comparator<ArchiveFile>() {
			public int compare(ArchiveFile arch1, ArchiveFile arch2)
			{
				if (arch1.time < arch2.time)
					return -1;
				else if (arch1.time > arch2.time)
					return 1;
				else
					return 0;
			}
		});
		_currentPos = -1;
	}
	
	public ArchiveFile getFirst()
	{
		if (_files.size() == 0)
			return null;
		
		_currentPos = 0;
		
		return _files.get(_currentPos);
	}
	
	public ArchiveFile getNext() throws Exception
	{
		if (_currentPos < 0)
			throw new Exception("Wrong method call order. You must call getFirst first");
		_currentPos += 1;
		if (_currentPos >= _files.size())
			return null;
		return _files.get(_currentPos);
	}
	
	public static ArchiveFilesManager getInstance()
	{
		if (_instance == null)
			_instance = new ArchiveFilesManager();
		return _instance;
	}
}
