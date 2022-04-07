package sadna.java.petsadoption;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import sadna.java.petsadoption.databinding.FragmentOfferToAdoptionBinding;


public class OfferToAdoptionFragment extends Fragment {
    public static final int MAX_LIST_SIZE = 50;

    private FragmentOfferToAdoptionBinding binding;

    private RecyclerView recyclerView;
    private ImageView[] imagesList;
    private String[] petsTextList;
    private Button[] buttonsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOfferToAdoptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnOfferPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(OfferToAdoptionFragment.this)
                        .navigate(R.id.action_OfferToAdoptionFragment_to_AddPetFragment);
            }
        });

        binding.btnBackOfferToAdoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(OfferToAdoptionFragment.this)
                        .navigate(R.id.action_OfferToAdoptionFragment_to_WelcomeFragment);
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