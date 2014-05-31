package somitsolutions.android.audio;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ca.uol.aig.fftpack.RealDoubleFFT;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiverFragment extends Fragment {

	private MainActivity mActivity;
	private TextView freq1, freq2;
	private ImageButton startStopButton;
	RecordAudio recordTask;
	int frequency = 44100;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	private RealDoubleFFT transformer;
	int blockSize = 1411;
	boolean started = false;
	long currentTime = 0;

    Uri uri_user;
    private Cursor userCursor;
    final String[] usercol = { "url", "time" };
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        uri_user = Uri.parse("content://" + Constant.AUTHORITY + "/user");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_receiver, container, false);
		mActivity = (MainActivity) getActivity();

		initUI(v);
		return v;
	}

	private void initUI(View v) {
		freq1 = (TextView) v.findViewById(R.id.freq_1);
		freq2 = (TextView) v.findViewById(R.id.freq_2);
		startStopButton = (ImageButton) v.findViewById(R.id.receive_btn);
		startStopButton.setOnClickListener(startstop);
	}

	private OnClickListener startstop = new OnClickListener() {
		public void onClick(View v) {
			if (started == true) {
				started = false;
				Toast.makeText(getActivity(), "stop", Toast.LENGTH_SHORT)
						.show();
				recordTask.cancel(true);
				recordTask = null;
			} else {
				started = true;
				recordTask = new RecordAudio();
				recordTask.execute();
				Toast.makeText(getActivity(), "start", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

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

				if (downy < 50) {
					if (x == 1151 || x == 1152)// 18000
						f1 = 1;
					if (x == 1167 || x == 1168)// 18250
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
			String soundcode = "0" + f1 + f2 + f3 + f4 + f5 + f6 + f7;
			freq1.setText(soundcode);
			freq1.append(BinaryToAscii(freq1.getText().toString()));
			if (currentTime == 0) {
				currentTime = System.currentTimeMillis();
			} else {
				long time = System.currentTimeMillis() - currentTime;
				if (time >= 1000) {
					currentTime = System.currentTimeMillis();
					freq2.setText(soundcode);
					freq2.append("一秒更新一次:"
							+ BinaryToAscii(soundcode));
					if (!soundcode.equals("00000000")) {
                        ContentValues values = new ContentValues();
                        values.put("url", BinaryToAscii(soundcode));
                        String DATE_FORMAT_NOW = "yyyy-MM-dd";
                        Calendar tmpCal = Calendar.getInstance();
                        SimpleDateFormat tmpSDF = new SimpleDateFormat(DATE_FORMAT_NOW);
                        values.put("time", tmpSDF.format(tmpCal.getTime()));
                        getActivity().getContentResolver().insert(uri_user, values);
						mActivity.setCurrentTab(1);
						mActivity.setMessage("http://goo.gl/" + BinaryToAscii(soundcode));
					}
				}
			}

			Log.v("Mylog", "freq:0" + f1 + f2 + f3 + f4 + f5 + f6 + f7);

			// imageViewDisplaySectrum.invalidate();
		}

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

	public void onStart() {
		super.onStart();
		transformer = new RealDoubleFFT(blockSize);
	}

	public void onBackPressed() {
		if (recordTask != null) {
			recordTask.cancel(true);
		}
	}
}
