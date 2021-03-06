package somitsolutions.android.audio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class MessageListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<MessageDomain> data;
    Context mContext;
    private Cursor userCursor;
    final String[] usercol = {"url", "time"};
   
    public MessageListViewAdapter(Context context, List<MessageDomain> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.mContext = context;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_message, null);
        }

        final TextView tvUrl = (TextView) convertView.findViewById(R.id.Message_Url);
        final TextView tvMessage = (TextView) convertView.findViewById(R.id.Message_Time);

        tvUrl.setText((String) data.get(position).getMessage_Url());
        tvMessage.setText((String) data.get(position).getMessage_Time());

        return convertView;
    }
}
