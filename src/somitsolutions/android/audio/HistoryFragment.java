package somitsolutions.android.audio;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_history, container,
				false);

		mActivity = (MainActivity) getActivity();
		listView = (ListView)v.findViewById(R.id.listView1);
		initUI(v);
		final String[] arr = new String[]{
		        "A","B","C","D","E","F","G"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arr);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
	        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
	            // TODO Auto-generated method stub
	            Toast.makeText(getActivity(), arr[position], Toast.LENGTH_SHORT).show();
	        }
	   
	    });
		return v;
	}
	
	private void initUI(View v) {


        
	}
	
}
