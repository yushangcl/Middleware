package com.whh.middleware;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * @author huahui.wu.
 *         Created on 2018/1/8.
 */
public class TestDemo {

    public static void main(String[] args) {
        m(9);
        System.out.println(f(3));
        t();
        bubbleSort();
    }

    public static void str() {
        String str1 = "Hello Word";
        String str2 = "Hello " + new String("Word");
        String str3 = "Word";
        String str4 = "Hello ";
        String str5 = str4 + str3;
        System.out.println(str1);
        System.out.println(str2);
        System.out.println(str5);
        System.out.println(str1 == str5.intern());
        System.out.println(Objects.equals(str1, str5));
        System.out.println(fact(100));

    }

    /**
     * 递归算法
     *
     * @param n
     * @return
     */
    public static long fact(long n) {
        if (n <= 1) {
            System.out.println(n + "=");
            return n;
        } else {
            System.out.print(n + "*");
            return n * fact(n - 1);
        }
    }

    /**
     * 九乘九 乘法表
     *
     * @param i
     */
    public static void m(int i) {

        if (i == 0) {
            return;
        }
        m(i - 1);
        for (int j = 1; j <= i; j++) {
            System.out.print(j + "*" + i + "=" + j * i + " ");
        }
        System.out.println();
    }

    public static int f(int i) {
        if (i == 1 || i == 2) {
            return 1;
        } else {
            return f(i - 1) + f(i - 2);
        }
    }

    public static void t() {
        System.out.println(Instant.now());
        System.out.println(Instant.now(Clock.system(ZoneId.of("Asia/Shanghai"))));
        Date date = new Date();
        System.out.println(date);
        Instant instant = date.toInstant();
        System.out.println(instant);
        System.out.println(Date.from(instant));
    }

    public static void bubbleSort() {
        int a[] = {49, 38, 65, 97, 76, 13, 27, 49, 78, 34, 12, 64, 5, 4, 62, 99, 98, 54, 56, 17, 18, 23, 34, 15, 35, 25, 53, 51};
        int temp = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length - i - 1; j++) {
                if (a[j] > a[j + 1]) {
                    temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }
        for (int anA : a) {
            System.out.print(anA + " ");
        }
    }


}
