package sadna.java.petsadoption;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import sadna.java.petsadoption.databinding.FragmentWatchPetsBinding;

public class WatchPetsFragment extends Fragment {
    public static final int MAX_LIST_SIZE = 50;

    private FragmentWatchPetsBinding binding;

    private RecyclerView recyclerView;
    private ArrayList<Bitmap> imagesList;
    private ArrayList<String> petNamesTextList;
    private ArrayList<String> petSpeciesTextList;
    private ArrayList<Button> buttonsList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,

            Bundle savedInstanceState
    ) {

        binding = FragmentWatchPetsBinding.inflate(inflater, container, false);
        imagesList = new ArrayList<>();
        petNamesTextList = new ArrayList<String>();
        petSpeciesTextList = new ArrayList<String>();
        buttonsList = new ArrayList<Button>();
        recyclerView = binding.rvWatchPetsList;
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<ParseObject> pets_list = DatabaseHandler.getAllPets();


        for(int i = 0; i < pets_list.size(); i++){
            petNamesTextList.add(i, pets_list.get(i).get("pet_name").toString());
            petSpeciesTextList.add(i, pets_list.get(i).get("species").toString());
            Button button = new Button(getContext());
            button.setText("Show Pet");
            buttonsList.add(i, button);
        }
        ListAdapter adapter = new ListAdapter(petNamesTextList, petSpeciesTextList, buttonsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}