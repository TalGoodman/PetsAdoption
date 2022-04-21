package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sadna.java.petsadoption.databinding.FragmentWatchPetsBinding;

//WatchPetsFragment is a fragment holding a list of available pets
//and also has an option to filter the pets list
//The class implements the OnClickListener for the filter button
public class WatchPetsFragment extends Fragment implements View.OnClickListener {
    public static final int ITEMS_TO_LOAD = 3;
    public static final int FIRST_ITEMS_TO_LOAD = 5;

    private FragmentWatchPetsBinding binding;

    //the recycler view's items are "pet_list_item" layouts used for displaying pets
    private RecyclerView recyclerView;
    private PetsListAdapter recyclerViewAdapter;
    private ProgressDialog progress;

    //holds a list of ParseObjects of pets obtained from the database
    private List<ParseObject> pets_list;

    //the id of the current user
    private String currentUserId;

    private boolean isLoading;  //indicates whether are new pets currently loaded
    private boolean isFilter;   //indicates whether the filter button was clicked
    private int itemsLoaded;    //counts the number of pets already loaded
    private int numberOfPets;   //holds the number of relevant pets in the database

    private Map<String, Object> filterMap;  //contains values for filtering
    


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWatchPetsBinding.inflate(inflater, container, false);
        recyclerView = binding.rvWatchPetsList;

        // Spinner Drop down elements
        List<String> categories = DatabaseHandler.getSpeciesNames();

        //Add "Any" to the categories
        categories.add(0, "Any");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( binding.spSpecieFilter.getContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        binding.spSpecieFilter.setAdapter(dataAdapter);

        //initializing the OnClickListener of btnFilter to be the current class
        binding.btnFilter.setOnClickListener(this);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isLoading = false;  //initializing
        isFilter = false;   //initializing
        itemsLoaded = 0;    //initializing

        //initialize currentUserId and numberOfPets
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
            numberOfPets = DatabaseHandler.getNumberOfPetsOfOtherUsers(currentUserId);
        } else {
            currentUserId = null;
            numberOfPets = DatabaseHandler.getNumberOfPets();
        }

        pets_list = new ArrayList<>();

        //initialize recyclerView and recyclerViewAdapter
        populateData();
        initAdapter();
        initScrollListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //OnClick implementation for btnFilter
    @Override
    public void onClick(View view) {
        progress = ProgressDialog.show(getContext(), "Filtering", "Please wait...");
        //check if there's internet connection
        if (!DatabaseHandler.isConnected(WatchPetsFragment.this.getContext())) {
            Toast.makeText(getActivity(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
            if (progress != null && progress.isShowing()){
                progress.dismiss();
            }
            return;
        }
        isFilter = true;    //indicates that the filter button was clicked
        isLoading = false;
        itemsLoaded = 0;

        //initialize filterMap according to the input from the Spinner and the Checkbox
        filterMap = new TreeMap<>();
        String specie = binding.spSpecieFilter.getSelectedItem().toString();
        String sex = binding.spSexFilter.getSelectedItem().toString();
        String diet = binding.spDietFilter.getSelectedItem().toString();
        boolean vaccinated = binding.cbVaccinatedFilter.isChecked();

        if (!specie.equals("Any")) {
            filterMap.put("species", specie);
        }
        if (!sex.equals("Any Sex")){
            filterMap.put("gander", sex);
        }
        if (!diet.equals("Any Diet")) {
            filterMap.put("diet", diet);
        }
        filterMap.put("vaccinated", vaccinated);

        //update numberOfPets to the number of pets in the database
        //which are still relevant after the filter
        numberOfPets = DatabaseHandler.getNumberOfPetsByKeysAndValue(currentUserId, filterMap);
        pets_list = new ArrayList<>();

        //reload recyclerView and recyclerViewAdapter
        populateData();
        initAdapter();
        initScrollListener();

        //dismiss progress dialog
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progress != null && progress.isShowing()){
                    progress.dismiss();
                }
            }
        }, 1000);
    }

    //read the first items from the database
    private void populateData() {
        if (currentUserId != null && !isFilter) {
            pets_list = DatabaseHandler.getPetsOfOtherUsersWithLimit(currentUserId, FIRST_ITEMS_TO_LOAD, itemsLoaded);
        } else if (isFilter) {
            pets_list = DatabaseHandler.getPetsByKeysAndValuesWithLimit(currentUserId, filterMap, FIRST_ITEMS_TO_LOAD, itemsLoaded);
        } else {
            pets_list = DatabaseHandler.getAllPetsWithLimit(FIRST_ITEMS_TO_LOAD, itemsLoaded);
        }
        itemsLoaded = FIRST_ITEMS_TO_LOAD;
    }

    //initialize the recyclerview adapter
    private void initAdapter() {
        recyclerViewAdapter = new PetsListAdapter(pets_list, currentUserId, WatchPetsFragment.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                MainActivity.dismissProgressDialog();
            }
        });
    }

    //initialize scroll listener for the recycler view
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (itemsLoaded >= numberOfPets) {
                    recyclerView.removeOnScrollListener(this);
                    return;
                }

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!isLoading) {
                    if (linearLayoutManager != null && lastCompletelyVisibleItemPosition == itemsLoaded - 1) {
                        if (!DatabaseHandler.isConnected(WatchPetsFragment.this.getContext())) {
                            Toast.makeText(getActivity(), "No Internet Connection",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }

    //load more items
    private void loadMore() {
        pets_list.add(null);
        recyclerViewAdapter.notifyItemInserted(pets_list.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remove the temporary null at the end of pets_list and notfy the adapter
                pets_list.remove(pets_list.size() - 1);
                int scrollPosition = pets_list.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int nextLimit = itemsLoaded + ITEMS_TO_LOAD;
                List<ParseObject> append_list = new ArrayList<>();

                //read more pets from the database and add them to pets_list
                if (itemsLoaded < nextLimit) {
                    if (currentUserId != null && !isFilter) {
                        append_list = DatabaseHandler.getPetsOfOtherUsersWithLimit(currentUserId, ITEMS_TO_LOAD, itemsLoaded);
                    } else if (isFilter) {
                        append_list = DatabaseHandler.getPetsByKeysAndValuesWithLimit(currentUserId, filterMap, ITEMS_TO_LOAD, itemsLoaded);
                    } else {
                        append_list = DatabaseHandler.getAllPetsWithLimit(ITEMS_TO_LOAD, itemsLoaded);
                    }
                    pets_list.addAll(append_list);
                    itemsLoaded += ITEMS_TO_LOAD;
                    if (itemsLoaded > numberOfPets) {
                        itemsLoaded = numberOfPets;
                    }
                }

                //update the recycler view
                recyclerViewAdapter.notifyItemRangeInserted(pets_list.size() - append_list.size(), append_list.size());
                recyclerView.scrollToPosition(itemsLoaded - 5);
                isLoading = false;
            }
        }, 1500);
    }
}