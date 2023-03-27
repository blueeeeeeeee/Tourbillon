package com.example.Tourbillon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;

import com.example.Tourbillon.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ScheduleView extends View {
    private static final String TAG = "ScheduleView";
    /**
     * 分隔线的相关
     */
    private Paint linePaint;
    private float lineSize = 2;
    private int lineColor;

    /**
     * 列数，取值5或者7
     */
    private int columnNumber;
    private float columnWidth;
    /**
     * 行高
     */
    private float rowHeight;

    /**
     * 颜色相关
     */
    private int bgColor;
    private int sideLineColor; //事件框边框颜色
    private int eventDefaultColor = getResources().getColor(R.color.pleasantColor_1);

    /**
     * 绘制时间轴的信息
     */
    private Paint hourTextPaint;
    //    private Paint hourTextAmPaint;
    private float hourTextWidth;
    private float hourTextSize;
    private int hourTextColor;
    private int hourTextBgColor;


    /**
     * 当前时间
     */
    private RectF currentTimeRect;
    private Paint currentTimePaint;
    private int currentTimeColor;
    private int currentTimeTextColor;
    private float currentTimeTextSize;

    /**
     * 事件相关
     */
    private TextPaint eventTextPaint;
    private Paint eventBgPaint;
    private float eventTextSize;
    private int eventTextColor = getResources().getColor(R.color.black);
    private int eventBgColor;
    private int eventBgLineColor;
    private List<EventRect> eventRectList = new ArrayList<EventRect>();

    private Calendar firstDay;
    private Calendar endDay;

    private boolean isFirst;
    private int frameWidth = 2;

    //点击相关
    private OnEventClickListener onEventClickListener;
    private GestureDetectorCompat gestureDetectorCompat;
    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        //public boolean onSingleTapConfirmed(MotionEvent e) {
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed");
            if (eventRectList != null && eventRectList.size() > 0) {
                for (EventRect eventRect : eventRectList) {
                    RectF rectF = eventRect.rectF;
                    if (rectF.contains(e.getX(), e.getY())) {
                        if (onEventClickListener != null) {
                            playSoundEffect(SoundEffectConstants.CLICK);
                            onEventClickListener.onEventClick(eventRect.event);
                            invalidate();
                        }
                        return true;
                    }
                }
            }
            return true;
        }
    };


    public ScheduleView(Context context) {
        this(context, null, 0);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ScheduleView, 0, 0);
        try {
            lineSize = a.getDimension(R.styleable.ScheduleView_lineSize, lineSize);
            lineColor = a.getColor(R.styleable.ScheduleView_lineColor, lineColor);
            columnNumber = a.getInteger(R.styleable.ScheduleView_columnNumber, columnNumber);
            rowHeight = a.getDimension(R.styleable.ScheduleView_rowHeight, rowHeight);
            bgColor = a.getColor(R.styleable.ScheduleView_bgColor, bgColor);
            sideLineColor = a.getColor(R.styleable.ScheduleView_bgColor, getResources().getColor(R.color.defaultGray));
            hourTextSize = a.getDimension(R.styleable.ScheduleView_hourTextSize, hourTextSize);
            hourTextColor = a.getColor(R.styleable.ScheduleView_hourTextColor, hourTextColor);
            hourTextBgColor = a.getColor(R.styleable.ScheduleView_hourTextBgColor, hourTextBgColor);
            currentTimeColor = a.getColor(R.styleable.ScheduleView_currentTimeColor, currentTimeColor);
            currentTimeTextColor = a.getColor(R.styleable.ScheduleView_currentTimeTextColor, currentTimeTextColor);
            currentTimeTextSize = a.getDimension(R.styleable.ScheduleView_currentTimeTextSize, currentTimeTextSize);
            eventTextSize = a.getDimension(R.styleable.ScheduleView_eventTextSize, eventTextSize);
            eventTextColor = a.getColor(R.styleable.ScheduleView_eventTextColor, eventTextColor);
            eventBgColor = a.getColor(R.styleable.ScheduleView_eventTextBgColor, eventBgColor);
            eventBgColor = a.getColor(R.styleable.ScheduleView_eventTextBgColor, eventBgColor);
            eventBgLineColor = a.getColor(R.styleable.ScheduleView_eventTextBgLineColor, eventBgLineColor);
        } finally {
            a.recycle();
        }
        if (columnNumber <= 0) {
            throw new IllegalArgumentException("columnNumber must > 0");
        }
        setMinimumHeight(getTotalHeight());
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        isFirst = true;
        /**
         * 初始化开始时间和结束时间，一般是一周
         */
        firstDay = Calendar.getInstance();
        /**
         * 这个值时周天表示本周的第一天，本view中使用周一表示第一天
         */
        int dayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "++++" + dayOfWeek);
        if (columnNumber > 1) {
            if (dayOfWeek == 1) {
                dayOfWeek = -6;
            } else {
                dayOfWeek = -(dayOfWeek - 2);
            }
            firstDay.add(Calendar.DAY_OF_WEEK, dayOfWeek);
        }
        firstDay.set(Calendar.HOUR_OF_DAY, 0);
        firstDay.set(Calendar.MINUTE, 0);
        firstDay.set(Calendar.SECOND, 0);
        firstDay.set(Calendar.MILLISECOND, 0);
        endDay = (Calendar) firstDay.clone();
        endDay.add(Calendar.DAY_OF_WEEK, columnNumber - 1);
        endDay.set(Calendar.HOUR_OF_DAY, 23);
        endDay.set(Calendar.MINUTE, 59);
        endDay.set(Calendar.SECOND, 59);
        firstDay.set(Calendar.MILLISECOND, 1000);

        /*
         * 初始化手势相关
         */
        gestureDetectorCompat = new GestureDetectorCompat(context, gestureListener);

        /*
         * 初始化分隔线画笔
         */
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setTextSize(lineSize);
        linePaint.setColor(lineColor);
        lineSize = linePaint.getTextSize();

        /*
         * 初始化时间轴画笔相关
         */
        hourTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourTextPaint.setTextSize(hourTextSize);
        hourTextPaint.setColor(hourTextColor);
        hourTextPaint.setTextAlign(Paint.Align.CENTER);
        hourTextWidth = hourTextPaint.measureText("024:00");
