package com.heaven7.java.logic;

import com.heaven7.java.base.anno.CalledInternal;

/**
 * the result of perform a logic task: contains result code and result data.
 * @author heaven7
 */
public class LogicResult {
	
	public static final LogicResult SUCCESS = new LogicResult(
			LogicAction.RESULT_SUCCESS, null); 
	public static final LogicResult FALIED = new LogicResult(
			LogicAction.RESULT_FAILED, null); 

	/** the result code. like {@linkplain LogicAction#RESULT_SUCCESS} and etc. */
	private int resultCode;
	private Object data;
	private int flags;
	
	public LogicResult(int resultCode, Object data) {
		super();
		this.resultCode = resultCode;
		this.data = data;
	}
	
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	@CalledInternal
	/*public*/ int getFlags() {
		return flags;
	}
	@CalledInternal
	/*public*/ void setFlags(int flags) {
		this.flags = flags;
	}

	@Override
	public String toString() {
		return "LogicResult [resultCode=" + resultCode + ", data=" + data + "]";
	}
	
}
