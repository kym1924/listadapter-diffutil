<div>
<img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=Android&logoColor=white" />
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white" />
<img src="https://img.shields.io/badge/writer-kym1924-yellow?&style=flat&logo=Android"/>
</div>

# ListAdapter and DiffUtil
SlidePuzzle using *ListAdapter* and *DiffUtil*.
<br><br>
<img width=360 height=760 src="https://user-images.githubusercontent.com/63637706/141768641-53882040-6117-4236-8d54-4e470ea3c4c7.gif"/>

#### 1. RecyclerView.Adapter
- RecyclerView.Adapter has a list that holds data.
- If there is a change in the list, the adapter should be notified for RecyclerView to redraw a new list.
```kotlin
class PuzzleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = listOf<Int>()
        set(value) {
            field = value
            // Notify the adapter
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
```
<br>

#### 1-1. The usage of RecyclerView.Adapter
Assign the value to the adapter's data.
```kotlin
viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.puzzle.collect { puzzle ->
            puzzle?.let {
                puzzleAdapter.data = puzzle
            }
        }
    }
}
```
<br>

#### 1-2. Data Change Events
There are two different classes of data change events.
- Change of Items.
  - notifyItemChanged(int)
  ```java
  // Notify that an item has changed in the position of the List.
  public final void notifyItemChanged(int position) {
      mObservable.notifyItemRangeChanged(position, 1);
  }
  ```
  - notifyItemRangeChanged(int, int)
  ```java
  // Notify that the items has been changed within that range.
  public final void notifyItemRangeChanged(int positionStart, int itemCount) {
      mObservable.notifyItemRangeChanged(positionStart, itemCount);
  }
  ```
- Change of Structure.
  - notifyDataSetChanged()
  ```java
  // Notify that the list has changed.
  public final void notifyDataSetChanged() {
      mObservable.notifyChanged();
  }
  ```
  - notifyItemInserted(int)
  ```java
  // Notify that an item has inserted in the position of the List.
  public final void notifyItemInserted(int position) {
      mObservable.notifyItemRangeInserted(position, 1);
  }
  ```
  - notifyItemRangeInserted(int, int)
  ```java
  // Notify that the items have been inserted within that range.
  public final void notifyItemRangeInserted(int positionStart, int itemCount) {
      mObservable.notifyItemRangeInserted(positionStart, itemCount);
  }
  ```
  - notifyItemRemoved(int)
  ```java
  // Notifies that an item has removed in the position of the List.
  public final void notifyItemRemoved(int position) {
      mObservable.notifyItemRangeRemoved(position, 1);
  }
  ```
  - notifyItemRangeRemoved(int, int)
  ```java
  // Notify that the items have been removed within that range.
  public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
      mObservable.notifyItemRangeRemoved(positionStart, itemCount);
  }
  ```
  - notifyItemMoved(int fromPosition, int toPosition)
  ```java
  // Notify that the item at fromPosition has been moved to toPosition.
  public final void notifyItemMoved(int fromPosition, int toPosition) {
      mObservable.notifyItemMoved(fromPosition, toPosition);
  }
  ```
<br>

#### 1-3. Rely on notifyDataSetChanged() as a last resort
- When `notifyDataSetChanged()` is called, the RecyclerView will redraw the whole list, not just the items that have changed.
- In other words, efficiency is inversely proportional to the size of the list.
- That's why it's always more efficient to use more specific change events whenever possible when writing adapters.
- However, cannot avoid `notifyDatasetChanged()` if don't know where the items changed.
<br>

#### 2. DiffUtil
- `DiffUtil` is a utility class that can calculate the difference between two lists.
- The max size of the list can be 2^26.
- if the lists are large, this operation may take significant time.
- So run this on *a background thread*.
- Below are some average run times for reference
  - 100 items and 10 modifications: avg: 0.39 ms, median: 0.35 ms
  - 100 items and 100 modifications: 3.82 ms, median: 3.75 ms
  - 100 items and 100 modifications without moves: 2.09 ms, median: 2.06 ms
  - 1000 items and 50 modifications: avg: 4.67 ms, median: 4.59 ms
  - 1000 items and 50 modifications without moves: avg: 3.59 ms, median: 3.50 ms
  - 1000 items and 200 modifications: 27.07 ms, median: 26.92 ms
  - 1000 items and 200 modifications without moves: 13.54 ms, median: 13.36 ms
