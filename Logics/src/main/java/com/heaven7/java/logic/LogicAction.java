package com.heaven7.java.logic;

import com.heaven7.java.base.anno.Nullable;

/**
 * the logic state.
 * @author heaven7
 */
public interface LogicAction extends ContextData {

    /**
     * the result code indicate success.
     */
    int RESULT_SUCCESS = 1;
    /**
     * the result code indicate failed.
     */
    int RESULT_FAILED  = 2;
    
    /** the running state : started */
    int STATE_STARTED        = 11;
    /** the running state : performing */
    int STATE_PERFORMING     = 12;
    /** the running state : cancelled */
    int STATE_CANCELLED      = 13;

    /**
     * get the logic parameter by target tag
     *
     * @param tag the tag
     * @return the logic parameter
     */
    LogicParam getLogicParameter(int tag);

    /**
     * set the logic callback
     *
     * @param callback the logic callback
     */
    void addStateCallback(int tag, LogicCallback callback);

    /**
     * remove the logic callback
     *
     * @param callback the logic callback.
     */
    void removeStateCallback(int tag, LogicCallback callback);
    
    
    /**
     * make the logic action schedule/perform on the target scheduler.
     * @param scheduler the target scheduler. if null means clear scheduler.
     * @param tag the tag which scheduler apply to. 
     * @see #perform(int, LogicParam)
     */
    void scheduleOn(int tag, @Nullable Scheduler scheduler);
    
    /**
     * set the delay to perform action.
     * @param tag the tag
     * @param delay the delay
     */
    void setDelay(int tag, long delay);
    
    /**
     * make the logic action observe/callback on the target scheduler.
     * @param scheduler the target scheduler. if null means clear scheduler.
     * @param tag the tag which scheduler apply to.
     * @see #perform(int, LogicParam)
     */
    void observeOn(int tag, @Nullable Scheduler scheduler);

    /**
     * perform this logic action.
     *
     * @param tag   the tag of this state.
     * @param param the logic parameter.
     */
    void perform(int tag, LogicParam param);

    /**
     * cancel this logic immediately or not.
     *
     * @param tag         the tag of this task.
     * @param immediately true to cancel immediately. current often is true.
     */
    void cancel(int tag);

    /**
     * dispatch the tag result by target code. subclass should call this in {@linkplain #perform(int, LogicParam)}
     * or relative method.
     *
     * @param tag        the tag
     * @param resultCode the result code.
     * @return true if dispatch success, false otherwise.
     */
    boolean dispatchResult(int resultCode, int tag);
    

    /**
     * indicate the logic action of target tag is running or not.
     * @param tag  the tag
     * @return true if the logic action of target tag is running
     */
	boolean isRunning(int tag);
	 /**
     * indicate  any tag of this logic action is running or not.
     * @param tag  the tag
     * @return true if any tag of this logic action is running
     */
	boolean isRunning();
	
	/**
	 * indicate the logic action of target tag is cancelled or not. if {@linkplain #cancel(int)} was called.
	 * the same tag of call this method will return true until you call {@linkplain #reset(int)}.
	 * @param tag  the tag of logic action. 
	 * @return true if the logic action of target tag is cancelled. false otherwise.
	 */
	boolean isCancelled(int tag);
	
	/**
	 * reset this logic action for target tag. if {@linkplain #cancel(int)} was called, 
	 * after call this method you can perform this action again. 
	 * but if tag of this action is running, it will never callback on success until you perform again.
	 * <p>This method is often called after {@linkplain #cancel(int)}.</p>
	 * @param tag the tag
	 */
	void reset(int tag);

	/**
	 * reset this logic action for all tag.if {@linkplain #cancel(int)} was called, 
	 * after call this method you can perform this action again. but if someone of this action is running, 
	 * it will never callback on success until you perform again.
	 * @see #reset(int)
	 */
	void reset();
	
    /**
     * the logic callback
     */
    abstract class LogicCallback{

        /**
         * called on logic start performing.
         * @param action the logic action
         * @param tag the tag
         * @param param the logic parameter
         */
        public abstract void onLogicStart(LogicAction action,int tag, LogicParam param);

        /**
         * called on logic result
         * @param action the logic action
         * @param resultCode the result code. like {@linkplain LogicAction#RESULT_SUCCESS} and etc.
         * @param tag the tag
         * @param param the logic parameter
         */
        public abstract void onLogicResult(LogicAction action, int resultCode, int tag, LogicParam param);
    }
}
