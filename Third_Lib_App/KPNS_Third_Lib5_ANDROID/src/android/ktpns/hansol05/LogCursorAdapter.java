package android.ktpns.hansol05;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ktpns.lib.database.DbMsgLog;
import com.ktpns.lib.database.data.DataMsgLog;

public class LogCursorAdapter extends BaseAdapter {

    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;
    private Context mContext;
    private ArrayList<DataMsgLog> mList;

    public LogCursorAdapter(Context context, ArrayList<DataMsgLog> list) {
        mContext = context;
        mList = list;
    }

    private void setViewHolder(ViewHolder vh, int position) {
        DataMsgLog logData = mList.get(position);
        
        vh.item1.setText(printTime(logData.date));
        vh.item2.setText(printDate(logData.date));
        vh.item3.setText("");
        vh.itemLog.setText(logData.getLog(mContext));
        
        int color;
        
        switch (logData.level) {
        case DbMsgLog.LEVEL_DEVELOPER:
            color = Color.rgb(0x00, 0x00, 0x00);
            break;
        case DbMsgLog.LEVEL_NETWORK:
            color = Color.rgb(0x00, 0x00, 0x00);
            break;
        case DbMsgLog.LEVEL_TESTER:
            color = Color.rgb(0x55, 0x6B, 0x2F);
            break;
        case DbMsgLog.LEVEL_USER:
            color = Color.rgb(0xCD, 0x85, 0x3F);
            break;
        default:
            color = Color.rgb(0xFF, 0x00, 0x00);
            break;
        }
        
        vh.item1.setTextColor(color);
        vh.item2.setTextColor(color);
        vh.item3.setTextColor(color);
        vh.itemLog.setTextColor(color);
    }

    private CharSequence printTime(long millis) {
        if (timeFormat == null) {
            timeFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        }
        return timeFormat.format(new Date(millis));
    }
    
    private CharSequence printDate(long millis) {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }
        return dateFormat.format(new Date(millis));
    }

    public void setList(ArrayList<DataMsgLog> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public DataMsgLog getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        ViewHolder vh = null;

        if (convertView == null) {
            
            view = LayoutInflater.from(mContext).inflate(R.layout.tab_log_list_item, parent, false);
            vh = new ViewHolder();
            vh.item1 = (TextView) view.findViewById(R.id.item1);
            vh.item2 = (TextView) view.findViewById(R.id.item2);
            vh.item3 = (TextView) view.findViewById(R.id.item3);
            vh.itemLog = (TextView) view.findViewById(R.id.itemLog);

            view.setTag(vh);

        } else {
            
            view = convertView;
            vh = (ViewHolder) view.getTag();
            
        }

        setViewHolder(vh, position);

        return view;
    }
    
    private class ViewHolder {
        TextView item1;
        TextView item2;
        TextView item3;
        TextView itemLog;
    }
}