<br>

#### 2-1. DiffUtil.calculateDiff(Callback, boolean)
This method returns [DiffUtil.DiffResult](#2-2-diffutildiffresult) containing information to *change oldList to newList*.
- `Callback cb` : Callback on how two lists are compared.
- `boolean detectMoves` : can set detection for movement. Default value is *true*.
- See the specific process [here](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:recyclerview/recyclerview/src/main/java/androidx/recyclerview/widget/DiffUtil.java).
```java
@NonNull
public static DiffResult calculateDiff(@NonNull Callback cb) {
    return calculateDiff(cb, true);
}

@NonNull
public static DiffResult calculateDiff(@NonNull Callback cb, boolean detectMoves) {
    ...
    return new DiffResult(cb, snakes, forward, backward, detectMoves);
}
```
<br>

#### 2-1. DiffUtil.Callback
Handles *list indexing and item diffing*.
```java
public abstract static class Callback {
    public abstract int getOldListSize();
    
    public abstract int getNewListSize();

    public abstract boolean areItemsTheSame(int oldItemPosition, int newItemPosition);

    public abstract boolean areContentsTheSame(int oldItemPosition, int newItemPosition);

    @Nullable
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return null;
    }
}
```
<br>

#### 2-1-1. The methods of DiffUtil.Callback
- `getOldListSize()`
  - Return the size the old list.
- `getNewListSize()`
  - Return the size the new list.
- `areItemsTheSame(int, int)`
  - Called by the DiffUtil to decide whether *two object represent the same Item*.
  - Compare items using unique field.
- `areContentsTheSame(int, int)`
  - DiffUtil uses this method to check equality instead of *Object.equals(Object)*.
  - Called by the DiffUtil when it wants to check whether two items have the same data.
  - When *areItemsTheSame(int, int) returns true*, this method is called.
- `getChangePayload(int, int) `
  - When areItemsTheSame(int, int) returns true for two items
  - And areContentsTheSame(int, int) returns false for them
  - DiffUtil calls this method to get a payload about the change.
  - With that payload, RecyclerView.ItemAnimator can run the animation.
<br>

#### 2-1-2. The example of DiffUtil.Callback
```java
class PuzzleDiffUtilCallback(
    private val oldList: List<Int>, private val newList: List<Int>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size
    
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
```
<br>

#### 2-2. DiffUtil.DiffResult
- This class containing the result of [DiffUtil.calculateDiff(Callback, boolean)](#2-1-diffutilcalculatediffcallback-boolean).
- The RecyclerView needs to know immediately about the adapter's data change.
- In other words, cannot defer the notify method calls.
<br>

#### 2-2-1. The methods of DiffUtil.DiffResult
- `dispatchUpdatesTo(RecylcerView.Adapter)`
```java
public void dispatchUpdatesTo(@NonNull final RecyclerView.Adapter adapter) {
    dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
}
```

- `dispatchUpdatesTo(ListUpdateCallback)`
```java
public void dispatchUpdatesTo(@NonNull ListUpdateCallback updateCallback) {
    final BatchingListUpdateCallback batchingCallback;
    if (updateCallback instanceof BatchingListUpdateCallback) {
        batchingCallback = (BatchingListUpdateCallback) updateCallback;
    } else {
        batchingCallback = new BatchingListUpdateCallback(updateCallback);
        // replace updateCallback with a batching callback
        // and override references to updateCallback
        // so that we don't call it directly by mistake
        // noinspection unusedAssignment
        updateCallback = batchingCallback;
    }
    
    ...
        
    batchingCallback.dispatchLastEvent();
}
```
<br>

#### 2-3. ListUpdateCallback
- An interface that can receive Update operations that are applied to a list.
```java
public interface ListUpdateCallback {
    // Called when count number of items are inserted at the given position.
    void onInserted(int position, int count);

    // Called when count number of items are removed at the given position.
    void onRemoved(int position, int count);

    // Called when an item changes its position in the list.
    void onMoved(int fromPosition, int toPosition);

    // Called when count number of items are updated at the given position.
    void onChanged(int position, int count, @Nullable Object payload);
}
```
<br>

#### 2-3-1. AdapterListUpdateCallback
- Dispatches update events to the given adapter.
```java
public final class AdapterListUpdateCallback implements ListUpdateCallback {
    @NonNull
    private final RecyclerView.Adapter mAdapter;

    /**
     * Creates an AdapterListUpdateCallback that will dispatch update events to the given adapter.
     * @param adapter The Adapter to send updates to.
     */
    public AdapterListUpdateCallback(@NonNull RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onInserted(int position, int count) {
        mAdapter.notifyItemRangeInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        mAdapter.notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        mAdapter.notifyItemRangeChanged(position, count, payload);
    }
}
```
<br>

#### 2-3-2. BatchingListUpdateCallback
- Dispatches update events to the given Callback.
```java
public class BatchingListUpdateCallback implements ListUpdateCallback {
    private static final int TYPE_NONE = 0;
    private static final int TYPE_ADD = 1;
    private static final int TYPE_REMOVE = 2;
    private static final int TYPE_CHANGE = 3;

    final ListUpdateCallback mWrapped;

    int mLastEventType = TYPE_NONE;
    int mLastEventPosition = -1;
    int mLastEventCount = -1;
    Object mLastEventPayload = null;

    public BatchingListUpdateCallback(@NonNull ListUpdateCallback callback) {
        mWrapped = callback;
    }

    /**
     * BatchingListUpdateCallback holds onto the last event to see if it can be merged with the next one.
     * When stream of events finish, you should call this method to dispatch the last event.
     */
    public void dispatchLastEvent() {
        if (mLastEventType == TYPE_NONE) {
            return;
        }
        switch (mLastEventType) {
            case TYPE_ADD:
                mWrapped.onInserted(mLastEventPosition, mLastEventCount);
                break;
            case TYPE_REMOVE:
                mWrapped.onRemoved(mLastEventPosition, mLastEventCount);
                break;
            case TYPE_CHANGE:
                mWrapped.onChanged(mLastEventPosition, mLastEventCount, mLastEventPayload);
                break;
        }
        mLastEventPayload = null;
        mLastEventType = TYPE_NONE;
    }

    @Override
    public void onInserted(int position, int count) {
        if (mLastEventType == TYPE_ADD && position >= mLastEventPosition
                && position <= mLastEventPosition + mLastEventCount) {
            mLastEventCount += count;
            mLastEventPosition = Math.min(position, mLastEventPosition);
            return;
        }
        dispatchLastEvent();
        mLastEventPosition = position;
        mLastEventCount = count;
        mLastEventType = TYPE_ADD;
    }

    @Override
    public void onRemoved(int position, int count) {
        if (mLastEventType == TYPE_REMOVE && mLastEventPosition >= position &&
            	mLastEventPosition <= position + count) {
            mLastEventCount += count;
            mLastEventPosition = position;
            return;
        }
        dispatchLastEvent();
        mLastEventPosition = position;
        mLastEventCount = count;
        mLastEventType = TYPE_REMOVE;
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        dispatchLastEvent(); // moves are not merged
        mWrapped.onMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        if (mLastEventType == TYPE_CHANGE &&
                !(position > mLastEventPosition + mLastEventCount
                        || position + count < mLastEventPosition || mLastEventPayload != payload)) {
            // take potential overlap into account
            int previousEnd = mLastEventPosition + mLastEventCount;
            mLastEventPosition = Math.min(position, mLastEventPosition);
            mLastEventCount = Math.max(previousEnd, position + count) - mLastEventPosition;
            return;
        }
        dispatchLastEvent();
        mLastEventPosition = position;
        mLastEventCount = count;
        mLastEventPayload = payload;
        mLastEventType = TYPE_CHANGE;
    }
}
```
<br>

#### 2-4. The usage of DiffUtil and RecyclerView.Adapter
DiffUtil can be used to calculate updates for a `RecyclerView.Adapter`.
```kotlin
class PuzzleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = listOf<Int>()
        set(value) {
            val diffUtilCallback = PuzzleDiffUtilCallback(oldList, newList);
            val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback);
            field = value
            diffUtilResult.dispatchUpdatesTo(this@PuzzleAdapter)
        }
    
    ...
    
}
```
<br>

#### 3. ListAdapter
- ListAdapter is abstract class extending RecyclerView.Adapter.
- ListAdapter computes differences between Lists on a background thread using `AsyncListDiffer`.
- ListAdapter hasn't a list that holds data unlike [RecyclerView.Adapter](#1-recyclerviewadapter).
- Can access the current list through methods like `getCurrentList()` and `getItem(int)`.
```java
public abstract class ListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    final AsyncListDiffer<T> mDiffer;
    private final AsyncListDiffer.ListListener<T> mListener = new AsyncListDiffer.ListListener<T>() {
        @Override
        public void onCurrentListChanged(@NonNull List<T> previousList, @NonNull List<T> currentList) { 
            ListAdapter.this.onCurrentListChanged(previousList, currentList);
        }
    };

    @SuppressWarnings("unused")
    protected ListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        mDiffer = new AsyncListDiffer<>(new AdapterListUpdateCallback(this),
                new AsyncDifferConfig.Builder<>(diffCallback).build());
        mDiffer.addListListener(mListener);
    }

    @SuppressWarnings("unused")
    protected ListAdapter(@NonNull AsyncDifferConfig<T> config) {
        mDiffer = new AsyncListDiffer<>(new AdapterListUpdateCallback(this), config);
        mDiffer.addListListener(mListener);
    }
    
    public void submitList(@Nullable List<T> list) {
        mDiffer.submitList(list);
    }
    
    public void submitList(@Nullable List<T> list, @Nullable final Runnable commitCallback) {
        mDiffer.submitList(list, commitCallback);
    }

    protected T getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    @NonNull
    public List<T> getCurrentList() {
        return mDiffer.getCurrentList();
    }
    
    public void onCurrentListChanged(@NonNull List<T> previousList, @NonNull List<T> currentList) {
    }
}
```
<br>

#### 3-1. The methods of ListAdapter
- `submitList(List<T> list)`
  - Submits a new list. This list are compared and displayed.
- `submitList(List<T> list, Runnable commitCallback)`
  - Submits a new list. This list are compared and displayed.
  - commitCallback is a runnable to run after the list is submitted and committed.
- `getItem(int position)`
  - Get the item at the position in the list.
- `getItemCount()`
  - Get the size of current list.
- `getCurrentList()`
  - Get the current list.
- `onCurrentListChanged(List<T> previousList, List<T> currentList)`
  - When the current list is updated.
  - If a null or no list has been submitted, the currentList is empty list.
<br>

#### 3-2. AsyncListDiffer
- ListAdapter and AsyncListDiffer can simplify the use of [DiffUtil](#2-diffutil).
- It will be signal the adapter of changes between submitted lists.
- `submitList(@Nullable final List<T> newList, @Nullable final Runnable commitCallback)`
  - If newList equals oldList, Nothing to do.
  - If has Runnable, run.
  ```java
  if (newList == mList) {
      // nothing to do (Note - still had to inc generation, since may have ongoing work)
      if (commitCallback != null) {
          commitCallback.run();
      }
      return;
  }
  ```

  - If newList is null, Just remove.
  - *onCurrentListChanged* method call.
  ```java
  if (newList == null) {
      // noinspection ConstantConditions
      int countRemoved = mList.size();
      mList = null;
      mReadOnlyList = Collections.emptyList();
      // notify last, after list is updated
      mUpdateCallback.onRemoved(0, countRemoved);
      onCurrentListChanged(previousList, commitCallback);
      return;
  }
  ```
  
  - If oldList is null, Just Insert.
  - *onCurrentListChanged* method call.
  ```java
  if (mList == null) {
      mList = newList;
      mReadOnlyList = Collections.unmodifiableList(newList);
      // notify last, after list is updated
      mUpdateCallback.onInserted(0, newList.size());
      onCurrentListChanged(previousList, commitCallback);
      return;
  }
  ```

  - If oldList is not null and newList is not null, Compare in *background Thread* using Handler.
  - *latchList* method call.
  ```java
  final List<T> oldList = mList;
  mConfig.getBackgroundThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
          final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
              @Override
              public int getOldListSize() {
                  return oldList.size();
              }
  
              @Override
              public int getNewListSize() {
                  return newList.size();
              }
  
              @Override
              public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                  T oldItem = oldList.get(oldItemPosition);
                  T newItem = newList.get(newItemPosition);
                  if (oldItem != null && newItem != null) {
                      return mConfig.getDiffCallback().areItemsTheSame(oldItem, newItem);
                  }
                  // If both items are null we consider them the same.
                  return oldItem == null && newItem == null;
              }
  
              @Override
              public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                  T oldItem = oldList.get(oldItemPosition);
                  T newItem = newList.get(newItemPosition);
                  if (oldItem != null && newItem != null) {
                      return mConfig.getDiffCallback().areContentsTheSame(oldItem, newItem);
                  }
                  if (oldItem == null && newItem == null) {
                      return true;
                  }
                  throw new AssertionError();
              }
  
              @Nullable
              @Override
              public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                  T oldItem = oldList.get(oldItemPosition);
                  T newItem = newList.get(newItemPosition);
                  if (oldItem != null && newItem != null) {
                      return mConfig.getDiffCallback().getChangePayload(oldItem, newItem);
                  }
                  throw new AssertionError();
              }
          });
  
          mMainThreadExecutor.execute(new Runnable() {
              @Override
              public void run() {
                  if (mMaxScheduledGeneration == runGeneration) {
                      latchList(newList, result, commitCallback);
                  }
              }
          });
      }
  });
  ```
<br>

- `latchList(List<T> newList, DiffUtil.DiffResult diffResult, Runnable commitCallback)`
```java
void latchList(
    @NonNull List<T> newList,
    @NonNull DiffUtil.DiffResult diffResult,
    @Nullable Runnable commitCallback
) {
    final List<T> previousList = mReadOnlyList;
    mList = newList;
    // notify last, after list is updated
    mReadOnlyList = Collections.unmodifiableList(newList);
    diffResult.dispatchUpdatesTo(mUpdateCallback);
    // onCurrentListChanged method call.
    onCurrentListChanged(previousList, commitCallback);
}
```
<br>

- `onCurrentListChanged(List<T> previousList, Runnable commitCallback)`
```java
private void onCurrentListChanged(
    @NonNull List<T> previousList,
    @Nullable Runnable commitCallback
) {
    // current list is always mReadOnlyList
    for (ListListener<T> listener : mListeners) {
        listener.onCurrentListChanged(previousList, mReadOnlyList);
    }
    if (commitCallback != null) {
        commitCallback.run();
    }
}
``` 
<br>

#### 3-2-1. AsyncDifferConfig&lt;T>
[Read more](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:recyclerview/recyclerview/src/main/java/androidx/recyclerview/widget/AsyncDifferConfig.java)
<br>

#### 3-3. DiffUtil.ItemCallback&lt;T>
- The ListAdapter constructor takes a DiffUtil.ItemCallback&lt;T>.
- This is Callback for calculating the difference between two non-null items.
- Unlike DiffUtil.Callback, This only handles *item diffing*.
- The meaning of each method is the same as that of [DiffUtil.Callback](#2-1-diffutilcallback).
```java
public abstract static class ItemCallback<T> {
    public abstract boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem);

    public abstract boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem);

    @SuppressWarnings({"WeakerAccess", "unused"})
    @Nullable
    public Object getChangePayload(@NonNull T oldItem, @NonNull T newItem) {
        return null;
    }
}
```
<br>

#### 3-3-1. The example of DiffUtil.ItemCallback&lt;T>
```kotlin
private val puzzleDiffUtil = object : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
}
```
<br>

#### 3-4. The usage of ListAdapter
- PuzzleAdapter extends ListAdapter so hasn't a list that holds data.
- List is submitted using [submitList(List&lt;T> list, Runnable commitCallback)](#3-1-the-methods-of-listadapter)
- Runnable can be executed after list is submitted.
```kotlin
class PuzzleAdapter : ListAdapter<Int, RecyclerView.ViewHolder>(puzzleDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}

viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.puzzle.collect { puzzle ->
            puzzle?.let {
                puzzleAdapter.submitList(puzzle) {
                    viewModel.clearCheck()
                }
            }
        }
    }
}
```
<br>

##### Reference

- https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
- https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil
- https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil.Callback
- https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil.DiffResult
- https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil.ItemCallback
- https://developer.android.com/reference/androidx/recyclerview/widget/ListUpdateCallback
- https://developer.android.com/reference/androidx/recyclerview/widget/AdapterListUpdateCallback
- https://developer.android.com/reference/androidx/recyclerview/widget/BatchingListUpdateCallback
- https://developer.android.com/reference/androidx/recyclerview/widget/AsyncListDiffer
- https://developer.android.com/codelabs/kotlin-android-training-recyclerview-fundamentals#0
- https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding#0
