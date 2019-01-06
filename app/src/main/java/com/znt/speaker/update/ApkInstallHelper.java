
package com.znt.speaker.update;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.PrintWriter;


public class ApkInstallHelper
{ 
	
	private Activity activity = null;
	
	private static ApkInstallHelper INSTANCE = null;
	
	public ApkInstallHelper(Activity activity)
	{
		this.activity = activity;
	}
	
	public static ApkInstallHelper getInstance(Activity activity)
	{
		if(INSTANCE == null)
			INSTANCE = new ApkInstallHelper(activity);
		return INSTANCE;
	}
	
    public boolean install(String apkPath)
    {

        if(hasRootPerssion())
        {

            return clientInstall(apkPath);
        }
        else
        {

            File file = new File(apkPath);
            if(!file.exists())
                return false; 
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
            activity.startActivity(intent);
            return true;
        }
    }
    
    public boolean uninstall(String packageName)
    {
        if(hasRootPerssion())
        {

            return clientUninstall(packageName);
        }
        else
        {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,packageURI);
            uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(uninstallIntent);
            return true;
        }
    }
     

    private boolean hasRootPerssion()
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     

    private boolean clientInstall(String apkPath)
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 "+apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r "+apkPath);
//          PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     

    private boolean clientUninstall(String packageName)
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall "+packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value); 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     

    public boolean startApp(String packageName,String activityName)
    {
        boolean isSuccess = false;
        String cmd = "am start -n " + packageName + "/" + activityName + " \n";
        Process process = null;
        try 
        {
           process = Runtime.getRuntime().exec(cmd);
           int value = process.waitFor();  
           return returnResult(value);
        } 
        catch (Exception e) 
        {
          e.printStackTrace();
        } 
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return isSuccess;
    }
     
     
    private boolean returnResult(int value)
    {

        if (value == 0) 
        {
            return true;
        } 
        else if (value == 1) 
        { 

            return false;
        } 
        else 
        { 

            return false;
        }  
    }
}