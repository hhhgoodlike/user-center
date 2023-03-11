package com.hh.usercenter.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 导入Excel表
 *
 * @author hh
 *
 */

public class ImportExcel {

    /**
     * 读取数据
     * @param args
     */
    public static void main(String[] args) {

        String fileName = "111";
        readByListener(fileName);
        synchronousRead(fileName);
    }

    /**
     * 通过监听器读取
     * @param fileName
     */
    public static void readByListener(String fileName){
        EasyExcel.read(fileName, UserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步读
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        List<UserInfo> totalDataList = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
        for (UserInfo userInfo : totalDataList){
            System.out.println(userInfo);
        }


    }
}

