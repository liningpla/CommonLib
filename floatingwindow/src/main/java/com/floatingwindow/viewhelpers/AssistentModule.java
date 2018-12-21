package com.floatingwindow.viewhelpers;

import android.content.Context;

import com.common.log.SDLog;
import com.common.utils.ScreenUtil;
import com.floatingwindow.R;

import java.util.ArrayList;
import java.util.List;

/**游戏助手悬浮数据模型*/
public class AssistentModule {

    public static final int LOCATION_RIGHT = 1;//位置右
    public static final String RIGHT = "right";//位置右

    public static final int LOCATION_RIGHT_TOP = 2;//位置右上方
    public static final String RIGHT_TOP = "right_top";//位置右上方

    public static final int LOCATION_RIGHT_BOTTOM = 3;//位置右下方
    public static final String RIGHT_BOTTOM = "right_bottom";//位置右下方

    public static final int LOCATION_LEFT = 4;//位置左
    public static final String LEFT = "LEFT";//位置左

    public static final int LOCATION_LEFT_TOP = 5;
    public static final String LEFT_TOP = "left_top";//位置左上

    public static final int LOCATION_LEFT_BOTTOM = 6;
    public static final String LEFT_BOTTOM = "left_bottom";//位置左下方

    public static final int LOCATION_TOP = 7;
    public static final String TOP = "top";//位置上方

    public static final int LOCATION_BOTTOM = 8;
    public static final String BOTTOM = "bottom";//位置下方

    public List<APoint> currChilds = new ArrayList<>();//当前子View点位

    public APoint initPoints(Context context, boolean isPortrait, int mainW, int mainX, int mainY, int childW, int sapceR){
        int barHight = isPortrait?ScreenUtil.getStatusBarHeight(context):0;
        SDLog.i(AssistentHelper.UU_TAG," isPortrait:" + isPortrait+" mainW:"+ mainW+" mainX:"+mainX+" mainY:"+mainY+" childW:"+childW+" sapceR:"+sapceR);
        APoint aPoint = new APoint();
        aPoint.startX = mainX;
        aPoint.startY = mainY;
        int Sx = (isPortrait?ScreenUtil.getScreenWidth(context)/2:ScreenUtil.getScreenHeight(context)/2) - childW/2;//屏幕圆心x
        int Sy = isPortrait?ScreenUtil.getScreenHeight(context)/2:ScreenUtil.getScreenWidth(context)/2;//屏幕圆心y
        int location = LOCATION_LEFT;//默认位置左边
        SDLog.i(AssistentHelper.UU_TAG," getStatusBarHeight:"+ScreenUtil.getStatusBarHeight(context)+" Sx:"+Sx+"  Sy:"+Sy);
        if(mainX < Sx && mainY < Sy){//左上区域
            int leftSpace = mainX;//左边距
            int topSpace = mainY;// 上边距
            if(leftSpace < topSpace){//左边
                location = LOCATION_LEFT;
                aPoint.pointX = mainX;
                aPoint.pointY = (mainY < (sapceR+barHight))?(sapceR+barHight):mainY;
                SDLog.i(AssistentHelper.UU_TAG,  "左上区域--左边");
            }else{//上边
                location = LOCATION_TOP;
                aPoint.pointX = (mainX < sapceR)?sapceR:mainX;
                aPoint.pointY = mainY + barHight;
                SDLog.i(AssistentHelper.UU_TAG,  "左上区域--上边");
            }
        }
        if(mainX < Sx && mainY > Sy){//左下区域
            int leftSpace = mainX;//左边距
            int botoomSpace = 2 * Sy - mainY;// 下边距
            if(leftSpace < botoomSpace){//左边
                location = LOCATION_LEFT;
                aPoint.pointX = mainX;
                aPoint.pointY = (mainY > (2*Sy - sapceR))?(2*Sy - sapceR):mainY;
                SDLog.i(AssistentHelper.UU_TAG, "左下区域--左边");
            }else{//下边
                location = LOCATION_BOTTOM;
                aPoint.pointX = (mainX < sapceR)?sapceR:mainX;
                aPoint.pointY = mainY;
                SDLog.i(AssistentHelper.UU_TAG, "左下区域--下边");
            }
        }
        if(mainX > Sx && mainY < Sy){//右上区域
            int rightSpace = 2*Sx - mainX;//右边距
            int topSpace = mainY;// 上边距
            if(topSpace > rightSpace){//右边
                location = LOCATION_RIGHT;
                aPoint.pointX = mainX;
                aPoint.pointY = (mainY < (sapceR+childW/2))?(sapceR+childW/2):mainY;
                SDLog.i(AssistentHelper.UU_TAG, "右上区域--右边");
            }else{//上边
                location = LOCATION_TOP;
                aPoint.pointX = (mainX > (2*Sx - sapceR))?(2*Sx - sapceR):mainX;
                aPoint.pointY = mainY + barHight;
                SDLog.i(AssistentHelper.UU_TAG, "右上区域--上边");
            }
        }
        if(mainX > Sx && mainY > Sy){//右下区域
            int rightSpace = 2*Sx - mainX;//右边距
            int botoomSpace = 2*Sy - mainY;// 下边距
            if(botoomSpace > rightSpace){//右边
                location = LOCATION_RIGHT;
                aPoint.pointX = mainX;
                aPoint.pointY = (mainY > (2*Sy - sapceR))?(2*Sy - sapceR):mainY;
                SDLog.i(AssistentHelper.UU_TAG, "右下区域--右边");
            }else{//下边
                location = LOCATION_BOTTOM;
                aPoint.pointX = (mainX > (2*Sx - sapceR))?(2*Sx - sapceR):mainX;
                aPoint.pointY = mainY;
                SDLog.i(AssistentHelper.UU_TAG,  "右下区域--下边");
            }
        }
        aPoint.location = location;
        countPortraitApoints(location, mainW, aPoint.pointX, aPoint.pointY, childW, sapceR);
        SDLog.i(AssistentHelper.UU_TAG, "currChilds.size():"+currChilds.size()+" aPoint.pointX = "+aPoint.pointX+" aPoint.pointY = "+aPoint.pointY);
        return aPoint;
    }

