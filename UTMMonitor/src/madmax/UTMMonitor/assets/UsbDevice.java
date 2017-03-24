package madmax.UTMMonitor.assets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsbDevice {
	private int _vid;
	private int _pid;
	
	public UsbDevice(int vid, int pid)
	{
		_vid = vid;
		_pid = pid;
	}
	
	public int getVid() {
		return _vid;
	}

	public int getPid() {
		return _pid;
	}

	public static UsbDevice fromDescription(String description)
	{
		Pattern vidPattern = Pattern.compile("idVendor\\s+0x([0-9ABCDEF]+)", Pattern.CASE_INSENSITIVE);
		Pattern pidPattern = Pattern.compile("idProduct\\s+0x([0-9ABCDEF]+)", Pattern.CASE_INSENSITIVE);
		
		int vid = 0, pid = 0;

		try {
			BufferedReader reader = new BufferedReader(new StringReader(description));
			String devInfoLine;
			while ((devInfoLine = reader.readLine()) != null) {
				Matcher vidMatcher = vidPattern.matcher(devInfoLine);
				Matcher pidMatcher = pidPattern.matcher(devInfoLine);
				
				if (vidMatcher.find()) {
					vid = Integer.parseUnsignedInt(vidMatcher.group(1), 16);
				}

				if (pidMatcher.find()) {
					pid = Integer.parseUnsignedInt(pidMatcher.group(1), 16);
				}
			}
		}
		catch (IOException | NullPointerException e) {
			e.printStackTrace();
			return null;
		}

		return new UsbDevice(vid, pid);
	}
	
	@Override
	public String toString()
	{
		return String.format("UsbDevice: vid - %x pid - %x", _vid, _pid);
	}
}
