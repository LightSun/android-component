package com.heaven7.android.component.search;

import java.util.List;

public interface SearchCallback {

    void onSearchStart();

    void addSearchResult(List<?> items);

    void onSearchEnd();
}