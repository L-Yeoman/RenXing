package com.dldzkj.app.renxing.utils;

import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.bean.User;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

/**
 * Created by Administrator on 2015/7/17.
 */
public class AppDbUtils {
    private DbUtils db;
    public AppDbUtils() {
        db= MyApplication.getInstance().getDbUtils();
    }
  public User getLoginUser(int uid){
      User u=null;
      try {
           u=db.findById(User.class,uid);
      } catch (DbException e) {
          e.printStackTrace();
      }
      return  u;
  }
}
