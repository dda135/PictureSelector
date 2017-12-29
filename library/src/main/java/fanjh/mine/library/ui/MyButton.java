package fanjh.mine.library.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import fanjh.mine.library.R;


/**
 * Created by fanjh on 2016/11/17.
 * 需要使用按压蒙层效果的Button
 */
public class MyButton extends View {
    private int secondTextColor;
    private String secondText;
    private int secondTextSize;
    private int disabledTextColor;
    private int disabledBackgroundColor;
    private int radius;
    private int borderWidth;
    private int borderColor;
    private int layersColor;
    private int disableLayersColor;
    private int normalTextColor;
    private int normalBackgroundColor;
    private String text;
    private int textSize;
    public static final int TYPE_DOWN = 0;
    public static final int TYPE_NORMAL = 1;
    private int type = TYPE_NORMAL;
    private Paint paint;
    private TextPaint textPaint;
    private RectF rectF;
    private RectF borderRectF;
    private int textColor;
    private int roundColor;
    int textWidth;
    private Rect textBounds = new Rect();
    private Rect secondTextBounds = new Rect();
    private OnClickListener onClickListener;
    private int normal_padding = 16;
    private int normal_delay_time = 800;//ms
    private boolean startMoved;
    private int touchSlop;
    private ViewConfiguration viewConfiguration;
    private OnLongClickListener onLongClickListener;
    private int mLastX;
    private int mLastY;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    consumeLongClick = callLongClick();
                    break;
            }
        }
    };

    private boolean consumeLongClick;

    @Override
    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
        setLongClickable(true);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        setClickable(true);
    }

    public void setTextSize(int textSize) {
        this.textSize = (int) (textSize * getResources().getDisplayMetrics().scaledDensity);
        requestLayout();
    }

    public MyButton(Context context) {
        this(context,null);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyButton);
        secondTextColor = array.getColor(R.styleable.MyButton_mybutton_second_text_color,0);
        secondText = array.getString(R.styleable.MyButton_mybutton_second_text);
        secondTextSize = array.getDimensionPixelOffset(R.styleable.MyButton_mybutton_second_text_size,12);
        disabledTextColor = array.getColor(R.styleable.MyButton_disabledTextColor,0);
        disabledBackgroundColor = array.getColor(R.styleable.MyButton_disabledBackgroundColor,0);
        radius = array.getDimensionPixelOffset(R.styleable.MyButton_radius,0);
        layersColor = array.getColor(R.styleable.MyButton_layersColor,0);
        disableLayersColor = array.getColor(R.styleable.MyButton_disableLayersColor,0);
        normalTextColor = array.getColor(R.styleable.MyButton_normalTextColor,0);
        borderColor = array.getColor(R.styleable.MyButton_mybutton_border_color,0);
        normalBackgroundColor = array.getColor(R.styleable.MyButton_normalBackgroundColor,0);
        text = array.getString(R.styleable.MyButton_text);
        if(null == text){
            text = "";
        }
        textSize = array.getDimensionPixelOffset(R.styleable.MyButton_textSize,16);
        setEnabled(array.getBoolean(R.styleable.MyButton_isEnabled,true));
        normal_padding = array.getDimensionPixelOffset(R.styleable.MyButton_vertical_padding,16);
        borderWidth = array.getDimensionPixelOffset(R.styleable.MyButton_mybutton_border_width,0);
        array.recycle();
        rectF = new RectF();
        borderRectF = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        textPaint = new TextPaint();
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        normal_padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,32,getResources().getDisplayMetrics());
        viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled())
            return false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (MotionEventCompat.getActionMasked(event)){
            case MotionEvent.ACTION_DOWN:
                consumeLongClick = false;
                startMoved = false;
                if(isLongClickable())
                    handler.sendEmptyMessageDelayed(0,normal_delay_time);
                setDown();
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                setNormal();
                consumeLongClick = false;
                startMoved = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(startMoved){
                    int y1 = (int) event.getRawY();
                    int x1 = (int) event.getRawX();
                    int pos[] = new int[2];
                    getLocationOnScreen(pos);
                    if(x1 < pos[0] || x1 > pos[0]+getWidth() || y1 < pos[1] || y1 > pos[1] + getHeight()){
                        setNormal();
                    }else{
                        setDown();
                    }
                }else {
                    if(Math.abs(x-mLastX) > touchSlop || Math.abs(y-mLastY) > touchSlop) {
                        startMoved = true;
                        handler.removeCallbacksAndMessages(null);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacksAndMessages(null);
                if(!consumeLongClick)
                    callClick();
                consumeLongClick = false;
                setNormal();
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }


    private void callClick(){
        if(isClickable() && null != onClickListener && type == TYPE_DOWN){
            onClickListener.onClick(this);
        }
    }

    private boolean callLongClick(){
        return null != onLongClickListener && type == TYPE_DOWN && onLongClickListener.onLongClick(this);
    }

    private void setDown(){
        if(type != TYPE_DOWN){
            type = TYPE_DOWN;
            invalidate();
        }
    }

    private void setNormal(){
        if(type != TYPE_NORMAL){
            type = TYPE_NORMAL;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        textPaint.setTextSize(textSize);
        textPaint.getTextBounds(text,0,text.length(),textBounds);
        if(!TextUtils.isEmpty(secondText)){
            textPaint.setTextSize(secondTextSize);
            textPaint.getTextBounds(secondText,0,secondText.length(),secondTextBounds);
        }
        if(MeasureSpec.EXACTLY != widthMode){
            int totalWidth = Math.max(textBounds.width() + normal_padding,secondTextBounds.width());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY);
        }
        if(MeasureSpec.EXACTLY != heightMode) {
            int totalHeight = (int) (textBounds.height() + secondTextBounds.height() + 16 * getResources().getDisplayMetrics().density);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY);
        }else {
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (textBounds.height() + secondTextBounds.height() > heightSize){
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (textBounds.height() + secondTextBounds.height() + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getResources().getDisplayMetrics())), MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        rectF.set(0,0,getWidth(),getHeight());
        //针对一些特殊的情况，需要圈定范围
        if(radius > getWidth()/2 || radius > getHeight()/2){
            radius = Math.min(getWidth()/2,getHeight()/2);
        }
        if(!isEnabled()){
            roundColor = disabledBackgroundColor;
            textColor = disabledTextColor;
        }else {
            switch (type) {
                case TYPE_NORMAL:
                case TYPE_DOWN:
                    roundColor = normalBackgroundColor;
                    textColor = normalTextColor;
                    break;
            }
        }
        paint.setColor(roundColor);
        canvas.drawRoundRect(rectF,radius,radius,paint);
        if(borderWidth != 0){
            borderRectF.set(borderWidth/2,borderWidth/2,getWidth()-borderWidth/2,getHeight()-borderWidth/2);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderWidth);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(borderColor);
            canvas.drawRoundRect(borderRectF,radius,radius,paint);
            paint.setStyle(Paint.Style.FILL);
        }
        if(TextUtils.isEmpty(secondText)) {
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            Paint.FontMetrics metrics = textPaint.getFontMetrics();
            textWidth = (int) textPaint.measureText(text);
            canvas.drawText(text, (getWidth() - textWidth) >> 1, ((int) (getHeight() - metrics.ascent - metrics.descent)) >> 1, textPaint);
        }else{
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            Paint.FontMetrics metrics = textPaint.getFontMetrics();
            textPaint.setColor(secondTextColor);
            textPaint.setTextSize(secondTextSize);
            Paint.FontMetrics secondMetrics = textPaint.getFontMetrics();

            int topPadding = ((int)(getHeight() - (metrics.bottom - metrics.top) - (secondMetrics.bottom - secondMetrics.top)) >> 1);

            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            textWidth = (int) textPaint.measureText(text);
            canvas.drawText(text, (getWidth() - textWidth) >> 1, topPadding - metrics.top, textPaint);

            textPaint.setColor(secondTextColor);
            textPaint.setTextSize(secondTextSize);
            textWidth = (int) textPaint.measureText(secondText);
            canvas.drawText(secondText, (getWidth() - textWidth) >> 1, getHeight() - topPadding - secondMetrics.bottom, textPaint);
        }
        if(type == TYPE_DOWN){
            paint.setColor(layersColor);
            canvas.drawRoundRect(rectF,radius,radius,paint);
        }
        if(!isEnabled()){
            paint.setColor(disableLayersColor);
            canvas.drawRoundRect(rectF,radius,radius,paint);
        }
    }

    /**
     * 设置文字
     *
     * @param text          文字
     * @param size          文字大小，单位px，小于0时不设置
     * @param normalColor   文字颜色，等于0时不设置
     */
    public void setText(String text, int size, int normalColor) {
        if (null != text)
            this.text = text;
        if (size >= 0)
            this.textSize = size;
        if (normalColor != 0)
            this.normalTextColor = normalColor;
        requestLayout();
        invalidate();
    }

    public void setSecondText(String text, int size, int color){
        this.secondText = text;
        this.secondTextSize = size;
        this.secondTextColor = color;
        requestLayout();
        invalidate();
    }

    /**
     * 设置边框
     *
     * @param color 颜色，等于0时不设置
     * @param width 宽度，小于0时不设置
     */
    public void setBorder(int color, int width) {
        if (color != 0)
            this.borderColor = color;
        if (width > 0)
            this.borderWidth = width;
        requestLayout();
    }

    public String getText() {
        return null == text ? "" : text;
    }
}
