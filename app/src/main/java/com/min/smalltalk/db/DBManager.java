package com.min.smalltalk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Min on 2016/12/3.
 * [数据库管理类，数据采用GreenDao来实现，所有实现通过模板自动生成；通过获取daoSession来获取所有的dao，从而实现操作对象]
 * @version 1.0
 */

public class DBManager {

    private final static String TAG="DBManager";
    private final static String DB_NAME="min_db";
    private static DBManager instance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper=new DaoMaster.DevOpenHelper(context,DB_NAME,null);
    }

    /**
     * 获取单例引用
     */
    public static DBManager getInstance(Context context){
        if(instance==null){
            synchronized (DBManager.class){
                if(instance==null){
                    instance=new DBManager(context);
                }
            }
        }
        return instance;
    }

    //可写
    private SQLiteDatabase getWritableDatabases(){
        SQLiteDatabase db=openHelper.getWritableDatabase();
        return db;
    }
    //可读
    private SQLiteDatabase getReadableDatabases(){
        SQLiteDatabase db=openHelper.getReadableDatabase();
        return db;
    }
}
