package madmax.UTMMonitor.monitors;

import madmax.UTMMonitor.UnsupportedOSTypeException;

public class MonitorsFactory {
	
	public static ProcessMonitor createProcessMonitor(String point, String processName) throws UnsupportedOSTypeException
	{
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("windows"))
			return new ProcessMonitorWin(point, processName);
		else if (os.contains("linux"))
			return new ProcessMonitorLinux(point, processName);
		
		throw new UnsupportedOSTypeException();
	}
	
	public static WebServerMonitor createWebServerMonitor(String point, String url)
	{
		return new WebServerMonitor(point, url);
	}
	
	public static FileSystemMonitor createFileSystemMonitor(String point, String path)
	{
		return new FileSystemMonitor(point, path);
	}
	
	public static DeviceMonitor createDeviceMonitor(String point, int vid, int pid)
	{
		return new DeviceMonitor(point, vid, pid);
	}
}
