package com.example.notificationtest.httplib;

public class Response<T> {
    private T body;
    private Throwable throwable;

    public void setBody(T body) {
        this.body = body;
    }

    public T body() {
        return body;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

}
