package com.example.notificationtest.httplib;

import java.io.File;

/**上传文件类
 * 
 * */
public class FormFile {
    
    private File file;
    //请求参数名称
    private String parameterName;  
    //内容类型  
    private String contentType = "application/octet-stream";  
      
    /** 
     * 上传大文件，一边读文件数据一边上传 
     * @param file
     * @param parameterName 
     * @param contentType
     */
    public FormFile(File file, String parameterName, String contentType)
    {  
        this.parameterName = parameterName;
        this.file = file;  
        if(contentType!=null) this.contentType = contentType;
    }  
      
    public File getFile()   
    {  
        return file;  
    }  
    public String getParameterName()
    {  
        return parameterName;  
    }  
    public void setParameterName(String parameterName)   
    {  
        this.parameterName = parameterName;  
    }  
    public String getContentType()  
    {  
        return contentType;  
    }  
    public void setContentType(String contentType)   
    {  
        this.contentType = contentType;  
    }     


}
