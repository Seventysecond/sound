package somitsolutions.android.audio;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
public class MessageFragment extends Fragment {
	
	private MainActivity mActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_message, container,
				false);
		//v.setTextColor(Color.BLUE);
		mActivity = (MainActivity) getActivity();

		initUI(v);

		return v;
	}
	
	private void initUI(View v) {
		TextView myTextView = (TextView)v.findViewById(R.id.show_message);
        myTextView.setText(mActivity.getMessage());
        Button History = (Button)v.findViewById(R.id.History);
        History.setOnClickListener(chat);
        
	}
	private OnClickListener chat = new OnClickListener() {
		public void onClick(View v) {
			
			
			
		}
	};
}
