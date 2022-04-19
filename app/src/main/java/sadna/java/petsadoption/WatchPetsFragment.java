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

public class WatchPetsFragment extends Fragment implements View.OnClickListener {
    public static final int ITEMS_TO_LOAD = 3;

    private FragmentWatchPetsBinding binding;

    private RecyclerView recyclerView;
    private PetsListAdapter recyclerViewAdapter;
    private ProgressDialog progress;
    
    private List<ParseObject> pets_list;

    private String currentUserId;

    private boolean isLoading;
    private boolean isFilter;
    private int itemsLoaded;
    private int numberOfPets;

    private Map<String, Object> filterMap;
    


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

        binding.btnFilter.setOnClickListener(this);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isLoading = false;
        isFilter = false;
        itemsLoaded = 0;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid(); //ToDo: Method invocation 'getUid' may produce 'NullPointerException'
            numberOfPets = DatabaseHandler.getNumberOfPetsOfOtherUsers(currentUserId);
        } else {
            currentUserId = null;
            numberOfPets = DatabaseHandler.getNumberOfPets();
        }

        pets_list = new ArrayList<>();

        populateData();
        initAdapter();
        initScrollListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        progress = ProgressDialog.show(getContext(), "Filtering", "Please wait...");
        if (!DatabaseHandler.isConnected(WatchPetsFragment.this.getContext())) {
            Toast.makeText(getActivity(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
            if (progress != null && progress.isShowing()){
                progress.dismiss();
            }
            return;
        }
        isFilter = true;
        isLoading = false;
        itemsLoaded = 0;
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

        numberOfPets = DatabaseHandler.getNumberOfPetsByKeysAndValue(currentUserId, filterMap);
        pets_list = new ArrayList<>();
        populateData();
        initAdapter();
        initScrollListener();
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

    private void populateData() {
        if (currentUserId != null && !isFilter) {
            pets_list = DatabaseHandler.getPetsOfOtherUsersWithLimit(currentUserId, 5, itemsLoaded);
        } else if (isFilter) {
            pets_list = DatabaseHandler.getPetsByKeysAndValuesWithLimit(currentUserId, filterMap, 5, itemsLoaded);
        } else {
            pets_list = DatabaseHandler.getAllPetsWithLimit(5, itemsLoaded);
        }
        itemsLoaded = 5;
    }

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

    private void loadMore() {
        pets_list.add(null);
        recyclerViewAdapter.notifyItemInserted(pets_list.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pets_list.remove(pets_list.size() - 1);
                int scrollPosition = pets_list.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int nextLimit = itemsLoaded + ITEMS_TO_LOAD;
                List<ParseObject> append_list = new ArrayList<>();

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

                //recyclerViewAdapter.notifyDataSetChanged();
                recyclerViewAdapter.notifyItemRangeInserted(pets_list.size() - append_list.size(), append_list.size());
                recyclerView.scrollToPosition(itemsLoaded - 5);
                isLoading = false;
            }
        }, 1500);
    }
}