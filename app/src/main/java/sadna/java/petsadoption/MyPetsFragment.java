package sadna.java.petsadoption;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseObject;

import java.util.List;

import sadna.java.petsadoption.databinding.FragmentMyPetsBinding;

//MyPetsFragment is a fragment holding a list of pets added by the current user
public class MyPetsFragment extends Fragment {
    private FragmentMyPetsBinding binding;

    //the recycler view's items are "pet_list_item" layouts used for displaying pets
    private RecyclerView recyclerView;
    private String currentUserId;   //the id of the current user

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyPetsBinding.inflate(inflater, container, false);
        recyclerView = binding.rvMyPetsList;
        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize currentUserId
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            currentUserId = fbUser.getUid();
        } else {
            currentUserId = null;
        }

        //read the relevant pets from the database to pets_list
        List<ParseObject> pets_list = DatabaseHandler.getUserPetsAsync(currentUserId);


        //initialize recyclerView and recyclerViewAdapter
        PetsListAdapter adapter = new PetsListAdapter(pets_list, currentUserId, MyPetsFragment.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                MainActivity.dismissProgressDialog();
            }
        });

        //navigate to AddPetFragment
        binding.btnOfferPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DatabaseHandler.isConnected(MyPetsFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                NavHostFragment.findNavController(MyPetsFragment.this)
                        .navigate(R.id.action_MyPetsFragment_to_AddPetFragment);
            }
        });

        //navigate to WelcomeFragment
        binding.btnBackMyPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MyPetsFragment.this)
                        .navigate(R.id.action_MyPetsFragment_to_WelcomeFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}