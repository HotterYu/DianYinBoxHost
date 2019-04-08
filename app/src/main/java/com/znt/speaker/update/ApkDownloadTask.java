package com.znt.speaker.update;

import android.os.AsyncTask;


import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ApkDownloadTask extends AsyncTask<String,Long,DownLoadResult>
{
    public static final int TYPE_SUCCESS=0;

    public static final int TYPE_FAILED=1;

    public static final int TYPE_PAUSED=2;

    public static final int TYPE_CANCELED=3;

    private ApkDownloadListener listener;

    private boolean isCanceled=false;

    private boolean isPaused=false;

    private long lastProgress;
    private String apkFile = "";
    private String endTag = ".temp";

    public ApkDownloadTask(ApkDownloadListener listener, String filePath) 
    {
    	apkFile = filePath + "/DianYinBox.apk";
        this.listener = listener;
    }
    private long contentLength = 0;

    @Override
    protected DownLoadResult doInBackground(String... params) 
    {
    	
    	DownLoadResult downLoadResult = new DownLoadResult();
    	
        InputStream is=null;
        RandomAccessFile savedFile = null;
        File file = null;

        String downloadUrl = params[0];
        downloadUrl = UrlUtil.decode(downloadUrl, "UTF-8");
        //downloadUrl = UrlUtil.getASCIIEncodedUrl(downloadUrl, "UTF-8");

        if(downloadUrl == null)
        {
        	downLoadResult.status = TYPE_FAILED;
        	downLoadResult.hint = "url is null";
        	return downLoadResult;
        }
        	
        int lastIndex = downloadUrl.lastIndexOf("/");
        if(lastIndex < 0)
    	{
	    	downLoadResult.status = TYPE_FAILED;
	    	downLoadResult.hint = "lastIndex char / < 0";
	    	return downLoadResult;
    	}

        file = new File(apkFile);
        if(file.exists())
        {
            file.delete();
        }

        if(downloadUrl.contains("`"))
            downloadUrl = downloadUrl.replace("`", "");

        contentLength = getContentLength(downloadUrl);
        if(contentLength <= 0)
        {
        	downLoadResult.status = TYPE_FAILED;
        	downLoadResult.hint = "contentLength :" + contentLength;
            return downLoadResult;
        }
        if(listener != null)
        	listener.onSpaceCheck(contentLength);

        long downloadLength = 0;
        File tempFile = new File(apkFile + ".temp");
        if(tempFile.exists())
        {
            tempFile.delete();
            //downloadLength = tempFile.length();
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                //.addHeader("RANGE","bytes=" + downloadLength+"-")
                .url(downloadUrl)
                .build();
        try 
        {
            Response response = client.newCall(request).execute();
            if(response!=null)
            {
                is = response.body().byteStream();
                
                savedFile = new RandomAccessFile(tempFile,"rw");
                //savedFile.seek(downloadLength);
                byte[] b = new byte[1024 * 2];
                //int total = 0;
                int len;
                while((len = is.read(b)) != -1)
                {
                    if(isCanceled)
                    {
                    	downLoadResult.status = TYPE_CANCELED;
                    	downLoadResult.hint = "canceled";
                        return downLoadResult;
                    }
                    else if(isPaused)
                    {
                    	downLoadResult.status = TYPE_PAUSED;
                    	downLoadResult.hint = "paused";
                        return downLoadResult;
                    }
                    else 
                    {
                    	downloadLength += len;
                        savedFile.write(b,0,len);

                        publishProgress(downloadLength, contentLength);
                    }
                }
                
                response.body().close();
                
                if(tempFile.exists() && getTempFile(file).length() == contentLength)
                {
                	downLoadResult.file = renameFile(tempFile);
                }
                else
                {
                	tempFile.delete();
                	downLoadResult.status = TYPE_FAILED;
                	downLoadResult.hint = "download success, but file length not correct or not exist";
                	return downLoadResult;
                }
                downLoadResult.status = TYPE_SUCCESS;
                return downLoadResult;
            }
        } 
        catch (Exception e)
        {
        	downLoadResult.status = TYPE_FAILED;
        	downLoadResult.hint = "download Exception :" + e.getMessage();
            e.printStackTrace();
        }
        finally 
        {
            try
            {
                if(is != null)
                {
                    is.close();
                }
                if(savedFile != null)
                {
                    savedFile.close();
                }
                if(isCanceled && file != null)
                {
                    file.delete();
                }
            }
            catch (Exception e)
            {
            	downLoadResult.status = TYPE_FAILED;
            	downLoadResult.hint = "download Exception :" + e.getMessage();
                e.printStackTrace();
            }
        }
        
        return downLoadResult;
    }


    @Override
    protected void onProgressUpdate(Long...values){
    	long progress=values[0];
        if(progress>lastProgress){
            listener.onDownloadProgress(progress, values[1]);
            lastProgress=progress;
        }
    }


    @Override
    protected void onPostExecute(DownLoadResult downLoadResult) {
    	int status = downLoadResult.status;
        switch (status){
            case TYPE_SUCCESS:
                listener.onDownloadFinish(downLoadResult.file);
                break;
            case TYPE_FAILED:
                listener.onDownloadError(null, downLoadResult.hint);
                break;
            case TYPE_PAUSED:
               // listener.onPaused();
                break;
            case TYPE_CANCELED:
                //listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void  pauseDownload(){
        isPaused=true;
    }

    public void cancelDownload(){
        isCanceled=true;
    }


    private long getContentLength(String downloadUrl)
    {
    	if(downloadUrl.contains("`"))
    		downloadUrl = downloadUrl.replace("`", "");
        OkHttpClient client = new OkHttpClient();
        try
        {
            Request request = new Request.Builder().url(downloadUrl).build();
            Response response = client.newCall(request).execute();
            if(response !=null && response.isSuccessful())
            {
                long contentLength = response.body().contentLength();
                response.body().close();
                return contentLength;
            
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return  0;
    }

    private File getTempFile(File file)
    {
    	String filePath = file.getAbsolutePath();
    	if(!filePath.endsWith(endTag))
    		filePath = filePath + endTag;
    	return new File(filePath);
    }
    
    private File renameFile(File file)
    {
    	String filePath = file.getAbsolutePath();
    	if(filePath.endsWith(endTag))
    		filePath = filePath.replace(endTag, "");
    	File renameFile = new File(filePath);
    	file.renameTo(renameFile);
    	return renameFile;
    }
    

}
