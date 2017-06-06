package com.cardiaci.dailygirl.network.api;

import com.cardiaci.dailygirl.entity.taomodel.TaoFemale;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TaoFemaleaApi {

  /**
   * 来自易源接口的淘宝妹纸
   */
  @GET("126-2")
  Observable<TaoFemale> getTaoFemale(@Query("page") String page,
                                     @Query("showapi_appid") String appId,
                                     @Query("showapi_sign") String sign);
}
