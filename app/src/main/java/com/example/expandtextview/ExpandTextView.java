package com.example.expandtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**自定义可展开的textView
 * Created by clement on 16/12/12.
 */

public class ExpandTextView extends LinearLayout {
    //显示内容的textView
    protected TextView contentView;
    //点击展开收缩的ImageView
    protected ImageView expandView;

    //设置默认属性
    private final int defaultTextColor = Color.BLACK;
    private final int defaultTextSize = 12;
    private final int defaultMaxLine = 3;
    private final int defaultDurationMillis = 300;

    //保存styleable中的属性
    protected int textColor = defaultTextColor;
    protected int textSize = defaultTextSize;
    protected int maxLine = defaultMaxLine;
    protected String text;
    protected int durationMillis = defaultDurationMillis;
    protected String imageGravity;

    //保存当前的状态，展开还是折叠
    private boolean isExpand = false;

    //为动画添加插值器
    private Interpolator interpolator = new AccelerateInterpolator();

    public ExpandTextView(Context context) {
        this(context,null);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        init(context,attrs);
    }
    private void init(Context context,AttributeSet attrs){
        initAttrs(context,attrs);
        initView(context);
    }

    /**
     * 初始化view
     */
    private void initView(Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_expandable,this,true);
        contentView = (TextView)rootView.findViewById(R.id.contentView);
        expandView = (ImageView)rootView.findViewById(R.id.expandView);
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(view);
            }
        });
        expandView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(view);
            }
        });
        //设置显示内容
        contentView.setText(text);
        contentView.setTextColor(textColor);
        contentView.setTextSize(textSize);
        //设置当前高度
        contentView.setHeight(contentView.getLineHeight()*maxLine);
        //是否应该显示expandView
        contentView.post(new Runnable() {
            @Override
            public void run() {
                expandView.setVisibility(contentView.getLineCount()>maxLine?View.VISIBLE:View.GONE);
            }
        });
    }
    private void initAttrs(Context context,AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.ExpandTextView);
        textColor = array.getColor(R.styleable.ExpandTextView_textColor,defaultTextColor);
        textSize = array.getInteger(R.styleable.ExpandTextView_textSize,defaultTextSize);
        maxLine = array.getInteger(R.styleable.ExpandTextView_maxLine,defaultMaxLine);
        text = array.getString(R.styleable.ExpandTextView_text);
        durationMillis = array.getInteger(R.styleable.ExpandTextView_duration,defaultDurationMillis);
        //回收释放
        array.recycle();
    }

    /**
     * 执行点击事件
     * @param view
     */
    private void doClick(View view){
        //切换状态
        isExpand = !isExpand;
        //移除动画效果
        expandView.clearAnimation();
        final int deltaValue ;
        final int startValue = contentView.getHeight();
        RotateAnimation rotateAnimation;
        //ImageView的动画
        if(isExpand){
            deltaValue = contentView.getLineHeight()*contentView.getLineCount()-startValue;
            //执行image旋转动画
            rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        else{
            deltaValue = contentView.getLineHeight()*maxLine-startValue;
            rotateAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rotateAnimation.setDuration(durationMillis);
        rotateAnimation.setFillAfter(true);
        expandView.startAnimation(rotateAnimation);
        //TextView的动画
        Animation animation = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //根据ImageView旋转动画的百分比来显示textView高度，达到动画效果
                contentView.setHeight((int) (startValue + deltaValue * interpolatedTime));
            }

        };
        //添加插值器
        if(interpolator!=null){
            animation.setInterpolator(interpolator);
        }
        animation.setDuration(durationMillis);
        contentView.startAnimation(animation);
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }
}
