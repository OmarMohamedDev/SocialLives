package omar.mohamed.socialphotoneighbour.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import omar.mohamed.socialphotoneighbour.adapters.GridViewAdapter;
import omar.mohamed.socialphotoneighbour.classes.ImageInfo;
import omar.mohamed.socialphotoneighbour.R;
import omar.mohamed.socialphotoneighbour.activities.FullScreenActivity;
import omar.mohamed.socialphotoneighbour.activities.ItemListActivity;


public class PhotoGalleryFragment extends Fragment {
    public  static ArrayList<ImageInfo> actualImagesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_gallery, container,
            false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gv = (GridView) getView().findViewById(R.id.grid_view);
        actualImagesList = ItemListActivity.closestImagesList;
        if (gv != null) {
            if (actualImagesList != null) {
                GridViewAdapter adapter = new GridViewAdapter(getActivity(), actualImagesList);
                gv.setAdapter(adapter);
                gv.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {

                        // Sending image url to FullScreenActivity
                        Intent i = new Intent(getActivity().getApplicationContext(), FullScreenActivity.class);

                        i.putExtra("position", position);
                        startActivity(i);
                    }
                });

            }
        } else {
            Toast.makeText(getContext(), R.string.grid_view_not_found, Toast.LENGTH_SHORT).show();
        }
    }
 }