//        hourTextAmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        hourTextAmPaint.setTextAlign(Paint.Align.CENTER);
//        hourTextAmPaint.setTextSize(hourTextSize);
//        hourTextAmPaint.setColor(hourTextColor);

        /*
         * 初始化当前时间
         */
        currentTimePaint = new Paint();
        currentTimePaint.setAntiAlias(true);
        currentTimePaint.setTextSize(currentTimeTextSize);

        /*
         * 初始化event画笔
         */
        eventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        eventTextPaint.setColor(eventTextColor);
        eventTextPaint.setTextSize(eventTextSize);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "font/simkai.ttf");
        eventTextPaint.setTypeface(font);
        eventBgPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
         * 计算列的宽度
         */
        if (columnNumber == 1) {
            columnWidth = getWidth() - hourTextWidth - lineSize;
        } else {
            columnWidth = (getWidth() - hourTextWidth - columnNumber * lineSize) / columnNumber;
        }

//        drawPastTime(canvas);
        drawHour(canvas);
        drawLine(canvas);
        drawEvents(canvas);
    }

    /**
     * 画时间
     */
    private void drawHour(Canvas canvas) {
        for (int i = 0; i < 24; i++) {
            float y = i * rowHeight + (i + 1) * lineSize;
//            float y = i * rowHeight + (i + 1) * lineSize + originOffset.y;
            if (i == 0) {
                y += hourTextSize;
            } else {
                y += (hourTextSize / 2);
            }
            canvas.drawText("" + i + ":00", (float) (hourTextWidth * 0.8) / 2, y, hourTextPaint);
        }
    }

    /**
     * 重写画线的方法 不再按照小格子一根一根的短线来话
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        /**
         * 画固定的竖线
         */
        for (int i = 0; i < columnNumber; i++) {
            float startX = hourTextWidth + i * lineSize + i * columnWidth;
            float startY = 0;
            canvas.drawLine(startX, startY, startX, startY + getTotalHeight(), linePaint);
        }
        /**
         * 画24根横线,可根据自己需求改变循环次数来决定是否画
         */
        for (int i = 0; i < 24; i++) {
            float startX = 0;
            if (i > 0) {
                startX = hourTextWidth - hourTextWidth / 4 + 3;
            }
//            float startY = minAllDayEventShowHeight() + i * lineSize + i * rowHeight + originOffset.y;
            float startY = i * lineSize + i * rowHeight;
            canvas.drawLine(startX, startY, startX + getWidth(), startY, linePaint);
        }
    }

    private int getTotalHeight() {
        return ((int) ((rowHeight + lineSize) * 24) + 1);
    }

    private void drawEvents(Canvas canvas) {
        if (eventRectList != null && eventRectList.size() > 0) {
            for (int i = 0; i < eventRectList.size(); i++) {
                EventRect eventRect = eventRectList.get(i);
                RectF eventRectFFrame = new RectF(eventRect.rectF.left, eventRect.rectF.top, eventRect.rectF.right, eventRect.rectF.bottom);
                RectF eventRectF = new RectF(eventRect.rectF.left + frameWidth, eventRect.rectF.top + frameWidth, eventRect.rectF.right - frameWidth, eventRect.rectF.bottom - frameWidth);
                eventBgPaint.setColor(sideLineColor);
                canvas.drawRect(eventRectFFrame, eventBgPaint);
                eventBgPaint.setColor(eventRect.event.getColor() == 0 ? eventDefaultColor : eventRect.event.getColor());
                canvas.drawRect(eventRectF, eventBgPaint);
                //写课程信息
                drawText(eventRect.event.getC_name(), canvas, eventTextPaint, eventRectF, (int) (eventRectF.right - eventRectF.left), eventTextSize);
            }
        }
    }

    private void drawText(String text, Canvas canvas, TextPaint textPaint, RectF rectF, int width, float textSize) {
        Log.d(TAG, "drawText: " + text + width);
        final DynamicLayout.Builder builder = DynamicLayout.Builder.obtain(text, textPaint,
                width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setIncludePad(true);
        DynamicLayout dLayout = builder.build();
        if (dLayout.getHeight() > rectF.bottom - rectF.top) {
            int availableLineCount = (int) ((rectF.bottom - rectF.top) / (dLayout.getHeight() / dLayout.getLineCount()));
            int charLineCount = (int) (width / textSize);
            int availableWidth = (int) (availableLineCount * charLineCount * textSize);
            CharSequence charSequence = TextUtils.ellipsize(text, textPaint, availableWidth, TextUtils.TruncateAt.END);
            builder.setDisplayText(charSequence);
            dLayout = builder.build();
        }
        canvas.save();
        canvas.translate(rectF.left, rectF.top);
        dLayout.draw(canvas);
        canvas.restore();
    }

    private String getDataHour(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String str = format.format(new Date(timeStamp));
        return str;
    }

    /*
     * 传入events
     *
     * @param scheduleViewEventList
     */

    public void setEvents(List<Class_t> scheduleViewEventList) {
        eventRectList.clear();
        if (scheduleViewEventList != null && scheduleViewEventList.size() > 0) {
            getEventRectList(scheduleViewEventList);
            invalidate();
        }
    }

    public void getEventRectList(List<Class_t> scheduleViewEventList) {
        for (int i = 0; i < scheduleViewEventList.size(); i++)
            eventRectList.add(calculateEventRect(scheduleViewEventList.get(i)));
    }

    /**
     * 将分组了event计算出在页面的rect
     *
     * @param event
     */
    private EventRect calculateEventRect(Class_t event) {
        EventRect eventRect = new EventRect();
        eventRect.event = event;
        float width = (columnWidth + lineSize);
        int dayOfWeek = event.getC_day() - 1;
        eventRect.rectF.left = hourTextWidth + dayOfWeek * columnWidth + dayOfWeek * lineSize;
        eventRect.rectF.right = eventRect.rectF.left + width;
        /*
          计算的时候，和originOffset无关，只需要计算出在整个表格中的位置，然后在绘制的根据originOffset来确定当前的位置
          最后+allDayEventShowHeight的原因是因为顶格显示全天事件，图表以及事件都是从全天事件之下开始绘制，相当于其实坐标不再是0，而是allDayEventShowHeight
         */
        String[] startTime_tmp = event.getStartTime().split(":");
        String[] endTime_tmp = event.getEndTime().split(":");
        eventRect.rectF.top = Integer.parseInt(startTime_tmp[0]) * (rowHeight + lineSize) + rowHeight / 60 * Integer.parseInt(startTime_tmp[1]);
        eventRect.rectF.bottom = Integer.parseInt(endTime_tmp[0]) * (rowHeight + lineSize) + rowHeight / 60 * Integer.parseInt(endTime_tmp[1]);
        return eventRect;
    }


    /**
     * 将事件转换成图形上的坐标对象
     */
    private static class EventRect {
        public Class_t event;
        public RectF rectF = new RectF();

        public EventRect() {
        }

        public EventRect(Class_t event, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
        }
    }

    public float getHourTextWidth() {
        return hourTextWidth;
    }

    //触摸事件重载
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "onTouchEvent: "+event.toString());
        return gestureDetectorCompat.onTouchEvent(event);
    }

    public interface OnEventClickListener {
        void onEventClick(Class_t event);
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }
}