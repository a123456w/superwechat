package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import cn.ucai.easeui.domain.User;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.ui.ContactDetailsActivity;
import cn.ucai.superwechat.ui.GuideActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SettingsActivity;
import cn.ucai.superwechat.ui.SplashActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;

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
    private static void startActivity(Context context,Intent intent) {
        context.startActivity(intent);
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

    public static void gotoSetting(Activity activity) {
        startActivity(activity,SettingsActivity.class);
    }

    public static void logout(Activity activity) {
        startActivity(activity,new Intent(activity,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    public static void gotoProfile(Activity activity) {
        startActivity(activity, UserProfileActivity.class);
    }
    public static void gotoProfile(Activity activity,User user) {
        startActivity(activity, new Intent(activity, ContactDetailsActivity.class)
                .putExtra(I.User.USER_NAME,user)
        );
    }
}
