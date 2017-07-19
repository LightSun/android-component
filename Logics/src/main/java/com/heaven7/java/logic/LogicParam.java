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

    public LogicParam setPosition(int position){
        this.mPosition = position;
        return this;
    }
    
    public Object getData() {
		return data;
	}
	public LogicParam setData(Object data) {
		this.data = data;
		return this;
	}

	public LogicParam setSerializableData(Serializable data){
        this.mSerData = data;
        return this;
    }

    public int getPosition() {
        return mPosition;
    }
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
