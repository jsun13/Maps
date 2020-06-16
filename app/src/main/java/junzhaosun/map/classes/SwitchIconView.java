package junzhaosun.map.classes;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Region;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import junzhaosun.map.R;


public class SwitchIconView extends LinearLayout {
    private static final int DEFAULT_ANIMATION_DURATION = 300;
    private static final float DEFAULT_DISABLED_ALPHA = .3f;

    //子控件
    private TextView mtext;
    private ImageView mImage;

    //declare attributes
    private int iconTintColor;
    private int animationDuration;
    private float disabledStateAlpha;
    private int disabledStateColor;
    private float fraction = 0f;//used for switch state
    private String text;
    private int src;

    //enable?
    private boolean enable=false;

    //为啥要写三个构造函数？
    public SwitchIconView(@NonNull Context context) {
        this(context, null);
    }

    public SwitchIconView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.switch_view,this);
        mtext=findViewById(R.id.mTextView);
        mImage=findViewById(R.id.mImageView);

        //apply attributes
        TypedArray array=getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SwitchIconView, 0, 0);
        try {
            iconTintColor = array.getColor(R.styleable.SwitchIconView_si_tint_color, Color.RED);
            animationDuration = array.getInteger(R.styleable.SwitchIconView_si_animation_duration, DEFAULT_ANIMATION_DURATION);
            disabledStateAlpha = array.getFloat(R.styleable.SwitchIconView_si_disabled_alpha, DEFAULT_DISABLED_ALPHA);
            disabledStateColor = array.getColor(R.styleable.SwitchIconView_si_disabled_color, iconTintColor);
            text=array.getString(R.styleable.SwitchIconView_si_text);
            src=array.getResourceId(R.styleable.SwitchIconView_si_src,-1);
        } finally {
            array.recycle();
        }

        mtext.setText(text);
        if(src!=-1){
            mImage.setImageResource(src);
        }

        setColor(disabledStateColor);
        set_Alpha(disabledStateAlpha);
    }
    public void setEnabled(boolean enable){
        if(this.enable == enable) return;
        this.enable=enable;
        this.fraction=enable? 0f: 1f;
        setColor(enable? iconTintColor: disabledStateColor);
        animateToFraction(enable? 1f: disabledStateAlpha);
    }

    public boolean isEnabled(){
        return enable;
    }

    private void setColor(int color){
        mtext.setTextColor(color);
        mImage.setColorFilter(color);
    }

    private void set_Alpha(float alpha){
        mtext.setAlpha(alpha);
        mImage.setAlpha(alpha);
    }

    private void animateToFraction(float toFraction) {
        ValueAnimator animator = ValueAnimator.ofFloat(fraction, toFraction);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                set_Alpha((float) animation.getAnimatedValue());
                postInvalidateOnAnimationCompat();
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(animationDuration);
        animator.start();
    }

    private void postInvalidateOnAnimationCompat() {
        final long fakeFrameTime = 10;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            postInvalidateOnAnimation();
        } else {
            postInvalidateDelayed(fakeFrameTime);
        }
    }
}
