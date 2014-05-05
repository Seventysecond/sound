package somitsolutions.android.audio;

import android.app.Activity;
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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.uol.aig.fftpack.RealDoubleFFT;

public class SoundRecordAndAnalysisActivity extends Activity implements
		OnClickListener {

	int frequency = 44100;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	private RealDoubleFFT transformer;
	int blockSize = 1411;
	int xLength = 1411;
	Button startStopButton;
	boolean started = false;
	TextView freq1, freq2;

	RecordAudio recordTask;
	ImageView imageViewDisplaySectrum;
	MyImageView imageViewScale;
	Bitmap bitmapDisplaySpectrum;

	Canvas canvasDisplaySpectrum;

	Paint paintSpectrumDisplay;
	Paint paintScaleDisplay;
	static SoundRecordAndAnalysisActivity mainActivity;
	LinearLayout main;
	long currentTime = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				canvasDisplaySpectrum.drawLine(x - 1000, downy, x - 1000, upy,
						paintSpectrumDisplay);

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
					freq2.append("一秒更新一次:" + BinaryToAscii(freq2.getText().toString()));
				}
			}

			Log.v("Mylog", "freq:0" + f1 + f2 + f3 + f4 + f5 + f6 + f7);

			imageViewDisplaySectrum.invalidate();
		}

	}

	public void onClick(View v) {

		if (started == true) {
			started = false;
			startStopButton.setText("Start");
			recordTask.cancel(true);
			recordTask = null;
			canvasDisplaySpectrum.drawColor(Color.BLACK);
		} else {
			started = true;
			startStopButton.setText("Stop");
			recordTask = new RecordAudio();
			recordTask.execute();
		}

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
		main = new LinearLayout(this);
		main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		main.setOrientation(LinearLayout.VERTICAL);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		transformer = new RealDoubleFFT(blockSize);

		imageViewDisplaySectrum = new ImageView(this);
		bitmapDisplaySpectrum = Bitmap.createBitmap((int) xLength - 1000,
				(int) 400, Bitmap.Config.ARGB_8888);
		canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
		paintSpectrumDisplay = new Paint();
		paintSpectrumDisplay.setColor(Color.GREEN);
		imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);
		imageViewDisplaySectrum.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		main.addView(imageViewDisplaySectrum);

		imageViewScale = new MyImageView(this);

		imageViewScale.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		main.addView(imageViewScale);

		startStopButton = new Button(this);
		startStopButton.setText("Start");
		startStopButton.setOnClickListener(this);
		startStopButton.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		main.addView(startStopButton);

		freq1 = new TextView(this);
		freq2 = new TextView(this);

		freq1.setText("HI");
		freq1.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		freq2.setText("HI");
		freq2.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		main.addView(freq1);
		main.addView(freq2);

		setContentView(main);
		mainActivity = this;
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
}
