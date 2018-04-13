package com.udacity.nanodegree.blooddonation.ui.login.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.udacity.nanodegree.blooddonation.injection.Injection;
import com.udacity.nanodegree.blooddonation.R;
import com.udacity.nanodegree.blooddonation.base.BaseActivity;
import com.udacity.nanodegree.blooddonation.databinding.ActivityLoginBinding;
import com.udacity.nanodegree.blooddonation.ui.home.HomeActivity;
import com.udacity.nanodegree.blooddonation.ui.login.LoginActivityContract;
import com.udacity.nanodegree.blooddonation.ui.login.LoginInfo;
import com.udacity.nanodegree.blooddonation.ui.login.presenter.LoginActivityPresenter;

/**
 * Created by riteshksingh on Apr, 2018
 */
public class LoginActivity extends BaseActivity implements
        LoginActivityContract.View {

    private LoginActivityContract.Presenter loginActivityPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        loginActivityPresenter = new LoginActivityPresenter(this,
                Injection.getFirebaseAuth());
        ((ActivityLoginBinding) mBinding).setPresenter(loginActivityPresenter);
        ((ActivityLoginBinding) mBinding).setLoginInfo(new LoginInfo());

        loginActivityPresenter.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginActivityPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginActivityPresenter.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginActivityPresenter.onDestroy();
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void loginSuccess() {
        launchHomeActivity();
    }

    @Override
    public void loginFailed() {
        Toast.makeText(this, "Autentication failed", Toast.LENGTH_SHORT).show();
    }
}
