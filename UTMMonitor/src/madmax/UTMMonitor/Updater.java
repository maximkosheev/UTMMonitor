package madmax.UTMMonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater {

	public static final String UPDATE_URL = "http://kt-alkogol.ru/utmmonitor/update.php";
	
	public String newVersion;
	
	// ���� � ���������� ����� (���������� ����������), 
	private String _tempFileName;
	
	/**
	 * ��������� ����� ��������� ������ �������� �� ������� ����������
	 * ����� ��������� ���������� ������ ��������, newVersion �������� ����� ��������� ������
	 * 
	 * @return boolean	true - �� ������� ������� ����������;
	 * 					false - ���������� �� ������� �� ����������
	 * 
	 * @throws IOException - ��� �������� ���������� ��������� ������
	 */
	public boolean checkForUpdates() throws IOException
	{
		String version = getVersion();
		
		if (version != null) {
			if (version.compareTo(Main.version) > 0) {
				newVersion = version;
				return true;
			}
			else
				return false;
		}
		else
			throw new IOException();
	}

	/**
	 * ���������� ��������� ������������� �������� ������ �������� �� ������� ����������
	 * @return String ������ �������� �� ������� ���������� � ������� <major>.<minor>-<release>
	 */
	public String getVersion()
	{
		URL updateURL;
		
		try {
			updateURL = new URL(Updater.UPDATE_URL + "?action=version");
			HttpURLConnection con = (HttpURLConnection)updateURL.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = con.getResponseCode();
			String version;
			if (responseCode == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				version = reader.readLine();
			}
			else
				version = null;
			con.disconnect();
			return version;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ��������� ���������� ������ ��������� ������ � ������� ����������
	 * � �������� ��� �� ��������� �������
	 * 
	 * @param version String - ����� ������ ��� ���������� � ������� <major>.<minor>-<release>
	 * 
	 * @return boolean	true - � ������ ��������� ���������� ������;
	 * 					false - � ������ ���������� ����������
	 */
	public boolean download(String version)
	{
		URL updateURL;
		
		try {
			updateURL = new URL(Updater.UPDATE_URL + "?action=download&version=" + version);
			HttpURLConnection con = (HttpURLConnection)updateURL.openConnection();
			int responseCode = con.getResponseCode();
			boolean bRet = false;
			if (responseCode == 200) {
				InputStream is = con.getInputStream();
				byte[] buf = new byte[512];
				int nReaded = 0;
				int nReadedTotal = 0;
				long nFileSize = con.getContentLengthLong();
				
				File tempFile = File.createTempFile("UTMMonitor", ".zip");
				
				_tempFileName = tempFile.getAbsolutePath();
				
				FileOutputStream os = new FileOutputStream(tempFile);
				
				while ((nReaded = is.read(buf)) > 0) {
					nReadedTotal += nReaded;
					os.write(buf, 0, nReaded);
				}
				os.close();
				is.close();
				// �������� ������� ��������� ������
				if (nReadedTotal == nFileSize)
					bRet = true;
				else
					bRet = false;
			}
			con.disconnect();
			return bRet;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * ��������� ��������� ���������� ����� ����� ���������� � �������,
	 * �� �������� ������� ������ ��������� ��������
	 *
	 * @param boolean bSaveDB - ����� �� ��������� ��������� �������� ������
	 * 
	 * @return boolean	true - � ������ ��������� ���������� ������;
	 * 					false - � ������ ���������� ����������
	 */
	public boolean install(boolean bSaveDB)
	{
		// ��� ������������� ������� ��� �������� �����
		if (!bSaveDB) {
			File archiveDir = new File(Settings.archiveDir);
			
			System.out.println("Remove archives files: " + archiveDir.getAbsolutePath());
			
			// ���������� ��� �������� �����
			File[] files = archiveDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".arch");
				}
			});
			// ������� ��� �������� �����
			for (File file : files) {
				file.delete();
			}
		}
		
		try {
			ZipInputStream zipIS = new ZipInputStream(new FileInputStream(_tempFileName));
			
			ZipEntry entry = zipIS.getNextEntry();
			while (entry != null) {
				String entryName = entry.getName();
				
				File destFile = new File(Main.__DIR__ + File.separator + entryName);
				
				// ���������������� ���� �������� ������ (����� ��������� ������ ��������� ������������)
				if (entryName.toLowerCase().equals("config.xml") && destFile.exists()) {
					System.out.println("Skip config.xml");
				}
				else {
					System.out.println("file unzip: " + destFile.getAbsolutePath());
					
					// create all non existing folders to avoid FileNotFoundException
					if (entry.isDirectory()) {
						destFile.mkdirs();
					}
					else {
						FileOutputStream fos = new FileOutputStream(destFile);
						byte[] buffer = new byte[1024];
			            int nReaded;
			            
			            while ((nReaded = zipIS.read(buffer)) > 0) {
			            	fos.write(buffer, 0, nReaded);
			            }
			            
			            fos.close();
					}
				}
				entry = zipIS.getNextEntry();
			}
			zipIS.closeEntry();
			zipIS.close();
			// ���������� ������� ���������
			System.out.println("Update finished successfuly");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
