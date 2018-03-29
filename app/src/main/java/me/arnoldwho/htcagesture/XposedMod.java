package me.arnoldwho.htcagesture;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.nfc.Tag;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import java.lang.Object;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XC_MethodReplacement;
import android.media.AudioManager;

import static android.content.ContentValues.TAG;


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
                XposedBridge.log("BEFORE EXECUTE!");
                temp = param;
                am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                int gesture = (Integer) param.args[0];
                XposedBridge.log(String.format("%1$d",gesture));
                if (am.isMusicActive()){
                    performAction(gesture, context);
                    XposedBridge.log("Music is playing");
                }
                XposedBridge.log("Music is not playing");
                param.setResult(null);
                return null;
            }
        });
    }

    private void performAction(int action, Context context) {
        switch(action){
            case GestureType.SWIPE_LEFT:
                sendMediaButton(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                XposedHelpers.callMethod(temp.thisObject, "doOnResetAction");
                XposedBridge.log("LEFT");
                break;
            case GestureType.SWIPE_RIGHT:
                sendMediaButton(context, KeyEvent.KEYCODE_MEDIA_NEXT);
                XposedHelpers.callMethod(temp.thisObject, "doOnResetAction");
                XposedBridge.log("RIGHT");
                break;
            case GestureType.CAMERA_ACTION:
                sendMediaButton(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                XposedHelpers.callMethod(temp.thisObject, "doOnResetAction");
                break;
            case GestureType.SWIPE_UP:
                //Context ctx=context;
                try {
                    Intent i = context.getPackageManager().getLaunchIntentForPackage("com.spotify.music");
                    context.startActivity(i);
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
                wakeUpDevice();
                break;
            case GestureType.DOUBLE_TAP_ACTION:
                wakeUpDevice();
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
        XposedBridge.log("WTFISGOINGON");
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

    public class GestureType {
        public static final int SWIPE_LEFT = 3;
        public static final int SWIPE_RIGHT = 2;
        public static final int SWIPE_UP = 1;
        public static final int CAMERA_ACTION = 6;
        public static final int DOUBLE_TAP_ACTION = 5;
    }
}