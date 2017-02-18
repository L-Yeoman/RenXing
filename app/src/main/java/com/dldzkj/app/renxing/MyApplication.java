package com.dldzkj.app.renxing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.Service;
import android.util.Log;

import com.dldzkj.app.renxing.bean.BBSModel;
import com.dldzkj.app.renxing.bean.Playlist;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.bean.UserDatum;
import com.dldzkj.app.renxing.blelib.services.BluetoothChatService;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;
import com.dldzkj.app.renxing.blelib.services.MusicService;
import com.dldzkj.app.renxing.utils.CrashHandler;
import com.dldzkj.app.renxing.utils.ExpressionUtil;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;


public class MyApplication extends Application {
    /**********
     * 常量
     ***********/
    public static final String HOST_URL = WebUtils.RENXING_WEB_PICS;
    public static final String UPDATE_VERSION_URL = HOST_URL + "/apk/version.xml";
    public static final String UPDATE_ENG_VERSION_URL = HOST_URL + "/apk/ENG/version.xml";
    public static final String WEB_SERVER_URL = HOST_URL + "/WebService/SchoolMessage.asmx";
    public static String SERVER_CONFIG_LOADPATH = "";
    public static final int SUCCESS_CODE = 0;
    public static final int FAILURE_CODE = 101;
    public static final int OPERATION_CODE = 102;
    public static int WIN_WIDTH = 0;
    public static final int DB_VERSION = 3;


    private static MyApplication instance;
    public BluetoothChatService mChatService;
    public Stack<Activity> mStack;
    private DbUtils mDbUtils;
    /***********
     * 音乐播放器相关
     **************/
    public NotificationManager nManager;
    public MusicService mMusicServer;
    public static final String ACTION_CHANGE_MUSIC = "com.dingliang.douqu.music";
    public int musicPlayPosition = -1;
    public boolean isPlay = false;
    public boolean isMusicList = false;
    public static boolean isSupportMuc = true;
    public boolean mBound = false;
    public ArrayList<Playlist> musicList;
    public ArrayList<Playlist> favList;
    public BluetoothLeService blueServer;
    public static DisplayImageOptions img_option = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_picture_loading)
            .showImageOnFail(R.drawable.ic_picture_loadfailed)
            .showImageForEmptyUri(R.drawable.ic_picture_loadfailed)
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mStack=new Stack<Activity>();
        ExpressionUtil.getFileText(this);
        //注册crashHandler
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }


    public void initU13ImgLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }

    public String getProductValue() {
        if (blueServer != null) {
            return blueServer.getProductValue();
        } else if (mChatService != null) {
            return mChatService.productValue;
        }
        return "";
    }

    public void sendBlueOrder(int key) {
        Log.d("zxl", "发送指令：" + key);
        //4.0
        byte[] data = null;
        if (blueServer != null && blueServer.isConnected()) {
//			data=SampleGattAttributes.getOrder(key);
            data = new byte[]{(byte) (key >> 8), (byte) key};
            blueServer.sendOrderCharact1(data);
        }
        //3.0
        if (mChatService != null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
            data = SampleGattAttributes.sendOrderChractOne(key);
            mChatService.write(data);
            return;
        }
    }

    public void sendBlueOrder2(String key) {
        byte[] data = null;
        if (blueServer != null && blueServer.isConnected()) {
            Log.d("zxl", "走4.0");
            data = SampleGattAttributes.getOrder(key);
            blueServer.sendOrderCharact2(data);
            return;
        }
        if (mChatService != null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
            Log.d("zxl", "走3.0");
            data = SampleGattAttributes.sendOrderChractTwo(key);
            mChatService.write(data);
            return;
        }
    }

    public boolean BlueServiceIsNull() {
        return blueServer == null && mChatService == null;
    }

    public boolean BlueServiceIsConnnected() {
        if (blueServer != null && blueServer.isConnected()) {
            return true;
        }
        if (mChatService != null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
            return true;
        }
        return false;
    }

    public void disConnectBlue() {
        if (blueServer != null && blueServer.isConnected()) {
            blueServer.disconnect();
            return;
        }
        if (mChatService != null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
            mChatService.stop();
            return;
        }
    }


    public boolean isSDcardCanRead() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public DbUtils getDbUtils() {
        if (mDbUtils == null) {
            return DbUtils.create(getApplicationContext(), "db_douqu", DB_VERSION, new DbUtils.DbUpgradeListener() {

                @Override
                public void onUpgrade(DbUtils db, int oldversion, int currentversion) {
                    if (oldversion < 3) {//低于3的版本都需要删除playlist表
                        try {
                            db.deleteAll(Playlist.class);
                        } catch (DbException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
        return mDbUtils;
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public String getAppPath() {
        String appPath = "";
        if (isSDcardCanRead()) {
            appPath = android.os.Environment.getExternalStorageDirectory()
                    .getPath() + "/" + "AoGu";
            File file = new File(appPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return appPath;
    }


    public String getCricleDir() {
        String temp = getAppPath() + "/cricleimg";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }


    public Properties loadConfig() {
        Properties properties = new Properties();
        try {
            File fil = new File(getFilesDir() + "/config1.properties");
            if (!fil.exists())
                fil.createNewFile();
            FileInputStream s = new FileInputStream(fil);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }


    public boolean saveConfig(Properties properties) {
        try {
            File fil = new File(getFilesDir() + "/config1.properties");
            if (!fil.exists())
                fil.createNewFile();
            FileOutputStream s = new FileOutputStream(fil, false);
            properties.store(s, "");
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String getAppTempDir() {
        String temp = getAppPath() + "/temp";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }


    public String getAppThumbDir() {
        String temp = getAppPath() + "/thumb";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }


    public String getAppImageDir() {
        String temp = getAppPath() + "/images";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }


    public String getAppCrashDir() {
        String temp = getAppPath() + "/crash";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }

    public String getAppFileDir() {
        String temp = getAppPath() + "/files";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }


    public String getCardDir() {
        String temp = getAppThumbDir() + "/card";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }

    public String getAppCacheDir() {
        String temp = getAppPath() + "/caches";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }


    public String getAppAvatarDir() {
        String temp = getAppThumbDir() + "/avatar";
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return temp;
    }

    public NotificationManager getNotify() {
        if (nManager == null) {
            nManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            return nManager;
        } else {
            return nManager;
        }
    }

    /************
     * activity堆栈管理
     **********/
    //退出栈顶Activity
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            mStack.remove(activity);
            activity = null;
        }
    }

    //获得当前栈顶Activity
    public Activity currentActivity() {
        try {
            Activity activity = mStack.lastElement();
            return activity;
        } catch (Exception e) {
            return null;
        }
    }

    public void finishAct(Class<?> cla) {
        for (int i = 0; i < mStack.size(); i++) {
            Activity activity = mStack.get(i);
            if (cla.getName().equals(activity.getClass().getName())) {
                activity.finish();
                mStack.remove(i);
                return;
            }
        }
    }
    public void finishAllAct() {
        for (int i = 0; i < mStack.size(); i++) {
            Activity activity = mStack.get(i);
                activity.finish();
                mStack.remove(i);
        }
    }
    //将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (mStack == null) {
            mStack = new Stack<Activity>();
        }
        mStack.add(activity);
    }

    //退出栈中所有Activity
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (cls == null && activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }
}
