package com.example.Tourbillon;

public class Class_t {
    public static final int MAX_STEPS = 14;
    public static final int MAX_WEEKS = 18;
    //列名
    public static final String KEY_id = "c_id";
    public static final String COLUMN_name = "c_name";
    public static final String COLUMN_time = "c_time";
    public static final String COLUMN_duration = "c_duration";
    public static final String COLUMN_startw = "c_startWeek";
    public static final String COLUMN_endw = "c_endWeek";
    public static final String COLUMN_day = "c_day";
    public static final String COLUMN_room = "c_room";
    public static final String COLUMN_teacher = "c_teacher";
    public static final String COLUMN_detail = "c_detail";
    public static final String COLUMN_isclass = "c_isClass";
    public static final String COLUMN_weekcode = "c_weekCode";

    public String c_id;
    public String c_name;
    public int c_time;   //1-12
    public int c_duration;
    public int c_startWeek;
    public int c_endWeek;
    public int c_day;    //1-7
    public String c_room;
    public String c_teacher;
    public String c_detail;  //备注
    private String weekCode = "000000000000000000";
    public boolean c_isClass = true;
    private int contentColor;
    private int textColor;
    private String[] start_times = {"8:00", "8:55", "10:00", "10:55", "12:00", "12:55", "14:00", "14:55", "16:00", "16:55", "18:00", "18:55", "19:45", "20:30"};
    private String [] end_times = {"8:45", "9:40", "10:45", "11:40", "12:45", "13:40", "14:45", "15:40", "16:45", "17:40", "18:45", "19:40", "20:20", "21:20"};


    @Override
    public String toString() {
        return "Classes{" +
                "c_id=" + c_id +
                ", c_name=" + c_name + '\'' +
                ", c_time=" + c_time + '\'' +
                ", c_duration=" + c_duration + '\'' +
                ", c_startWeek=" + c_startWeek + '\'' +
                ", c_endWeek=" + c_endWeek + '\'' +
                ", c_day=" + c_day + '\'' +
                ", c_room=" + c_room + '\'' +
                ", c_teacher=" + c_teacher + '\'' +
                ", c_isClass=" + c_isClass + '\'' +
                ", c_weekCode=" + weekCode + '\'' +
                ", c_detail=" + c_detail +
                '}';
    }

    public String getDisplayText() {
        return c_name + '\n' + c_room + '\n' + c_teacher;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public int getC_time() {
        return c_time;
    }

    public void setC_time(int c_time) {
        this.c_time = c_time;
    }

    public int getC_duration() {
        return c_duration;
    }

    public void setC_duration(int c_duration) {
        this.c_duration = c_duration;
    }

    public int getC_startWeek() {
        return c_startWeek;
    }

    public void setC_startWeek(int c_startWeek) {
        this.c_startWeek = c_startWeek;
    }

    public int getC_endWeek() {
        return c_endWeek;
    }

    public void setC_endWeek(int c_endWeek) {
        this.c_endWeek = c_endWeek;
    }

    public int getC_day() {
        return c_day;
    }

    public void setC_day(int c_day) {
        this.c_day = c_day;
    }

    public String getC_room() {
        return c_room;
    }

    public void setC_room(String c_room) {
        this.c_room = c_room;
    }

    public String getC_teacher() {
        return c_teacher;
    }

    public void setC_teacher(String c_teacher) {
        this.c_teacher = c_teacher;
    }

    public int getColor(){
        return contentColor;
    }

    public void setColor(int color){
        this.contentColor = color;
    }

    public int getTextColor(){
        return textColor;
    }

    public void setTextColor(int color){
        this.textColor = color;
    }

    public String getStartTime(){
        return start_times[c_time-1];
    }

    public String getEndTime(){
        return end_times[c_time+c_duration-2];
    }

    public String getC_detail() {
        return c_detail;
    }

    public void setC_detail(String detail) {
        this.c_detail = detail;
    }

    public boolean getC_isClass(){
        return c_isClass;
    }

    public void setC_isClass(boolean isClass) {
        this.c_isClass = isClass;
    }

    public String getWeekCode() {
        return weekCode;
    }

    public void setWeekCode(String weekCode) {
        this.weekCode = weekCode;
    }
}