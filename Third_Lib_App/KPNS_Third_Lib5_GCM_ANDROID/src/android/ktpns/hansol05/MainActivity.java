package android.ktpns.hansol05;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import com.google.android.gcm.GCMRegistrar;
import com.ktpns.lib.KPNSApis;
import com.ktpns.lib.OnKPNSInitializeEventListener;
import com.ktpns.lib.service.PushClientService;
import com.ktpns.lib.util.Constant;
import com.ktpns.lib.util.Logger;
import com.ktpns.lib.util.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String PUSH_APP_ID = "0000000016";
    public static final String PUSH_CP_ID = "hansol";
    private TextView textResult;
    private static StringBuilder messageBuilder;
    private WeakHandler mHandler = new WeakHandler(this);
    public static final String GCM_SENDER_KEY = "489404086197";
    public static final String SERVER_KEY = "AIzaSyDzyrvyyu3H-95tKRCyYl1K_32-dPy8ue0";
    public static String GCM_REGISTRATION_ID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textResult = (TextView) findViewById(R.id.textResult);
        messageBuilder = new StringBuilder();
        PushReceiver.setHandler(mHandler);
        registerGCM();
        
        // try {
        // packageInfo = getPackageManager().getPackageInfo("com.ktpns.pa", 0);
        // int flags = packageInfo.applicationInfo.flags;
        // boolean isDebugMode = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        // Log.e(getClass().getName(), "flag : " + isDebugMode + " / " + flags);
        // } catch (NameNotFoundException e) {
        // e.printStackTrace();
        // }

        // startService(new Intent(this, PushClientService.class));
        // sendBroadcast(new Intent(Constant.ACTION_START_PUSH_CLIENT));

        // try {
        // SSLContext sslContext = SSLContext.getInstance("TLSv1");
        // sslContext.init(null, null, null);
        // String[] protocols = sslContext.getSupportedSSLParameters().getProtocols();
        // for (String protocol : protocols) {
        // Log.d(getClass().getName(),"Context supported protocol: " + protocol);
        // }
        //
        // SSLEngine engine = sslContext.createSSLEngine();
        // String[] supportedProtocols = engine.getSupportedProtocols();
        // for (String protocol : supportedProtocols) {
        // Log.d(getClass().getName(),"Engine supported protocol: " + protocol);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        PackageManager pm = this.getPackageManager();
        Logger.mLogLevel = 2;
        
    }
    
    private boolean isAdminUser(Context context)
    {
        try
        {
            Method getUserHandle = UserManager.class.getMethod("getUserHandle");
            int userHandle = (Integer) getUserHandle.invoke(context.getSystemService(Context.USER_SERVICE));
            return userHandle == 0;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    
    private void checkUser() {
        try {
            UserManager um = (UserManager) getSystemService(Context.USER_SERVICE);
            int count = um.getUserCount();
            boolean adminUser = um.isSystemUser();
            Log.i("TAG", "isAdminUser : " + count + " / " +adminUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // Intent result_intent = new Intent();
        // result_intent.setComponent(new ComponentName("com.ktpns.pa", "com.ktpns.lib.receiver.DeviceEventReceiver"));
        // result_intent.setAction("VERIFICATION_MODE_CHANGE");
        // result_intent.putExtra("VERIFICATION_MODE", 0x02);
        // sendOrderedBroadcast(result_intent, null, new BroadcastReceiver() {
        // @Override
        // public void onReceive(Context context, Intent intent) {
        // Bundle extra = intent.getExtras();
        // appendString( "VERIFICATION_MODE_CHANGE "+extra.size());
        // appendString( "VERIFICATION_MODE_CHANGE "+extra.keySet().iterator().next());
        // if (extra == null) {
        // appendString("sendOrderedBroadcast onReceive is null / " +intent.getAction());
        // } else {
        // appendString("sendOrderedBroadcast onReceive " + extra.getInt("VERIFICATION_MODE", -1));
        // }
        // }
        // }, null, Activity.RESULT_OK, null, null);
    }
    

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.textResult:
            messageBuilder = new StringBuilder();
            textResult.setText("");
            break;

        case R.id.btnTokenRegister:
            registerGCM();
            KPNSApis.requestInstance(this, new OnKPNSInitializeEventListener() {

                @Override
                public void onSuccessInitialize(KPNSApis kpnsApis) {
                    appendString("REGISTER :: onSuccessInitialize");
                    appendString("REGISTER :: SERVER_KEY : "+SERVER_KEY);
                    appendString("REGISTER :: GCM_REGISTRATION_ID : "+GCM_REGISTRATION_ID);
                    
                    kpnsApis.register(PUSH_APP_ID, PUSH_CP_ID, SERVER_KEY, GCM_REGISTRATION_ID);
                }

                @Override
                public void onFailInitialize() {
                    Logger.d(getClass(), "KPNS Initialize is Fail.");
                    appendString("REGISTER :: KPNS Initialize is Fail.");
                    startService(new Intent(MainActivity.this, PushClientService.class).setAction(Constant.ACTION_START_SERVICE));
                }
            });
            appendString("REGISTER :: 요청\n============================");
            break;

        case R.id.btnCheckStatus:
            KPNSApis.requestInstance(this, new OnKPNSInitializeEventListener() {

                @Override
                public void onSuccessInitialize(KPNSApis kpnsApis) {
                    kpnsApis.getConnectionState();
                }

                @Override
                public void onFailInitialize() {
                    Logger.d(getClass(), "KPNS Initialize is Fail.");
                }
            });

            appendString("SERVICE_STATUS :: 요청\n============================");
            break;

        case R.id.btnCheckClientInstall:
            boolean isInstall = Utils.isExistClient(this);
            appendString("CLIENT 설치 :: " + (isInstall ? "설치" : "미설치"));
            // Intent result_intent = new Intent();
            // result_intent.setComponent(new ComponentName("com.ktpns.pa", "com.ktpns.lib.receiver.DeviceEventReceiver"));
            // result_intent.setAction("REQUEST_CLIENT_TOKEN");
            // sendBroadcast(result_intent);
            // sendOrderedBroadcast(result_intent, null, new BroadcastReceiver() {
            // @Override
            // public void onReceive(Context context, Intent intent) {
            // Bundle extra = intent.getExtras();
            // if (extra == null) {
            // appendString("sendOrderedBroadcast onReceive is null / " +intent.getAction());
            // } else {
            // appendString("sendOrderedBroadcast onReceive " + extra.getString("RESULT"));
            // appendString("sendOrderedBroadcast onReceive " + extra.getString("MESSAGE"));
            // }
            // }
            // }, null, Activity.RESULT_OK, null, null);
            break;

        case R.id.btnCheckClientRunning:
            boolean isRunning = Utils.isClientRunning();
            appendString("CLIENT 실행 :: " + (isRunning ? "실행중" : "중지"));
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.ktpns.pa", "com.ktpns.pa.KpnsReceiver"));
            intent.setAction(Constant.ACTION_SHOW_TESTVIEW);
            sendBroadcast(intent);
            break;

        case R.id.btnGetPushClientVersion:
            int pushClientVer = Utils.getPushClientVer(this);
            appendString("CLIENT 버전 :: " + (pushClientVer < 200 ? "1.x CLIENT 설치" : "2.x CLIENT 설치"));

            // updateKPNS(getApplicationContext());
            break;
        }
    }

    // private void updateKPNS(Context context) {
    // // 마켓 강제 업데이트 실행
    // DbMsgLog.getInstance().putMsgLogForTester(context, "올레 마켓에 업데이트 요청 시도");
    // Intent intent = new Intent();
    // try {
    // intent.setAction(Constant.ACTION_SEND_FORCE_UPDATE);
    // intent.putExtra("package_name", Constant.PUSH_CLIENT_PACKAGE_NAME);
    // intent.putExtra("contents_id", Config.KPNS_CONTENTS_ID);
    // context.startService(intent);
    // } catch (Exception e) {
    // DbMsgLog.getInstance().putMsgLogForTester(context, e.toString());
    // Log.e(getClass().getCanonicalName(), e.toString());
    // }
    //
    // intent = new Intent("com.kt.olleh.intent.action.FORCE_DOWNLOAD_RECEIVED");
    // intent.putExtra("download_type", "EXT_APP");
    // intent.putExtra("contents_id", Config.KPNS_CONTENTS_ID);
    // intent.putExtra("package_name", Constant.PUSH_CLIENT_PACKAGE_NAME);
    // intent.putExtra("p_mode", 0);
    // context.sendBroadcast(intent, "com.kt.olleh.permission.DOWNLOAD");
    // DbMsgLog.getInstance().putMsgLogForTester(context, "올레 마켓에 업데이트 요청 성공");
    // }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PushReceiver.setHandler(null);
        
        // Intent result_intent = new Intent();
        // result_intent.setComponent(new ComponentName("com.ktpns.pa", "com.ktpns.lib.receiver.DeviceEventReceiver"));
        // result_intent.setAction("VERIFICATION_MODE_CHANGE");
        // result_intent.putExtra("VERIFICATION_MODE", 0x03);
        // sendBroadcast(result_intent);
    }

    protected void appendString(String str) {
        mHandler.sendMessage(mHandler.obtainMessage(1, str));
    }

    private static class WeakHandler extends Handler {
        private final WeakReference<MainActivity> mSections;

        public WeakHandler(MainActivity section) {
            mSections = new WeakReference<MainActivity>(section);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mSections.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
        case 1:
            String content = (String) msg.obj;

            if (messageBuilder != null) {

                messageBuilder.append(content);
                messageBuilder.append("\n\n");

                if (textResult != null) {
                    textResult.setText(messageBuilder.toString());
                }
            }
            break;
        }
    }
    
    public void registerGCM() {
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        GCM_REGISTRATION_ID = GCMRegistrar.getRegistrationId(this);
        if (TextUtils.isEmpty(GCM_REGISTRATION_ID)) {
            GCMRegistrar.register(this, GCM_SENDER_KEY);
        }
        Log.e(getClass().getCanonicalName(), "regid : "+GCM_REGISTRATION_ID);
    }
}
