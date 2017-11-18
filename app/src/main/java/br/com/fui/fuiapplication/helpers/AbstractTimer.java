package br.com.fui.fuiapplication.helpers;

import android.app.Activity;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by guilherme on 02/11/17.
 */

public abstract class AbstractTimer {
    private Timer timer;
    private Activity activity;
    protected abstract boolean onVerification();
    protected abstract void doInBackground();
    public AbstractTimer(Activity activity){
        this.activity = activity;
    }
    public void run(){
        timer = new Timer();
        timer.schedule(new ProgrammedTask(), 500);
    }
    class ProgrammedTask extends TimerTask {
        public void run() {
            Log.d("timer", "running timer");
            if(onVerification()){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doInBackground();
                    }
                });

                timer.cancel();
            }else{
                AbstractTimer.this.run();
            }
        }
    }
}
