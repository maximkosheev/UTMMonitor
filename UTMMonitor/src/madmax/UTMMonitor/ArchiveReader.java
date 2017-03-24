package madmax.UTMMonitor;

public interface ArchiveReader {
	/** возвращает заголовок архивного файла */
	public ArchiveHeader readHeader();
	/** выполняет чтение и возвращает данные архивной точки или NULL, если точек больше нет */ 
	public ArchivePoint readPoint();
	/** закрытие архива */
	public void close();
}