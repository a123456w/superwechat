package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.easeui.domain.User;
import cn.ucai.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.MFGT;

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
        String userName = getIntent().getStringExtra(I.User.USER_NAME);
        if (userName != null) {
            user = SuperWeChatHelper.getInstance().getAppContactList().get(userName);
        }
        if (user == null) {
            user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        }
        if (user != null) {
            showView();
        } else {
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
        btnAddContact.setVisibility(isContact ? View.GONE : View.VISIBLE);
        btnSendMsg.setVisibility(isContact ? View.VISIBLE : View.GONE);
        btnSendVideo.setVisibility(isContact ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.btn_add_contact)
    public void onViewClicked() {
        MFGT.gotoProfiles(ContactDetailsActivity.this, user.getMUserName());
    }

    @OnClick({R.id.btn_send_msg, R.id.btn_send_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send_msg:
                break;
            case R.id.btn_send_video:
                break;
        }
    }
}
