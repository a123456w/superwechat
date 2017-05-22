package cn.ucai.superwechat.data.net;

import android.content.Context;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.data.OkHttpUtils;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class UserModel implements IUserModel {
    @Override
    public void registers(Context context, String username, String usernick, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.NICK,usernick)
                .addParam(I.User.PASSWORD,password)
                .post()
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void unRegister(Context context, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void loadUserInfo(Context context, String username, OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(listener);
    }
}
