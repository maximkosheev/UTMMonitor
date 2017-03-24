package madmax.UTMMonitor.db;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import madmax.UTMMonitor.ArchivePoint;

/**
 * ��������� ��������� ��������� �� �������
 * @author MadMax
 *
 */
public class Trend {
	private DBPoint _point;
	private List<ArchivePoint> _points;
	private long _startTime;
	
	public Trend(DBPoint point)
	{
		_point = point;
		_points = new ArrayList<ArchivePoint>();
		_startTime = 0;
	}
	
	public void setStartTime(long startTime)
	{
		_startTime = startTime;
	}
	
	public void appendPoint(ArchivePoint point)
	{
		// ������ ����� ��������� ����� � ������ ������� �����.
		// ������ ��� ����� ������ ������ ������.
		// ����� ������ ����� � ����� �� ���������.
		
		// ����������� ������ �������� ����� � ��������� ������
		if (_points.size() == 0)
			_points.add(point);
		else if (point.time < _startTime) {
			if (point.time > _points.get(0).time) {
				_points.set(0, point);
			}
		}
		else {
			_points.add(point);
		}
		_points.sort(new Comparator<ArchivePoint>() {
			public int compare(ArchivePoint arg0, ArchivePoint arg1) {
				return (int)(arg0.time - arg1.time);
			}
		});
	}

	/**
	 * ���������� �������� ��������� � ����� ������ �������
	 * @param timeOffset - ����� � ������������ �� 01.01.1970
	 * @throws IndexOutOfBoundsException - ������������ � ������ �������� ������� "�� ������ ������"
	 * @return int - �������� ���������
	 */
	public int getValueByTime(long time) throws IndexOutOfBoundsException
	{
		int value = _points.get(0).value;
		
		if (time < _points.get(0).time)
			throw new IndexOutOfBoundsException();
		
		for (ArchivePoint point : _points) {
			if (point.time > time)
				return value;
			value = point.value;
		}
		return value;
	}

	/**
	 * ���������� ������� ��������� ������ ����� � ������ �������� �����, 
	 * @param time
	 * @return
	 */
	public ArchivePoint getNearestPoint(long time)
	{
		// ������ � ������ ���
		if (getPointsCount() == 0)
			return null;

		ArchivePoint point = null;
		
		for (int nI = getPointsCount() - 1; nI >= 0; nI--) {
			point = _points.get(nI); 
			if (point.time < time)
				break;
		}
		return point;
	}
	
	/**
	 * ��������� ������� (������) �������� ����� � �������� ������
	 * @param point  �����, ����� ������� ����� ���������
	 * @return ������� ����� � ������ ��� -1, ���� ����� �� �������
	 */
	public int findPoint(ArchivePoint point)
	{
		return _points.indexOf(point);
	}
	
	/**
	 * ���������� �������� ��������� � ����� ������ ������� ������������ ������ ������
	 * @param timeOffset - �������� � �� �� ������ ������
	 * @throws IndexOutOfBoundsException - ������������ � ������ �������� �������������� ��������
	 * @return int - �������� ���������
	 */
	public int getValueByOffset(long timeOffset) throws IndexOutOfBoundsException
	{
		long time = _points.get(0).time + timeOffset;
		return getValueByTime(time);
	}
	
	/**
	 * ���������� �������� ����� �� �� ������� � ������
	 * @param index - ������� ����� � ������
	 * @return �������� �����, ����������� � �������� ������ �� ������� �������
	 */
	public ArchivePoint getPointByIndex(int index)
	{
		return _points.get(index);
	}
	
	/**
	 * ���������� ���-�� ����� � �������� ������
	 * @return ���-�� ����� � �������� ������
	 */
	public int getPointsCount()
	{
		return _points.size();
	}

	/**
	 * ���������� ��������� ������ ������� ������
	 * @return long - ��������� ��������� ����� (�� �� 01.01.1970)
	 */
	public long getStartTime()
	{
		long res;
		
		try {
			res = _points.get(0).time;
		}
		catch (Exception e) {
			res = -1;
		}
		return res;
	}
	
	/**
	 * ���������� �������� ������ ������� ������
	 * @return long - �������� ��������� ����� (�� �� 01.01.1970)
	 */
	public long getLastTime()
	{
		long res;
		
		try {
			res = _points.get(_points.size() - 1).time;
		}
		catch (Exception e) {
			res = -1;
		}
		return res;
	}
	
	public DBPoint getPoint()
	{
		return _point;
	}
}
