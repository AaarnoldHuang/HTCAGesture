package me.arnoldwho.htcagesture;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.PowerManager;
import android.view.KeyEvent;
import java.lang.Object;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XC_MethodReplacement;


public class XposedMod implements IXposedHookLoadPackage {

    private Context context;
    private XC_MethodHook.MethodHookParam temp;
    private AudioManager am;


    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.htc.sense.easyaccessservice"))
            return;
        Class<?> EasyAction =
                XposedHelpers.findClass("com.htc.sense.easyaccessservice.easy.sense81.EasySensorAction81", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(EasyAction, "triggerMotionAction", int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                temp = param;
                context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                int gesture = (Integer) param.args[0];
                if (isMusicPlaying()) {
                    playingPerformAction(gesture, context);
                }
                else {
                    notPlayingPerformAction(gesture, context);
                }
                param.setResult(null);
                return null;
            }
        });
    }

    public class GestureType {
        public static final int SWIPE_LEFT = 3;
        public static final int SWIPE_RIGHT = 2;
        public static final int SWIPE_UP = 1;
        public static final int CAMERA_ACTION = 6;
        public static final int DOUBLE_TAP_ACTION = 5;
    }

    private boolean isMusicPlaying(){
        am =  (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.isMusicActive();
    }


    private  void playingPerformAction(int action, Context context) {
        switch(action){
            case GestureType.SWIPE_LEFT:           //last song
                    sendMediaButton(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                    XposedHelpers.callMethod(temp.thisObject, "doOnResetAction");
                break;
            case GestureType.SWIPE_RIGHT:         //next song
                    sendMediaButton(context, KeyEvent.KEYCODE_MEDIA_NEXT);
                    XposedHelpers.callMethod(temp.thisObject, "doOnResetAction");
                break;
            case GestureType.CAMERA_ACTION:       //pause
                    sendMediaButton(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                    XposedHelpers.callMethod(temp.thisObject, "doOnResetAction");
                break;
            case GestureType.SWIPE_UP:          //open apps
                try {

                    Intent i = context.getPackageManager().getLaunchIntentForPackage("com.spotify.music");
                    context.startActivity(i);
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
                wakeUpDevice();
                break;
            case GestureType.DOUBLE_TAP_ACTION:       //wake up
                wakeUpDevice();
                XposedBridge.log("wake up");
                break;
            default: break;
        }
    }

    private void notPlayingPerformAction(int action, Context context) {
        switch(action){
            case GestureType.DOUBLE_TAP_ACTION:       //wake up
                wakeUpDevice();
                XposedBridge.log("wake up");
                break;
            default: break;
        }
    }

    private void wakeUpDevice() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
        wakeLock.acquire();
        wakeLock.release();
    }

    private void sendMediaButton(Context context, int keyCode) {
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);
        keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);
    }


}