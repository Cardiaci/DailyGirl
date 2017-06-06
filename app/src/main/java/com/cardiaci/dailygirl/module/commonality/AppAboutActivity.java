package com.cardiaci.dailygirl.module.commonality;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cardiaci.dailygirl.R;
import com.cardiaci.dailygirl.base.RxBaseActivity;

import butterknife.Bind;

/**
 * App关于界面
 */
public class AppAboutActivity extends RxBaseActivity {

  @Bind(R.id.toolbar)
  Toolbar mToolbar;

  @Bind(R.id.collapsing_toolbar)
  CollapsingToolbarLayout mCollapsingToolbarLayout;


  @Override
  public int getLayoutId() {

    return R.layout.activity_about;
  }


  @Override
  public void initViews(Bundle savedInstanceState) {

  }


  @Override
  public void initToolBar() {

    setSupportActionBar(mToolbar);
    ActionBar supportActionBar = getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    String version = getVersion();
    mCollapsingToolbarLayout.setTitle("关于每日妹纸" + "v" + version);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }


  private String getVersion() {

    try {
      PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
      return pi.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return getString(R.string.about_version);
    }
  }
}
