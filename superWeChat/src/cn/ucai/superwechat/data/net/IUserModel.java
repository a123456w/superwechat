package cn.ucai.superwechat.data.net;

import android.content.Context;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public interface IUserModel {
    void registers(Context context, String username, String usernick, String password, OnCompleteListener<String> listener);
    void unRegister(Context context, String username, OnCompleteListener<String> listener);
    
}