    /**计算点位信息，这里主，子两窗都是正方形
     * @param location 位置类型
     * @param mainW 主窗宽，高大小
     * @param mainX 主窗X点
     * @param mainY 主窗Y点
     * @param childW 子窗宽，高
     * @param sapceR 主距离子的半径距离
     * */
    private void countPortraitApoints(int location, int mainW, int mainX, int mainY, int childW, int sapceR){
        int Cx = mainW/2 + mainX - childW/2;//原始圆点X
        int Cy = mainY + mainW/2 - childW/2;//原始圆点Y
        int sinR = (int) ((Math.sin(40.00)*(double) sapceR) * 0.93);
        APoint aPoint1 = new APoint();
        aPoint1.setResPoint(0, R.drawable.ic_launcher, "录屏");
        APoint aPoint2 = new APoint();
        aPoint2.setResPoint(1, R.drawable.ic_launcher, "截图");
        APoint aPoint3 = new APoint();
        aPoint3.setResPoint(2, R.drawable.ic_launcher, "加速");
        APoint aPoint4 = new APoint();
        aPoint4.setResPoint(3, R.drawable.ic_launcher, "福利");
        APoint aPoint5 = new APoint();
        aPoint5.setResPoint(4, R.drawable.ic_launcher, "聊天");
        currChilds.clear();
        currChilds.add(aPoint1);
        currChilds.add(aPoint2);
        currChilds.add(aPoint3);
        currChilds.add(aPoint4);
        currChilds.add(aPoint5);
        switch (location){
            case LOCATION_RIGHT://位置右 - 左状态顺时针旋转180度
                aPoint1.setAPoint(Cx, Cy - sapceR);
                aPoint2.setAPoint(Cx - sinR, Cy - sinR);
                aPoint3.setAPoint(Cx - sapceR, Cy);
                aPoint4.setAPoint(Cx - sinR, Cy + sinR);
                aPoint5.setAPoint(Cx, Cy + sapceR);
                break;
            case LOCATION_RIGHT_TOP://位置右上方
                break;
            case LOCATION_RIGHT_BOTTOM://位置右下方
                break;
            case LOCATION_LEFT://位置左
                SDLog.i(AssistentHelper.UU_TAG, "Cx :"+Cx+" Cy:"+Cy+" sinR:"+sinR+" sapceR:"+sapceR);
                aPoint1.setAPoint(Cx, Cy - sapceR);
                aPoint2.setAPoint(Cx + sinR, Cy - sinR);
                aPoint3.setAPoint(Cx + sapceR, Cy);
                aPoint4.setAPoint(Cx + sinR, Cy + sinR);
                aPoint5.setAPoint(Cx, Cy + sapceR);
                break;
            case LOCATION_LEFT_TOP://位置左上
                break;
            case LOCATION_LEFT_BOTTOM://位置左下方
                break;
            case LOCATION_TOP://位置上方
                aPoint1.setAPoint(Cx + sapceR, Cy);
                aPoint2.setAPoint(Cx + sinR, Cy + sinR);
                aPoint3.setAPoint(Cx, Cy + sapceR);
                aPoint4.setAPoint(Cx - sinR, Cy + sinR);
                aPoint5.setAPoint(Cx - sapceR, Cy);
                break;
            case LOCATION_BOTTOM://位置下方
                aPoint1.setAPoint(Cx - sapceR, Cy);
                aPoint2.setAPoint(Cx - sinR, Cy - sinR);
                aPoint3.setAPoint(Cx, Cy - sapceR);
                aPoint4.setAPoint(Cx + sinR, Cy - sinR);
                aPoint5.setAPoint(Cx + sapceR, Cy);
                break;
        }
    }

    /**悬浮窗口位置信息类*/
    public static class APoint{
        public int pointX;//目标x : 主窗和子窗的目标X
        public int pointY;//目标y : 主窗和子窗的目标Y

        public int startX;//起始x : 主窗起始X
        public int startY;//起始y : 主窗起始Y

        public int location;//当前主窗位置类型对应上，下，左，右

        public int positon;//标记位置
        public int resId;//资源图片id
        public String text;


        public APoint() {}

        public void setAPoint(int pointX, int pointY) {
            this.pointX = pointX;
            this.pointY = pointY;
        }

        public void setResPoint(int positon, int resId, String text) {
            this.positon = positon;
            this.resId = resId;
            this.text = text;
        }
    }

}
