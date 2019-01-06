package com.znt.speaker.update;

import android.content.Context;


import java.util.LinkedList;

public class ApkDownLoadManager 
{
	private Context context = null;
	
	private ApkDownloadTask downloadTask;
    
    private boolean isCancel = false;
    private MediaInfor curDownloadSong = null;
    private LinkedList<MediaInfor> downloadList = new LinkedList<MediaInfor>();
    
    public static ApkDownLoadManager INSTANCE = null;
    public static ApkDownLoadManager getInstance()
	{
		if(INSTANCE == null)
		{
			synchronized (ApkDownLoadManager.class) 
			{
				if(INSTANCE == null)
					INSTANCE = new ApkDownLoadManager();
			}
		}
		
		return INSTANCE;
	}
    
    public void init(Context context)
    {
    	this.context = context.getApplicationContext();
    }
    
    public void cancelAll()
    {
    	isCancel = true;
    	if(downloadTask != null)
    	{
    		downloadTask.cancelDownload();
        	//downloadTask.cancel(true);
        	downloadTask = null;
    	}
    }

    public void  startDownload(String url, String dir, ApkDownloadListener listener)
    {
    	if(downloadTask != null)
    	{
    		downloadTask.cancelDownload();
    		downloadTask.cancel(true);
    		downloadTask = null;
    	}
    	downloadTask= new ApkDownloadTask(listener, dir);
        downloadTask.execute(url);
    }


    public void pauseDownload()
    {
        if(downloadTask!=null)
        {
            downloadTask.pauseDownload();
        }
    }


    public void cancelDownload()
    {
        if(downloadTask!=null)
        {
            downloadTask.cancelDownload();
        }
        isCancel = true;

    }
}
