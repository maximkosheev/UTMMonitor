package madmax.UTMMonitor.db;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import madmax.UTMMonitor.ArchivePoint;

/**
 * Описывает изменение параметра во времени
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
		// Задача найти ближайшую слева к началу графика точку.
		// Именно она будет первой точкой тренда.
		// Более ранние точки в тренд не добавлять.
		
		// добавляется первая архивная точка к архивному тренду
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
	 * Возвращает значение параметра в некий момент времени
	 * @param timeOffset - время в милисекундах от 01.01.1970
	 * @throws IndexOutOfBoundsException - генерируется в случае указания времени "до начала времен"
	 * @return int - значение параметра
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
	 * Возвращает позицию ближайщей ранней точки в списке архивных точке, 
	 * @param time
	 * @return
	 */
	public ArchivePoint getNearestPoint(long time)
	{
		// данных в архиве нет
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
	 * Возращает позицию (индекс) архивной точки в архивном тренде
	 * @param point  точка, поиск которой нужно выполнить
	 * @return позиция точки в тренде или -1, если точка не найдена
	 */
	public int findPoint(ArchivePoint point)
	{
		return _points.indexOf(point);
	}
	
	/**
	 * Возвращает значение параметра в некий момент времени относительно начала тренда
	 * @param timeOffset - смещение в мс от начала тренда
	 * @throws IndexOutOfBoundsException - генерируется в случае указания отрицательного смещения
	 * @return int - значение параметра
	 */
	public int getValueByOffset(long timeOffset) throws IndexOutOfBoundsException
	{
		long time = _points.get(0).time + timeOffset;
		return getValueByTime(time);
	}
	
	/**
	 * Возвращает архивную точку по ее индексу в тренде
	 * @param index - позиция точки в тренде
	 * @return архивная точка, находящаяся в архивном тренде по данному индексу
	 */
	public ArchivePoint getPointByIndex(int index)
	{
		return _points.get(index);
	}
	
	/**
	 * Возвращает кол-во точек а архивном тренде
	 * @return кол-во точек в архивном тренде
	 */
	public int getPointsCount()
	{
		return _points.size();
	}

	/**
	 * Возвращает начальный момент времени тренда
	 * @return long - начальная временная метка (мс от 01.01.1970)
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
	 * Возвращает конечный момент времени тренда
	 * @return long - конечная временная метка (мс от 01.01.1970)
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
