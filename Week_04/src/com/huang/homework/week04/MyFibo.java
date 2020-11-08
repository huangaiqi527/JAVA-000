package com.huang.homework.week04;

public final class MyFibo {
    public static int fibo(int num){
        if(num < 2 ){
            return 1;

        }
        int first = 1;
        int second = 1;
        int current = 0;
        for(int i = 2; i <=num ; i++){
            current = first + second;
            first = second;
            second = current;
        }

        return current;
    }
}
