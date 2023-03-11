package com.hh.usercenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


@Data
public class UserInfo {
    /**
     * 星球编号
     */
    @ExcelProperty("用户编号")
    private String plantCode;


    /**
     * 用户昵称
     */
    @ExcelProperty("用户昵称")
    private String username;
}