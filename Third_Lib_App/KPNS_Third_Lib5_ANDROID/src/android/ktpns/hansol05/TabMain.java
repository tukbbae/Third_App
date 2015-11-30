package android.ktpns.hansol05;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.ktpns.lib.util.Logger;

public class TabMain extends TabActivity {

    private static String currentTab = "app";
    private static String previousTab = "app";
    private static TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabmain);
        tabHost = getTabHost();

        TabSpec spec1 = tabHost.newTabSpec("app").setIndicator(getTabView("테스트"))
                .setContent(new Intent(this, MainActivity.class));
        TabSpec spec2 = tabHost.newTabSpec("log").setIndicator(getTabView("로그"))
                .setContent(new Intent(this, TabLogList.class));
        TabSpec spec3 = tabHost.newTabSpec("set").setIndicator(getTabView("설정"))
                .setContent(new Intent(this, TabSetting.class));

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        tabHost.setCurrentTab(0);
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Logger.d(getClass(), "onTabChanged tabId=" + tabId);

                previousTab = currentTab;
                currentTab = tabId;
            }
        });

        tabHost.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Logger.d(getClass(), "onLongClick v.getId()=" + v.getId());
                return false;
            }
        });
    }

    private View getTabView(String tabName) {
        TextView tv = new TextView(this);
        tv.setText(tabName);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundResource(R.drawable.tab_bg_selector);
        return tv;
    }
}
