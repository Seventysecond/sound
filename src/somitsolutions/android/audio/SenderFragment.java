package somitsolutions.android.audio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SenderFragment extends Fragment {
	
	private MainActivity mActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sender, container,
				false);

		mActivity = (MainActivity) getActivity();

		initUI(v);

		return v;
	}
	
	private void initUI(View v) {
		
	}
}
