package com.dldzkj.app.renxing.blelib.contains;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.blelib.utils.CsvWriterHelper;
import com.dldzkj.app.renxing.blelib.utils.TimeFormatter;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;
import uk.co.alt236.bluetoothlelib.util.ByteUtils;
import uk.co.alt236.bluetoothlelib.util.IBeaconUtils;
import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class BluetoothLeDeviceStore {
	private final Map<String, BluetoothLeDevice> mDeviceMap;
	public BluetoothLeDeviceStore(){
		mDeviceMap = new HashMap<String, BluetoothLeDevice>();
	}

	public void addDevice(BluetoothLeDevice device){
		if(mDeviceMap.containsKey(device.getAddress())){
			mDeviceMap.get(device.getAddress()).updateRssiReading(device.getTimestamp(), device.getRssi());
		} else {
			mDeviceMap.put(device.getAddress(), device);
		}
	}

	public void clear(){
		mDeviceMap.clear();
	}
	private FileWriter generateFile(File file, String contents){
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.append(contents);
			writer.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return writer;
	}

	public EasyObjectCursor<BluetoothLeDevice> getDeviceCursor(){
		return new EasyObjectCursor<BluetoothLeDevice>(
				BluetoothLeDevice.class,
				getDeviceList(),
				"address");
	}

	public List<BluetoothLeDevice> getDeviceList(){
		final List<BluetoothLeDevice> methodResult = new ArrayList<BluetoothLeDevice>(mDeviceMap.values());

		Collections.sort(methodResult, new Comparator<BluetoothLeDevice>() {

			@Override
			public int compare(BluetoothLeDevice arg0, BluetoothLeDevice arg1) {
				return arg0.getAddress().compareToIgnoreCase(arg1.getAddress());
			}
		});

		return methodResult;
	}


	private String getListAsCsv(){
		final List<BluetoothLeDevice> list = getDeviceList();
		final StringBuilder sb =  new StringBuilder();
		sb.append(CsvWriterHelper.addStuff("mac"));
		sb.append(CsvWriterHelper.addStuff("name"));
		sb.append(CsvWriterHelper.addStuff("firstTimestamp"));
		sb.append(CsvWriterHelper.addStuff("firstRssi"));
		sb.append(CsvWriterHelper.addStuff("currentTimestamp"));
		sb.append(CsvWriterHelper.addStuff("currentRssi"));
		sb.append(CsvWriterHelper.addStuff("adRecord"));
		sb.append(CsvWriterHelper.addStuff("iBeacon"));
		sb.append(CsvWriterHelper.addStuff("uuid"));
		sb.append(CsvWriterHelper.addStuff("major"));
		sb.append(CsvWriterHelper.addStuff("minor"));
		sb.append(CsvWriterHelper.addStuff("txPower"));
		sb.append(CsvWriterHelper.addStuff("distance"));
		sb.append(CsvWriterHelper.addStuff("accuracy"));
		sb.append('\n');

		for(BluetoothLeDevice device : list){
			sb.append(CsvWriterHelper.addStuff(device.getAddress()));
			sb.append(CsvWriterHelper.addStuff(device.getName()));
			sb.append(CsvWriterHelper.addStuff(TimeFormatter.getIsoDateTime(device.getFirstTimestamp())));
			sb.append(CsvWriterHelper.addStuff(device.getFirstRssi()));
			sb.append(CsvWriterHelper.addStuff(TimeFormatter.getIsoDateTime(device.getTimestamp())));
			sb.append(CsvWriterHelper.addStuff(device.getRssi()));
			sb.append(CsvWriterHelper.addStuff(ByteUtils.byteArrayToHexString(device.getScanRecord())));
			final boolean isIBeacon = IBeaconUtils.isThisAnIBeacon(device);
			final String uuid;
			final String minor;
			final String major;
			final String txPower;
			final String distance;
			final String accuracy;

			if(isIBeacon){
				final IBeaconDevice beacon = new IBeaconDevice(device);
				uuid = String.valueOf(beacon.getUUID());
				minor = String.valueOf(beacon.getMinor());
				major = String.valueOf(beacon.getMajor());
				txPower = String.valueOf(beacon.getCalibratedTxPower());
				distance = beacon.getDistanceDescriptor().toString().toLowerCase(Locale.US);
				accuracy = String.valueOf(beacon.getAccuracy());
			} else {
				uuid = "";
				minor = "";
				major = "";
				txPower = "";
				distance = "";
				accuracy = "";
			}

			sb.append(CsvWriterHelper.addStuff(isIBeacon));
			sb.append(CsvWriterHelper.addStuff(uuid));
			sb.append(CsvWriterHelper.addStuff(minor));
			sb.append(CsvWriterHelper.addStuff(major));
			sb.append(CsvWriterHelper.addStuff(txPower));
			sb.append(CsvWriterHelper.addStuff(distance));
			sb.append(CsvWriterHelper.addStuff(accuracy));

			sb.append('\n');
		}

		return sb.toString();
	}


	public void shareDataAsEmail(Context context){
		final long timeInMillis = System.currentTimeMillis();

		final String to = null;
        final String subject = context.getString(
        		R.string.app_name,
        		TimeFormatter.getIsoDateTime(timeInMillis));

        final String message = "Please find attached the scan results.";

        final Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("plain/text");
        try {
        	final File outputDir = context.getCacheDir();
        	final File outputFile = File.createTempFile("bluetooth_le_" + timeInMillis, ".csv", outputDir);
        	outputFile.setReadable(true, false);
            generateFile(outputFile, getListAsCsv());
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFile));
            i.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(i, "Please select your email client:"));

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
