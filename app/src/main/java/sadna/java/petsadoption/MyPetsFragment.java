package sadna.java.petsadoption;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sadna.java.petsadoption.databinding.FragmentMyPetsBinding;


public class MyPetsFragment extends Fragment {
    public static final int MAX_LIST_SIZE = 50;

    private FragmentMyPetsBinding binding;

    private RecyclerView recyclerView;
    private ArrayList<Bitmap> imagesList;
    private ArrayList<String> petNamesTextList;
    private ArrayList<String> petSpeciesTextList;
    private ArrayList<Button> buttonsList;
    private String currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyPetsBinding.inflate(inflater, container, false);
        imagesList = new ArrayList<>();
        petNamesTextList = new ArrayList<String>();
        petSpeciesTextList = new ArrayList<String>();
        buttonsList = new ArrayList<Button>();
        recyclerView = binding.rvMyPetsList;
        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            currentUserId = null;
        }

        /****************************************************************/
        //ToDo: Might be possible to filter the existing full pets list instead of getting it again to be more data efficient
        List<ParseObject> pets_list = DatabaseHandler.getUserPets();
        /*
        ListAdapter adapter = new ListAdapter(pets_list, OfferToAdoptionFragment.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));*/


        ListAdapter adapter = new ListAdapter(pets_list, currentUserId, MyPetsFragment.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //ToDo: can we make something to create an item?
        for(int i = 0; i < Objects.requireNonNull(pets_list).size(); i++){
            petNamesTextList.add(i, Objects.requireNonNull(pets_list.get(i).get("pet_name")).toString());
            petSpeciesTextList.add(i, Objects.requireNonNull(pets_list.get(i).get("species")).toString());
            Button button = new Button(getContext());
                button.setText("Show Pet");
                button.setId(i);
                buttonsList.add(i, button);
        }

        /***************************************************************/

        binding.btnOfferPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MyPetsFragment.this)
                        .navigate(R.id.action_MyPetsFragment_to_AddPetFragment);
            }
        });

        binding.btnBackMyPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MyPetsFragment.this)
                        .navigate(R.id.action_MyPetsFragment_to_WelcomeFragment);
            }
        });

        DatabaseHandler.getAllPets();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}