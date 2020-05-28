package com.heaven7.android.component.network.list;

import java.util.List;

/**
 * the adapter delegate(contains refresh ui) of list data. exclude header and footer.
 * @author heaven7
 * @since 1.1.6
 */
public interface IAdapterDelegate {

    /**
     * get the real item size.
     * @return item size
     */
    int getItemSize();

    /**
     *  clear datas
     */
    void clearItems();

    /**
     * add items
     * @param datas the datas
     */
    void addItems(List<?> datas);

    /**
     * replace datas
     * @param datas the datas
     */
    void replaceAllItems(List<?> datas);
}
