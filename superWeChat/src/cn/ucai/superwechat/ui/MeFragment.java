package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easemob.redpacketui.utils.RPRedPacketUtil;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.ucai.easeui.domain.User;
import cn.ucai.easeui.ui.EaseBaseFragment;
import cn.ucai.easeui.utils.EaseUserUtils;
import cn.ucai.easeui.widget.EaseTitleBar;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.MFGT;

/**
 * Created by Administrator on 2017/5/23 0023.
 */

public class MeFragment extends EaseBaseFragment {


    @BindView(R.id.iv_profile_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_profile_nickname)
    TextView tvNick;
    Unbinder unbinder;
    @BindView(R.id.tv_profile_username)
    TextView tvName;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setUpView() {
        titleBar.setRightImageResource(R.drawable.em_add);
        titleBar.setTitle(getString(R.string.me));
    }

    private void loadUserInfo() {
        String name=EMClient.getInstance().getCurrentUser();
        if(name!=null){
            tvName.setText("微信号: "+EMClient.getInstance().getCurrentUser());
            EaseUserUtils.setAppUserNick(name, tvNick);
            EaseUserUtils.setAppUserAvatar(getContext(), name, ivAvatar);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

    @OnClick({R.id.layout_profile_view, R.id.tv_profile_money, R.id.tv_profile_settings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_profile_view:
                MFGT.gotoProfile(getActivity());
                /*startActivity(new Intent(getActivity(), UserProfileActivity.class).putExtra("setting", true)
                        .putExtra("username", EMClient.getInstance().getCurrentUser()));*/
                break;
            //red packet code : 进入零钱或红包记录页面
            case R.id.tv_profile_money:
                //支付宝版红包SDK调用如下方法进入红包记录页面
                RPRedPacketUtil.getInstance().startRecordActivity(getActivity());
                break;
            case R.id.tv_profile_settings:
                MFGT.gotoSetting(getActivity());
                break;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }
}
