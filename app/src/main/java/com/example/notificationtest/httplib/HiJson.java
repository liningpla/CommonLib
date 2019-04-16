package com.example.notificationtest.httplib;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HiJson {

    private static final String TAG = HiHttp.TAG;

    /**
     * 解析JSON字符串到具体类
     *
     * @param clazz 和JSON对象对应的类的Class，必须拥有setXxx()函数，其中xxx为属性
     * @param json  被解析的JSON字符串
     * @return 返回传入的Object对象实例
     */
    public static <T> T jsonObject(Class<T> clazz, String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            HiLog.d(TAG, "method jsonObject" + e.getMessage());
            return null;
        }
        return parseJson2Object(clazz, jsonObject);
    }

    /**
     * 解析JSONObject对象到具体类，递归算法
     *
     * @param clazz      和JSON对象对应的类的Class，必须拥有setXxx()函数，其中xxx为属性
     * @param jsonObject 被解析的JSON对象
     * @return 返回传入的Object对象实例
     */
    public static <T> T parseJson2Object(Class<T> clazz, JSONObject jsonObject) {
        T obj = null;
        try {
            //获取clazz的实例
            obj = clazz.newInstance();
            // 获取属性列表
            Field[] fields = clazz.getDeclaredFields();
            // 遍历每个属性，如果为基本类型和String则直接赋值，如果为List则得到每个Item添加后再赋值，如果是其它类则得到类的实例后赋值
            for (Field field : fields) {
                // 设置属性可操作
                field.setAccessible(true);
                // 获取字段类型
                Class<?> typeClazz = field.getType();

                String fieldName = field.getName();
                SerializedName column = field.getAnnotation(SerializedName.class); //获取指定类型注解
                if (column != null) {
                    fieldName = column.name();
                }
                if (typeClazz.isPrimitive()) {
                    // 是否基础变量
                    setProperty(obj, field, jsonObject.opt(fieldName));
                } else if (typeClazz == List.class) {
                    // 得到List的JSONArray数组
                    JSONArray jArray = jsonObject.getJSONArray(fieldName);
                    if (jArray.length() > 0) {
                        Object typeObj = new ArrayList<>();
                        Type type = field.getGenericType();
                        ParameterizedType pt = (ParameterizedType) type;
                        // 获得List元素类型
                        Class<?> dataClass = (Class<?>) pt.getActualTypeArguments()[0];
                        // 将每个元素的实例类加入到类型的实例中
                        for (int i = 0; i < jArray.length(); i++) {
                            //对于数组，递归调用解析子元素
                            if (!jsonObject.isNull(fieldName)) {
                                ((List<Object>) typeObj).add(parseJson2Object(dataClass, jsonObject.getJSONArray(fieldName).getJSONObject(i)));
                            }
                        }
                        setProperty(obj, field, typeObj);
                    }
                } else if (typeClazz == String.class) {
                    // 是否为String
                    setProperty(obj, field, jsonObject.opt(fieldName));
                } else {
                    // 是否为其它对象
                    //递归解析对象
                    if (!jsonObject.isNull(fieldName)) {
                        setProperty(obj, field, parseJson2Object(typeClazz, jsonObject.getJSONObject(fieldName)));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 设置实例类的属性
     *
     * @param obj      要被赋值的实例类
     * @param field    要被赋值的属性
     * @param valueObj 要被赋值的属性的值
     */
    private static void setPropertyPublic(Object obj, Field field, Object valueObj) {
        try {
            field.set(obj, valueObj);
        } catch (Exception e) {
        }
    }

    /**
     * 设置实例类的属性
     *
     * @param obj      要被赋值的实例类
     * @param field    要被赋值的属性
     * @param valueObj 要被赋值的属性的值
     */
    private static void setProperty(Object obj, Field field, Object valueObj) {
        String setMethodName = "set" + field.getName().substring(0, 1).toUpperCase(Locale.getDefault()) + field.getName().substring(1);
        try {
            Class<?> clazz = obj.getClass();
            //获取类的setXxx方法，xxx为属性
            Method method = clazz.getMethod(setMethodName, field.getType());
            //设置set方法可访问
            method.setAccessible(true);
            //调用set方法为类的实例赋值
            method.invoke(obj, valueObj);
        } catch (NoSuchMethodException e) {
            setPropertyPublic(obj, field, valueObj);
        } catch (IllegalArgumentException e) {
            HiLog.d(TAG, "method [" + setMethodName + "] illegal argument:" + valueObj + "," + e.getMessage());
        } catch (IllegalAccessException e) {
            HiLog.d(TAG, "method [" + setMethodName + "] illegal access:" + valueObj + "," + e.getMessage());
        } catch (InvocationTargetException e) {
            HiLog.d(TAG, "method [" + setMethodName + "] invocation target:" + valueObj + "," + e.getMessage());
        }
    }

    /**
     * 对象转json字符
     */
    public static String objectJson(Object obj) {
        JSONObject jsonObject = new JSONObject();
        return objectJson(jsonObject, obj);
    }

    /**
     * 递归调用
     * 对象转json字符
     */
    public static String objectJson(JSONObject jsonObject, Object obj) {
        try {
            // 获取属性列表
            Field[] fields = obj.getClass().getDeclaredFields();
            // 遍历每个属性，如果为基本类型和String则直接赋值，如果为List则得到每个Item添加后再赋值，如果是其它类则得到类的实例后赋值
            for (Field field : fields) {
                // 设置属性可操作
                field.setAccessible(true);
                // 获取字段类型
                Class<?> typeClazz = field.getType();
                String fieldName = field.getName();
                if (fieldName.endsWith("$change") || fieldName.endsWith("serialVersionUID")) {
                    continue;
                }
                SerializedName column = field.getAnnotation(SerializedName.class); //获取指定类型注解
                if (column != null) {
                    fieldName = column.name();
                }
                if (typeClazz.isPrimitive()) {
                    // 基础变量
                    jsonObject.put(fieldName, field.get(obj));
                } else if (typeClazz == List.class) {
                    JSONArray childArray = new JSONArray();
                    // 得到List的JSONArray数组
                    List list = (List) field.get(obj);
                    for (int i = 0; i < list.size(); i++) {
                        JSONObject arrayChild = new JSONObject();
                        objectJson(arrayChild, list.get(i));
                        childArray.put(arrayChild);
                    }
                    jsonObject.put(fieldName, childArray);
                } else if (typeClazz == String.class) {
                    //为String
                    jsonObject.put(fieldName, field.get(obj));
                } else {// 是否为其它对象
                    JSONObject childObject = new JSONObject();
                    objectJson(childObject, field.get(obj));
                    jsonObject.put(fieldName, childObject);
                }

            }
        } catch (Exception e) {
            HiLog.e(TAG, e.getMessage());
        }
        return jsonObject.toString();
    }


}
