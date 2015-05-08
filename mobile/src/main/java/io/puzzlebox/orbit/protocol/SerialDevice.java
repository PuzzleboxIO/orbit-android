package io.puzzlebox.orbit.protocol;

//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.io.IOException;
//import android.widget.TextView;

class SerialDevice extends AsyncTask<String, Void, String> {

//	int device_frame_cycle = 21; // 22ms frame cycle for Blade mCX2 (MLP4DSM RC)
//	int device_frame_cycle = 500; // 0.5s frame cycle for Arduino
	int device_frame_cycle = 1000; // 1s frame cycle for Arduino
	boolean keep_running = true;
	private UsbSerialDriver mSerialDevice;
	//	TextView tv;

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
//		byte[] setting = commandNeutral;
		String setting = commandNeutral;
//		String setting = commandHover;
		
		while (keep_running) {

			if (command.equals("neutral")) {
				setting = commandNeutral;
			} else if (command.equals("idle")) {
				setting = commandIdle;
			} else if (command.equals("hover")) {
				setting = commandHover;
			} else if (command.equals("maximum_thrust")) {
				setting = commandMaximumThrust;
			}

			if (mSerialDevice != null) {

				try {
					mSerialDevice.write(setting.getBytes(), device_frame_cycle);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//tv.append("Error writing command to serial device\n");
					e.printStackTrace();
				}

			} else {
				keep_running = false;
				//tv.append("Attempted to write command but no serial device found\n");
			}


			try {
				Thread.sleep(device_frame_cycle);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} // while

		return response;

	} // doInBackground


	// #############################################################

	//	protected void setTextView(TextView tv) {
	//		
	//		this.tv = tv;
	//		
	//	}


	// #############################################################

	protected void setSerialDevice(UsbSerialDriver mSerialDevice) {

		this.mSerialDevice = mSerialDevice;

	}


	// #############################################################

	protected void setCommand(String command) {

		this.command = command;

	}


	// #############################################################

//	protected void onPostExecute(String buffer) {
//
//		// pass
//
//	} // onPostExecute


} // class SerialDevice
