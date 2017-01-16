package com.huahua.weread.mvp.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huahua.weread.R;

/**
 * 不用MVP，只是展示源码地址
 *
 * Created by Administrator on 2016/12/8.
 */

public class AboutFragment extends Fragment {

    TextView mVersionCodeLabel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVersionCodeLabel = (TextView) view.findViewById(R.id.versioncode_label);

        PackageManager pm = getContext().getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            String currentVersionName = pi.versionName;
            mVersionCodeLabel.setText(currentVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
//        String version = pInfo.versionName;

    }
}




















