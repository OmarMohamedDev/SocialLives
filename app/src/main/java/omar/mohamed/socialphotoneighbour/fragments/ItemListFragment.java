package omar.mohamed.socialphotoneighbour.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import omar.mohamed.socialphotoneighbour.R;
import omar.mohamed.socialphotoneighbour.activities.ItemDetailActivity;
import omar.mohamed.socialphotoneighbour.classes.ListContent;

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ItemDetailFragment}.
 * <p>
 *
 */
public class ItemListFragment extends ListFragment {

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
            R.layout.cardview_item_list, R.id.card_view_item_list_title,
        ListContent.ITEMS));

  }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);
    }

    @Override
  public void onListItemClick(ListView listView, View view, int position,
      long id) {
    super.onListItemClick(listView, view, position, id);

      try {
        Thread.sleep(1200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    // Start the detail activity for the selected item ID.
    Intent detailIntent = new Intent(getContext(), ItemDetailActivity.class);
    detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
    startActivity(detailIntent);
  }
  
}
