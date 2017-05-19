package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.ucai.superwechat.ui.GuideActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SplashActivity;

/**
 * Created by Administrator on 2017/5/19 0019.
 */
public class MFGT {


    public static void gotoLogin(Activity activity) {
        startActivity(activity, LoginActivity.class);
    }

    public static void gotoMain(Activity activity) {
        startActivity(activity,MainActivity.class);
    }




    private static void startActivity(Context context, Class clazz) {
        context.startActivity(new Intent(context,clazz));
    }

    public static void gotoGuide(Activity activity) {
       startActivity(activity,GuideActivity.class);
    }
    public static void gotoRegister(Activity activity) {
        startActivity(activity, RegisterActivity.class);
    }

    public static void finish(Activity activity) {
        activity.finish();
    }
}
