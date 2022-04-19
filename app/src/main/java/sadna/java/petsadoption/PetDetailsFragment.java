package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.google.firebase.auth.FirebaseUser;

import sadna.java.petsadoption.databinding.FragmentPetDetailsBinding;

public class PetDetailsFragment extends Fragment {
    private FragmentPetDetailsBinding binding;

    private String ownerId;
    private String petId;


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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = null;
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

        boolean isRequested = getArguments().getBoolean("isRequested");

        petId = getArguments().getString("petId");
        String petName = getArguments().getString("name");
        String petSpecie = getArguments().getString("specie");
        String petSex = getArguments().getString("sex");
        boolean petVaccinated = getArguments().getBoolean("vaccinated");
        String petDiet = getArguments().getString("diet");
        String petDescription = getArguments().getString("description");

        binding.tvPetNameContent.setText(petName);
        binding.tvSpecieContent.setText(petSpecie);
        binding.tvSexContent.setText(petSex);
        binding.tvVaccinatedContent.setText(Boolean.toString(petVaccinated));
        binding.tvDietContent.setText(petDiet);
        binding.tvDescriptionContent.setText(petDescription);

        if(firebaseUser != null && !currentUserId.equals(ownerId)) {
            binding.btnRequestToAdopt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                        Toast.makeText(getActivity(), "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
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
                    if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                        Toast.makeText(getActivity(), "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
                }
            });
            if(isRequested) {
                binding.btnRequestToAdopt.setText(R.string.bttnAlreadyRequested);
                binding.btnRequestToAdopt.setTextColor(Color.GREEN);
                binding.btnRequestToAdopt.setEnabled(false);
            }
        } else if (ownerId.equals(currentUserId)){
            binding.btnRequestToAdopt.setText(R.string.bttnTextDeletPet);
            binding.btnRequestToAdopt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                        Toast.makeText(getActivity(), "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    String toastString = DatabaseHandler.deletePetByID(petId);
                    Log.d("btnRequestToAdopt.onClick", "deletePetByID");
                    Handler handler = new Handler();
                    progress = ProgressDialog.show(getContext(), "Deleting", "Wait a second...");
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (progress != null && progress.isShowing()){
                                progress.dismiss();
                            }
                        }
                    }, 1500);
                    Toast.makeText(getContext(), toastString,Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_MyPetsFragment);
                }
            });
            binding.btnBackPetDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                        Toast.makeText(getActivity(), "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_MyPetsFragment);
                }
            });
        } else if (currentUserId == null) {
            binding.btnRequestToAdopt.setEnabled(false);
            binding.btnBackPetDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                        Toast.makeText(getActivity(), "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
                }
            });
        }

        if(getArguments()!=null)
        {
            byte[] data = getArguments().getByteArray("image_data");
            if (data != null)
            {
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);
                binding.ivPetImage.setImageBitmap(bmp);
            }
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