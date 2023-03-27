package com.example.Tourbillon;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    final ClassManager classOp = new ClassManager(MainActivity.this);
    ImageButton add;
    ImageButton button_setting;
    public static Date curDate;
    private static AlertDialog dialog_editWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add = (ImageButton) findViewById(R.id.ImageButton_Add);
        LinearLayout linearLayout_Time = findViewById(R.id.LinearLayout_Time);

        //打印当前数据库
        printClassDatabase();

        // 查询空闲教室
        button_setting = findViewById(R.id.ImageButton_Settings);
        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 显示setting的菜单栏
                showSettingsMenu(view);
            }
        });

        // 导入课程按钮
        ImageButton button_import = findViewById(R.id.ImageButton_Import);
        button_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //填写日期
        TextView textView_date = findViewById(R.id.TextView_date);
        TextView textView_week = findViewById(R.id.TextView_Week);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        curDate = new Date(System.currentTimeMillis());
        textView_date.setText(formatter.format(curDate));
        textView_week.setText("第"+ClassManager.getCurWeek()+"周");

        //添加按钮设置
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        //切换当前周按钮设置
        linearLayout_Time.setOnClickListener(view -> showEditWeekDialog(MainActivity.this
                , new DialogButtonOnClickListener() {
                    @Override
                    public void onPositive(String Week) {
                        int week = Integer.parseInt(Week);
                        if (week > 0 && week < 21)
                            ClassManager.setCurWeek(week);
                        TextView curweek = findViewById(R.id.TextView_Week);
                        curweek.setText("第" + week + "周");
                    }

                    @Override
                    public void onNegative() {
                        //Execute when cancel is pressed
                    }
                }));

        //课程点击事件设置
        ScheduleView scheduleView = findViewById(R.id.ScheduleView);
        scheduleView.setOnEventClickListener(new ScheduleView.OnEventClickListener() {
            @Override
            public void onEventClick(Class_t event) {
                Log.i(TAG, "onEventClick:"+event.c_name);
                DetailDialog.showEventDetailDialog(MainActivity.this, event
                        , new DetailDialog.DialogButtonOnClickListener() {
                        @Override
                        public void onDelete(Class_t event1){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(String.format(getString(R.string.sure_to_delete), event1.getC_name()));
                            builder.setTitle(getString(R.string.warning));

                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    ClassManager.delete(event1);
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                }
                            });
                            builder.create().show();
                        }

                        public void onEdit(Class_t event1){
                            //modify
                            if(event1.c_isClass){
                                Intent intent = new Intent(MainActivity.this, ClassActivity.class);
                                intent.putExtra("action", ClassActivity.ACTION_MODIFY);
                                intent.putExtra("startweek",event1.getC_startWeek() );
                                intent.putExtra("time",event1.getC_time());
                                intent.putExtra("day",event1.getC_day());
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                                intent.putExtra("action", ClassActivity.ACTION_MODIFY);
                                intent.putExtra("startweek",event1.getC_startWeek() );
                                intent.putExtra("time",event1.getC_time());
                                intent.putExtra("day",event1.getC_day());
                                startActivity(intent);
                            }
                        }
                });
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //可以获得窗口大小，防止需要计算大小的函数
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged: ");
        drawClassBoxes();
        TextView view_weekHeader = findViewById(R.id.textView_WeekHeader);
        ScheduleView scheduleView = findViewById(R.id.ScheduleView);
        view_weekHeader.setWidth(scheduleView.getWidth());
        Log.d(TAG, "onWindowFocusChanged: header width:" + view_weekHeader.getWidth());
        //设置初始滚动位置
        ScrollView scrollView = findViewById(R.id.scrollView_main);
        scrollView.scrollTo(0, 1280);
    }


//    private void insertData(String id, String name, Integer time, Integer day) {
//        classOp.insertData(id, name, time, 2, 1, 16, day, " ", " ");
//    }

//    private void addTestData() {
//        // 加入测试课程
//        insertData("Gaoshu", "高数", 3, 1);
//        insertData("RuanGong", "软件工程", 1, 2);
//        insertData("RuanGong2", "软件工程", 3, 2);
//        classOp.insertData("ShuJuKu", "数据库", 5, 3, 1, 16, 3, "东上院101", "x老师");
//        classOp.insertData("KC4", "课程4", 3, 2, 1, 8, 4, "东上院101", "x老师");
//        classOp.insertData("KC5", "课程5", 10, 3, 1, 16, 5, "东上院101", "x老师");
//        classOp.insertData("KC6", "课程6", 8, 2, 1, 16, 6, "东上院101", "x老师");
//        //insertData(6, "课程7", 1, 2, 1, 16, 7, "东上院102", "x老师");
//        printClassDatabase();
//    }

    public void drawClassBoxes() {
        //从数据库拿到课程数据保存在链表
        List<Class_t> classes = ClassManager.getCurrentWeekClasses();
        ScheduleView scheduleView = findViewById(R.id.ScheduleView);
        scheduleView.setEvents(classes);
    }

    public void printClassDatabase() {
        List<Class_t> classes = ClassManager.query();
        for (Class_t c : classes) {
            Log.i(TAG, "printClassDatabase: " + c.toString());
        }
    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_addcourse) {
                    Intent intent = new Intent(MainActivity.this, ClassActivity.class);
                    intent.putExtra("action", ClassActivity.ACTION_INSERT);
                    startActivity(intent);
                } else if (id == R.id.action_addschedule) {
                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    intent.putExtra("action", ScheduleActivity.ACTION_INSERT);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void showSettingsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.settings_queryClassroom) {
                    Intent intent = new Intent(MainActivity.this, ClassroomQueryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.settings_queryLibrary) {
                    Intent intent = new Intent(MainActivity.this, LibraryQueryActivity.class);
                    startActivity(intent);
                }
//              else if (id == R.id.settings_others) {
//                    // to do
//                }
                return false;
            }
        });
    }

    public static void showEditWeekDialog(Activity activity
            , DialogButtonOnClickListener dialogButtonOnClickListener) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(activity).inflate(R.layout.edit_week,
                null);
        Button button_confirm = view.findViewById(R.id.button_dialog_confirm);
        Button button_cancel = view.findViewById(R.id.button_dialog_cancel);
        TextView textView_title = view.findViewById(R.id.textView_dialog_title);
        EditText textView_message = view.findViewById(R.id.textView_dialog_message);
        button_confirm.setOnClickListener(view12 -> {
            dialogButtonOnClickListener.onPositive(textView_message.getText().toString().trim());
            dialog_editWeek.dismiss();
        });
        button_cancel.setOnClickListener(view1 -> {
            dialogButtonOnClickListener.onNegative();
            dialog_editWeek.dismiss();
        });
        AlertDialog.Builder builder_editWeek = new AlertDialog.Builder(activity);
        builder_editWeek.setView(view);
        builder_editWeek.setCancelable(true);
        dialog_editWeek = builder_editWeek.create();
        dialog_editWeek.setCanceledOnTouchOutside(true);
        dialog_editWeek.show();
    }
}