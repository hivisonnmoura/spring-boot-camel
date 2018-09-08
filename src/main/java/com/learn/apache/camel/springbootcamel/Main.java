package com.learn.apache.camel.springbootcamel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) throws ParseException {
       boolean b1 = false;
       boolean b2 = true;

        System.out.println(Boolean.compare(b1, false));
        System.out.println(Boolean.compare(b2, false));
    }
}
