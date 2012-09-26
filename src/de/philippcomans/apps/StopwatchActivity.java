package de.philippcomans.apps;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class StopwatchActivity extends Activity {
    private long mStartTimestamp;
    private Context mContext;
    private boolean mTimerRunning;
    private Handler mHandler = new Handler();

    private static final long REFRESH_RATE = 1000/60; //60 fps

    private static final String LOGTAG = "StopwatchActivity";

    private Runnable startTimer = new Runnable() {
        public static final String LOGTAG = "TimerRunnable";

        public void run() {
            long elapsedTimeMillis = SystemClock.uptimeMillis() - mStartTimestamp;
            updateTimer(elapsedTimeMillis);
            mHandler.postDelayed(this,REFRESH_RATE);
            //Log.d("Still running... " + elapsedTimeMillis, LOGTAG);
        }
    };



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new OnStartButtonClickedListener());
        mContext = getApplicationContext();
        mTimerRunning = false; //TODO get from Bundle

        if (savedInstanceState != null) {
            long startTimeStamp = savedInstanceState.getLong("startTimestamp");
            if (startTimeStamp != 0) {
                mStartTimestamp = startTimeStamp;
                mHandler.postDelayed(startTimer, 0);
                mTimerRunning = true;
                ((Button)findViewById(R.id.start)).setText(R.string.stop_timer);
                Toast.makeText(mContext, "Restored instance state", Toast.LENGTH_LONG).show();
            }
        }

        //TODO get current system time and timezone
        //TODO Check if timezone has changed
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        int repeatCount = event.getRepeatCount();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if ((action == KeyEvent.ACTION_DOWN) && (repeatCount == 0)) {
                Log.v("Up " + event.getRepeatCount(), LOGTAG);
                //TODO Start / Stop timer
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if ((action == KeyEvent.ACTION_DOWN) && (repeatCount == 0)) {
                Log.v("Down " + event.getRepeatCount(), LOGTAG);
                //TODO Take lap time
            }
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.stopwatch_activity, menu);
        View xyz = findViewById(R.id.action_new);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO save time and running state
        if (mTimerRunning) {
            outState.putLong("startTimestamp", mStartTimestamp);
        }

    }

    private class OnStartButtonClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Button thisButton = (Button) view;
            if (mTimerRunning) {
                //Stop the timer
                mTimerRunning = false;

                mHandler.removeCallbacks(startTimer);
                thisButton.setText(R.string.start_timer);


            } else {
                //Start the timer
                mStartTimestamp = SystemClock.uptimeMillis();
                mTimerRunning = true;
                thisButton.setText(R.string.stop_timer);
                mHandler.postDelayed(startTimer, 0);
            }

        }
    }

    private void updateTimer(long elapsedTimeMillis) {
        TextView timeView = (TextView) findViewById(R.id.time);

        String x = String.format("%02d:%03d",
                elapsedTimeMillis / 1000, //Seconds
                elapsedTimeMillis % 1000  //remaining ms
        );

        timeView.setText(x);

    }
}
