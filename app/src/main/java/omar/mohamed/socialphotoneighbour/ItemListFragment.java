package omar.mohamed.socialphotoneighbour;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;


/**
 * A list fragment representing a list of Items. 
 * <p>
 * 
 */
public class ItemListFragment extends ListFragment {

  /**
   * The serialization (saved instance state) Bundle key representing the
   * activated item position. Only used on tablets.
   */
  private static final String STATE_ACTIVATED_POSITION = "activated_position";

  /**
   * The fragment's current callback object, which is notified of list item
   * clicks.
   */
  private Callbacks mCallbacks = sDummyCallbacks;

  /**
   * The current activated item position. Only used on tablets.
   */
  private int mActivatedPosition = ListView.INVALID_POSITION;

  /**
   * A callback interface that all activities containing this fragment must
   * implement. This mechanism allows activities to be notified of item
   * selections.
   */
  interface Callbacks {
    /**
     * Callback for when an item has been selected.
     * @throws IOException Input/Output Exception
     */
    void onItemSelected(String id) throws IOException;
  }

  /**
   * A dummy implementation of the {@link Callbacks} interface that does
   * nothing. Used only when this fragment is not attached to an activity.
   */
  private static Callbacks sDummyCallbacks = new Callbacks() {
    @Override
    public void onItemSelected(String id) {
    }
  };

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public ItemListFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setListAdapter(new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_list_item_activated_1, android.R.id.text1,
        ListContent.ITEMS));
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Restore the previously serialized activated item position.
    if (savedInstanceState != null
        && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
      setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
    }

  }

  @Override
  public void onListItemClick(ListView listView, View view, int position,
      long id) {
    super.onListItemClick(listView, view, position, id);

    // Notify the active callbacks interface (the activity, if the
    // fragment is attached to one) that an item has been selected.
    try {
      mCallbacks.onItemSelected(ListContent.ITEMS.get(position).id);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mActivatedPosition != ListView.INVALID_POSITION) {
      // Serialize and persist the activated item position.
      outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
    }
  }

  /**
   * Turns on activate-on-click mode. When this mode is on, list items will be
   * given the 'activated' state when touched.
   */
  public void setActivateOnItemClick(boolean activateOnItemClick) {
    // When setting CHOICE_MODE_SINGLE, ListView will automatically
    // give items the 'activated' state when touched.
    getListView().setChoiceMode(
        activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
            : ListView.CHOICE_MODE_NONE);
  }

  private void setActivatedPosition(int position) {
    if (position == ListView.INVALID_POSITION) {
      getListView().setItemChecked(mActivatedPosition, false);
    } else {
      getListView().setItemChecked(position, true);
    }

    mActivatedPosition = position;
  }
  
}
