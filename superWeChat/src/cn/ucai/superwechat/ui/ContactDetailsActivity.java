package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.easeui.domain.EaseUser;
import cn.ucai.easeui.domain.User;
import cn.ucai.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;

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

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.activity_contact_details);
        ButterKnife.bind(this);
        super.onCreate(arg0);
        initData();
        showTitleBarBack();
    }
    private void initData() {
        user= (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        Log.i("main",user.toString());
        if(user!=null){
            showView();
        }else{
            finish();
        }
    }

    private void showView() {
        tvUserinfoName.setText(user.getMUserName());
        EaseUserUtils.setAppUserNick(user,tvUserinfoNick);
        EaseUserUtils.setAppUserAvatar(ContactDetailsActivity.this,user,profileImage);
        showButton(SuperWeChatHelper.getInstance().getContactList().containsKey(user.getMUserName()));
    }

    private void showButton(boolean isContact) {
        btnAddContact.setVisibility(isContact? View.GONE:View.VISIBLE);
        btnSendMsg.setVisibility(isContact?View.VISIBLE:View.GONE);
        btnSendVideo.setVisibility(isContact?View.VISIBLE:View.GONE);
    }
}
