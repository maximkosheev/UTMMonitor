package madmax.UTMMonitor.monitors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import madmax.UTMMonitor.Settings;

public class WebServerMonitor extends Monitor {

	public String url;
	protected URL internalUrl;
	
	public WebServerMonitor(String dbPoint, String url) {
		super(dbPoint);
		this.url = url;

		try {
			internalUrl = new URL(this.url);
		}
		catch(MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		while (!interrupted()) {
			try {
				HttpURLConnection con = (HttpURLConnection)internalUrl.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				int responseCode = con.getResponseCode();
				if (responseCode == -1)
					throw new IOException("response code is not available");
				set();
			}
			catch (IOException e) {
				reset();
			}
			
			try {
				sleep(Settings.dumpPeriod);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
