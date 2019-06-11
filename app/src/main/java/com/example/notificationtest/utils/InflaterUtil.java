package com.example.notificationtest.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.notificationtest.httplib.HiLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class InflaterUtil {

    public static void buildXml(){
        try{
            String strXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<android.support.constraint.ConstraintLayout\n" +
                    "    xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                    "    android:layout_width=\"match_parent\"\n" +
                    "    android:layout_height=\"match_parent\"\n" +
                    "    android:background=\"@color/colorPrimary\"\n" +
                    "    >\n" +
                    "    <TextView\n" +
                    "        android:id=\"@+id/tv_content\"\n" +
                    "        android:layout_width=\"wrap_content\"\n" +
                    "        android:layout_height=\"wrap_content\"\n" +
                    "        android:text=\"Hello World!\"\n" +
                    "        app:layout_constraintBottom_toBottomOf=\"parent\"\n" +
                    "        app:layout_constraintLeft_toLeftOf=\"parent\"\n" +
                    "        app:layout_constraintRight_toRightOf=\"parent\"\n" +
                    "        app:layout_constraintTop_toTopOf=\"parent\" />\n" +
                    "</android.support.constraint.ConstraintLayout>";
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader(strXml ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    HiLog.i("Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    HiLog.i("Start tag "+xpp.getName());
                } else if(eventType == XmlPullParser.END_TAG) {
                    HiLog.i("End tag "+xpp.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    HiLog.i("Text "+xpp.getText());
                }
                eventType = xpp.next();
            }
            HiLog.i("End document");
        }catch (Exception e){
            HiLog.i(""+e.getMessage());
        }
    }


    public static void buildXml(Context context, ViewGroup root){
        try{
            String strXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<FrameLayout layout_width=\"match_parent\" layout_height=\"101dp\" background=\"#ffffff\"> \n" +
                    "  <action type=\"MimoAction\" deeplink_url=\"@JsonData:adInfos[0]:deeplink\" target_type=\"@JsonData:adInfos[0]:targetType\" landing_page_url=\"@JsonData:adInfos[0]:landingPageUrl\" action_url=\"@JsonData:adInfos[0]:actionUrl\" is_pop_up_download=\"@JsonData:adInfos[0]:sdkAdDetail:isPopUpDownload\" validation_info=\"@JsonData:adInfos[0]:sdkAdDetail:validationInfo\" is_aa=\"@JsonData:adInfos[0]:sdkAdDetail:isAA\" package_name=\"@JsonData:adInfos[0]:packageName\"/>  \n" +
                    "  <RelativeLayout layout_width=\"match_parent\" layout_height=\"73dp\" layout_gravity=\"center\" layout_marginLeft=\"13dp\" layout_marginRight=\"13dp\" background=\"#f7f7f7\"> \n" +
                    "    <TextView id=\"@+id/summary\" layout_width=\"193dp\" layout_height=\"wrap_content\" layout_marginLeft=\"10dp\" layout_marginTop=\"8dp\" ellipsize=\"end\" maxLines=\"2\" text=\"@JsonData:adInfos[0]:summary\" textColor=\"#000000\" textSize=\"14sp\"/>  \n" +
                    "    <TextView id=\"@+id/dsp\" layout_width=\"wrap_content\" layout_height=\"15dp\" layout_alignParentBottom=\"true\" layout_alignParentLeft=\"true\" layout_marginBottom=\"10dp\" layout_marginLeft=\"10dp\" background=\"@LocalData:@drawable/banner_dsp_background\" gravity=\"center\" paddingLeft=\"4dp\" paddingRight=\"4dp\" text=\"@JsonData:adInfos[0]:adMark\" textSize=\"9sp\" textColor=\"#000000\" alpha=\"0.5\"/>  \n" +
                    "    <ImageView id=\"@+id/picture\" layout_width=\"110dp\" layout_height=\"match_parent\" layout_alignParentRight=\"true\" background=\"@JsonData:adInfos[0]:assets[0]:url\"/>  \n" +
                    "    <ImageView id=\"@+id/close\" layout_width=\"20dp\" layout_height=\"13dp\" layout_alignParentBottom=\"true\" layout_marginBottom=\"10dp\" layout_marginRight=\"10dp\" layout_toLeftOf=\"@id/picture\" background=\"@LocalData:@drawable/news_feed_close_bg\"> \n" +
                    "      <action type=\"CloseAdAction\" is_using_feedback=\"false\"/> \n" +
                    "    </ImageView> \n" +
                    "  </RelativeLayout> \n" +
                    "</FrameLayout>\n" +
                    "</FrameLayout>";
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(strXml));

//            int eventType = xpp.getEventType();
//            while (eventType != XmlPullParser.END_DOCUMENT) {
////                if(eventType == XmlPullParser.START_DOCUMENT) {
////                    HiLog.i("Start document");
////                } else if(eventType == XmlPullParser.START_TAG) {
////                    HiLog.i("Start tag "+xpp.getName());
////                } else if(eventType == XmlPullParser.END_TAG) {
////                    HiLog.i("End tag "+xpp.getName());
////                } else if(eventType == XmlPullParser.TEXT) {
////                    HiLog.i("Text "+xpp.getText());
////                }
//                ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(xpp, root, false);
//                ViewGroup viewGroup1 = (ViewGroup) viewGroup.getChildAt(0);
//                TextView textView = (TextView) viewGroup1.getChildAt(0);
//                HiLog.i("viewGroup "+textView.getText());
//
//
//                eventType = xpp.next();
//            }
//            HiLog.i("End document");

//
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(xpp, root, false);

            ViewGroup viewGroup1 = (ViewGroup) viewGroup.getChildAt(0);
            TextView textView = (TextView) viewGroup1.getChildAt(0);
            HiLog.i("viewGroup "+textView.getText());

        }catch (Exception e){
            HiLog.i(""+e.getMessage());
        }
    }
}
