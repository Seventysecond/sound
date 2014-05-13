package somitsolutions.android.audio;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.uol.aig.fftpack.RealDoubleFFT;

@SuppressLint("ValidFragment")
public class SoundRecordAndAnalysisActivity extends FragmentActivity implements
		ActionBar.TabListener {

	int frequency = 44100;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	private RealDoubleFFT transformer;
	int blockSize = 1411;
	int xLength = 1411;
	ImageButton startStopButton;
	boolean started = false;
	TextView freq1, freq2;

	RecordAudio recordTask;
	/*ImageView imageViewDisplaySectrum;
	MyImageView imageViewScale;
	Bitmap bitmapDisplaySpectrum;

	Canvas canvasDisplaySpectrum;

	Paint paintSpectrumDisplay;
	Paint paintScaleDisplay;*/
	static SoundRecordAndAnalysisActivity mainActivity;
	LinearLayout main;
	long currentTime = 0;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	private class RecordAudio extends AsyncTask<Void, double[], Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Log.v("tony", "doInBackground");
			if (isCancelled()) {
				return null;
			}

			int bufferSize = AudioRecord.getMinBufferSize(44100,
					channelConfiguration, audioEncoding); // /half is 22050Hz
			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.DEFAULT, 44100,
					channelConfiguration, audioEncoding, bufferSize);

			short[] buffer = new short[blockSize];
			double[] toTransform = new double[blockSize];
			try {
				audioRecord.startRecording();
			} catch (IllegalStateException e) {
				Log.e("Recording failed", e.toString());
			}
			while (started) {
				if (isCancelled()) {
					break;
				}
				int bufferReadResult = audioRecord.read(buffer, 0, blockSize);

				for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
					toTransform[i] = (double) buffer[i] / 32768.0; // signed 16
																	// bit
				}

				transformer.ft(toTransform);

				publishProgress(toTransform);
			}
			try {
				audioRecord.stop();
			} catch (IllegalStateException e) {
				Log.e("Stop failed", e.toString());

			}

			return null;

		}

		protected void onProgressUpdate(double[]... toTransform) {
			Log.e("RecordingProgress", "Displaying in progress");
			// double max = 0;
			short f1, f2, f3, f4, f5, f6, f7;
			f1 = f2 = f3 = f4 = f5 = f6 = f7 = 0;
			int x = 0;
			int downy = 0;
			int upy = 0;
			for (int i = 1000; i < toTransform[0].length; i++) {
				x = i;
				downy = (int) (200 - (toTransform[0][i] * 3));
				upy = 200;
				/*canvasDisplaySpectrum.drawLine(x - 1000, downy, x - 1000, upy,
						paintSpectrumDisplay);*/

				if (downy < 50) {
					if (x == 1151 || x == 1152)// 18000
						f1 = 1;
					if (x == 1177 || x == 1178)// 18250
						f2 = 1;
					if (x == 1183 || x == 1184)// 18500
						f3 = 1;
					if (x == 1215 || x == 1216)// 19000
						f4 = 1;
					if (x == 1231 || x == 1232)// 19250
						f5 = 1;
					if (x == 1247 || x == 1248)// 19500
						f6 = 1;
					if (x == 1279 || x == 1280)// 20000
						f7 = 1;
				}
			}
			freq1.setText("0" + f1 + f2 + f3 + f4 + f5 + f6 + f7);
			freq1.append(BinaryToAscii(freq1.getText().toString()));
			if (currentTime == 0) {
				currentTime = System.currentTimeMillis();
			} else {
				long time = System.currentTimeMillis() - currentTime;
				if (time >= 1000) {
					currentTime = System.currentTimeMillis();
					freq2.setText("0" + f1 + f2 + f3 + f4 + f5 + f6 + f7);
					freq2.append("一秒更新一次:"
							+ BinaryToAscii(freq2.getText().toString()));
				}
			}

			Log.v("Mylog", "freq:0" + f1 + f2 + f3 + f4 + f5 + f6 + f7);

			//imageViewDisplaySectrum.invalidate();
		}

	}

	public void setStartStatus(boolean status) {
		started = true;
	}

	public boolean getStartStatus() {
		return started;
	}

	static SoundRecordAndAnalysisActivity getMainActivity() {
		return mainActivity;
	}

	/*
	 * public void onStop(){ super.onStop(); started = false;
	 * startStopButton.setText("Start"); if(recordTask != null){
	 * recordTask.cancel(true); } }
	 */

	public void onStart() {
		super.onStart();
		transformer = new RealDoubleFFT(blockSize);
		/*bitmapDisplaySpectrum = Bitmap.createBitmap((int) xLength - 1000,
				(int) 400, Bitmap.Config.ARGB_8888);
		canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
		paintSpectrumDisplay = new Paint();
		paintSpectrumDisplay.setColor(Color.GREEN);
		imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);
		imageViewDisplaySectrum.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));*/
		
	}

	@Override
	public void onBackPressed() {
		if (recordTask != null) {
			recordTask.cancel(true);
		}
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	// Custom Imageview Class
	public class MyImageView extends ImageView {
		Paint paintScaleDisplay;
		Bitmap bitmapScale;
		Canvas canvasScale;

		public MyImageView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			bitmapScale = Bitmap.createBitmap((int) xLength - 1000, (int) 100,
					Bitmap.Config.ARGB_8888);
			paintScaleDisplay = new Paint();
			paintScaleDisplay.setColor(Color.WHITE);
			paintScaleDisplay.setStyle(Paint.Style.FILL);

			canvasScale = new Canvas(bitmapScale);

			setImageBitmap(bitmapScale);

		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			canvasScale.drawLine(0, 60, xLength - 1000, 60, paintScaleDisplay);
			for (int i = 0, j = 0; i < xLength; i = i + 64, j++) {
				if (i >= 1000) {
					for (int k = i; k < (i + 64); k = k + 8) {
						canvasScale.drawLine(k - 1000, 60, k - 1000, 55,
								paintScaleDisplay);
					}
					canvasScale.drawLine(i - 1000, 70, i - 1000, 55,
							paintScaleDisplay);
					String text = Integer.toString(j) + " KHz";
					canvasScale.drawText(text, i - 1000, 75, paintScaleDisplay);
				}
			}
			canvas.drawBitmap(bitmapScale, 0, 100, paintScaleDisplay);
			invalidate();
		}

	}

	public static int calculate(int sampleRate, short[] audioData) {

		int numSamples = audioData.length;
		int numCrossing = 0;
		for (int p = 0; p < numSamples - 1; p++) {
			if ((audioData[p] > 0 && audioData[p + 1] <= 0)
					|| (audioData[p] < 0 && audioData[p + 1] >= 0)) {
				numCrossing++;
			}
		}

		float numSecondsRecorded = (float) numSamples / (float) sampleRate;
		float numCycles = numCrossing / 2;
		float frequency = numCycles / numSecondsRecorded;

		return (int) frequency;
	}

	public static String AsciiToBinary(String sin) {
		byte[] bytes = sin.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
				if (i == 7)
					binary.append(" ");
			}
			// binary.append(' ');
		}
		return binary.toString();
	}

	public static String BinaryToAscii(String bin) {
		String str = "";
		String s2 = "";
		char[] c = bin.toCharArray();
		int length = bin.length();
		int i = 0;
		while (i < length) {
			if (length - i >= 8) {
				String temp = "";
				temp = String.copyValueOf(c, i, 8);
				Log.v("Mylog", "copied:" + temp);
				int charCode = Integer.parseInt(temp, 2);
				s2 = new Character((char) charCode).toString();
				i = i + 8;
				str += s2; // update str
			} else
				break;
		}
		return str;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				Fragment fragment = new ReceiverFragment();
				return fragment;
			} else {
				Fragment fragment = new SenderFragment();
				return fragment;
			}
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "接收";
			case 1:
				return "傳送";
				// case 2:
				// return getString(R.string.title_section3).toUpperCase(l);
				// case 3:
				// return "my tabneme";
			}
			return null;
		}
	}

	// / Tab 1

	public class ReceiverFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_receiver,
					container, false);
			freq1 = (TextView) rootView.findViewById(R.id.freq_1);
			freq2 = (TextView) rootView.findViewById(R.id.freq_2);
			startStopButton = (ImageButton) rootView
					.findViewById(R.id.receive_btn);
			startStopButton.setOnClickListener(startstop);

			return rootView;
		}

		private OnClickListener startstop = new OnClickListener() {
			public void onClick(View v) {
				if (started == true) {
					started = false;
					Toast.makeText(getActivity(), "stop", Toast.LENGTH_SHORT)
							.show();
					recordTask.cancel(true);
					recordTask = null;
					//canvasDisplaySpectrum.drawColor(Color.BLACK);
				} else {
					started = true;
					recordTask = new RecordAudio();
					recordTask.execute();
					Toast.makeText(getActivity(), "start", Toast.LENGTH_SHORT)
							.show();
				}
			}
		};
	}

	// / Tab 2
	public static class SenderFragment extends Fragment implements
			OnClickListener {
		// public static final String ARG_SECTION_NUMBER = "section_number";

		private EditText input, output;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_sender,
					container, false);
			return rootView;
		}

		public void onClick(View v) {
		}
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		mViewPager.setCurrentItem(tab.getPosition());
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
}
