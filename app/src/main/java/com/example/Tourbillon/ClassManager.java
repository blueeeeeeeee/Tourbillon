package com.example.Tourbillon;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ClassManager {
    private static final String TAG = "ClassManager";
    public static int CurWeek = 16;
    public static ClassDBHelper dbHelper;

    public ClassManager(Context context) {
        dbHelper = new ClassDBHelper(context);
    }

    public static final String[] pleasantColors = {"#7FFC6472", "#7FF4B2A6", "#7FECCDB3", "#7FBCEFD0", "#7FA1E8E4", "#7F23C8B2", "#7FC3ACEE"};

    /**
     * 插入数据
     */
    public static void insertData(String id, String name, Integer time, Integer duration, Integer startWeek,
                                  Integer endWeek, Integer day, String room, String teacher, String detail,
                                  String weekCode, Boolean isClass) {
        // 实际上是更新，为了保存顺序
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.i(TAG, "insertData" + id + name);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Class_t.KEY_id, id);
        contentValues.put(Class_t.COLUMN_name, name);
        contentValues.put(Class_t.COLUMN_time, time);
        contentValues.put(Class_t.COLUMN_duration, duration);
        contentValues.put(Class_t.COLUMN_startw, startWeek);
        contentValues.put(Class_t.COLUMN_endw, endWeek);
        contentValues.put(Class_t.COLUMN_day, day);
        contentValues.put(Class_t.COLUMN_room, room);
        contentValues.put(Class_t.COLUMN_teacher, teacher);
        contentValues.put(Class_t.COLUMN_detail, detail);
        contentValues.put(Class_t.COLUMN_isclass, isClass);
        contentValues.put(Class_t.COLUMN_weekcode, weekCode);
        long r = db.insert(ClassDBHelper.TABLE_NAME, null, contentValues);
        db.close();
    }

    public static void insertData(String id, String name, Integer time, Integer duration, Integer startWeek,
                                  Integer endWeek, Integer day, String room, String teacher) {
        char[] weekCodeArray = new char[18];
        for (int i=0; i<18; ++i)
            weekCodeArray[i] = '0';
        for (int i=startWeek; i<=endWeek; ++i)
            weekCodeArray[i-1] = '1';
        insertData(id, name, time, duration, startWeek, endWeek, day, room, teacher, null,  new String(weekCodeArray), true);
    }

    public static boolean insert(Class_t myclass) {
        //与数据库建立连接
        Log.d("insert_class", "insert by Class_t: " + myclass.getC_name());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Class_t.KEY_id, myclass.getC_id());
        contentValues.put(Class_t.COLUMN_name, myclass.getC_name());
        contentValues.put(Class_t.COLUMN_time, myclass.getC_time());
        contentValues.put(Class_t.COLUMN_duration, myclass.getC_duration());
        contentValues.put(Class_t.COLUMN_startw, myclass.getC_startWeek());
        contentValues.put(Class_t.COLUMN_endw, myclass.getC_endWeek());
        contentValues.put(Class_t.COLUMN_day, myclass.getC_day());
        contentValues.put(Class_t.COLUMN_room, myclass.getC_room());
        contentValues.put(Class_t.COLUMN_teacher, myclass.getC_teacher());
        contentValues.put(Class_t.COLUMN_detail, myclass.getC_detail());
        contentValues.put(Class_t.COLUMN_isclass, myclass.getC_isClass());
        contentValues.put(Class_t.COLUMN_weekcode, myclass.getWeekCode());
        //插入每一行数据
        long r = db.insert(ClassDBHelper.TABLE_NAME, null, contentValues);
        db.close();
        return r != -1;
    }

    /**
     * 删除数据
     */
    public static void delete(String id) {
        //与数据库建立连接
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ClassDBHelper.TABLE_NAME, "c_id" + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public static void delete(Class_t myclass) {
        //与数据库建立连接
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(ClassDBHelper.TABLE_NAME,
                Class_t.COLUMN_startw + "=? and " +
                        Class_t.COLUMN_day + "=? and " +
                        Class_t.COLUMN_time + "=?",
                        new String[]{String.valueOf(myclass.c_startWeek),
                                String.valueOf(myclass.c_day),
                                String.valueOf(myclass.c_time)});
        db.close();
    }

    public static void deleteAllClass(){
        List<Class_t> classes = query();
        for (Class_t c : classes){
            if (c.c_isClass)
                delete(c);
        }
    }

    public static Class_t query(int week, int day, int time) {
        //与数据库建立连接
        Log.d(TAG, "query: "+week + ' '+ day+' '+time);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = " select " + Class_t.COLUMN_name + "," + Class_t.COLUMN_time +
                "," + Class_t.COLUMN_duration + "," + Class_t.COLUMN_startw +
                "," + Class_t.COLUMN_endw + "," + Class_t.COLUMN_day +
                "," + Class_t.COLUMN_detail +  "," + Class_t.KEY_id +
                "," + Class_t.COLUMN_room + "," + Class_t.COLUMN_teacher +
                "," + Class_t.COLUMN_weekcode +
                " from " + ClassDBHelper.TABLE_NAME + " where " + Class_t.COLUMN_startw + "=? and " +
                Class_t.COLUMN_time + "=? and " + Class_t.COLUMN_day + "=?";
        Class_t myclass = new Class_t();
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(week),String.valueOf(time),String.valueOf(day)});
        while (cursor.moveToNext()) {
            Log.d(TAG, "query: ");
            int i = cursor.getColumnIndex(Class_t.KEY_id);
            int j = cursor.getColumnIndex(Class_t.COLUMN_name);
            int k = cursor.getColumnIndex(Class_t.COLUMN_time);
            int l = cursor.getColumnIndex(Class_t.COLUMN_duration);
            int p = cursor.getColumnIndex(Class_t.COLUMN_startw);
            int q = cursor.getColumnIndex(Class_t.COLUMN_endw);
            int t = cursor.getColumnIndex(Class_t.COLUMN_day);
            int m = cursor.getColumnIndex(Class_t.COLUMN_room);
            int n = cursor.getColumnIndex(Class_t.COLUMN_teacher);
            int a = cursor.getColumnIndex(Class_t.COLUMN_detail);
            int b = cursor.getColumnIndex(Class_t.COLUMN_weekcode);
            if(i >= 0)
                myclass.c_id = cursor.getString(i);
            if(j >= 0)
                myclass.c_name = cursor.getString(j);
            if(k >= 0)
                myclass.c_time = cursor.getInt(k);
            if(l >= 0)
                myclass.c_duration = cursor.getInt(l);
            if(p >= 0)
                myclass.c_startWeek = cursor.getInt(p);
            if(q >= 0)
                myclass.c_endWeek = cursor.getInt(q);
            if(t >= 0)
                myclass.c_day = cursor.getInt(t);
            if(m >= 0)
                myclass.c_room = cursor.getString(m);
            if(n >= 0)
                myclass.c_teacher = cursor.getString(n);
            if(a >= 0)
                myclass.c_detail = cursor.getString(a);
            if (b>0)
                myclass.setWeekCode(cursor.getString(b));
        }
        cursor.close();
        db.close();
        return myclass;
    }

    @SuppressLint("Range")
    public static List<Class_t> query() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = " select " + Class_t.KEY_id + "," + Class_t.COLUMN_name +
                "," + Class_t.COLUMN_time + "," + Class_t.COLUMN_duration +
                "," + Class_t.COLUMN_startw + "," + Class_t.COLUMN_endw +
                "," + Class_t.COLUMN_day + "," + Class_t.COLUMN_room +
                "," + Class_t.COLUMN_teacher + "," + Class_t.COLUMN_detail +
                " from " + ClassDBHelper.TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        List<Class_t> classes = new ArrayList<>();

        // 将游标移到开头
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) { // 游标只要不是在最后一行之后，就一直循环
            Log.i(TAG, "query: ");
            Class_t Class = new Class_t();
            String name = cursor.getString(cursor.getColumnIndex("c_name"));
            Class.setC_id(cursor.getString(cursor.getColumnIndex("c_id")));
            Class.setC_name(name);
            Class.setC_time(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_time"))));
            Class.setC_startWeek(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_startWeek"))));
            Class.setC_endWeek(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_endWeek"))));
            Class.setC_duration(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_duration"))));
            Class.setC_day(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_day"))));
            Class.setC_room(cursor.getString(cursor.getColumnIndex("c_room")));
            Class.setC_teacher(cursor.getString(cursor.getColumnIndex("c_teacher")));
            Class.setC_detail(cursor.getString(cursor.getColumnIndex(Class_t.COLUMN_detail)));
            int seed = ((int) name.charAt(0)) % 7;
            Class.setColor(Color.parseColor("#525252"));
            classes.add(Class);
            // 将游标移到下一行
            cursor.moveToNext();

        }

        cursor.close();
        return classes;
    }


    @SuppressLint("Range")
    public static List<Class_t> getCurrentWeekClasses() {
        // 与query类似，只返回当前周课程
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = " select " + Class_t.KEY_id + "," + Class_t.COLUMN_name +
                "," + Class_t.COLUMN_time + "," + Class_t.COLUMN_duration +
                "," + Class_t.COLUMN_startw + "," + Class_t.COLUMN_endw +
                "," + Class_t.COLUMN_day + "," + Class_t.COLUMN_room +
                "," + Class_t.COLUMN_teacher + "," + Class_t.COLUMN_isclass +
                "," + Class_t.COLUMN_weekcode + "," + Class_t.COLUMN_detail +
                " from " + ClassDBHelper.TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        List<Class_t> classes = new ArrayList<>();

        // 将游标移到开头
        cursor.moveToFirst();
        int seed = 0;
        while (!cursor.isAfterLast()) { // 游标只要不是在最后一行之后，就一直循环
            String name = cursor.getString(cursor.getColumnIndex("c_name"));
            boolean isClass = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Class_t.COLUMN_isclass)))>0;
            String weekCodeString = cursor.getString(cursor.getColumnIndex(Class_t.COLUMN_weekcode));
            char [] weekCode = weekCodeString.toCharArray();
            int startWeek = Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_startWeek")));
            int endWeek = Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_endWeek")));
            Log.i(TAG, "getCurrentWeekClasses: "+name+' '+isClass+' '+weekCodeString);
            if ((isClass && startWeek <= CurWeek && endWeek >= CurWeek) || (weekCode[CurWeek-1]=='1')) {
                seed++;
                Class_t Class = new Class_t();
                Class.setC_id(cursor.getString(cursor.getColumnIndex("c_id")));
                Class.setC_name(name);
                Class.setC_time(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_time"))));
                Class.setC_startWeek(startWeek);
                Class.setC_endWeek(endWeek);
                Class.setC_duration(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_duration"))));
                Class.setC_day(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_day"))));
                Class.setC_room(cursor.getString(cursor.getColumnIndex("c_room")));
                Class.setC_teacher(cursor.getString(cursor.getColumnIndex("c_teacher")));
                Class.setC_isClass(isClass);
                Class.setC_detail(cursor.getString(cursor.getColumnIndex(Class_t.COLUMN_detail)));
                Class.setWeekCode(weekCodeString);
                //Class.setColor(Color.parseColor("#9F7D8C82"));
                //Class.setColor(Color.parseColor("#80BDC3C7"));银
                if (isClass)
                    Class.setColor(Color.parseColor("#9F696969"));
                else
                    Class.setColor(Color.parseColor("#9F405955"));
                classes.add(Class);
            }
            // 将游标移到下一行
            cursor.moveToNext();

        }

        cursor.close();
        return classes;
    }

    /**
     * 更新数据
     */
    public static boolean update(Class_t myclass) {
        Log.i(TAG, "update: "+myclass.toString());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("c_id", myclass.getC_id());
        contentValues.put("c_name", myclass.getC_name());
        contentValues.put("c_time", myclass.getC_time());
        contentValues.put("c_duration", myclass.getC_duration());
        contentValues.put("c_startWeek", myclass.getC_startWeek());
        contentValues.put("c_endWeek", myclass.getC_endWeek());
        contentValues.put("c_day", myclass.getC_day());
        contentValues.put("c_room", myclass.getC_room());
        contentValues.put("c_teacher", myclass.getC_teacher());
        contentValues.put("c_weekCode", myclass.getWeekCode());
        contentValues.put("c_detail", myclass.getC_detail());

        db.update(ClassDBHelper.TABLE_NAME, contentValues,
                Class_t.COLUMN_startw + "=? and " +
                        Class_t.COLUMN_day + "=? and " +
                        Class_t.COLUMN_time + "=?",
                new String[]{String.valueOf(myclass.c_startWeek),
                        String.valueOf(myclass.c_day),
                        String.valueOf(myclass.c_time)});
        db.close();
        return true;
    }

    public static void setCurWeek(int w){
        CurWeek = w;
    }

    public static int getCurWeek(){
        return CurWeek;
    }
}

