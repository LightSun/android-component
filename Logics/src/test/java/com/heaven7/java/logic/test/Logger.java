package com.heaven7.java.logic.test;

public class Logger {

	public static void i(String tag, String methd, String msg){
		System.out.println(tag + " >>> called [ "+ methd +"() ]: " + msg);
	}
	public static void w(String tag, String methd, String msg){
		System.err.println(tag + " >>> called [ "+ methd +"() ]: " + msg);
	}
}
