
package com.findhotel.image;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import java.util.concurrent.atomic.AtomicBoolean;
import com.commonsware.cwac.adapter.AdapterWrapper;
abstract public class EndlessAdapter extends AdapterWrapper {
  abstract protected boolean cacheInBackground() throws Exception;

  abstract protected void appendCachedData();

  private View pendingView=null;
  private AtomicBoolean keepOnAppending=new AtomicBoolean(true);
  private Context context;
  private int pendingResource=-1;
  private boolean isSerialized=false;
  private boolean runInBackground=true;

  /**
   * Constructor wrapping a supplied ListAdapter
   */
  public EndlessAdapter(ListAdapter wrapped) {
    super(wrapped);
  }

  /**
   * Constructor wrapping a supplied ListAdapter and
   * explicitly set if there is more data that needs to be fetched or not.
   */
  public EndlessAdapter(ListAdapter wrapped, boolean keepOnAppending) {
    super(wrapped);
    this.keepOnAppending.set(keepOnAppending);
  }

  /**
   * Constructor wrapping a supplied ListAdapter and
   * providing a id for a pending view.
   */
  public EndlessAdapter(Context context, ListAdapter wrapped,
                        int pendingResource) {
    super(wrapped);
    this.context=context;
    this.pendingResource=pendingResource;
  }

  /**
   * Constructor wrapping a supplied ListAdapter, providing
   * a id for a pending view and explicitly set if there is
   * more data that needs to be fetched or not.
   */
  public EndlessAdapter(Context context, ListAdapter wrapped,
                        int pendingResource, boolean keepOnAppending) {
    super(wrapped);
    this.context=context;
    this.pendingResource=pendingResource;
    this.keepOnAppending.set(keepOnAppending);
  }

  public boolean isSerialized() {
    return(isSerialized);
  }

  public void setSerialized(boolean isSerialized) {
    this.isSerialized=isSerialized;
  }

  public void stopAppending() {
    keepOnAppending.set(false);
  }

  public void restartAppending() {
    keepOnAppending.set(true);
  }
  
  public void setRunInBackground(boolean runInBackground) {
    this.runInBackground=runInBackground;
  }

  /**
   * Use to manually notify the adapter that it's dataset
   * has changed. Will remove the pendingView and update the
   * display.
   */
  public void onDataReady() {
    pendingView=null;
    notifyDataSetChanged();
  }

  /**
   * How many items are in the data set represented by this
   * Adapter.
   */
  @Override
  public int getCount() {
    if (keepOnAppending.get()) {
      return(super.getCount() + 1); 
    }

    return(super.getCount());
  }

  /**
   * Masks ViewType so the AdapterView replaces the
   * "Pending" row when new data is loaded.
   */
  public int getItemViewType(int position) {
    if (position == getWrappedAdapter().getCount()) {
      return(IGNORE_ITEM_VIEW_TYPE);
    }

    return(super.getItemViewType(position));
  }

  /**
   * Masks ViewType so the AdapterView replaces the
   * "Pending" row when new data is loaded.
   * 
   * @see #getItemViewType(int)
   */
  public int getViewTypeCount() {
    return(super.getViewTypeCount() + 1);
  }

  @Override
  public Object getItem(int position) {
    if (position >= super.getCount()) {
      return(null);
    }

    return(super.getItem(position));
  }

  @Override
  public boolean areAllItemsEnabled() {
    return(false);
  }

  @Override
  public boolean isEnabled(int position) {
    if (position >= super.getCount()) {
      return(false);
    }

    return(super.isEnabled(position));
  }

  /**
   * Get a View that displays the data at the specified
   * position in the data set. In this case, if we are at
   * the end of the list and we are still in append mode, we
   * ask for a pending view and return it, plus kick off the
   * background task to append more data to the wrapped
   * adapter.
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (position == super.getCount() && keepOnAppending.get()) {
      if (pendingView == null) {
        pendingView=getPendingView(parent);

        if (runInBackground) {
          executeAsyncTask(buildTask());
        }
        else {
          try {
            keepOnAppending.set(cacheInBackground());
          }
          catch (Exception e) {
            keepOnAppending.set(onException(pendingView, e));
          }
        }
      }

      return(pendingView);
    }

    return(super.getView(position, convertView, parent));
  }
  protected boolean onException(View pendingView, Exception e) {
    Log.e("EndlessAdapter", "Exception in cacheInBackground()", e);

    return(false);
  }

  protected AppendTask buildTask() {
    return(new AppendTask(this));
  }

  @TargetApi(11)
  private <T> void executeAsyncTask(AsyncTask<T, ?, ?> task,
                                    T... params) {
    if (!isSerialized
        && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
      task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }
    else {
      task.execute(params);
    }
  }

  /**
   * A background task that will be run when there is a need
   * to append more data. Mostly, this code delegates to the
   * subclass, to append the data in the background thread
   * and rebind the pending view once that is done.
   */
  protected static class AppendTask extends
      AsyncTask<Void, Void, Exception> {
    EndlessAdapter adapter=null;
    boolean tempKeep;

    protected AppendTask(EndlessAdapter adapter) {
      this.adapter=adapter;
    }

    @Override
    protected Exception doInBackground(Void... params) {
      Exception result=null;

      try {
        tempKeep=adapter.cacheInBackground();
      }
      catch (Exception e) {
        result=e;
      }

      return(result);
    }

    @Override
    protected void onPostExecute(Exception e) {
      adapter.keepOnAppending.set(tempKeep);

      if (e == null) {
        adapter.appendCachedData();
      }
      else {
        adapter.keepOnAppending.set(adapter.onException(adapter.pendingView,
                                                        e));
      }

      adapter.onDataReady();
    }
  }

  /**
   * Inflates pending view using the pendingResource ID
   * passed into the constructor
   * 
   * @param parent
   * @return inflated pending view, or null if the context
   *         passed into the pending view constructor was
   *         null.
   */
  protected View getPendingView(ViewGroup parent) {
    if (context != null) {
      LayoutInflater inflater=
          (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      return inflater.inflate(pendingResource, parent, false);
    }

    throw new RuntimeException("You must either override getPendingView() or supply a pending View resource via the constructor");
  }

  /**
   * Getter method for the Context being held by the adapter
   * 
   * @return Context
   */
  protected Context getContext() {
    return(context);
  }
}
