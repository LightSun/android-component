package com.heaven7.java.logic;

import java.io.Serializable;

/**
 * the logic parameter
 * Created by heaven7 on 2017/6/17 0017.
 */

public class LogicParam{

    private int mPosition;
    private Serializable mSerData;

    public LogicParam setPosition(int position){
        this.mPosition = position;
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
        return "LogicParam{" +
                "mPosition=" + mPosition +
                ", mSerData=" + mSerData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogicParam that = (LogicParam) o;

        if (mPosition != that.mPosition)
            return false;
        return mSerData != null ? mSerData.equals(that.mSerData) : that.mSerData == null;

    }

    @Override
    public int hashCode() {
        int result = mPosition;
        result = 31 * result + (mSerData != null ? mSerData.hashCode() : 0);
        return result;
    }
    public LogicParam() {
    }

}
