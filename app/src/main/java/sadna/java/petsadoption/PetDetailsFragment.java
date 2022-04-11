package sadna.java.petsadoption;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;

import sadna.java.petsadoption.databinding.FragmentPetDetailsBinding;

public class PetDetailsFragment extends Fragment {
    private FragmentPetDetailsBinding binding;

    private String petId;
    private String petName;
    private String petSpecie;
    private String petSex;
    private Boolean petVaccinated;
    private String ownerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPetDetailsBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ownerId = getArguments().getString("ownerId");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(currentUserId.equals(ownerId)){
            binding.btnRequestToAdopt.setVisibility(View.INVISIBLE);
        }

        petId = getArguments().getString("id");
        petName = getArguments().getString("name");
        petSpecie = getArguments().getString("specie");
        petSex = getArguments().getString("sex");
        petVaccinated = getArguments().getBoolean("vaccinated");

        binding.tvPetNameContent.setText(petName);
        binding.tvSpecieContent.setText(petSpecie);
        binding.tvSexContent.setText(petSex);
        binding.tvVaccinatedContent.setText(petVaccinated.toString());

        if(!currentUserId.equals(ownerId)) {
            binding.btnRequestToAdopt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler.createMessage(petId, ownerId);
                    Log.d("btnRequestToAdopt.onClick", "RequestToAdaptHere");
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
                }
            });

            binding.btnBackPetDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
                }
            });
        } else {
            binding.btnRequestToAdopt.setVisibility(View.INVISIBLE);
            binding.btnBackPetDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_OfferToAdoptionFragment);
                }
            });
        }

        byte[] data = getArguments().getByteArray("image_data");
        if (data != null){
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);
            binding.ivPetImage.setImageBitmap(bmp);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}