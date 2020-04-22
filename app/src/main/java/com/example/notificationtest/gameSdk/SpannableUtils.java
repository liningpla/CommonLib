package com.example.notificationtest.gameSdk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

import androidx.core.content.ContextCompat;

import com.example.notificationtest.httplib.HiLog;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 富文本操作工具类
 * 支持图文混合，颜色改变等
 */
public class SpannableUtils {

    /**
     * 文字签名添加图片
     *
     * @param text        文字信息
     * @param drawableId  图片id
     * @param imageWeidth 图片显示宽度
     * @param imageHeight 图片显示高度
     */
    public static SpannableStringBuilder imageToTextHead(Context context, String text, int drawableId, int imageWeidth, int imageHeight) {
        Drawable image = ContextCompat.getDrawable(context, drawableId);
        image.setBounds(0, 0, imageWeidth, imageHeight);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        SpannableStringBuilder ss = new SpannableStringBuilder(" ");
        ss.setSpan(imageSpan, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.append(" " + text);
        return ss;
    }

    /**
     * 改变文本信息中字符颜色
     * @param originalStr 原始数据字符
     * @param changeInfo 需要改变信息的键值对  key是需要改变的颜色 value是需要改变的颜色“#FFFFFF”
     */
    public static SpannableStringBuilder textColorChange(String originalStr, Map<String, String> changeInfo) {
        SpannableStringBuilder builder = new SpannableStringBuilder(originalStr);
        try{
            Set<Map.Entry<String, String>> entrySet = changeInfo.entrySet();
            Iterator<Map.Entry<String, String>> iter = entrySet.iterator();
            while (iter.hasNext())
            {
                Map.Entry<String, String> entry = iter.next();
                String changeStr = entry.getKey();
                String changeColor = entry.getValue();
                String[] splits = originalStr.split(changeStr);
                if(splits.length > 0){
                    int startIndex = splits[0].length();
                    int endIndex = startIndex + changeStr.length();
                    ForegroundColorSpan buleSpan = new ForegroundColorSpan(Color.parseColor(changeColor));
                    builder.setSpan(buleSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return builder;
    }

    /**字符串format函数
     * @param stringId 目标字符串
     * @param args 需要替换的字符串
     * */
    public static String formatStr(Context context, int stringId, String... args){
        try {
            String formatstr = "";
            for(int i= 0; i < args.length; i ++){
                formatstr = String.format(context.getResources().getString(stringId), args[i]);
            }
            return formatstr;
        }catch (Exception e){
            return context.getResources().getString(stringId);
        }
    }

}
