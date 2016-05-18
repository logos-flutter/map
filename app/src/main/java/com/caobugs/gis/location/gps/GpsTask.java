package com.caobugs.gis.location.gps;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GpsTask extends AsyncTask
{

	private GpsTaskCallBack callBk = null;
	private Context context = null;
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	private Location location = null;
	private boolean TIME_OUT = false;
	private boolean DATA_CONNTECTED = false;
	private long TIME_DURATION = 5000;
	private GpsHandler handler = null;

	private class GpsHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (callBk == null)
				return;
			switch (msg.what)
			{
				case 0:
					callBk.gpsConnected((GpsData) msg.obj);
					break;
				case 1:
					callBk.gpsConnectedTimeOut();
					break;
			}
		}

	}

	public GpsTask(Context context, GpsTaskCallBack callBk)
	{
		this.callBk = callBk;
		this.context = context;
		gpsInit();
	}

	public GpsTask(Context context, GpsTaskCallBack callBk, long time_out)
	{
		this(context, callBk);
		this.TIME_DURATION = time_out;
	}

	private void gpsInit()
	{
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		handler = new GpsHandler();
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
		} else
		{
			//GPS没有打开
			TIME_OUT = true;
		}
		locationListener = new LocationListener()
		{

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras)
			{
			}

			@Override
			public void onProviderEnabled(String provider)
			{
			}

			@Override
			public void onProviderDisabled(String provider)
			{
			}

			@Override
			public void onLocationChanged(Location l)
			{
				DATA_CONNTECTED = true;
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = transData(l);
				handler.sendMessage(msg);
			}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
	}

	@Override
	protected Object doInBackground(Object... params)
	{
		while (!TIME_OUT && !DATA_CONNTECTED)
		{
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			if (location != null && callBk != null)
			{
				//获得上一次数据
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = transData(location);
				handler.sendMessage(msg);
				break;
			}
		}
		return null;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				TIME_OUT = true;
			}
		}, TIME_DURATION);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(Object result)
	{
		locationManager.removeUpdates(locationListener);
		// ��ȡ��ʱ
		if (TIME_OUT && callBk != null)
			handler.sendEmptyMessage(1);
		super.onPostExecute(result);
	}

	private GpsData transData(Location location)
	{
		GpsData gpsData = new GpsData();
		gpsData.setAccuracy(location.getAccuracy());
		gpsData.setAltitude(location.getAltitude());
		gpsData.setBearing(location.getBearing());
		gpsData.setLatitude(location.getLatitude());
		gpsData.setLongitude(location.getLongitude());
		gpsData.setSpeed(location.getSpeed());
		gpsData.setTime(location.getTime());
		return gpsData;
	}

}