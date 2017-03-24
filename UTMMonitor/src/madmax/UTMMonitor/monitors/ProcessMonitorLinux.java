package madmax.UTMMonitor.monitors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessMonitorLinux extends ProcessMonitor
{
	
	public ProcessMonitorLinux(String dbPoint, String processName)
	{
		super(dbPoint, processName);
	}
	
	@Override
	protected int getServiceProcessPID() throws IOException
	{
		try {
			// получение списка запущенных процессов
			Process tasklist = Runtime.getRuntime().exec("ps -e");
			InputStream is = tasklist.getInputStream();
			InputStreamReader is_reader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(is_reader);
			String strLine = "";
			boolean process1 = false;
			boolean process2 = false;
			boolean process3 = false;
			
			while ((strLine = bufferedReader.readLine()) != null) {
				if (strLine.contains("process1"))
					process1 = true;
				else if (strLine.contains("process2"))
					process2 = true;
				else if (strLine.contains("process3"))
					process3 = true;
			}
			
			if (process1 && process2 && process3)
				System.out.println("ProcessMonitor: OK");
			else
				System.out.println("ProcessMonitor: Failed");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
