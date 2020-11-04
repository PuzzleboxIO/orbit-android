package io.puzzlebox.orbit.protocol;

import android.os.AsyncTask;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.io.IOException;

class SerialDevice extends AsyncTask<String, Void, String> {

//	int device_frame_cycle = 21; // 22ms frame cycle for Blade mCX2 (MLP4DSM RC)
//	int device_frame_cycle = 500; // 0.5s frame cycle for Arduino
	int device_frame_cycle = 1000; // 1s frame cycle for Arduino
	boolean keep_running = true;
	private UsbSerialDriver mSerialDevice;

//	byte[] commandNeutral = {0x00, 0x00, 0x00, (byte) 0xaa, 0x05, (byte) 0xff, 0x09, (byte) 0xff, 0x0d, (byte) 0xff, 0x13, 0x54, 0x14, (byte) 0xaa};
//	byte[] commandHover = {0x00, 0x00, 0x01, 0x7d, 0x05, (byte) 0xc5, 0x09, (byte) 0xde, 0x0e, 0x0b, 0x13, 0x54, 0x14, (byte) 0xaa};
//	byte[] commandMaximumThrust = {0x00, 0x00, 0x03, 0x54, 0x05, (byte) 0xc5, 0x09, (byte) 0xde, 0x0e, 0x0b, 0x13, 0x54, 0x14, (byte) 0xaa};
	
	String commandNeutral = "x000";
	String commandHover = "x085";
	String commandIdle = "x030";
	String commandMaximumThrust = "x100";

	String command = "neutral";

	protected String doInBackground(String... buffers) {
		String response = "";
		String setting = commandNeutral;

		while (keep_running) {

			switch (command) {
				case "neutral":
					setting = commandNeutral;
					break;
				case "idle":
					setting = commandIdle;
					break;
				case "hover":
					setting = commandHover;
					break;
				case "maximum_thrust":
					setting = commandMaximumThrust;
					break;
			}

			if (mSerialDevice != null) {

				try {
					mSerialDevice.write(setting.getBytes(), device_frame_cycle);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				keep_running = false;
				//tv.append("Attempted to write command but no serial device found\n");
			}

			try {
				Thread.sleep(device_frame_cycle);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	protected void setSerialDevice(UsbSerialDriver mSerialDevice) {
		this.mSerialDevice = mSerialDevice;
	}

	protected void setCommand(String command) {
		this.command = command;
	}
}
