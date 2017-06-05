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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.exceptions.HyphenateException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.easeui.domain.Group;
import cn.ucai.easeui.widget.EaseAlertDialog;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.SuperWeChatModel;
import cn.ucai.superwechat.data.Result;
import cn.ucai.superwechat.data.net.GroupsModel;
import cn.ucai.superwechat.data.net.OnCompleteListener;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.utils.ResultUtils;

public class NewGroupActivity extends BaseActivity {
    private static final int REQUEST_CODE_GET_AVATAR = 2;
    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_OK = 3;
    @BindView(R.id.ivGroupAvatar)
    ImageView ivGroupAvatar;
    private EditText groupNameEditText;
    private ProgressDialog progressDialog;
    private EditText introductionEditText;
    private CheckBox publibCheckBox;
    private CheckBox memberCheckbox;
    private TextView secondTextView;
    private LinearLayout addGroupAvatar;
    private String avatarName;
    GroupsModel model;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.em_activity_new_group);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        showTitleBarBack();
        model = new GroupsModel();
        addGroupAvatar = (LinearLayout) findViewById(R.id.addGroupAvatar);
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        publibCheckBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        secondTextView = (TextView) findViewById(R.id.second_desc);

        publibCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondTextView.setText(R.string.join_need_owner_approval);
                } else {
                    secondTextView.setText(R.string.Open_group_members_invited);
                }
            }
        });
        setListener();
    }

    private void setListener() {
        addGroupAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAvatar();
            }
        });
        titleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void getAvatar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(NewGroupActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUEST_CODE_GET_AVATAR);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void save() {
        String name = groupNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Group_name_cannot_be_empty).show();
        } else {
            // select from contact list
            startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), REQUEST_CODE_OK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (REQUEST_CODE_PICK == requestCode) {
            setPicToView(data);
        }
        if (requestCode == REQUEST_CODE_GET_AVATAR) {
            if (data != null) {
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_OK == requestCode) {
            if (resultCode != RESULT_OK) {
                return;
            }
            //new group
            newGroup(data);
        }
    }

    private void newGroup(final Intent data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String groupName = groupNameEditText.getText().toString().trim();
                String desc = introductionEditText.getText().toString();
                String[] members = data.getStringArrayExtra("newmembers");
                try {
                    EMGroupOptions option = new EMGroupOptions();
                    option.maxUsers = 200;
                    option.inviteNeedConfirm = true;

                    String reason = NewGroupActivity.this.getString(R.string.invite_join_group);
                    reason = EMClient.getInstance().getCurrentUser() + reason + groupName;

                    if (publibCheckBox.isChecked()) {
                        option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                    EMGroup group = EMClient.getInstance().groupManager().createGroup(groupName
                            , desc, members, reason, option);
                    createGroups(group,members);


                } catch (final HyphenateException e) {
                    createFailes(e);
                }

            }
        }).start();
    }



    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE_PICK);
    }

    private void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            file = saveBitmapFile(photo);
            ivGroupAvatar.setImageBitmap(photo);
        }


    }

    private File saveBitmapFile(Bitmap bitmap) {
        if (bitmap != null) {
            String imagePath = getAvatarPath(NewGroupActivity.this, I.AVATAR_TYPE) + "/" + getAvatarName() + ".jpg";
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

    private String getAvatarName() {
        avatarName = groupNameEditText.getText().toString().trim()+"-"+EMClient.getInstance().getCurrentUser() + System.currentTimeMillis();
        return avatarName;
    }

    public static String getAvatarPath(Context context, String path) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File folder = new File(dir, path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }

    public void createFailes(final HyphenateException e) {
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                if (e != null) {
                    Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } else {
                    CommonUtils.showShortToast(st2);
                }
            }
        });
    }

    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    private void createGroups(final EMGroup groups,final String[] members) {
        if (file != null) {
            model.createGroup(NewGroupActivity.this, groups.getGroupId(), groups.getGroupName()
                    , groups.getDescription(), groups.getOwner(), groups.isPublic(), groups.isAllowInvites()
                    , file, new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.e("main","NewGroupActivity.createGroups.s="+s);
                            boolean isSuccess = false;
                            if (s != null) {
                                Result<Group> result = ResultUtils.getResultFromJson(s, Group.class);
                                if (result != null && result.isRetMsg()) {
                                    isSuccess = true;
                                }
                            }
                            if (!isSuccess) {
                                createFailes(null);
                            }else {
                                if(members!=null && members.length>0){
                                    addGroup(members,groups.getGroupId());
                                }else{
                                    dismissDialog();
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {
                            createFailes(null);
                        }
                    });
        }else{
            model.createGroup(NewGroupActivity.this, groups.getGroupId(), groups.getGroupName()
                    , groups.getDescription(), groups.getOwner(), groups.isPublic(), groups.isAllowInvites()
                    , new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.e("main","NewGroupActivity.createGroups.s="+s);
                            boolean isSuccess = false;
                            if (s != null) {
                                Result<Group> result = ResultUtils.getResultFromJson(s, Group.class);
                                if (result != null && result.isRetMsg()) {
                                    isSuccess = true;
                                }
                            }
                            if (!isSuccess) {
                                createFailes(null);
                            }else {
                                if(members!=null && members.length>0){
                                    addGroup(members,groups.getGroupId());
                                }else{
                                    dismissDialog();
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {
                            createFailes(null);
                        }
                    });
        }
    }
    private void addGroup(String[] members, String groupId) {

        model.addGroupMembers(NewGroupActivity.this, StringByArrays(members), groupId,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        boolean isSuccess = false;
                        if (s != null) {
                            Result<Group> result = ResultUtils.getResultFromJson(s, Group.class);
                            if (result != null && result.isRetMsg()) {
                                isSuccess=true;
                                dismissDialog();
                            }
                        }
                        if (!isSuccess){
                            createFailes(null);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        createFailes(null);
                    }
                });
    }

    private String StringByArrays(String[] members) {
        StringBuilder sb=new StringBuilder();
        for (String member : members) {
            sb.append(member);
            sb.append(",");
        }
        return sb.toString();
    }

    private void setCodeorView(Group group) {
        String avatar = group.getAvatar();

    }
    private void setCurrentAppUserAvatar(String avatar) {
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }


    public void back(View view) {
        finish();
    }
}
