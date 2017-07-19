package com.heaven7.java.logic.util;

import java.util.concurrent.atomic.AtomicReference;

/**
 * util calss of safe.
 * @author Administrator
 *
 */
public class SafeUtil {

	
	public static <V> V getAndUpdate(AtomicReference<V> ar, SafeOperator<V> operator) {
		V prev, next;
		do {
			prev = ar.get();
			next = operator.apply(prev);
		} while (!ar.compareAndSet(prev, next));
		return prev;
	}

	/**
	 * the save operator
	 * @author heaven7
	 *
	 * @param <T> the type
	 */
	public interface SafeOperator<T> {
		/**
		 * apply the operation 
		 * @param pre the previous object.
		 * @return the result. often is new object.
		 */
		T apply(T pre);
	}
}
