package com.cardiaci.dailygirl.module.commonality;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.cardiaci.dailygirl.R;
import com.cardiaci.dailygirl.base.RxBaseActivity;
import com.cardiaci.dailygirl.module.gank.GankMeiziFragment;
import com.cardiaci.dailygirl.module.taogirl.TaoFemaleFragment;
import com.cardiaci.dailygirl.utils.AlarmManagerUtils;
import com.cardiaci.dailygirl.utils.ShareUtil;
import com.cardiaci.dailygirl.utils.SnackbarUtil;
import com.cardiaci.dailygirl.widget.CircleImageView;

import java.util.Random;

import butterknife.Bind;

/**
 * 主界面
 */
public class MainActivity extends RxBaseActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @Bind(R.id.toolbar)
  Toolbar mToolbar;

  @Bind(R.id.drawer_layout)
  DrawerLayout mDrawerLayout;

  @Bind(R.id.nav_view)
  NavigationView mNavigationView;

  private Fragment[] fragments;

  private int currentTabIndex;

  private int index;

  private Random random = new Random();

  private int[] avatars = new int[] {
      R.drawable.ic_avatar1,
      R.drawable.ic_avatar2,
      R.drawable.ic_avatar3,
      R.drawable.ic_avatar4,
      R.drawable.ic_avatar5,
      R.drawable.ic_avatar6,
      R.drawable.ic_avatar7,
      R.drawable.ic_avatar8,
      R.drawable.ic_avatar9,
      R.drawable.ic_avatar10,
      R.drawable.ic_avatar11,
  };

  private long exitTime;


  @Override
  public int getLayoutId() {

    return R.layout.activity_main;
  }


  @Override
  public void initViews(Bundle savedInstanceState) {

    setNavigationView();
    initFragment();
    AlarmManagerUtils.register(this);
  }


  private void initFragment() {

    GankMeiziFragment gankMeiziFragment = GankMeiziFragment.newInstance();
    TaoFemaleFragment taoFemaleFragment = TaoFemaleFragment.newInstance();

    fragments = new Fragment[] {
        gankMeiziFragment,
        taoFemaleFragment
    };

    //显示第一个 每日妹纸
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.content, gankMeiziFragment)
        .commit();
  }


  private void setNavigationView() {

    mNavigationView.setNavigationItemSelectedListener(this);
    View headerView = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
    CircleImageView mCircleImageView = (CircleImageView) headerView.findViewById(
        R.id.nav_head_avatar);
    int randomNum = random.nextInt(avatars.length);
    mCircleImageView.setImageResource(avatars[randomNum]);
  }


  @Override
  public void initToolBar() {

    mToolbar.setTitle("每日妹纸");
    setSupportActionBar(mToolbar);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);

    mDrawerLayout.addDrawerListener(toggle);
    toggle.syncState();
  }


  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.nav_home:
        changIndex(0, getResources().getString(R.string.gank_meizi), item);
        return true;

      case R.id.nav_tao:
        changIndex(1, getResources().getString(R.string.tao_female), item);
        return true;

      case R.id.nav_about:

        startActivity(new Intent(MainActivity.this, AppAboutActivity.class));
        return true;

      case R.id.nav_share:

        ShareUtil.shareLink(getString(R.string.project_link), "每日更新妹纸福利图", MainActivity.this);
        return true;

      default:
        break;
    }
    return true;
  }


  public void changIndex(int changNum, String title, MenuItem item) {

    index = changNum;
    switchFragment(fragments[changNum]);
    item.setChecked(true);
    mToolbar.setTitle(title);
    mDrawerLayout.closeDrawers();
  }


  public void switchFragment(Fragment fragment) {

    FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
    trx.hide(fragments[currentTabIndex]);
    if (!fragments[index].isAdded()) {
      trx.add(R.id.content, fragments[index]);
    }
    trx.show(fragments[index]).commit();
    currentTabIndex = index;
  }


  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK) {
      logoutApp();
    }

    return true;
  }


  private void logoutApp() {

    if (System.currentTimeMillis() - exitTime > 2000) {
      SnackbarUtil.showMessage(mDrawerLayout, getString(R.string.back_message));

      exitTime = System.currentTimeMillis();
    } else {
      finish();
    }
  }
}
