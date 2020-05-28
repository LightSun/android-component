package com.heaven7.android.component.network.list;

import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.adapter.AdapterManager;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.QuickRecycleViewAdapter2;

import java.util.List;

/*public*/ final class AdapterDelegateFactory {

    public static IAdapterDelegate getAdapterDelegate(RecyclerView rv){
        RecyclerView.Adapter adapter = rv.getAdapter();
        if(adapter instanceof QuickRecycleViewAdapter){
            return new QuickRecycleViewAdapterDelegate((QuickRecycleViewAdapter)adapter);
        }else if(adapter instanceof QuickRecycleViewAdapter2){
            return new QuickRecycleViewAdapterDelegate((QuickRecycleViewAdapter2)adapter);
        }else {
            return null;
        }
    }

    private static class QuickRecycleViewAdapterDelegate implements IAdapterDelegate{

        final AdapterManager am;

        public QuickRecycleViewAdapterDelegate(QuickRecycleViewAdapter adapter) {
            this.am = adapter.getAdapterManager();
        }
        public QuickRecycleViewAdapterDelegate(QuickRecycleViewAdapter2 adapter) {
            this.am = adapter.getAdapterManager();
        }
        @Override
        public int getItemSize() {
            return am.getItemSize();
        }
        @Override
        public void clearItems() {
            am.clearItems();
        }
        @Override
        public void addItems(List<?> datas) {
            am.addItems(datas);
        }
        @Override
        public void replaceAllItems(List<?> datas) {
            am.replaceAllItems(datas);
        }
    }
}
