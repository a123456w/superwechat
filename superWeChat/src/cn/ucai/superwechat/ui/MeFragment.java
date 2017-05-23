package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.ucai.easeui.domain.User;
import cn.ucai.easeui.ui.EaseBaseFragment;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;

/**
 * Created by Administrator on 2017/5/23 0023.
 */

public class MeFragment extends EaseBaseFragment {


    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.tvNick)
    TextView tvNick;
    Unbinder unbinder;
    @BindView(R.id.tvName)
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
        User user = SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentAppUserInfo();
        if (user != null) {
            tvName.setText("微信号 :" + user.getMUserName());
            tvNick.setText(user.getMUserNick());
            if (!TextUtils.isEmpty(user.getAvatar())) {
                Glide.with(getContext()).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(ivAvatar);
            } else {
                Glide.with(getContext()).load(R.drawable.em_default_avatar).into(ivAvatar);
            }
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

    @OnClick({R.id.rlPhoto, R.id.rlMoney, R.id.rlSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlPhoto:
                break;
            case R.id.rlMoney:
                break;
            case R.id.rlSetting:
                break;
        }
    }
}
