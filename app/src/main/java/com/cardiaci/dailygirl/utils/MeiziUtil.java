package com.cardiaci.dailygirl.utils;
import com.cardiaci.dailygirl.entity.gank.GankMeizi;
import com.cardiaci.dailygirl.entity.gank.GankMeiziInfo;

import java.util.List;

import io.realm.Realm;

/**
 * 工具类
 */
public class MeiziUtil {

  private static volatile MeiziUtil mCache;


  private MeiziUtil() {

  }


  public static MeiziUtil getInstance() {

    if (mCache == null) {
      synchronized (MeiziUtil.class) {
        if (mCache == null) {
          mCache = new MeiziUtil();
        }
      }
    }

    return mCache;
  }


  /**
   * 保存gank妹子到数据库中
   */

  public void putGankMeiziCache(List<GankMeiziInfo> gankMeiziInfos) {

    GankMeizi meizi;
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    for (int i = 0; i < gankMeiziInfos.size(); i++) {
      meizi = new GankMeizi();
      String url = gankMeiziInfos.get(i).url;
      String desc = gankMeiziInfos.get(i).desc;
      meizi.setUrl(url);
      meizi.setDesc(desc);
      realm.copyToRealm(meizi);
    }
    realm.commitTransaction();
    realm.close();
  }

}
