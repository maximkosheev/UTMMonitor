package madmax.UTMMonitor;

public interface ArchiveReader {
	/** ���������� ��������� ��������� ����� */
	public ArchiveHeader readHeader();
	/** ��������� ������ � ���������� ������ �������� ����� ��� NULL, ���� ����� ������ ��� */ 
	public ArchivePoint readPoint();
	/** �������� ������ */
	public void close();
}