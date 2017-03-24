package madmax.UTMMonitor.monitors;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import madmax.UTMMonitor.Settings;
import madmax.UTMMonitor.assets.UsbDevice;

public class DeviceMonitor extends Monitor {

	public int vid;
	public int pid;
	Device _jacart;
	
	public DeviceMonitor(String dbPoint, int vid, int pid) {
		super(dbPoint);
		
		int result = LibUsb.init(new Context());
		if (result != LibUsb.SUCCESS)
			throw new LibUsbException("Unable to initialize the usb device", result);
		
		this.vid = vid;
		this.pid = pid;
	}
	
	private boolean isJacartExists()
	{
		int result;
		
		DeviceList deviceList = new DeviceList();
		if (LibUsb.getDeviceList(null, deviceList) < 0)
			return false;

		_jacart = null;
		
		// ищем JACART'у в списке USB устройств
		for (Device device : deviceList) {
			DeviceDescriptor deviceDescriptor = new DeviceDescriptor();
			result = LibUsb.getDeviceDescriptor(device, deviceDescriptor);
			
			if(result == LibUsb.SUCCESS) {
				UsbDevice usbDevice = UsbDevice.fromDescription(deviceDescriptor.dump());
				// Ура, нашли jacart
				if (usbDevice.getVid() == vid && usbDevice.getPid() == pid) {
					_jacart = device;
				}
			}
		}
		
		LibUsb.freeDeviceList(deviceList, true);
		return _jacart != null;
	}
	
	@Override
	public void run()
	{
		while(!interrupted()) {
			try {
				if (isJacartExists())
					set();
				else
					reset();
				sleep(Settings.dumpPeriod);
			}
			catch (InterruptedException e) {
				break;
			}
		}
		
	}

}
