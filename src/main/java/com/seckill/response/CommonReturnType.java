package com.seckill.response;


public class CommonReturnType {

    private String status;

    //若stauts=fail，则data内使用通用的错误码格式
    private Object data;

    //通用的创建方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    //通用的创建方法
    public static CommonReturnType create(Object result,String status){

        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
