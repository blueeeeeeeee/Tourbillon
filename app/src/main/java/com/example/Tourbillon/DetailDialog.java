package com.example.Tourbillon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class DetailDialog {
    private static AlertDialog dialog_detail;

    public interface DialogButtonOnClickListener {
        void onDelete(Class_t event);
        void onEdit(Class_t event);
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public static void showEventDetailDialog(Activity activity, Class_t event,
                                             DialogButtonOnClickListener dialogButtonOnClickListener) {
        ImageButton button_delete = null;
        ImageButton button_edit = null;
        View view = null;
        if (event.c_isClass) {
            view = LayoutInflater.from(activity).inflate(R.layout.class_detail,null);
            button_delete = view.findViewById(R.id.button_classDetail_delete);
            button_edit = view.findViewById(R.id.button_classDetail_edit);
            TextView textView_name = view.findViewById(R.id.TextView_classDetail_ClassName);
            TextView textView_week = view.findViewById(R.id.TextView_classDetail_Week);
            TextView textView_time = view.findViewById(R.id.TextView_classDetail_Time);
            TextView textView_teacher = view.findViewById(R.id.TextView_classDetail_Teacher);
            TextView textView_loacation = view.findViewById(R.id.TextView_classDetail_location);
            TextView textView_note = view.findViewById(R.id.TextView_classDetail_note);

            textView_name.setText(event.getC_name());
            textView_week.setText("第" + event.getC_startWeek() + '-' + event.getC_endWeek() + "周");
            textView_time.setText(event.getStartTime() + " - " + event.getEndTime());
            textView_teacher.setText(event.getC_teacher());
            textView_loacation.setText(event.getC_room());
            textView_note.setText(event.getC_detail());
        }
        else{
            view = LayoutInflater.from(activity).inflate(R.layout.event_detail,null);
            button_delete = view.findViewById(R.id.button_eventDetail_delete);
            button_edit = view.findViewById(R.id.button_eventDetail_edit);
            TextView textView_name = view.findViewById(R.id.TextView_eventDetail_Name);
            TextView textView_time = view.findViewById(R.id.TextView_eventDetail_Time);
            TextView textView_note = view.findViewById(R.id.TextView_eventDetail_note);

            textView_name.setText(event.getC_name());
            textView_time.setText(event.getStartTime() + " - " + event.getEndTime());
            textView_note.setText(event.getC_detail());
        }

        button_delete.setOnClickListener(view12 -> {
            dialogButtonOnClickListener.onDelete(event);
            dialog_detail.dismiss();
        });
        button_edit.setOnClickListener(view1 -> {
            dialogButtonOnClickListener.onEdit(event);
            dialog_detail.dismiss();
        });

        AlertDialog.Builder builder_detail = new AlertDialog.Builder(activity);
        dialog_detail = builder_detail.create();
        dialog_detail.setCanceledOnTouchOutside(true);
        dialog_detail.setCancelable(true);
        dialog_detail.show();
        Window window = dialog_detail.getWindow();
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setContentView(view);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = 1000;
        window.setAttributes(attributes);
    }
}
