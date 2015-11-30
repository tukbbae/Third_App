package android.ktpns.hansol07;

import java.lang.ref.WeakReference;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.ktpns.lib.KPNSApis;
import com.ktpns.lib.OnKPNSInitializeEventListener;
import com.ktpns.lib.service.PushClientService;
import com.ktpns.lib.util.Constant;
import com.ktpns.lib.util.Logger;
import com.ktpns.lib.util.Utils;

public class MainActivity extends Activity {
    public static final String PUSH_APP_ID = "0000000018";
    public static final String PUSH_CP_ID = "hansol";

    private TextView textResult;
    private static StringBuilder messageBuilder;
    private WeakHandler mHandler = new WeakHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = (TextView) findViewById(R.id.textResult);
        
        messageBuilder = new StringBuilder();
        PushReceiver.setHandler(mHandler);
        appendString("SDK 버전:: " + Build.VERSION.RELEASE);
        appendString("SDK 버전:: " + Build.VERSION.SDK);
        appendString("SDK 버전:: " + new Date(Build.TIME));
        appendString("SDK 버전:: " + Build.VERSION.INCREMENTAL);
        appendString("SDK 버전:: " + Build.VERSION.CODENAME);
        
        // startService(new Intent(this, PushClientService.class));
//        sendBroadcast(new Intent(Constant.ACTION_START_PUSH_CLIENT));
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.textResult:
            messageBuilder = new StringBuilder();
            textResult.setText("");
            break;

        case R.id.btnTokenRegister:
            KPNSApis.requestInstance(this, new OnKPNSInitializeEventListener() {

                @Override
                public void onSuccessInitialize(KPNSApis kpnsApis) {
                    kpnsApis.register(PUSH_APP_ID, PUSH_CP_ID);
                }

                @Override
                public void onFailInitialize() {
                    Logger.d(getClass(), "KPNS Initialize is Fail.");
                    if (Utils.isRequestPermission(getApplicationContext())) {
                        startService(new Intent(MainActivity.this, PushClientService.class).setAction(Constant.ACTION_START_SERVICE));
                    }
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
            break;

        case R.id.btnCheckClientRunning:
            boolean isRunning = Utils.isClientRunning();
            appendString("CLIENT 실행 :: " + (isRunning ? "실행중" : "중지"));
            break;

        case R.id.btnGetPushClientVersion:
            int pushClientVer = Utils.getPushClientVer(this);
            appendString("CLIENT 버전 :: "
                    + (pushClientVer < 200 ? "1.x CLIENT 설치" : "2.x CLIENT 설치"));
            break;
        case R.id.btnNsrmSetting:
        	appendString("NSRM 설정 변경 이벤트 발생");
        	break;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PushReceiver.setHandler(null);
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
}
