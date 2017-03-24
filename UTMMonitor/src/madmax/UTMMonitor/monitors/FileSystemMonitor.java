package madmax.UTMMonitor.monitors;

import java.io.File;

import madmax.UTMMonitor.Settings;

public class FileSystemMonitor extends Monitor {

	public String path;
	private File _dir;
	
	public FileSystemMonitor(String dbPoint, String path) {
		super(dbPoint);
		this.path = path;
		
		_dir = new File(path);
	}
	
	@Override
	public void run()
	{
		while(!interrupted()) {
			try {
				
				if (_dir.exists())
					set();
				else
					reset();
				
				sleep(Settings.dumpPeriod);
			}
			catch (SecurityException e) {
				reset();
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}

}
