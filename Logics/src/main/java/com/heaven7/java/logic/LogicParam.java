package com.heaven7.java.logic;

import java.io.Serializable;

/**
 * the logic parameter
 * Created by heaven7 on 2017/6/17 0017.
 */

public class LogicParam{

    private int mPosition;
    private Serializable mSerData;
    private Object data;
    
    public LogicParam() {
    }
    
    /**
     * create the logic parameter by target data.
     * @param data the target data.
     * @return an instance of LogicParam.
     */
    public static LogicParam create(Object data){
    	return new LogicParam().setData(data);
    }
    /**
     * create the logic parameter by target data.
     * @param data the target data.
     * @return an instance of LogicParam.
     */
    public static LogicParam create(Serializable data){
    	return new LogicParam().setSerializableData(data);
    }

    /**
     * set the position parameter. this often called when we want to do something with position.
     * eg: animation/animator in a item of adapter(such as android).
     * @param position the position
     * @return this
     */
    public LogicParam setPosition(int position){
        this.mPosition = position;
        return this;
    }
    /**
     * get the data.
     * @return the data.
     */
    public Object getData() {
		return data;
	}
    /**
     * set the data.
     * @param data the object data 
     * @return this.
     */
	public LogicParam setData(Object data) {
		this.data = data;
		return this;
	}

	/**
	 * set the serialize data. 
	 * @param data the serialize data.
	 * @return this
	 * @see Serializable
	 */
	public LogicParam setSerializableData(Serializable data){
        this.mSerData = data;
        return this;
    }
	/**
	 * get the position
	 * @return the position
	 */
    public int getPosition() {
        return mPosition;
    }
    /**
     * get the serialize data. 
     * @return the serialize data. 
     */
    public Serializable getSerializableData(){
        return mSerData;
    }

    @Override
	public String toString() {
		return "LogicParam [mPosition=" + mPosition + ", mSerData=" + mSerData + ", data=" + data + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + mPosition;
		result = prime * result + ((mSerData == null) ? 0 : mSerData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogicParam other = (LogicParam) obj;
		
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		
		if (mPosition != other.mPosition)
			return false;
		
		if (mSerData == null) {
			if (other.mSerData != null)
				return false;
		} else if (!mSerData.equals(other.mSerData))
			return false;
		return true;
	}
    
    
    
}
