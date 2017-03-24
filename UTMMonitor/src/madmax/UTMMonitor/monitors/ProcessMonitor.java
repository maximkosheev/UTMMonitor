package madmax.UTMMonitor.monitors;

import java.io.IOException;

import madmax.UTMMonitor.Settings;

public abstract class ProcessMonitor extends Monitor
{
	// имя контролируемого процесса
	protected String serviceName;

	public ProcessMonitor(String dbPoint, String serviceName)
	{
		super(dbPoint);
		this.serviceName = serviceName.toLowerCase();
	}

	@Override
	public void run()
	{
		while (!interrupted()) {
			try {
				int nPID = getServiceProcessPID();
				// проверка запущенна ли служба
				if (nPID > 0) set(); else reset();
			}
			catch (IOException e) {
				reset();
			}
			
			try {
				// небольшая пауза
				sleep(Settings.dumpPeriod);
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}

	/**
	 * По имени службы возвращает идентификатор соответствующего процесса. либо 0, если служба не запущена 
	 * @return int - идентификатор процесса (PID)
	 * @throws IOException
	 */
	protected abstract int getServiceProcessPID() throws IOException;
}
