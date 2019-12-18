package com.heaven7.android.component.res;

/**
 * the repository can used to get resource from anywhere. like net, local file,db and etc.
 */
public interface Repository {

    Object getResource(RepositoryContext context, Key key);

    void getResourceAsync(RepositoryContext context, Key key, Callback callback);

    interface Callback{
         void onSuccess(RepositoryContext context, Key key, Object data);

         void onFailed(RepositoryContext context, Key key, int code);
    }
}
