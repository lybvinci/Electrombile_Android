package com.xunce.electrombile.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.FragmentActivity;

/**
 * App自动更新之通知栏下载
 * @author 402-9
 *
 */
public class UpdateAppService extends Service{
    File file;
    private Context context;
    private Notification notification;
    private NotificationManager nManager;
    private PendingIntent pendingIntent,updatePendingIntent;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CreateInform();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //创建通知
    public void CreateInform() {
        //定义一个PendingIntent，当用户点击通知时，跳转到某个Activity(也可以发送广播等)
        Intent intent = new Intent(context,FragmentActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        //创建一个通知
        notification = new Notification(R.drawable.logo, "开始下载~~", System.currentTimeMillis());
        notification.setLatestEventInfo(context, "正在下载传安全宝", "点击查看详细内容", pendingIntent);

        //用NotificationManager的notify方法通知用户生成标题栏消息通知
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nManager.notify(100, notification);//id是应用中通知的唯一标识
        //如果拥有相同id的通知已经被提交而且没有被移除，该方法会用更新的信息来替换之前的通知。
        new Thread(new updateRunnable()).start();//这个是下载的重点，是下载的过程
    }
    class updateRunnable implements Runnable{
        int downnum = 0;//已下载的大小
        int downcount= 0;//下载百分比
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
              //test  DownLoadApp("http://fir.im/api/v2/app/install/554331e6bf7f222c2600493b?token=39d16f30ebf111e4a2da4efe6522248a4b9d9ed4");
             //正式链接
                DownLoadApp("http://fir.im/api/v2/app/install/556c810d2bb8ac0e5d001a30?token=b9d54ba0b12411e4bc2c492c76a46d264a53ba2f");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        public void DownLoadApp(String urlString) throws Exception{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int length = urlConnection.getContentLength();
            InputStream inputStream = urlConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream(getFile());
//          OutputStream outputStream = new FileOutputStream(new File("/mnt/sdcard/App/hello.apk"));

            Uri uri = Uri.fromFile(file);
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            updatePendingIntent = PendingIntent.getActivity(UpdateAppService.this, 0, installIntent, 0);

            byte buffer[] = new byte[1024*3];
            int readsize = 0;
            while((readsize = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, readsize);
                downnum += readsize;
                if((downcount == 0)||(int) (downnum*100/length)-1>downcount){
                    downcount += 1;
                    notification.setLatestEventInfo(context, "正在下载传安全宝", "已下载了"+(int)downnum*100/length+"%", pendingIntent);
                    nManager.notify(100, notification);
                }
                if (downnum==length) {
                  //  notification.setLatestEventInfo(context, "已下载完成传安全宝", "点击安装", updatePendingIntent);
                    nManager.notify(100, notification);
                    Intent intent1 = new Intent();
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.setAction(android.content.Intent.ACTION_VIEW);
                    intent1.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                    startActivity(intent1);
                    Intent stopservice=new Intent(context,UpdateAppService.class);
                    stopService(stopservice);
                }
            }
            inputStream.close();
            outputStream.close();
        }
        //获取文件的保存路径
        public File getFile() throws Exception{
            String SavePath = getSDCardPath() + "/App";
            File path = new File(SavePath);
            file = new File(SavePath + "/anquanbao.apk");
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        }
        //获取SDCard的目录路径功能
        private String getSDCardPath() {
            File sdcardDir = null;
            // 判断SDCard是否存在
            boolean sdcardExist = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            if (sdcardExist) {
                sdcardDir = Environment.getExternalStorageDirectory();
            }
            return sdcardDir.toString();
        }
    }
}