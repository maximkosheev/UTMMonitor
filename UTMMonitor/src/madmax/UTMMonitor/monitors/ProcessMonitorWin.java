package madmax.UTMMonitor.monitors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessMonitorWin extends ProcessMonitor
{
	private String uriServiceProcessCheckerUtil;
	
	public ProcessMonitorWin(String dbPoint, String processName)
	{
		super(dbPoint, processName);
		// определяем расположение программы ServiceStatusChecker.exe
		File module = new File(System.getProperty("java.class.path")).getAbsoluteFile();
		if (module.isFile())
			uriServiceProcessCheckerUtil = module.getParentFile().toString() + "\\ServiceStatusChecker.exe";
		else
			uriServiceProcessCheckerUtil = "D:\\Projects\\workspace\\UTMMonitor\\bin\\ServiceStatusChecker.exe";
	}
	
	@Override
	protected int getServiceProcessPID() throws IOException
	{
		String[] serviceChecker = {uriServiceProcessCheckerUtil, serviceName};
		Process checker = Runtime.getRuntime().exec(serviceChecker);
		try {
			int nRetCode = checker.waitFor();
			// чекер выполнился без ошибок
			if (nRetCode < 0)
				throw new IOException("Exception while ServiceProcessChecker running");
			InputStream is = checker.getInputStream();
			InputStreamReader is_reader = new InputStreamReader(is);
			BufferedReader buffered_reader = new BufferedReader(is_reader);
			String strLine = buffered_reader.readLine();
			
			Pattern pidPattern = Pattern.compile("Service PID: (\\d+)");
			Matcher pidMatcher = pidPattern.matcher(strLine);
			if (pidMatcher.find()) {
				return Integer.parseInt(pidMatcher.group(1));
			}
			else
				throw new IOException("ServiceProcessChecker wrong output format");
		}
		catch (InterruptedException e) {
			throw new IOException("Process::waitFor exception");
		}
	}
}
