package com.heaven7.java.logic.test;

import com.heaven7.java.base.util.DefaultPrinter;

public class Logger {

	public static void i(String tag, String method, String msg){
		DefaultPrinter.getDefault().info(tag, method, msg);
		//System.out.println(tag + " >>> called [ "+ method +"() ]: " + msg);
	}
	public static void w(String tag, String method, String msg){
		DefaultPrinter.getDefault().warn(tag, method, msg);
		//.err.println(tag + " >>> called [ "+ method +"() ]: " + msg);
	}
}
