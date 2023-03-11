package com.hh.usercenter.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入用户到数据库中
 */
public class ImportUser {
    public static void main(String[] args) {
        String fileName = "111";
        List<UserInfo> userInfoList = EasyExcel.read(fileName)
                .head(UserInfo.class)
                .sheet()
                .doReadSync();
        System.out.println("总数 = " + userInfoList.size());
        Map<String,List<UserInfo>> listMap = userInfoList.stream()
                .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                .collect(Collectors.groupingBy(UserInfo::getUsername));

        for (Map.Entry<String,List<UserInfo>> stringListEntry : listMap.entrySet()){
            if (stringListEntry.getValue().size() > 1){
                System.out.println("重复昵称" + stringListEntry.getKey() );
            }
        }
    }
}
