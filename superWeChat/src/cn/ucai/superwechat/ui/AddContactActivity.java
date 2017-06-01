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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import cn.ucai.easeui.domain.User;

import cn.ucai.superwechat.R;

import cn.ucai.easeui.widget.EaseAlertDialog;
import cn.ucai.superwechat.data.Result;
import cn.ucai.superwechat.data.net.IUserModel;
import cn.ucai.superwechat.data.net.OnCompleteListener;
import cn.ucai.superwechat.data.net.UserModel;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class AddContactActivity extends BaseActivity {
    private EditText editText;
    private RelativeLayout searchedUserLayout;
    private String toAddUsername;
    private ProgressDialog progressDialog;
    IUserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.em_activity_add_contact);
        super.onCreate(savedInstanceState);
        titleBar.setRightText(getString(R.string.search));
        editText = (EditText) findViewById(R.id.edit_note);
        searchedUserLayout = (RelativeLayout) findViewById(R.id.ll_user);
        initView();
        showTitleBarBack();
    }

    private void initView() {
        model=new UserModel();
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchContact();
            }
        });
    }

    public void searchContact() {
        final String name = editText.getText().toString();
            toAddUsername = name;
            if (TextUtils.isEmpty(name)) {
                new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
                return;
            }

            // TODO you can search the user from your app server here.
            searchContactFromAppService();
            //show the userame and add button if user exist
        }
    private void initDialog(){
        progressDialog=new ProgressDialog(AddContactActivity.this);
        progressDialog.setMessage(getString(R.string.searching));
        progressDialog.show();
    }
    private void dismissDialog(){
        if (progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    private void searchContactFromAppService() {
        initDialog();
        model.loadUserInfo(AddContactActivity.this, toAddUsername,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        boolean isSuccess=false;
                        User user=null;
                        if(s!=null){
                            Result<User> result = ResultUtils.getResultFromJson(s, User.class);
                            if(result!=null){
                                user = result.getRetData();
                                if(user!=null){
                                    isSuccess=true;
                                }
                            }
                        }
                        showSearchResult(isSuccess,user);
                    }

                    @Override
                    public void onError(String error) {
                        showSearchResult(false,null);
                    }
                });
    }

    private void showSearchResult(boolean isSuccess,User user ) {
        dismissDialog();
        searchedUserLayout.setVisibility(isSuccess?View.GONE:View.VISIBLE);
        if(isSuccess){
            MFGT.gotoProfile(AddContactActivity.this,user);
        }
    }

    /**
     *  add contact
     * @param view
     */
   /* public void addContact(View view) {
        if (EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (SuperWeChatHelper.getInstance().getContactList().containsKey(nameText.getText().toString())) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
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
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(toAddUsername, s);
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
    }*/

}
