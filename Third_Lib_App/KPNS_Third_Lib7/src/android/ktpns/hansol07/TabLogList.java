package android.ktpns.hansol07;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.ktpns.lib.database.DbMsgLog;
import com.ktpns.lib.database.data.DataMsgLog;
import com.ktpns.lib.service.PushClientService;
import com.ktpns.lib.service.ServiceGateway;
import com.ktpns.lib.util.Constant;
import com.ktpns.lib.util.Prefs;

public class TabLogList extends Activity {

    private CheckBox checkUser;
    private CheckBox checkTester;
    private CheckBox checkDeveloper;
    private CheckBox checkNetwork;
    private CheckBox checkUtil;

    private ListView lvLog;
    
    private LogCursorAdapter adapter;

    private Handler displayHandler = new WeakHandler(this);
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_log_list);

        findViews();
        
        setViews();
        Log.e(getClass().getName(), "IP "+Constant.KPNS_SERVER_IP);
        adapter = new LogCursorAdapter(this, queryLog());
        lvLog.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayHandler.sendEmptyMessage(Constant.MSG_NEW_PUSH_INOUT);
        DbMsgLog.getInstance().setDisplayHandler(displayHandler);
    }
    
    public void onClick(View v) {
        
        switch (v.getId()) {
        case R.id.btnPush:
//            short transId = RequestData.getNextTransactionID();
//            ServiceGateway.sendTestPushMessage(this, "com.example.thirdparty01", Utils.getBytes("hello kpns id=" + transId), transId);
            break;
        
        case R.id.btnStartLib:
//            if (!Utils.isServiceRunning(this)) {
                DbMsgLog.getInstance().putMsgLogForUser(this, "몇 초 후에 서비스가 시작됩니다.");
//                startService(new Intent(this, PushClientService.class).setAction(Constant.ACTION_FINISH_SERVICE));
                sendBroadcast(new Intent(Constant.ACTION_CHECK_SERVICE_PRIORITY).putExtra(Constant.EXTRA_PRIORITY, Constant.PRIORITY_TYPE_START_CLIENT));
//            } else {
//                DbMsgLog.putMsgLogForUser(this, "이미 서비스가 실행중입니다.");
//            }
            break;
            
        case R.id.btnRegister:
            ServiceGateway.sendReRegister(this);
            break;
            
        case R.id.btnForceClose:
            forceClose();
            break;
            
        case R.id.checkUser:
        case R.id.checkTester:
        case R.id.checkDeveloper:
        case R.id.checkNetwork:
            if (adapter != null) {
                adapter.setList(queryLog());
                adapter.notifyDataSetChanged();
            }
            break;
            
        case R.id.checkUtil:
            if (checkUtil.isChecked()) {
                findViewById(R.id.layoutUtil).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.layoutUtil).setVisibility(View.GONE);
            }
            break;
            
        case R.id.btnClearLog:
            DbMsgLog.getInstance().deleteAll(this);
            if (adapter != null) {
                adapter.setList(queryLog());
                adapter.notifyDataSetChanged();
            }
            break;
            
        case R.id.btnClear:
            try {
                Intent clear = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + getPackageName()));
                clear.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(clear);
            } catch (Exception e) {
            }
            break;
            
        case R.id.btnFinish:
//            if (Utils.isServiceRunning(this)) {
                startService(new Intent(this, PushClientService.class).setAction(Constant.ACTION_FINISH_SERVICE));
//            } else {
//                DbMsgLog.putMsgLogForUser(this, "서비스가 실행되고 있지 않습니다.");
//            }
            break;
            
        case R.id.btnDbToSdcard:
            //Toast.makeText(this, copyDbToSdcard(), Toast.LENGTH_LONG).show();
            break;
        }
    }

    private String copyDbToSdcard() {

        String fromFolder = getFilesDir().getParentFile().getPath();
        String toParent = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + getPackageName();

        File source = new File(fromFolder);
        File dest = new File(toParent,
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date(System
                        .currentTimeMillis())));
        // File destParent = new File(toFolderParent);

        Log.d(getClass().getSimpleName(), "copyDbToSdcard source=" + source);
        Log.d(getClass().getSimpleName(), "copyDbToSdcard dest=" + dest);

        // removeDir(destParent);

        if (dest.mkdirs()) {
            copyFile(source, dest);
        }

        String result =  "Every data file is in folder \"" + dest.getAbsolutePath() + "\"";
        return result;
    }

    private void copyFile(File sourceF, File targetF) {

//        File[] ff = sourceF.listFiles();
//        for (File file : ff) {
//            File temp = new File(targetF.getAbsolutePath() + File.separator + file.getName());
//            if (file.isDirectory()) {
//                temp.mkdir();
//                copyFile(file, temp);
//            } else {
//                FileInputStream fis = null;
//                FileOutputStream fos = null;
//                try {
//                    fis = new FileInputStream(file);
//                    fos = new FileOutputStream(temp);
//                    byte[] b = new byte[4096];
//                    int cnt = 0;
//                    while ((cnt = fis.read(b)) != -1) {
//                        fos.write(b, 0, cnt);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        fis.close();
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }
    }

    @SuppressWarnings("null")
    private void forceClose() {
        Intent i = null;
        i.setAction("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        DbMsgLog.getInstance().setDisplayHandler(null);
        
        Prefs.setLogDisplay(this, checkUser.isChecked(), checkTester.isChecked(), checkDeveloper.isChecked(), checkNetwork.isChecked(), checkUtil.isChecked());
    }

    protected void findViews() {
        lvLog = (ListView) findViewById(R.id.lv_log);
        
        checkUser = (CheckBox) findViewById(R.id.checkUser);
        checkTester = (CheckBox) findViewById(R.id.checkTester);
        checkDeveloper = (CheckBox) findViewById(R.id.checkDeveloper);
        checkNetwork = (CheckBox) findViewById(R.id.checkNetwork);
        checkUtil = (CheckBox) findViewById(R.id.checkUtil);
    }

    protected void setViews() {
        boolean[] b = Prefs.getLogDisplay(this);
        checkUser.setChecked(b[0]);
        checkTester.setChecked(b[1]);
        checkDeveloper.setChecked(b[2]);
        checkNetwork.setChecked(b[3]);
        checkUtil.setChecked(b[4]);
        onClick(checkUtil);
    }
    
    private static class WeakHandler extends Handler {
        private final WeakReference<TabLogList> mSections;

        public WeakHandler(TabLogList section) {
            mSections = new WeakReference<TabLogList>(section);
        }

        @Override
        public void handleMessage(Message msg) {
            TabLogList activity = mSections.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
        case Constant.MSG_NEW_PUSH_INOUT:
            if (adapter != null) {
                adapter.setList(queryLog());
                adapter.notifyDataSetChanged();
            }
            break;
        }
    }

    private ArrayList<DataMsgLog> queryLog() {
        return DbMsgLog.getInstance().query(TabLogList.this, checkUser.isChecked(), checkTester.isChecked(), checkDeveloper.isChecked(), checkNetwork.isChecked());
    }
}