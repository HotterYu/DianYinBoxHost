package com.znt.speaker;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.znt.speaker.permission.PermissionHelper;
import com.znt.speaker.update.ApkDownLoadManager;
import com.znt.speaker.update.ApkDownloadListener;
import com.znt.speaker.update.ApkTools;
import com.znt.speaker.update.DownloadFileInfo;
import com.zyao89.view.zloading.ZLoadingView;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements permission.PermissionInterface,ApkDownloadListener {

    private String pluginName = "com.znt.speaker";

    private NotificationManager mNotificationManager = null;

    private PermissionHelper mPermissionHelper;

    private  ZLoadingView zLoadingView = null;
    private TextView tvStatus = null;
    private Button btnLoad = null;
    private Button btnInstallClick = null;
    private ImageView ivLogo = null;

    private long touchTime = 0;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivLogo = (ImageView) findViewById(R.id.iv_dy_host_log);
        tvStatus = (TextView) findViewById(R.id.tv_loading_status);
        btnLoad = (Button) findViewById(R.id.btn_start);
        btnInstallClick = (Button) findViewById(R.id.btn_install_click);
        zLoadingView = (ZLoadingView) findViewById(R.id.loadingView_1);
        zLoadingView.setLoadingBuilder(Z_TYPE.MUSIC_PATH);//设置类型
        // zLoadingView.setLoadingBuilder(Z_TYPE.values()[type], 0.5); //设置类型 + 动画时间百分比 - 0.5倍
        zLoadingView.setColorFilter(Color.WHITE);//设置颜色

        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();

        loadPlugin();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        showNotification();

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlugin();
            }
        });
        btnInstallClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installByClick();
            }
        });
        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if((System.currentTimeMillis() - touchTime) < 2000)
                {
                    btnInstallClick.setVisibility(View.VISIBLE);
                    // TODO Auto-generated method stub
                }
                else
                {
                    Toast.makeText(MainActivity.this, "再点击一次启动手动安装", Toast.LENGTH_SHORT).show();;
                    touchTime = System.currentTimeMillis();
                }
            }
        });
    }

    private String downloadDir = Environment.getExternalStorageDirectory() + "/DianYinBox/update";
    private void downloadApkFile(final String downUrl)
    {
        ApkDownLoadManager.getInstance().startDownload(downUrl, downloadDir, this);
    }

    private void showLoadingView(boolean show)
    {
        if(show)
        {
            tvStatus.setVisibility(View.VISIBLE);
            zLoadingView.setVisibility(View.VISIBLE);
            btnLoad.setVisibility(View.GONE);

        }
        else
        {
            tvStatus.setVisibility(View.GONE);
            zLoadingView.setVisibility(View.GONE);
            btnLoad.setVisibility(View.VISIBLE);
        }
    }

    private void loadPlugin()
    {
        showLoadingView(true);
        tvStatus.setText("正在加载组建...");

        initDirs();
        File file = new File(downloadDir + "/DianYinBox.apk");
        if(file.exists())
        {
            PluginInfo pluginInfo = RePlugin.install(file.getAbsolutePath());
            if(pluginInfo != null)
            {
                RePlugin.preload(pluginInfo);//耗时
                pluginName = pluginInfo.getName();
                Intent intent = RePlugin.createIntent(pluginName,
                        "com.znt.speaker.VideoPageActivity");
                intent.putExtra("ZNT_SOURCE","1");
                RePlugin.startActivity(MainActivity.this,intent );

                tvStatus.setText("加载成功，正在启动...");
                finish();

            }
            else
            {
                showLoadingView(false);
                Toast.makeText(getApplicationContext(),"插件安装失败",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else
        {
            if(RePlugin.isPluginInstalled(pluginName))
            {
                Intent intent = RePlugin.createIntent(pluginName,
                        "com.znt.speaker.VideoPageActivity");
                intent.putExtra("ZNT_SOURCE","1");
                RePlugin.startActivity(MainActivity.this,intent );
                finish();
            }
            else
            {
                downLoadApk();
            }
            //PluginInfo mPluginInfo = RePlugin.getPluginInfo(pluginName);
        }
    }

    private void downLoadApk()
    {
        downloadApkFile("http://zhunit-music.oss-cn-shenzhen.aliyuncs.com/apk2/DianYinBox.apk");
    }

    final String CHANNEL_ID = "channel_id_1";
    final String CHANNEL_NAME = "channel_name_1";
    private void showNotification()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //只在Android O之上需要渠道
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
            //通知才能正常弹出
            notificationChannel.setSound(null,null);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,CHANNEL_ID);
        Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this,0,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        //RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.view_update_dialog);

        builder.setSmallIcon(R.mipmap.logo)
                .setTicker(getResources().getString(R.string.app_name))
                .setContentTitle(getResources().getString(R.string.app_name_running))
                .setContentText(getResources().getString(R.string.app_desc))
                .setPriority(Notification.PRIORITY_HIGH)
                /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：Notification.DEFAULT_ALL就是3种全部提醒**/
                //.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setSound(null)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setContentIntent(pendingIntent2)
                .setAutoCancel(false);
        Notification  notification=builder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager.notify(18, notification);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        showLoadingView(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION

        };
    }
    @Override
    public void requestPermissionsSuccess()
    {
        //权限请求用户已经全部允许
        try
        {
            loadPlugin();
        }
        catch (Exception e)
        {
            if(e == null)
                Toast.makeText(getApplicationContext(),"插件安装失败",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(),"初始化失败："+e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        showPermissions();
        //mPermissionHelper.requestPermissions();
        //close();
    }

    private void showPermissions(){
        final Dialog dialog=new android.app.AlertDialog.Builder(this).create();
        View v=LayoutInflater.from(this).inflate(R.layout.dialog_permissions,null);
        dialog.show();
        dialog.setContentView(v);

        Button btn_add= (Button) v.findViewById(R.id.btn_add);
        Button btn_diss= (Button) v.findViewById(R.id.btn_diss);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionHelper.toPermissionSetting(MainActivity.this);
                dialog.dismiss();
                close();
            }
        });

        btn_diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                close();
            }
        });
    }

    private void close()
    {
        System.exit(0);
    }

    private void initDirs()
    {
        File dirFv = new File(downloadDir);
        if(!dirFv.exists())
            dirFv.mkdirs();
    }

    private void installByClick()
    {

        File apkFile = new File(downloadDir + "/DianYinBox.apk");

        if(apkFile.exists())
        {
            if(isSignatureMatch(apkFile))
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(
                            getApplicationContext()
                            , "com.znt.speaker.fileprovider"
                            , apkFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {

                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                }
                startActivity(intent);

            }
            else
                Toast.makeText(getApplicationContext(),"签名不一致，请重试" ,Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"安装包不存在，开始下载" ,Toast.LENGTH_SHORT).show();
            downLoadApk();
        }

    }

    private boolean isSignatureMatch(File apkFile)
    {
        String curSign = ApkTools.getSignature(this);
        List<String> signs = ApkTools.getSignaturesFromApk(apkFile);

        if(curSign == null || signs == null || signs.size() == 0
                || signs.get(0) == null || !curSign.equals(signs.get(0)))
        {
            //apkFile.delete();
            return false;
        }

        return true;
    }

    @Override
    public void onDownloadStart(DownloadFileInfo info) {

    }

    @Override
    public void onFileExist(final DownloadFileInfo info) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(btnInstallClick.isShown())
                {
                    btnInstallClick.setText("开始安装");
                }
                else
                    loadPlugin();
            }
        });

    }

    @Override
    public void onDownloadProgress(final long progress, final long size) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(btnInstallClick.isShown())
                {
                    btnInstallClick.setText("下载:" + progress +" / " + size);
                }
                else
                    tvStatus.setText("正在下载组建:" + progress +" / " + size);
            }
        });
    }

    @Override
    public void onDownloadError(DownloadFileInfo info, String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tvStatus.setText("组建下载失败，请重试");
                showLoadingView(false);
            }
        });

    }

    @Override
    public void onDownloadFinish(final File info) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(btnInstallClick.isShown())
                {
                    if(info.exists())
                        btnInstallClick.setText("开始安装");
                }
                else
                    loadPlugin();
            }
        });

    }

    @Override
    public void onDownloadExit(DownloadFileInfo info) {

    }

    @Override
    public void onSpaceCheck(long size) {

    }
}
