package com.hh.usercenter.study;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

public class day01Test {

    @Test
    public void test1(){
        String a = "43";
        int b = Integer.parseInt(a);
        System.out.println(b);

    }

    @Test
    public void test6(){
        int[] a = new int[]{6,5,7,9,3,4,10};
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > 5){
                count++;
            }
        }
        System.out.println(count);
    }

    @Test
    public void test2(){
        int a = 153;
        System.out.println("个位数：" + a%10);
        System.out.println("十位数：" + a%100/10);
        System.out.println("百位数：" + a/100);
    }
    @Test
    public void test5(){
        int[] a = new int[]{10,50,60,13,26,37,55,1,5,6};
        for (int i = 1; i < a.length; i++) {
            for (int j = i; j > 0; j--) {
                if (a[j] < a[j-1]){
                    int temp = a[j];
                    a[j] = a[j-1];
                    a[j-1] = temp;
                }
            }
        }
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
    }

    @Test
    public void test4(){
        int[] nums = new int[6];
        for (int i = 0; i < 6; i++) {
            nums[i] = (int)(Math.random()*30) +1;
            for (int j = 0; j < i; j++) {
                if (nums[i] == nums[j]){
                    i--;
                    break;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            System.out.println(nums[i]);
        }

    }

    @Test
    public void test3(){
        Scanner input = new Scanner(System.in);
        System.out.println("请输入年份:");
        int year = input.nextInt();
        System.out.println("请输入月份:");
        int mouth = input.nextInt();
        System.out.println("请输入多少号:");
        int day = input.nextInt();

        int dayNum = day;

        switch (mouth){
            case 12:
                dayNum += 30;
            case 11:
                dayNum += 31;
            case 10:
                dayNum += 30;
            case 9:
                dayNum += 31;
            case 8:
                dayNum += 31;
            case 7:
                dayNum += 30;
            case 6:
                dayNum += 31;
            case 5:
                dayNum += 30;
            case 4:
                dayNum += 31;
            case 3:
                dayNum += 28;
                if (year/4 == 0 && year/100 != 0 || year/400 == 0){
                    dayNum++;
                }
            case 2:
                dayNum += 31;
                break;
        }
        System.out.println(dayNum);
        input.close();

    }
}