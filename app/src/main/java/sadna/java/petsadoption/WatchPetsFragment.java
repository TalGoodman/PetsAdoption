package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sadna.java.petsadoption.databinding.FragmentWatchPetsBinding;

public class WatchPetsFragment extends Fragment implements View.OnClickListener {
    public static final int MAX_LIST_SIZE = 50;

    private FragmentWatchPetsBinding binding;

    private RecyclerView recyclerView;

    private ProgressDialog progress;


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
        

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //ToDo: Method invocation 'getUid' may produce 'NullPointerException'
        List<ParseObject> pets_list = DatabaseHandler.getPetsOfOtherUsers(currentUserId);
        List<ParseObject> not_requested_pets_list = DatabaseHandler.getNotRequestedPets(currentUserId);


        ListAdapter adapter = new ListAdapter(pets_list, not_requested_pets_list, WatchPetsFragment.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        progress = ProgressDialog.show(getContext(), "Filtering", "Please wait...");
        String specie = binding.spSpecieFilter.getSelectedItem().toString();
        String sex = binding.spSexFilter.getSelectedItem().toString();
        String diet = binding.spDietFilter.getSelectedItem().toString();
        boolean vaccinated = binding.cbVaccinatedFilter.isChecked();

        Map<String, Object> filterMap = new TreeMap<>();

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

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //ToDo: Method invocation 'getUid' may produce 'NullPointerException'
        List<ParseObject> pets_list = DatabaseHandler.getPetsByKeysAndValues(currentUserId, filterMap);
        List<ParseObject> not_requested_pets_list = DatabaseHandler.getNotRequestedPets(currentUserId);

        ListAdapter adapter = new ListAdapter(pets_list, not_requested_pets_list, WatchPetsFragment.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (progress != null && progress.isShowing()){
            progress.dismiss();
        }
    }

    public void onResume() {
        if (progress != null && progress.isShowing()){
            progress.dismiss();
        }
        super.onResume();
    }
}