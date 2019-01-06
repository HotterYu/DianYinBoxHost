package com.znt.speaker.update;


import java.io.File;

/**
 * Created by Administrator on 2017/2/23.
 */
public interface ApkDownloadListener {


	public void onDownloadStart(DownloadFileInfo info);
	
	public void onFileExist(DownloadFileInfo info);
	
	public void onDownloadProgress(long progress, long size);
	
	public void onDownloadError(DownloadFileInfo info, String error);
	
	public void onDownloadFinish(File info);
	
	public void onDownloadExit(DownloadFileInfo info);
	
	public void onSpaceCheck(long size);

}
