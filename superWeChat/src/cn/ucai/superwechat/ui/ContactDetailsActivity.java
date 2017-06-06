package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.easeui.domain.User;
import cn.ucai.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.data.Result;
import cn.ucai.superwechat.data.net.OnCompleteListener;
import cn.ucai.superwechat.data.net.UserModel;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * Created by Administrator on 2017/5/25 0025.
 */
public class ContactDetailsActivity extends BaseActivity {
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.tv_userinfo_nick)
    TextView tvUserinfoNick;
    @BindView(R.id.tv_userinfo_name)
    TextView tvUserinfoName;
    @BindView(R.id.btn_add_contact)
    Button btnAddContact;
    @BindView(R.id.btn_send_msg)
    Button btnSendMsg;
    @BindView(R.id.btn_send_video)
    Button btnSendVideo;
    User user;
    UserModel model;

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.activity_contact_details);
        ButterKnife.bind(this);
        super.onCreate(arg0);
        model=new UserModel();
        initData();
        showTitleBarBack();
    }

    String userName;
    private void initData() {
        userName = getIntent().getStringExtra(I.User.USER_NAME);
        Log.i("main","ContactDetailsActivity.initData.userName="+userName);
        if (userName != null) {
            user = SuperWeChatHelper.getInstance().getAppContactList().get(userName);
        }
        if(userName != null&&!userName.equals(EMClient.getInstance().getCurrentUser())){
            syncUserInfo();
        }

        if (user == null) {
            user = (User) getIntent().getSerializableExtra(I.User.TABLE_NAME);
        }
        if(user==null&&userName.equals(EMClient.getInstance().getCurrentUser())){
            user=SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentAppUserInfo();
            Log.i("main","ContactDetailsActivity.initData.user="+user);
        }
        if (user != null) {
            showView();

        } else if(userName==null){
            finish();
        }
    }

    private void showView() {
        tvUserinfoName.setText(user.getMUserName());
        EaseUserUtils.setAppUserNick(user, tvUserinfoNick);
        EaseUserUtils.setAppUserAvatar(ContactDetailsActivity.this, user, profileImage);
        showButton(SuperWeChatHelper.getInstance().getAppContactList().containsKey(user.getMUserName()));
    }

    private void showButton(boolean isContact) {
        if(!user.getMUserName().equals(EMClient.getInstance().getCurrentUser())){
            btnAddContact.setVisibility(isContact ? View.GONE : View.VISIBLE);
            btnSendMsg.setVisibility(isContact ? View.VISIBLE : View.GONE);
            btnSendVideo.setVisibility(isContact ? View.VISIBLE : View.GONE);
        }
    }

    @OnClick(R.id.btn_add_contact)
    public void onViewClicked() {
        MFGT.gotoProfiles(ContactDetailsActivity.this, user.getMUserName());
    }
    public void syncUserInfo(){
        model.loadUserInfo(ContactDetailsActivity.this, userName,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("main","ContactDetailsActivity." +
                                "syncUserInfo.onSuccess:"+s);
                        boolean isSuccess=false;
                        if (s!=null){
                            Result<User> result = ResultUtils.getResultFromJson(s, User.class);
                            Log.i("main","ContactDetailsActivity." +
                                    "syncUserInfo.result.isRetMsg:"+result.isRetMsg());
                            if(result!=null&&result.isRetMsg()){
                                user=result.getRetData();
                                isSuccess=true;
                            }
                        }
                        if(!isSuccess){
                            showUser();
                        }else{
                            showView();
                            saveUser2db();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        showUser();
                    }
                });
    }

    private void saveUser2db() {

        UserDao userDao=new UserDao(ContactDetailsActivity.this);
        Map<String, User> appContactList = userDao.getAppContactList();
        if(appContactList.containsKey(user.getMUserName())){
            userDao.saveAppContact(user);
            SuperWeChatHelper.getInstance().getAppContactList().put(user.getMUserName(),user);
        }
    }

    private void showUser() {
        tvUserinfoName.setText(userName);
        EaseUserUtils.setAppUserNick(userName, tvUserinfoNick);
        EaseUserUtils.setAppUserAvatar(ContactDetailsActivity.this, userName, profileImage);
    }

    @OnClick({R.id.btn_send_msg, R.id.btn_send_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send_msg:
                this.finish();
                MFGT.gotoChat(ContactDetailsActivity.this,user.getMUserName());
                break;
            case R.id.btn_send_video:
                MFGT.gotoVideo(ContactDetailsActivity.this,user.getMUserName());
                break;
        }
    }
}
