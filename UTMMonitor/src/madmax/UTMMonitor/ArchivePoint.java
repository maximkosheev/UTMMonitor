/**
 * 
 */
package madmax.UTMMonitor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Описывает точку в архиве (значение и время в милисекундах)
 * @author MadMax
 *
 */

public class ArchivePoint {
	public int paramId;
	public int value;
	public long time;
	
	public ArchivePoint(int paramId, int value, long time)
	{
		this.paramId = paramId;
		this.value = value;
		this.time = time;
	}
	
	public String getTime(String format)
	{
		return new SimpleDateFormat(format).format(new Date(this.time));
	}
	
	@Override
	public String toString()
	{
		return String.format("%s: %d", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(this.time)), this.value);
	}
}
