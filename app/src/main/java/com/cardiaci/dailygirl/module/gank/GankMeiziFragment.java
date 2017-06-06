package com.cardiaci.dailygirl.module.gank;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

import com.cardiaci.dailygirl.R;
import com.cardiaci.dailygirl.adapter.GankMeiziAdapter;
import com.cardiaci.dailygirl.base.RxBaseFragment;
import com.cardiaci.dailygirl.entity.gank.GankMeizi;
import com.cardiaci.dailygirl.network.RetrofitHelper;
import com.cardiaci.dailygirl.rx.RxBus;
import com.cardiaci.dailygirl.utils.LogUtil;
import com.cardiaci.dailygirl.utils.MeiziUtil;
import com.cardiaci.dailygirl.utils.SnackbarUtil;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * gank妹子
 */
public class GankMeiziFragment extends RxBaseFragment {

  @Bind(R.id.swipe_refresh)
  SwipeRefreshLayout mSwipeRefreshLayout;

  @Bind(R.id.recycle)
  RecyclerView mRecyclerView;

  private StaggeredGridLayoutManager mLayoutManager;

  private RealmResults<GankMeizi> gankMeizis;

  private int pageNum = 20;

  private int page = 1;

  private static final int PRELOAD_SIZE = 6;

  private boolean mIsLoadMore = true;

  private GankMeiziAdapter mAdapter;

  private Realm realm;

  private int imageIndex;

  //RecycleView是否正在刷新
  private boolean mIsRefreshing = false;


  public static GankMeiziFragment newInstance() {

    return new GankMeiziFragment();
  }


  @Override
  public int getLayoutId() {

    return R.layout.fragment_gank_meizi;
  }


  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void initViews() {

    showProgress();
    realm = Realm.getDefaultInstance();
    gankMeizis = realm.where(GankMeizi.class).findAll();
    initRecycleView();

    RxBus.getInstance().toObserverable(Intent.class)
        .compose(this.bindToLifecycle())
        .subscribe(intent -> {

          imageIndex = intent.getIntExtra("index", -1);
          scrollIndex();
          finishTask();
        }, throwable -> {

          LogUtil.all(throwable.getMessage());
        });

    setEnterSharedElementCallback(new SharedElementCallback() {

      @Override
      public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {

        super.onMapSharedElements(names, sharedElements);
        String newTransitionName = gankMeizis.get(imageIndex).getUrl();
        View newSharedView = mRecyclerView.findViewWithTag(newTransitionName);
        if (newSharedView != null) {
          names.clear();
          names.add(newTransitionName);
          sharedElements.clear();
          sharedElements.put(newTransitionName, newSharedView);
        }
      }
    });
  }


  private void initRecycleView() {
    mRecyclerView.setHasFixedSize(true);
    mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.addOnScrollListener(OnLoadMoreListener(mLayoutManager));
    mAdapter = new GankMeiziAdapter(mRecyclerView, gankMeizis);
    mRecyclerView.setAdapter(mAdapter);
    setRecycleScrollBug();
  }


  public void showProgress() {

    mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mSwipeRefreshLayout.post(() -> {

      mSwipeRefreshLayout.setRefreshing(true);
      clearCache();
      mIsRefreshing = true;
      getGankMeizi();
    });

    mSwipeRefreshLayout.setOnRefreshListener(() -> {

      page = 1;
      clearCache();
      mIsRefreshing = true;
      getGankMeizi();
    });
  }


  private void clearCache() {

    try {
      realm.beginTransaction();
      realm.where(GankMeizi.class)
          .findAll().clear();
      realm.commitTransaction();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void getGankMeizi() {

    RetrofitHelper.getGankMeiziApi()
        .getGankMeizi(pageNum, page)
        .compose(this.bindToLifecycle())
        .filter(gankMeiziResult -> !gankMeiziResult.error)
        .map(gankMeiziResult -> gankMeiziResult.gankMeizis)
        .doOnNext(gankMeiziInfos -> MeiziUtil.getInstance().putGankMeiziCache(gankMeiziInfos))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(gankMeiziInfos -> {

          finishTask();
        }, throwable -> {

          mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
          SnackbarUtil.showMessage(mRecyclerView, getString(R.string.error_message));
        });
  }


  private void finishTask() {

    if (page * pageNum - pageNum - 1 > 0) {
      mAdapter.notifyItemRangeChanged(page * pageNum - pageNum - 1, pageNum);
    } else {
      mAdapter.notifyDataSetChanged();
    }
    if (mSwipeRefreshLayout.isRefreshing()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    mIsRefreshing = false;

    mAdapter.setOnItemClickListener((position, holder) -> {

      Intent intent = GankMeiziPageActivity.luanch(getActivity(), position);
      if (Build.VERSION.SDK_INT >= 22) {
        startActivity(intent,
            ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                holder.getParentView().findViewById(R.id.item_img),
                gankMeizis.get(position).getUrl()).toBundle());
      } else {
        startActivity(intent);
      }
    });
  }


  RecyclerView.OnScrollListener OnLoadMoreListener(StaggeredGridLayoutManager layoutManager) {

    return new RecyclerView.OnScrollListener() {

      @Override
      public void onScrolled(RecyclerView rv, int dx, int dy) {

        boolean isBottom = mLayoutManager.findLastCompletelyVisibleItemPositions(
            new int[2])[1] >= mAdapter.getItemCount() - PRELOAD_SIZE;
        if (!mSwipeRefreshLayout.isRefreshing() && isBottom) {
          if (!mIsLoadMore) {
            mSwipeRefreshLayout.setRefreshing(true);
            page++;
            getGankMeizi();
          } else {
            mIsLoadMore = false;
          }
        }
      }
    };
  }


  public void scrollIndex() {

    if (imageIndex != -1) {
      mRecyclerView.scrollToPosition(imageIndex);
      mRecyclerView.getViewTreeObserver()
          .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {

              mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
              mRecyclerView.requestLayout();
              return true;
            }
          });
    }
  }


  private void setRecycleScrollBug() {

    mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
  }
}
