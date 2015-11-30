package android.ktpns.hansol07;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ktpns.lib.database.DbAppInfo;
import com.ktpns.lib.database.DbPushMsg;
import com.ktpns.lib.database.data.DataAppInfo;
import com.ktpns.lib.net.data.PushInfoResponse;
import com.ktpns.lib.net.data.ResponseData.ReturnCode;
import com.ktpns.lib.service.PushClientManager;
import com.ktpns.lib.util.Constant;
import com.ktpns.lib.util.Prefs;
import com.ktpns.lib.util.Utils;

public class TabSetting extends Activity {

    private TextView text01;
    private TextView text11;
    private TextView text02;
    private TextView text04;
    private TextView text03;
    private TextView text06;
    private TextView text05;

    private Button btnApply04;
    private Button btnApply03;
    private Button btnApply06;
    private Button btnApply05;
    
    private Button btnError1;
    private Button btnError2;
    private Button btnError3;
    

    private ListView listApp;
    private AppInfoAdapter mAdapter;
    
    private Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_setting);
        
        findViews();

        setViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void findViews() {

        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        text01 = (TextView) findViewById(R.id.text01);
        text11 = (TextView) findViewById(R.id.text11);
        text02 = (TextView) findViewById(R.id.text02);
        text04 = (TextView) findViewById(R.id.text04);
        text03 = (TextView) findViewById(R.id.text03);
        text06 = (TextView) findViewById(R.id.text06);
        text05 = (TextView) findViewById(R.id.text05);
        
        btnApply04 = (Button) findViewById(R.id.btnApply04);
        btnApply03 = (Button) findViewById(R.id.btnApply03);
        btnApply06 = (Button) findViewById(R.id.btnApply06);
        btnApply05 = (Button) findViewById(R.id.btnApply05);
        
        btnError1 = (Button) findViewById(R.id.btnError1);
        btnError2 = (Button) findViewById(R.id.btnError2);
        btnError3 = (Button) findViewById(R.id.btnError3);

        listApp = (ListView) findViewById(R.id.listApp);
        mAdapter = new AppInfoAdapter(new DataAppInfo[0]);
        listApp.setAdapter(mAdapter);
        listApp.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataAppInfo item = mAdapter.getItem(position);
                if (item.pushFlag == DbAppInfo.FLAG_PUSH_ON) {
                    DbAppInfo.update(TabSetting.this, DbAppInfo.FLAG_PUSH_OFF, item.id);
                    mToast.setText("[" + Utils.getAppName(TabSetting.this, item.packageName) + "] 앱 푸시 \"사용하지 않음\" 으로 설정");
                    mToast.show();
                } else {
                    DbAppInfo.update(TabSetting.this, DbAppInfo.FLAG_PUSH_ON, item.id);
                    mToast.setText("[" + Utils.getAppName(TabSetting.this, item.packageName) + "] 앱 푸시 \"사용함\" 으로 설정");
                    mToast.show();
                }
                
                setViews();
            }
        });
    }

    protected void setViews() {
        DataAppInfo[] arrAppInfo = DbAppInfo.queryAppInfos(this, null, null);
        DbPushMsg.setPushCount(this, arrAppInfo);

        text02.setText(printToList(PushClientManager.getInstance(this).getPushInfoResponse()));
        text04.setText(Prefs.isMainLibApp(this) + "");
        
        if (Utils.getForceUserName() == null) {
            btnApply03.setText("변경");
            text03.setText(Utils.getUserName(this));
            text03.setTextColor(Color.BLACK);
        } else {
            btnApply03.setText("원복");
            text03.setText(Utils.getUserName(this));
            text03.setTextColor(Color.RED);
        }

        if (Utils.getForceNetType() == null) {
            btnApply06.setText("변경");
            text06.setText(Utils.getNetType(this));
            text06.setTextColor(Color.BLACK);
        } else {
            btnApply06.setText("원복");
            text06.setText(Utils.getNetType(this));
            text06.setTextColor(Color.RED);
        }

        if (Utils.getForceClientId() == null) {
            btnApply05.setText("변경");
            text05.setText(Prefs.getClientId(this));
            text05.setTextColor(Color.BLACK);
        } else {
            btnApply05.setText("원복");
            text05.setText(Prefs.getClientId(this));
            text05.setTextColor(Color.RED);
        }

        if (Utils.getForceLbIp() == null) {
            btnApply04.setText("변경");
            text01.setText(Utils.getLbIp());
            text01.setTextColor(Color.BLACK);
            text11.setText(Utils.getLbPort() + "");
            text11.setTextColor(Color.BLACK);
        } else {
            btnApply04.setText("원복");
            text01.setText(Utils.getLbIp());
            text01.setTextColor(Color.RED);
            text11.setText(Utils.getLbPort() + "");
            text11.setTextColor(Color.RED);
        }
        
        ReturnCode err = Utils.getForceError();
        if (err == null) {
            btnError1.setSelected(false);
            btnError2.setSelected(false);
            btnError3.setSelected(false);
            btnError1.setTextColor(Color.BLACK);
            btnError2.setTextColor(Color.BLACK);
            btnError3.setTextColor(Color.BLACK);
        } else {
            switch (err) {
            case SESSION_CLOSED:
                btnError1.setSelected(true);
                btnError2.setSelected(false);
                btnError3.setSelected(false);
                btnError1.setTextColor(Color.RED);
                btnError2.setTextColor(Color.BLACK);
                btnError3.setTextColor(Color.BLACK);
                break;
            case PAYLOAD_PARSING_ERROR:
                btnError1.setSelected(false);
                btnError2.setSelected(true);
                btnError3.setSelected(false);
                btnError1.setTextColor(Color.BLACK);
                btnError2.setTextColor(Color.RED);
                btnError3.setTextColor(Color.BLACK);
                break;
            case SERVICE_UNAVAILABLE:
                btnError1.setSelected(false);
                btnError2.setSelected(false);
                btnError3.setSelected(true);
                btnError1.setTextColor(Color.BLACK);
                btnError2.setTextColor(Color.BLACK);
                btnError3.setTextColor(Color.RED);
                break;
            default:
                btnError1.setSelected(false);
                btnError2.setSelected(false);
                btnError3.setSelected(false);
                btnError1.setTextColor(Color.BLACK);
                btnError2.setTextColor(Color.BLACK);
                btnError3.setTextColor(Color.BLACK);
                break;
            }
        }

        if (arrAppInfo == null) {
            arrAppInfo = new DataAppInfo[0];
        }

        int height = (int) getResources().getDimension(R.dimen.app_list_height);;
        mAdapter.setAppInfoArray(arrAppInfo);
        listApp.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, height * mAdapter.getCount()));
    }

    private CharSequence printToList(PushInfoResponse resp) {
        if (resp != null) {
            return printToList(resp.getPushInfoList());
        }
        return null;
    }

    private CharSequence printToList(LinkedBlockingQueue<? extends Object> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                sb.append("\n");
            }

            sb.append(" [");
            sb.append(i);
            sb.append("] ");
            sb.append(list.poll().toString());
        }
        return sb.toString();
    }

    public void onClick(View v) {
        
        Intent intent;
        
        switch (v.getId()) {
        case R.id.btnRefresh:
            setViews();
            break;
            
        case R.id.btnApply03:
            if ("변경".equals(btnApply03.getText())) {
                Utils.setForceUserName(text03.getText().toString());
            } else {
                Utils.removeForceUserName();
            }
            setViews();
            break;
            
        case R.id.btnApply06:
            if ("변경".equals(btnApply06.getText())) {
                Utils.setForceNetType(text06.getText().toString());
            } else {
                Utils.removeForceNetType();
            }
            setViews();
            break;
            
        case R.id.btnApply04:
            if ("변경".equals(btnApply04.getText())) {
                Utils.setForceLbIp(text01.getText().toString());
                try {
                    Utils.setForceLbPort(Integer.parseInt(text11.getText().toString()));
                } catch (Exception e) {
                    Utils.removeForceLbPort();
                }
            } else {
                Utils.removeForceLbIp();
                Utils.removeForceLbPort();
            }
            setViews();
            break;
            
        case R.id.btnApply05:
            if ("변경".equals(btnApply05.getText())) {
                Utils.setForceClientId(text05.getText().toString());
            } else {
                Utils.removeForceClientId();
            }
            setViews();
            break;

        case R.id.btnError1:
            if (btnError1.isSelected()) {
                Utils.setForceError(null);
            } else {
                Utils.setForceError(ReturnCode.SESSION_CLOSED);
            }
            setViews();
            break;

        case R.id.btnError2:
            if (btnError2.isSelected()) {
                Utils.setForceError(null);
            } else {
                Utils.setForceError(ReturnCode.PAYLOAD_PARSING_ERROR);
            }
            setViews();
            break;

        case R.id.btnError3:
            if (btnError3.isSelected()) {
                Utils.setForceError(null);
            } else {
                Utils.setForceError(ReturnCode.SERVICE_UNAVAILABLE);
            }
            setViews();
            break;

        case R.id.btnLogLevel1:
            intent = new Intent(Constant.ACTION_CHECK_SERVICE_PRIORITY)
            .putExtra(Constant.EXTRA_PRIORITY, Constant.PRIORITY_TYPE_CHANGE_LOG_LEVEL)
            .putExtra(Constant.EXTRA_LOG_LEVEL, Log.VERBOSE);
            sendBroadcast(intent, Constant.PERMISSION_PUSH_SEND);
            break;
            
        case R.id.btnLogLevel2:
            intent = new Intent(Constant.ACTION_CHECK_SERVICE_PRIORITY)
            .putExtra(Constant.EXTRA_PRIORITY, Constant.PRIORITY_TYPE_CHANGE_LOG_LEVEL)
            .putExtra(Constant.EXTRA_LOG_LEVEL, Log.DEBUG);
            sendBroadcast(intent, Constant.PERMISSION_PUSH_SEND);
            break;
            
        case R.id.btnLogLevel3:
            intent = new Intent(Constant.ACTION_CHECK_SERVICE_PRIORITY)
            .putExtra(Constant.EXTRA_PRIORITY, Constant.PRIORITY_TYPE_CHANGE_LOG_LEVEL)
            .putExtra(Constant.EXTRA_LOG_LEVEL, Log.INFO);
            sendBroadcast(intent, Constant.PERMISSION_PUSH_SEND);
            break;
            
        case R.id.btnLogLevel4:
            intent = new Intent(Constant.ACTION_CHECK_SERVICE_PRIORITY)
            .putExtra(Constant.EXTRA_PRIORITY, Constant.PRIORITY_TYPE_CHANGE_LOG_LEVEL)
            .putExtra(Constant.EXTRA_LOG_LEVEL, Log.WARN);
            sendBroadcast(intent, Constant.PERMISSION_PUSH_SEND);
            break;
            
        case R.id.btnLogLevel5:
            intent = new Intent(Constant.ACTION_CHECK_SERVICE_PRIORITY)
            .putExtra(Constant.EXTRA_PRIORITY, Constant.PRIORITY_TYPE_CHANGE_LOG_LEVEL)
            .putExtra(Constant.EXTRA_LOG_LEVEL, Log.ERROR);
            sendBroadcast(intent, Constant.PERMISSION_PUSH_SEND);
            break;
            
        }
    }

    class AppInfoAdapter extends BaseAdapter {

        private DataAppInfo[] arrAppInfo;

        public AppInfoAdapter(DataAppInfo[] arr) {
            arrAppInfo = arr;
        }

        public void setAppInfoArray(DataAppInfo[] arr) {
            arrAppInfo = arr;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return arrAppInfo.length;
        }

        @Override
        public DataAppInfo getItem(int position) {
            return arrAppInfo[position];
        }

        @Override
        public long getItemId(int position) {
            return arrAppInfo[position].id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.item_app_info, null);
            }
            
            DataAppInfo item = arrAppInfo[position];
            
            ImageView img = (ImageView) view.findViewById(R.id.img);
            try {
                img.setImageDrawable(getPackageManager().getApplicationIcon(item.packageName));
            } catch (NameNotFoundException e) {
                img.setImageDrawable(null);
                e.printStackTrace();
            }

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText("TITLE : " + Utils.getAppName(TabSetting.this, item.packageName));

            TextView packageName = (TextView) view.findViewById(R.id.packageName);
            packageName.setText("PACKAGE : " + item.packageName);
            
            TextView appId = (TextView) view.findViewById(R.id.appId);
            appId.setText("APP ID : " + item.appId);
            
            TextView clientId = (TextView) view.findViewById(R.id.clientId);
            clientId.setText("CLIENT ID : " + item.clientId);
            
            TextView thirdType = (TextView) view.findViewById(R.id.thirdType);
            thirdType.setText("서드파티타입 : " + item.printThirdType());
            
            TextView pushFlag = (TextView) view.findViewById(R.id.pushFlag);
            pushFlag.setText("푸시 사용 여부 : " + item.printPushFlag());
            
            TextView receiveDate = (TextView) view.findViewById(R.id.receiveDate);
            receiveDate.setText("등록 시간 : " + Utils.printDate(item.receiveDate));
            
            TextView expired = (TextView) view.findViewById(R.id.expired);
            expired.setText("토큰 유효 여부 : " + item.printExpired());
            
            TextView count = (TextView) view.findViewById(R.id.count);
            count.setText("받은 푸시 갯수 : " + item.count);

            return view;
        }
    }
}