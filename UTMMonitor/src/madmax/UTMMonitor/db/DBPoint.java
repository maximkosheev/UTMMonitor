package madmax.UTMMonitor.db;

/**
 * ��������� �������� �������, �� ������� ������� ����������
 * @author MadMax
 *
 */
public class DBPoint {
	public int id;
	public String name;
	public String shortname;
	public String description;
	public int value;
	// �������� ������
	public Trend trend;
	
	public DBPoint(int id, String name)
	{
		this.id = id;
		this.name = name;
		this.shortname = "";
		this.description = "";
		this.value = -1;
		this.trend = new Trend(this);
	}
}
