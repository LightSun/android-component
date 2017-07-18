package com.heaven7.java.logic;

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
    void addStateCallback(LogicCallback callback);

    /**
     * remove the logic callback
     *
     * @param callback the logic callback.
     */
    void removeStateCallback(LogicCallback callback);
    
    
    /**
     * make the logic action schedule/perform on the target scheduler.
     * @param scheduler the target scheduler.
     * @param tag the tag which scheduler apply to. -1 means apply to all tag.
     * @see #perform(int, LogicParam)
     */
    void scheduleOn(int tag, Scheduler scheduler);
    
    /**
     * make the logic action observe/callback on the target scheduler.
     * @param scheduler the target scheduler.
     * @param tag the tag which scheduler apply to. -1 means apply to all tag.
     * @see #perform(int, LogicParam)
     */
    void observeOn(int tag, Scheduler scheduler);

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
    void cancel(int tag, boolean immediately);

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
     * the logic callback
     */
    abstract class LogicCallback{

        /**
         * called on logic start
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
