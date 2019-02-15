package com.znt.speaker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class NetWorkUtils 
{



	/**
	 * 鐏忓攨p閻ㄥ嫭鏆ｉ弫鏉胯埌瀵繗娴嗛幑銏″灇ip瑜般垹绱?
	 * 
	 * @param ipInt
	 * @return
	 */
	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲ip閸︽澘娼?
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		try {
			// for (Enumeration<NetworkInterface> en = NetworkInterface
			// .getNetworkInterfaces(); en.hasMoreElements();) {
			// NetworkInterface intf = en.nextElement();
			// for (Enumeration<InetAddress> enumIpAddr = intf
			// .getInetAddresses(); enumIpAddr.hasMoreElements();) {
			// InetAddress inetAddress = enumIpAddr.nextElement();
			// if (!inetAddress.isLoopbackAddress()) {
			// return inetAddress.getHostAddress().toString();
			// }
			// }
			// }
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			return int2ip(i);
		} catch (Exception ex) {
			return "";
		}
		// return null;
	}
	
	public static String getMacAddress() {
       
		String macSerial = null;
        String str = "";
        try {
                Process pp = Runtime.getRuntime().exec(
                                "cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);


                for (; null != str;) {
                        str = input.readLine();
                        if (str != null) {
                                macSerial = str.trim();// 閸樿崵鈹栭弽锟?
                                break;
                        }
                }
        } catch (Exception ex) {
                // 鐠у绨ｆ妯款吇閸婏拷
                ex.printStackTrace();
        }
        if(TextUtils.isEmpty(macSerial))
        	return "";
        if(macSerial.contains(":"))
        	macSerial = macSerial.replace(":", "");
        return macSerial.trim();
    }
	public static String callCmd(String cmd,String filter) {   
	     String result = "";   
	     String line = "";   
	     try {
	         Process proc = Runtime.getRuntime().exec(cmd);
	         InputStreamReader is = new InputStreamReader(proc.getInputStream());   
	         BufferedReader br = new BufferedReader (is);   
	          
	         //閹笛嗩攽閸涙垝鎶md閿涘苯褰ч崣鏍波閺嬫粈鑵戦崥顐ｆ箒filter閻ㄥ嫯绻栨稉锟界悰锟?
	         while ((line = br.readLine ()) != null && line.contains(filter)== false) {   
	             //result += line;
	             Log.i("test","line: "+line);
	         }
	          
	         result = line;
	         Log.i("test","result: "+result);
	     }   
	     catch(Exception e) {   
	         e.printStackTrace();   
	     }   
	     return result;   
	 }
	public static boolean checkEthernet(Context context)
	{
		ConnectivityManager conn =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if(networkInfo == null)
			return false;
		return networkInfo.isConnected();
	}
	
	/** 
     * 
     *  
     * @return 
     */ 
    public static boolean isNetConnected(Context context) 
    {  
    	if(context == null)
    		return false;
    	
    	if(TextUtils.isEmpty(getWifiName(context)))
    		return false;
    	
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (cm != null) 
        {  
            NetworkInfo[] infos = cm.getAllNetworkInfo();  
            if (infos != null) 
            {  
                for (NetworkInfo ni : infos) 
                {  
                    if (ni.isConnected())
                    {  
                        return true;  
                    }  
                }  
            }  
        }  
        return false;  
    }   
    public static String getWifiName(Context context)
    {
    	WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        return info != null ? info.getSSID() : null;
    }



	private static String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}