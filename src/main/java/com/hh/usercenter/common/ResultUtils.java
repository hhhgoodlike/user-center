package com.hh.usercenter.common;

/**
 * 返回工具类
 */

public class ResultUtils {

    /**
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"OK","");
    }

    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse(errorCode);
    }

    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse(code,message,description);
    }

    public static BaseResponse error(ErrorCode errorCode,String message,String description){
        return new BaseResponse(errorCode.getCode(),message,description);
    }

    public static BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse(errorCode.getCode(),errorCode.getMessage(),description);
    }
}
