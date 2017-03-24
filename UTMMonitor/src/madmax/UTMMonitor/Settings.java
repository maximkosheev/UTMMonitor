/**
 * 
 */
package madmax.UTMMonitor;

/**
 * Содержит настройки приложения
 * @author MadMax
 *
 */
public class Settings {
	/** каталог для хранения архивных файлов */
	public static String archiveDir = "./";
	public static String moduleDir = "";
	/** флаг автоматического обновления граиков */
	public static boolean autoRefresh = true;
	/** Период обновления графиков в мс. */
	public static int refreshPeriod = 1000;
	/** Отобразить архивные данные за последние N-милисекундах */
	public static int viewLastTime = 3600000;
	/** шаг по оси времени (шаг выборки данных из архива */
	public static int viewTimeTick = 900000;
	/** Период архивации параметров в мс. */
	public static int dumpPeriod = 30000;
	/** задержка рабочих потоков... на в мс */
	public static int monitorSleepTime = 1000;
}
