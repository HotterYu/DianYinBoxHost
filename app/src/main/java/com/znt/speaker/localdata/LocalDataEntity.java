package com.znt.speaker.localdata;

import android.content.Context;

public class LocalDataEntity
{

	private Context context = null;
	private final String DEVICE_CODE_OLD = "DEVICE_CODE";
	private static LocalDataEntity INSTANCE = null;
	private MySharedPreference sharedPre = null;
	public static LocalDataEntity newInstance(Context context)
	{
		if(INSTANCE == null)
		{
			synchronized (LocalDataEntity.class)
			{
				if(INSTANCE == null)
					INSTANCE = new LocalDataEntity(context);
			}
		}
		return INSTANCE;
	}
	public LocalDataEntity(Context context)
	{
		this.context = context;
		sharedPre = MySharedPreference.newInstance(context);
	}
	public String getOldDeviceId()
	{
		return sharedPre.getData(DEVICE_CODE_OLD, "");
	}

}
