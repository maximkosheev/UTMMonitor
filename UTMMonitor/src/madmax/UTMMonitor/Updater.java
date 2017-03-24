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
	
	// путь к временному файлу (скаченному обновлению), 
	private String _tempFileName;
	
	/**
	 * Выполняет поиск последней версии монитора на сервере обновлений
	 * После успешного выполнения данной операции, newVersion содержит номер последней версии
	 * 
	 * @return boolean	true - на сервере имеется обновление;
	 * 					false - обновлений на сервере не обнаружено
	 * 
	 * @throws IOException - при проверке обновлений произошла ошибка
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
	 * Возвращает строковое представления последей версии монитора на сервере обновлений
	 * @return String версия монитора на сервере обновлений в формате <major>.<minor>-<release>
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
	 * Выполняет скачивание архива последней версии с сервера обновлений
	 * и помещает его во временный каталог
	 * 
	 * @param version String - номер версии для скачивания в формате <major>.<minor>-<release>
	 * 
	 * @return boolean	true - в случае успешного завершения работы;
	 * 					false - в случае аварийного завершения
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
				// проверка полноты скаченных данных
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
	 * Выполняет установку скаченного ранее файла обновлений в каталог,
	 * из которого запущен данный экземпляр монитора
	 *
	 * @param boolean bSaveDB - нужно ли сохранять имеющиеся архивные данные
	 * 
	 * @return boolean	true - в случае успешного завершения работы;
	 * 					false - в случае аварийного завершения
	 */
	public boolean install(boolean bSaveDB)
	{
		// при необходимости удаляем все архивные файлы
		if (!bSaveDB) {
			File archiveDir = new File(Settings.archiveDir);
			
			System.out.println("Remove archives files: " + archiveDir.getAbsolutePath());
			
			// отыскиваем все архивные файлы
			File[] files = archiveDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".arch");
				}
			});
			// удаляем все архивные файлы
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
				
				// конфигурационный файл заменять нельзя (может содержать особые настройки пользователя)
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
			// обновление успешно завершено
			System.out.println("Update finished successfuly");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
