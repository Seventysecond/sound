package somitsolutions.android.audio;


import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
public class HistoryFragment extends Fragment {
	
	private MainActivity mActivity;
	private ListView listView;
	private ArrayAdapter<String> listAdapter;
    Uri uri_user;
    private Cursor userCursor;
    final String[] usercol = { "name", "message" };
    List<MessageDomain> list = new ArrayList<MessageDomain>();// the list of all data
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        uri_user = Uri.parse("content://" + Constant.AUTHORITY + "/user");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_history, container,
				false);

		mActivity = (MainActivity) getActivity();
		listView = (ListView)v.findViewById(R.id.listView1);
		initUI(v);
        MessageDomain domain = new MessageDomain();
		try {
			list.clear();
            userCursor = getActivity().getContentResolver().query(uri_user,
                    usercol, null, null, null);
            userCursor.moveToFirst();
            for (int i = 0; i < userCursor.getCount(); i++) {
            	Log.v("tony","userCursor.getCount()"+userCursor.getCount());
            	domain = new MessageDomain();
            	domain.setMessage_Name(userCursor.getString(0));
            	domain.setMessage_Message(userCursor.getString(1));
            	list.add(domain);
                userCursor.moveToNext();
            }
        } finally {
            userCursor.close();
        }
		MessageListViewAdapter adapter = new MessageListViewAdapter(mActivity, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
	            // TODO Auto-generated method stub
	            TextView tvName = (TextView) view.findViewById(R.id.Message_Name);
	            TextView tvMessage = (TextView) view.findViewById(R.id.Message_Message);
	            String name = tvName.getText().toString();
	            String message = tvMessage.getText().toString();
	            Toast.makeText(getActivity(), name+message, Toast.LENGTH_SHORT).show();
	        }
	   
	    });
		return v;
	}
	
	private void initUI(View v) {


        
	}
	
}
