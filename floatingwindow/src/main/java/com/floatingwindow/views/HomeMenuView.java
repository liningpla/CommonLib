package com.floatingwindow.views;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;
/**
 * 自定义圆形的方向布局
 * @since 2014-06-07
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
public class HomeMenuView extends ViewGroup {
	public static float paintwidth = 120;//笔的宽度，圆环的宽度
	public static float angle = 0;//菜单第一和最后一个调整角度
	public static int height_size = 5;//height为屏幕高度的1/5
	public static int duration = 1000;//渐变动画时间
	
    private Context context;
    private int phone_height = 0;
    private int phone_width = 0;
    private float height = 0;//组件高度
    private float center_x = 0; //组件宽度的一半圆心x轴
    private float center_y = 0;//圆心y轴
    private float center_r = 0;//半径
    private int chlid_size = 0;//摆放子View的数量
    private float rcf_w = 0;//摆放子View矩形的宽度
    private float rcf_h = 0;//摆放子View矩形的高度
    private List<ChildPoint> childPoints = new ArrayList<ChildPoint>();
    private Paint paint = new Paint();
    private ViewGroup viewGroup;
    private ObjectAnimator upAnimator, downAnimator;
    private ViewPropertyAnimator childAnimator;
    private void getPhoneSize(){
    	DisplayMetrics dm = context.getResources().getDisplayMetrics();  
    	phone_height = dm.heightPixels;
    	phone_width = dm.widthPixels;
    }
	public HomeMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		getPhoneSize(); 
		viewGroup = this;
		viewGroup.setTag(true);
		viewGroup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				viewGroup.clearAnimation();
				if((Boolean)viewGroup.getTag() == true){
					downView();
					viewGroup.setTag(false);
				}else{
					upView();
					viewGroup.setTag(true);
				}
			}
		});
	}

	private void childAnimator(int i){
		viewGroup.getChildAt(i).clearAnimation();
		childAnimator = viewGroup.getChildAt(i).animate();
    	//透明度
		if((Boolean)viewGroup.getTag() == true){
			childAnimator.alpha(0.0f);
		}else{
			childAnimator.alpha(1.0f);
		}
		childAnimator.rotation(childPoints.get(i).getDegress());
		childAnimator.setDuration(duration);
		childAnimator.start();
	}
    /**
     * 隐藏
     * */
    public void downView(){
    	if(downAnimator == null){
    		downAnimator = ObjectAnimator.ofFloat(viewGroup, "translationY", 0f, height - paintwidth / 2);
    		downAnimator.setDuration(400);
    		downAnimator.addListener(new AnimatorListener() {
    			public void onAnimationStart(Animator animation) {
    				for(int i = 0; i < viewGroup.getChildCount(); i ++){
    					viewGroup.getChildAt(i).setClickable(false);
    					childAnimator(i);
    				}
    			}
    			public void onAnimationRepeat(Animator animation) {
    			}
    			public void onAnimationEnd(Animator animation) {
    			}
    			public void onAnimationCancel(Animator animation) {
    			}
    		});
    	}
		downAnimator.start();
    }
    public void upView(){
    	if(upAnimator == null){
    		upAnimator = ObjectAnimator.ofFloat(viewGroup, "translationY", height - paintwidth / 2, 0f);
    		upAnimator.setDuration(400);
    		upAnimator.addListener(new AnimatorListener() {
    			public void onAnimationStart(Animator animation) {
    				for(int i = 0; i < viewGroup.getChildCount(); i ++){
    					viewGroup.getChildAt(i).setClickable(true);
    					childAnimator(i);
    				}
    			}
    			public void onAnimationRepeat(Animator animation) {
    			}
    			public void onAnimationEnd(Animator animation) {
    			}
    			public void onAnimationCancel(Animator animation) {
    			}
    		});
    	}
    	upAnimator.start();
    }
    @Override  
    public LayoutParams generateLayoutParams(AttributeSet attrs)  
    {  
        return (LayoutParams) new MarginLayoutParams(getContext(), attrs);  
    } 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	/**设置View 的大小*/
    	setMeasuredDimension(phone_width, phone_height/height_size);
    	center_x = phone_width / 2;
        height = phone_height/height_size;
        center_r = (float) ((Math.pow(height,2) + Math.pow(center_x,2)) / (2*height) - paintwidth/2);
        center_y = center_r + paintwidth/2;//圆环的圆心

		Log.i("lining","-----center_x = "+center_x+"---center_r = "+center_r+" center_y =  "+center_y);
        computeRcf();
    }
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	drawOnclikColor(canvas);
    }
    /**
     * 绘制扇形
     * @param canvas 0xff014c4c
     */
    private void drawOnclikColor(Canvas canvas) {
        paint.setColor(0xff087d6d);
//        paint.setAlpha(100);
		paint.setAntiAlias(true); //消除锯齿
		paint.setStyle(Paint.Style.STROKE); //绘制空心圆 
        paint.setStrokeWidth(paintwidth);
        canvas.drawCircle(center_x, center_y , center_r, paint);
    }
    
    /**计算子View组件矩形的宽高和中心点坐标*/
    public void computeRcf(){
    	chlid_size = getChildCount();
		Log.i("lining","-----chlid_size = "+chlid_size);
        double B = Math.asin((center_y - height)/center_r);
        // 用角度表示的角
        B = Math.toDegrees(B);
		Log.i("lining","-----B  = "+B);
        double trueB = 180 - 2*B;
    	double degro = trueB /(2*chlid_size);

		Log.i("lining","----trueB  = "+trueB+" degro = "+degro);
    	rcf_w = (float) (2*(center_y - paintwidth) * Math.tan(Math.toRadians(degro)));
    	rcf_h = (float) ( (((center_y/(center_y - paintwidth)) - 1) * rcf_w) / (2 * Math.tan(Math.toRadians(degro))));
		Log.i("lining","----rcf_w  = "+rcf_w+" rcf_h = "+rcf_h);
        for(int i = 0; i < chlid_size; i ++){
         	double degress = (trueB /(2*chlid_size))*(2*i+1) + B;
         	if(i == 0){
         		degress = (trueB /(2*chlid_size))*(2*i+1) + B + angle;
         	}else if(i == chlid_size - 1){
         		degress = (trueB /(2*chlid_size))*(2*i+1) + B - angle;
         	}
             float chlid_x = (float) (center_x - center_r * Math.cos(Math.toRadians(degress)));
             float chlid_y = (float) (center_y - center_r * Math.sin(Math.toRadians(degress)));
             int roateDegress = (int) (degress  - 90);
             ChildPoint childPoint = new ChildPoint(chlid_x, chlid_y, i, roateDegress);
             if(!Float.isNaN(chlid_x) && !Float.isNaN(chlid_y)){
            	 if(!childPoints.contains(childPoint)){
            		 childPoints.add(childPoint);
            	 }
             }
			Log.i("lining","-----chlid_x = "+chlid_x+" chlid_y = "+chlid_y);
             getChildAt(i).measure((int)rcf_w, (int)rcf_h);
        }
    }
    
	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		final int childCount = getChildCount();
		if(childPoints.size() == 0){
			return;
		}
		int cWidth = 0;//子View的宽度
		int cHeight = 0;//子View的高度
		ChildPoint childPoint = null;
		int child_lift = 0;
		int child_top = 0;
		int child_right = 0;
		int child_bottom = 0;
		View childView;
		for(int i = 0; i< childCount; i ++){
			childView = getChildAt(i);  
			childPoint = childPoints.get(i);
			childView.setVisibility(View.VISIBLE);
            cWidth = childView.getMeasuredWidth();  
            cHeight = childView.getMeasuredHeight();  
            child_lift = (int) (childPoint.getChlid_x() - cWidth/2);
            child_top = (int) (childPoint.getChild_y() - cHeight/2);
            child_right = cWidth + child_lift;
            child_bottom = child_top + cHeight; 
            //View的宽 = r - l。View的高 = b - t。
            childView.layout(child_lift, child_top, child_right, child_bottom);
    		childAnimator = childView.animate();
    		childAnimator.rotation(childPoint.getDegress());
    		childAnimator.setDuration(1);
    		childAnimator.start();
		}
	}
}
