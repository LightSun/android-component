package com.heaven7.java.logic;

import com.heaven7.java.base.util.SparseArray;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * the logic action. support async and count analyse.
 * default support multi tag in one {@linkplain AbstractLogicAction}.
 * you should call {@link #dispatchResult(int, int)} in {@linkplain #performImpl(int, int, LogicParam)} or it's relative method.
 * Created by heaven7 on 2017/6/17.
 */
public abstract class AbstractLogicAction extends ContextDataImpl implements LogicAction {
	
    private final ArrayList<LogicCallback> mCallbacks;

    /**
     * tag info map.
     */
    private final SparseArray<TagInfo> mTagMap;

    /**
     * the map which used to count the tag of state perform count.
     */
    private final SparseArray<Integer> mCountMap;

    /**
     * create an instance of AbstractLogicAction.
     * @param wantCount true if you want to COUNT the count of perform assigned tag.
     * @see #perform(int, LogicParam)
     */
    public AbstractLogicAction(boolean wantCount) {
        this.mCallbacks = new ArrayList<LogicCallback>(4);
        this.mTagMap = new SparseArray<TagInfo>(4);
        mCountMap = wantCount ? new SparseArray<Integer>(4) : null;
    }

    @Override
    public final void addStateCallback(LogicCallback callback) {
        synchronized (mCallbacks) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public final void removeStateCallback(LogicCallback callback) {
        synchronized (mCallbacks) {
            mCallbacks.remove(callback);
        }
    }

    @Override
    public  LogicParam getLogicParameter(int tag) {
        TagInfo info;
        synchronized (mTagMap) {
            info = mTagMap.get(tag);
        }
        return info != null ? info.mLogicParam : null;
    }
    
    @Override
    public void scheduleOn(int tag, Scheduler scheduler) {
    	
    }
    
    @Override
    public boolean isRunning(int tag) {
        TagInfo info = mTagMap.get(tag);
        if(info != null && !info.mCancelled.get()){
            return true;
        }
        return false;
    }

    @Override
    public boolean isRunning(){
        final int size = mTagMap.size();
        for(int i = 0 ; i< size ; i ++){
            TagInfo info = mTagMap.valueAt(i);
            if(!info.mCancelled.get()){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void perform(int tag, LogicParam param) {
        //put tag info
        synchronized (mTagMap) {
            mTagMap.put(tag, new TagInfo(param));
        }
        //start
        dispatchLogicStart(tag, param);

        //COUNT tag count if need
        final int targetCount ;
        if (mCountMap != null) {
            synchronized (mCountMap) {
                targetCount = mCountMap.get(tag) + 1;
                mCountMap.put(tag, targetCount);
            }
        }else{
            targetCount = 1;
        }
        //perform impl
        performImpl(tag, targetCount,  param);
    }

    @Override
    public final boolean dispatchResult(int resultCode, int tag) {
        //usually callback
        final LogicParam lm = getLogicParameter(tag);
        final ArrayList<LogicCallback> callbacks = (ArrayList<LogicCallback>) mCallbacks.clone();
        for (LogicCallback cl : callbacks) {
            cl.onLogicResult(this, resultCode, tag, lm);
        }

        switch (resultCode){
            case RESULT_SUCCESS:
                return onLogicSuccess(tag);

            case RESULT_FAILED:
                return onLogicFailed(tag);
        }
        return dispatchLogicResult(resultCode, tag, lm);
    }

    @Override
    public final void cancel(int tag ,boolean immediately) {
        TagInfo info;
        synchronized (mTagMap) {
            info = mTagMap.get(tag);
        }
        if(info == null){
            return;
        }
        if(!info.mCancelled.compareAndSet(false, true)){
            System.err.println("AbstractLogicAction >>>>>> called [ cancel() ]: " + "cancel failed. tag = "
                    + tag + " ,param = " + info.mLogicParam);
        }
        cancelImpl(tag, immediately);
    }

    /**
     * clear the count map or analyse .
     */
    public final void clearCount() {
        if (mCountMap != null) {
        	synchronized (mCountMap) {
        		mCountMap.clear();
			}
        }
    }

    /**
     * remove the count which is assigned by target tag.
     * @param tag the tag.
     */
    public final void removeCount(int tag){
    	if (mCountMap != null) {
        	synchronized (mCountMap) {
        		mCountMap.delete(tag);
			}
        }
    }

    /**
     * called on cancel last perform. often called by {@linkplain #onLogicSuccess(int)}}.
     * @param tag the tag .
     * @param param the logic parameter.
     */
    protected void onCancel(int tag, LogicParam param){

    }

    /**
     * called on logic result.
     * @param resultCode the result code. but not {@linkplain #RESULT_SUCCESS} or  {@linkplain #RESULT_FAILED}.
     * @param tag the tag
     * @param lm the logic parameter
     * @return true if dispatch success.
     */

    protected  boolean dispatchLogicResult(int resultCode, int tag, LogicParam lm){
        return false;
    }

    /**
     * do perform this logic state. also support async perform this logic state.
     *
     * @param tag       the tag of this logic state.
     * @param count     the count of perform this tag by logic. but if it was cleaned it always be one. start from 1
     * @param param     the logic parameter of this logic state
     */
    protected abstract void performImpl(int tag, int count ,LogicParam param);

    /**
     * do cancel this perform logic.
     * @param tag the tag
     * @param immediately true if cancel immediately.
     */
    protected abstract void cancelImpl(int tag, boolean immediately);


    //====================== self method ============================

    protected boolean onLogicFailed(int tag){
        getAndRemoveTagInfo(tag);
        return true;
    }

    /**
     * called on logic success.
     * @param tag the tag
     * @return true .if it is started ,but not cancelled and normal success.
     */
    protected boolean onLogicSuccess(int tag) {
        //get and remove tag info
        TagInfo info = getAndRemoveTagInfo(tag);
        if(info == null){
            return false;
        }

        //true, means it is cancelled.
        if(info.mCancelled.get()){
            onCancel(tag, info.mLogicParam);
            return false;
        }
        return true;
    }

    private TagInfo getAndRemoveTagInfo(int tag) {
        TagInfo info;
        synchronized (mTagMap) {
            info = mTagMap.get(tag);
            mTagMap.remove(tag);
        }
        return info;
    }

    private void dispatchLogicStart(int tag, LogicParam lp) {
        ArrayList<LogicCallback> callbacks = (ArrayList<LogicCallback>) mCallbacks.clone();
        for (LogicCallback cl : callbacks) {
            cl.onLogicStart(this, tag, lp);
        }
    }

    private static class TagInfo{
        AtomicBoolean mCancelled;
        LogicParam mLogicParam;
        Scheduler mScheduler;

        public TagInfo(LogicParam mLogicParam) {
            this.mCancelled = new AtomicBoolean(false);
            this.mLogicParam = mLogicParam;
        }
    }
}
