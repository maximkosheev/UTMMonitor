/**
 * 
 */
package madmax.UTMMonitor;

/**
 * �������� ��������� ����������
 * @author MadMax
 *
 */
public class Settings {
	/** ������� ��� �������� �������� ������ */
	public static String archiveDir = "./";
	public static String moduleDir = "";
	/** ���� ��������������� ���������� ������� */
	public static boolean autoRefresh = true;
	/** ������ ���������� �������� � ��. */
	public static int refreshPeriod = 1000;
	/** ���������� �������� ������ �� ��������� N-������������ */
	public static int viewLastTime = 3600000;
	/** ��� �� ��� ������� (��� ������� ������ �� ������ */
	public static int viewTimeTick = 900000;
	/** ������ ��������� ���������� � ��. */
	public static int dumpPeriod = 30000;
	/** �������� ������� �������... �� � �� */
	public static int monitorSleepTime = 1000;
}
