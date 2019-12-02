package com.guns.utils;

import java.util.ArrayList;

/**
 * @author Cats-Fish
 * @version 1.0
 * @date 2019/11/29 14:58
 */
public class String2Array {

    public static ArrayList<Integer> string2Array(String str){
        ArrayList<Integer> arrayList = new ArrayList<>();
        String[] split = str.split("#");
        for (String s : split) {
            if(s != null && !"".equals(s)){
                int i = Integer.parseInt(s.trim());
                arrayList.add(i);
            }
        }
        return arrayList;
    }

    public static String array2String(){

        return "";
    }

    public static ArrayList<Integer> string2Arrays(String str){
        ArrayList<Integer> arrayList = new ArrayList<>();
        String[] split = str.split(",");
        for (String s : split) {
            if(s != null && !"".equals(s)){
                int i = Integer.parseInt(s.trim());
                arrayList.add(i);
            }
        }
        return arrayList;
    }
}
