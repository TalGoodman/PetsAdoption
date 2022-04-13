package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private boolean isRequested;

    private ProgressDialog progress;

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

        isRequested = getArguments().getBoolean("isRequested");

        petId = getArguments().getString("petId");
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
                    progress = ProgressDialog.show(getContext(), "Requesting", "Wait a second...");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (progress != null && progress.isShowing()){
                                progress.dismiss();
                            }
                        }
                    }, 1500);
                    Toast.makeText(getContext(), "Pet Requested",Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
                }
            });

            binding.btnBackPetDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
                }
            });
            if(isRequested) {
                binding.btnRequestToAdopt.setText("Already Requested");
                binding.btnRequestToAdopt.setEnabled(false);
            }
        } else {
            binding.btnRequestToAdopt.setText("Delete Pet");
            binding.btnRequestToAdopt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler.deletePetByID(petId);
                    Log.d("btnRequestToAdopt.onClick", "DeletePet");
                    Handler handler = new Handler();
                    progress = ProgressDialog.show(getContext(), "Deleteing", "Wait a second...");
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (progress != null && progress.isShowing()){
                                progress.dismiss();
                            }
                        }
                    }, 1500);
                    Toast.makeText(getContext(), "Pet Deleted",Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_OfferToAdoptionFragment);
                }
            });
            binding.btnBackPetDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
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

    public void startShowingProgressDialog(){
        progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
    }

    @Override
    public void onDestroyView() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        super.onDestroyView();
        binding = null;
    }

    public void onResume() {
        if (progress != null && progress.isShowing()){
            progress.dismiss();
        }
        super.onResume();
    }
}