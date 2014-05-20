package somitsolutions.android.audio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MessageFragment extends Fragment {

	private MainActivity mActivity;
	private WebView mWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_message, container, false);
		mActivity = (MainActivity) getActivity();

		initUI(v);

		return v;
	}

	private void initUI(View v) {
		mWebView = (WebView) v.findViewById(R.id.show_message);

		WebSettings websettings = mWebView.getSettings();
		websettings.setSupportZoom(true);
		websettings.setBuiltInZoomControls(true);
		websettings.setJavaScriptEnabled(true);

		mWebView.setWebViewClient(new WebViewClient());
	}

	public void reloadUrl() {

		String myURL = mActivity.getMessage();

		mWebView.loadUrl(myURL);
	}
}
