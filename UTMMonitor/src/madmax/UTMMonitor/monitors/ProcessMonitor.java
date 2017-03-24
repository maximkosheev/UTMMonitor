package madmax.UTMMonitor.monitors;

import java.io.IOException;

import madmax.UTMMonitor.Settings;

public abstract class ProcessMonitor extends Monitor
{
	// ��� ��������������� ��������
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
				// �������� ��������� �� ������
				if (nPID > 0) set(); else reset();
			}
			catch (IOException e) {
				reset();
			}
			
			try {
				// ��������� �����
				sleep(Settings.dumpPeriod);
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}

	/**
	 * �� ����� ������ ���������� ������������� ���������������� ��������. ���� 0, ���� ������ �� �������� 
	 * @return int - ������������� �������� (PID)
	 * @throws IOException
	 */
	protected abstract int getServiceProcessPID() throws IOException;
}
