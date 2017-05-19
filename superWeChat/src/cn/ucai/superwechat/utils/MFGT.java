package cn.ucai.superwechat.utils;

import android.content.Context;
import android.content.Intent;

import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.SplashActivity;

/**
 * Created by Administrator on 2017/5/19 0019.
 */
public class MFGT {


    public static void gotoLogin(Context context) {
        startActivity(context, LoginActivity.class);
    }

    public static void gotoMain(Context context) {
        context.startActivity(new Intent(context,MainActivity.class));
    }




    private static void startActivity(Context context, Class clazz) {
        context.startActivity(new Intent(context,clazz));
    }
}
