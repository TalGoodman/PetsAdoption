package sadna.java.petsadoption;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;

import sadna.java.petsadoption.databinding.FragmentAddPetBinding;


public class AddPetFragment extends Fragment {
    private FragmentAddPetBinding binding;

    private ImageView image;
    private Spinner spSpecie;

    private Pet newPet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPetBinding.inflate(inflater, container, false);
        spSpecie = binding.spSpecieContentAdd;
        spSpecie.setSelection(0);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                int Genus = binding.spSpecieContentAdd.getSelectedItemPosition();
                String Name = binding.etPetNameContentAdd.getText().toString();
                Integer Identifier;
                String Breed = binding.etBreedContentAdd.getText().toString();
                int Sex = binding.spSexContentAdd.getSelectedItemPosition();
                Integer Age = Integer.parseInt(binding.etAgeContentAdd.getText().toString());
                Integer Weight = Integer.parseInt(binding.etWeightContentAdd.getText().toString());
                Boolean Vaccinated = binding.cbVaccinatedAdd.isChecked();
                int Diet = binding.spDietContentAdd.getSelectedItemPosition();
                String Description = binding.etDescriptionContentAdd.getText().toString();*/

                NavHostFragment.findNavController(AddPetFragment.this)
                        .navigate(R.id.action_AddPetFragment_to_OfferToAdoptionFragment);
            }
        });

        binding.btnBackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AddPetFragment.this)
                        .navigate(R.id.action_AddPetFragment_to_OfferToAdoptionFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}