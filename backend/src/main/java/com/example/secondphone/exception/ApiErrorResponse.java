package com.example.secondphone.exception;
import java.time.OffsetDateTime;import java.util.Map;
public record ApiErrorResponse(boolean success,String message,Object data,OffsetDateTime timestamp,String errorCode,Map<String,String> fieldErrors,String path){public static ApiErrorResponse of(String message,String path){return new ApiErrorResponse(false,message,null,OffsetDateTime.now(),null,Map.of(),path);}public static ApiErrorResponse of(String message,String path,String code,Map<String,String> fields){return new ApiErrorResponse(false,message,null,OffsetDateTime.now(),code,fields,path);}}
