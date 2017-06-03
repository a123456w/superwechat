package cn.ucai.superwechat.data.net;

import android.content.Context;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.data.OkHttpUtils;

/**
 * Created by Administrator on 2017/6/2 0002.
 */

public class GroupsModel implements IGroupsModel {
    @Override
    public void createGroup(Context context, String hxId, String name, String des, String owner
            , boolean isPublic, boolean isInviets, File file, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,hxId)
                .addParam(I.Group.NAME,name)
                .addParam(I.Group.DESCRIPTION,des)
                .addParam(I.Group.OWNER,owner)
                .addParam(I.Group.IS_PUBLIC,String.valueOf(isPublic))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(isInviets))
                .addFile2(file)
                .post()
                .targetClass(String.class)
                .execute(listener);
    }
}
