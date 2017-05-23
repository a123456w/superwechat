/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.data.Result;
import cn.ucai.superwechat.data.net.OnCompleteListener;
import cn.ucai.superwechat.data.net.UserModel;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * register screen
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.tvTitles_back)
    TextView tvTitlesBack;
    @BindView(R.id.username)
    EditText userNameEditText;
    @BindView(R.id.usernick)
    EditText usernick;
    @BindView(R.id.password)
    EditText passwordEditText;
    @BindView(R.id.confirm_password)
    EditText confirmPwdEditText;
    String username;
    String password;
    String nick;
    String confirm_pwd;
    UserModel model;
    Unbinder bind;
    ProgressDialog pd;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_register);
        bind = ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        tvTitlesBack.setVisibility(View.VISIBLE);
        model = new UserModel();
    }

    public void register(View view) {
        username = userNameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        nick = usernick.getText().toString().trim();
        confirm_pwd = confirmPwdEditText.getText().toString().trim();
        if (checkInput()) {
            initDialog();
            model.registers(RegisterActivity.this, username, nick, MD5.getMessageDigest(password),
                    new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            boolean isSuccess=true;
                            if (s != null) {
                                Result results = ResultUtils.getResultFromJson(s,null);
                                if (results != null) {
                                    if (results.getRetCode()==I.MSG_REGISTER_USERNAME_EXISTS){
                                        CommonUtils.showLongToast(R.string.User_already_exists);
                                    }else if(results.getRetCode()==I.MSG_REGISTER_FAIL){
                                        CommonUtils.showLongToast(R.string.Registration_failed);
                                    }else if(results.isRetMsg()){
                                        isSuccess=false;
                                        HXResgister();
                                    }
                                }
                                if(isSuccess){
                                    dismissDialog();
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {
                            CommonUtils.showLongToast(error.toString());
                            dismissDialog();
                        }
                    });
            dismissDialog();
        }
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(nick)) {
            Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return false;
        } else if (!password.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void HXResgister() {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(MD5.getMessageDigest(password))) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        // call method in SDK
                        EMClient.getInstance().createAccount(username, password);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // save current user
                                SuperWeChatHelper.getInstance().setCurrentUserName(username);
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                                if (!RegisterActivity.this.isFinishing())
                                    dismissDialog();
                                MFGT.gotoLogin(RegisterActivity.this);
                                finish();
                            }
                        });
                    } catch (final HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!RegisterActivity.this.isFinishing())
                                    dismissDialog();
                                int errorCode = e.getErrorCode();
                                if (errorCode == EMError.NETWORK_ERROR) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                                }
                                unregister();
                            }
                        });
                    }
                }
            }).start();

        }else{
            unregister();
        }
    }

    private void unRegister() {
        model.unRegister(RegisterActivity.this, username,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if(result!=null){
                            Result results = ResultUtils.getResultFromJson(result, Result.class);
                            if(results!=null&&results.isRetMsg()){
                                L.e(TAG, "取消注册成功");
                                return;
                            }else{
                                unregister();
                            }
                        }else{
                            unregister();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        unregister();
                    }
                });
    }

    private void unregister() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                unRegister();
            }
        });
    }

    private void initDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.Is_the_registered));
        pd.show();
    }
    private void dismissDialog(){
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
    }
    @OnClick({R.id.tvTitles_back, R.id.bt_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvTitles_back:
                MFGT.finish(RegisterActivity.this);
                break;
            case R.id.bt_register:
                register(view);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bind!=null){
            bind.unbind();
        }
    }
}
