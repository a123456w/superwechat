package cn.ucai.superwechat.data.net;

import android.content.Context;

import java.io.File;

import cn.ucai.superwechat.data.OkHttpUtils;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public interface IUserModel {
    void registers(Context context, String username, String usernick, String password, OnCompleteListener<String> listener);
    void unRegister(Context context, String username, OnCompleteListener<String> listener);
    void loadUserInfo(Context context, String username , OnCompleteListener<String> listener);
    void upDateUserNick(Context context, String username, String usernick, OnCompleteListener<String> listener);
    void updateAvatar(Context context, String name, String avatartype, File file, OnCompleteListener<String> listener);
    void addContact(Context context, String username, String cname, OnCompleteListener<String> listener);

}