package com.cardiaci.dailygirl.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.cardiaci.dailygirl.DailyGirl;
import com.cardiaci.dailygirl.network.api.GankMeiziApi;
import com.cardiaci.dailygirl.network.api.TaoFemaleaApi;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

  private static final String BASE_GANK_URL = "http://gank.io/api/";

  private static final String BASE_HUABAN_URL = "http://route.showapi.com/";

  private static OkHttpClient mOkHttpClient;


  static {
    initOkHttpClient();
  }


  /**
   * Gank妹子Api
   */
  public static GankMeiziApi getGankMeiziApi() {

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_GANK_URL)
        .client(mOkHttpClient)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(GankMeiziApi.class);
  }


  /**
   * 淘宝妹纸Api
   */
  public static TaoFemaleaApi getTaoFemaleApi() {

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_HUABAN_URL)
        .client(mOkHttpClient)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(TaoFemaleaApi.class);
  }


  /**
   * 初始化OKHttpClient
   */
  private static void initOkHttpClient() {

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    if (mOkHttpClient == null) {
      synchronized (RetrofitHelper.class) {
        if (mOkHttpClient == null) {
          //设置Http缓存
          Cache cache = new Cache(new File(DailyGirl.getContext().getCacheDir(), "HttpCache"),
              1024 * 1024 * 100);

          mOkHttpClient = new OkHttpClient.Builder()
              .cache(cache)
              .addInterceptor(interceptor)
              .addNetworkInterceptor(new StethoInterceptor())
              .retryOnConnectionFailure(true)
              .connectTimeout(20, TimeUnit.SECONDS)
              .build();
        }
      }
    }
  }
}
