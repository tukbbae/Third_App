package android.ktpns.hansol05;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.ktpns.lib.database.DbMsgLog;
import com.ktpns.lib.util.Constant;
import com.ktpns.lib.util.Logger;

public class PushReceiver extends BroadcastReceiver {
    private static Handler mHandler;
    private static int messageId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("com.tta.push.intent.receive.REGISTRATION".equals(action)) {
            byte[] token = intent.getByteArrayExtra("token");
            String error = intent.getStringExtra("error");

            if (token == null) {
                sendMessage("REGISTRATION error=" + error);
            } else {
                try {
                    String tokenStr = new String(token, "UTF-8");
                    sendMessage("REGISTRATION (tokenStr=)" + tokenStr + ", error=" + error);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        } else if ("com.tta.push.intent.receive.MESSAGE".equals(action)) {

            byte[] message = intent.getByteArrayExtra("message");
            String transactionId = intent.getStringExtra("transactionId");
            boolean needAck = intent.getBooleanExtra("needAck", false);

            String strMsg = "";
            try {
                strMsg = new String(message, "UTF-8");

                if (needAck) {
                    intent = new Intent("com.tta.push.intent.send.ACK");
                    intent.putExtra("appId", MainActivity.PUSH_APP_ID);
                    intent.putExtra("transactionId", transactionId);
                    context.sendBroadcast(intent);

                    sendMessage("send ACK with appId=" + MainActivity.PUSH_APP_ID + ", transId=" + transactionId);
                }

                strMsg = new String(message, "UTF-8");

                setPushNotification(context, messageId++, strMsg);
                sendMessage("receive.MESSAGE type=일반메시지, transactionId=" + transactionId + ", message=" + strMsg);
            } catch (Exception e) {
                sendMessage("Exception Occurs");
                e.printStackTrace();
            }

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNet = cm.getActiveNetworkInfo();
                Logger.d(getClass(), "CONNECTIVITY_CHANGE_FOR_START_SERVICE activeNet=" + activeNet + ", isConnected="
                        + (activeNet == null ? null : activeNet.isConnected()));

                if (activeNet != null && activeNet.isConnected()) {
                    DbMsgLog.getInstance().putMsgLogForUser(context, "네트워크가 연결되어 있습니다.");
                } else {
                    DbMsgLog.getInstance().putMsgLogForUser(context, "네트워크가 되지 않았습니다.");
                }
            }

        } else if ("com.tta.push.intent.receive.SERVICE_AVAILABLE".equals(action)) {

            String reason = intent.getStringExtra("reason");
            sendMessage("SERVICE_AVAILABLE reason=" + reason);

        } else if ("com.tta.push.intent.receive.SERVICE_UNAVAILABLE".equals(action)) {

            String reason = intent.getStringExtra("reason");
            sendMessage("SERVICE_UNAVAILABLE reason=" + reason);

        } else if ("com.tta.push.intent.receive.STATUS_OF_SERVICE".equals(action)) {

            int status = intent.getIntExtra("status", -2);
            switch (status) {
            case 0:
                sendMessage("STATUS_OF_SERVICE status=미연결 상태");
                break;
            case 1:
                sendMessage("STATUS_OF_SERVICE status=(연결 상태)");
                break;
            default:
                sendMessage("STATUS_OF_SERVICE status=(알수없음)");
                break;
            }

        } else if ("com.tta.push.intent.receive.STATUS_OF_MY_PUSH".equals(action)) {

            int status = intent.getIntExtra("status", -2);
            switch (status) {
            case 0:
                sendMessage("STATUS_OF_MY_PUSH status=미설정");
                break;
            case 1:
                sendMessage("STATUS_OF_MY_PUSH status=설정");
                break;
            case -1:
                sendMessage("STATUS_OF_MY_PUSH status=미등록 package");
                break;
            default:
                sendMessage("STATUS_OF_MY_PUSH status=알수없음");
                break;
            }

        } else if ("com.tta.push.intent.receive.RE_REGISTER".equals(action)) {
            intent = new Intent("com.tta.push.intent.send.REGISTER");
            intent.putExtra("appId", MainActivity.PUSH_APP_ID);
            intent.putExtra("package", context.getPackageName());
            intent.putExtra("operator", 2);
            context.sendBroadcast(intent);

            Log.e("", "ACTION=" + intent.getAction());

            sendMessage("receive RE_REGISTER, and send REGISTER");

        } else if ("com.ktpns.pa.receive.REGISTRATION".equals(action)) {
            Log.e("", "REGISTRATION RESULT = " + intent.getIntExtra("result", -1));
            Log.e("", "REGISTRATION regId = " + intent.getStringExtra("regId"));
            Log.e("", "REGISTRATION detail = " + intent.getStringExtra("detail"));

            sendMessage("REGISTER :: 결과 = " + (intent.getIntExtra("result", -1) == 1 ? "성공" : "실패"));
            sendMessage("REGISTER :: Register ID = " + intent.getStringExtra("regId"));
            sendMessage("REGISTER :: detail = " + intent.getStringExtra("detail"));
            sendMessage("============================");

        } else if ("com.ktpns.pa.receive.MESSAGE".equals(action)) {

            String msgId = intent.getStringExtra("id");
            String msg1 = intent.getStringExtra("noti_type");
            String msg2 = intent.getStringExtra("noti_action");
            String msg3 = intent.getStringExtra("noti_content");

            Log.e("", "MESSAGE ID = " + msgId);
            Log.e("", "MESSAGE alert = " + msg1);
            Log.e("", "MESSAGE url = " + msg2);
            Log.e("", "MESSAGE msg = " + msg3);

            sendMessage("PUSH MESSAGE :: 수신\n============================");
            sendMessage("PUSH MESSAGE :: ID = " + msgId);
            sendMessage("PUSH MESSAGE :: alert = " + msg1);
            sendMessage("PUSH MESSAGE :: url = " + msg2);
            sendMessage("PUSH MESSAGE :: msg = " + msg3);
            sendMessage("============================");

            dumpIntent(intent);
            setPushNotification(context, messageId++, msg1 + msg2 + msg3);

        } else if ("com.ktpns.pa.receive.SERVICE_STATUS".equals(action)) {
            Log.e("", "SERVICE_STATUS status = " + intent.getStringExtra("status"));
            Log.e("", "SERVICE_STATUS detail = " + intent.getStringExtra("detail"));

            sendMessage("SERVICE_STATUS :: 수신\n============================");
            sendMessage("SERVICE_STATUS :: status = " + (intent.getIntExtra("status", 0) == 1 ? "가능" : "불가능"));
            sendMessage("SERVICE_STATUS :: detail = " + intent.getStringExtra("detail"));
            sendMessage("============================");
        } else if ("com.ktpns.pa.receive.UNREGISTERED".equals(action)) {
            Log.e("", "com.ktpns.pa.receive.UNREGISTERED");

            intent = new Intent("com.ktpns.pa.send.REGISTER");
            intent.putExtra(Constant.EXTRA_CP_ID, MainActivity.PUSH_CP_ID);
            intent.putExtra(Constant.EXTRA_PACKAGE, context.getPackageName());
            context.startService(intent);
        }
    }

    public void dumpIntent(Intent i) {

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            sendMessage("Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                sendMessage("[" + key + "=" + bundle.get(key) + "]");
            }
            sendMessage("Dumping Intent end");
        }
    }

    private void sendMessage(String str) {
        Log.e("", str);
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(1, str);
            mHandler.sendMessage(msg);
        }
    }

    public static void setHandler(Handler sb) {
        mHandler = sb;
    }

    static class WakeUpScreen {

        private static PowerManager.WakeLock wakeLock;

        /**
         * timeout을 설정하면, 자동으로 릴리즈됨
         * 
         * @param context
         * @param timeout
         */
        public static void acquire(Context context, long timeout) {

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                    context.getClass().getName());

            if (timeout > 0)
                wakeLock.acquire(timeout);
            else
                wakeLock.acquire();

        }

        public static void release() {
            if (wakeLock.isHeld())
                wakeLock.release();
        }
    }

    private void setPushNotification(Context context, int id, String msg) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getPackageName()).setContentText(msg).setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
        // WakeUpScreen.acquire(context, 1000);
    }
}
