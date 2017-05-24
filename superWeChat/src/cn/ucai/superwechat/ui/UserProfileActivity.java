package cn.ucai.superwechat.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.easeui.domain.EaseUser;
import cn.ucai.easeui.domain.User;
import cn.ucai.easeui.utils.EaseUserUtils;
import cn.ucai.easeui.widget.EaseTitleBar;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.data.Result;
import cn.ucai.superwechat.data.net.IUserModel;
import cn.ucai.superwechat.data.net.OnCompleteListener;
import cn.ucai.superwechat.data.net.UserModel;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class UserProfileActivity extends BaseActivity  {

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    @BindView(R.id.title_bar)
    EaseTitleBar titleBar;
    @BindView(R.id.iv_userinfo_avatar)
    ImageView ivUserinfoAvatar;
    @BindView(R.id.tv_userinfo_nick)
    TextView tvUserinfoNick;
    @BindView(R.id.tv_userinfo_name)
    TextView tvUserinfoName;

    private ProgressDialog dialog;
    User user=null;
    IUserModel model=null;
    String avatarName=null;
    UpdateAvatarBroadcastReceiver mReceiver;



    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        ButterKnife.bind(this);
        initListener();
        user=SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentAppUserInfo();
        model=new UserModel();
    }
    private void initListener() {

        String name=EMClient.getInstance().getCurrentUser();
        if(name!=null){
            tvUserinfoName.setText("微信号: "+EMClient.getInstance().getCurrentUser());
            EaseUserUtils.setAppUserNick(name, tvUserinfoNick);
            EaseUserUtils.setAppUserAvatar(this, name, ivUserinfoAvatar);
            //asyncFetchUserInfo(user.getMUserName());
        }
        titleBar.setLeftLayoutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.finish(UserProfileActivity.this);
            }
        });
        mReceiver=new UpdateAvatarBroadcastReceiver();
        IntentFilter filter = new IntentFilter(I.BROADCAST_UPDATE_AVATAR);
        registerReceiver(mReceiver,filter);
    }
    public void asyncFetchUserInfo(String username) {
        SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser user) {
                if (user != null) {
                    SuperWeChatHelper.getInstance().saveContact(user);
                    if (isFinishing()) {
                        return;
                    }
                    tvUserinfoNick.setText(user.getNick());
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(ivUserinfoAvatar);
                    } else {
                        Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(ivUserinfoAvatar);
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }


    private void uploadHeadPhoto() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        model.upDateUserNick(UserProfileActivity.this, user.getMUserName(), nickName,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(s!=null){
                            Result<User> result = ResultUtils.getResultFromJson(s, User.class);
                            if(result.getRetCode()== I.MSG_USER_SAME_NICK){
                                CommonUtils.showShortToast(getString(R.string.dl_update_no_nick));
                            }else if (result.getRetCode()==I.MSG_USER_UPDATE_NICK_FAIL){
                                CommonUtils.showShortToast(getString(R.string.toast_updatenick_fail));
                            }else if(result.isRetMsg()){
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                                        .show();
                                tvUserinfoNick.setText(nickName);
                                SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentAppUserNickName(nickName);
                            }
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                .show();
                        dialog.dismiss();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
          /*  Drawable drawable = new BitmapDrawable(getResources(), photo);
            ivUserinfoAvatar.setImageDrawable(drawable);
            uploadUserAvatar(Bitmap2Bytes(photo));*/
            SuperWeChatHelper.getInstance().getUserProfileManager()
                    .uploadAppUserAvatar(saveBitmapFile(photo));
        }

    }


    private void uploadUserAvatar(final byte[] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();

        dialog.show();
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    private String getAvatarName() {
        avatarName = user.getMUserName()+ System.currentTimeMillis();
        return avatarName;
    }

    /**
     * 返回头像保存在sd卡的位置:
     * Android/data/cn.ucai.superwechat/files/pictures/user_avatar
     * @param context
     * @param path
     * @return
     */
    public static String getAvatarPath(Context context, String path){
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File folder = new File(dir,path);
        if(!folder.exists()){
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }

    private File saveBitmapFile(Bitmap bitmap) {
        if (bitmap != null) {
            String imagePath = getAvatarPath(UserProfileActivity.this,I.AVATAR_TYPE)+"/"+getAvatarName()+".jpg";
            File file = new File(imagePath);//将要保存图片的路径
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }


    @OnClick({R.id.title_bar, R.id.layout_userinfo_avatar, R.id.layout_userinfo_nick, R.id.layout_userinfo_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_userinfo_avatar:
                uploadHeadPhoto();
                break;
            case R.id.layout_userinfo_nick:
                final EditText editText = new EditText(this);
                editText.setText(user.getMUserNick());
                editText.selectAll();
                new Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString();
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
        }
    }
    class UpdateAvatarBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            uploadAvatar(intent.getBooleanExtra(I.RESULT_UPDATE_AVATAR,false));
        }
    }

    private void uploadAvatar(boolean isSuccess) {
        if(isSuccess){
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success), Toast.LENGTH_SHORT).show();
            EaseUserUtils.setAppUserAvatar(UserProfileActivity.this,user.getMUserName(),ivUserinfoAvatar);
        }else{
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail), Toast.LENGTH_SHORT).show();
        }
        dialogdismiss();
    }

    private void dialogdismiss() {
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
