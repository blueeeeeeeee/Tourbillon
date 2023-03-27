package com.example.Tourbillon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private String login_url = "https://i.sjtu.edu.cn/xtgl/login_slogin.html";
    WebView webview;
    Button get_courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        webview = (WebView) findViewById(R.id.login_web);
        get_courses = (Button) findViewById(R.id.get_courses_button);

        // 跳转到 SJTU 教学信息服务网
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(login_url);

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        }
        );

        webview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //按返回键操作并且能回退网页
                    if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
                        //后退
                        webview.goBack();
                        return true;
                    }
                }
                return false;
            }
        });


        // 返回按钮，找到课程表页面时按下，开启解析课程HTML文件，并将其加入数据库的操作
        get_courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CookieManager cookieManager = CookieManager.getInstance();
                String urlStr = webview.getUrl();
                String cookieStr  = cookieManager.getCookie(webview.getUrl());
                //System.out.println(cookieStr);

                CourseDownloader course_downloader = new CourseDownloader();
                course_downloader.sendPostRequest(cookieStr, urlStr, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d("CourseDownloader", "Post request failed!");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        ClassManager.deleteAllClass();
                        int code = response.code();
                        String body = response.body().string();
                        Log.d("CourseDownloader", "Post request received a response -- " + code + "\n" + body);

                        if (body.contains("jAccount")) {
                            Log.d("CourseDownloader", "Login Failed!");
                        }
                        else {
                            Log.d("CourseDownloader", "Down load courses successfully!");
                            Log.d("CourseDownloader", body);
                            List<Class_t> courses = parseFrom(body);
                            
                            if (courses != null) {
                                Log.d("CourseDownloader", "Down load courses successfully!");
                            } else {
                                Log.d("CourseDownloader", "Empty Courses!");
                            }
                            
                        }
                        finish();
                    }
                });
            }
        });
    }

    public static List<Integer> getStartAndStep(String jcor) {
        List<Integer> startAndStep = new ArrayList<>();
        String[] jc = jcor.split("-");
        startAndStep.add(Integer.valueOf(jc[0]));
        startAndStep.add(Integer.parseInt(jc[1]) - Integer.parseInt(jc[0]) + 1);
        return startAndStep;
    }

    public List<Class_t> parseFrom(String json) {
        List<Class_t> courses = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArrayKb = jsonObject.getJSONArray("kbList");

            for (int i = 0; i < jsonArrayKb.length(); i++) {
                JSONObject jsonCourse = jsonArrayKb.getJSONObject(i);
                Class_t course = new Class_t();
                course.setC_id(jsonCourse.getString("kch_id"));
                course.setC_name(jsonCourse.getString("kcmc"));
                course.setC_room(jsonCourse.getString("cdmc"));
                course.setC_day(jsonCourse.getInt("xqj"));
                course.setC_detail(jsonCourse.getString("xkbz"));
                List<Integer> startAndStep = getStartAndStep(jsonCourse.getString("jcs"));
                course.setC_time(startAndStep.get(0));
                course.setC_duration(startAndStep.get(1));
                course.setC_teacher(jsonCourse.getString("xm"));
                // to do 单双周
                String week = jsonCourse.getString("zcd").substring(0, jsonCourse.getString("zcd").length() - 1);
                String [] weeks = week.split("-");
                course.setC_startWeek(Integer.parseInt(weeks[0]));
                if (weeks.length == 1)
                    course.setC_endWeek(Integer.parseInt(weeks[0]));
                else
                    course.setC_endWeek(Integer.parseInt(weeks[1]));
                char[] weekCodeArray = new char[18];
                for (int j=0; j<18; ++j)
                    weekCodeArray[j] = '0';
                for (int j=course.getC_startWeek(); j<=course.getC_endWeek(); ++j)
                    weekCodeArray[j-1] = '1';
                course.setWeekCode(new String(weekCodeArray));
                course.setC_detail(jsonCourse.getString("xkbz"));
                courses.add(course);
                ClassManager.insert(course);
            }
            return courses;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}