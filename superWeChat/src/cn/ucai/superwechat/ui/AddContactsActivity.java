package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.easeui.widget.EaseAlertDialog;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.SuperWeChatHelper;

/**
 * Created by Administrator on 2017/5/31 0031.
 */
public class AddContactsActivity extends BaseActivity {
    @BindView(R.id.etMsg)
    EditText etMsg;
    String userName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);
        super.onCreate(arg0);
        userName = getIntent().getStringExtra(I.User.USER_NAME);
        showTitleBarBack();
        initView();
    }

    private void initView() {
        etMsg.setText("我是"+ SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentAppUserInfo().getMUserNick());

    }

    @OnClick(R.id.btn_add_contact)
    public void onViewClicked() {
        if (EMClient.getInstance().getCurrentUser().equals(userName)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (SuperWeChatHelper.getInstance().getContactList().containsKey(userName)) {
            //let the user know the contact already in your contact list

            /*if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(userName)) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }*/
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String trim = etMsg.getText().toString().trim();
                    EMClient.getInstance().contactManager().addContact(userName, trim);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
}
