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
import com.parse.ParseObject;
import com.parse.ParseUser;

import sadna.java.petsadoption.databinding.FragmentPetDetailsBinding;

public class PetDetailsFragment extends Fragment implements View.OnClickListener {
    private FragmentPetDetailsBinding binding;

    private String ownerId;
    private String petId;

    private FirebaseUser firebaseUser;
    private String currentUserId;
    private boolean isRequested;


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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = null;
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

        isRequested = getArguments().getBoolean("isRequested");

        petId = getArguments().getString("petId");
        String petName = getArguments().getString("name");
        String petSpecie = getArguments().getString("specie");
        String petSex = getArguments().getString("sex");
        boolean petVaccinated = getArguments().getBoolean("vaccinated");
        String petDiet = getArguments().getString("diet");
        String petDescription = getArguments().getString("description");

        ParseUser ownerParseObject = DatabaseHandler.getUserByID(ownerId);
        String ownerName = ownerParseObject.getUsername();

        binding.tvPetNameContent.setText(petName);
        binding.tvSpecieContent.setText(petSpecie);
        binding.tvSexContent.setText(petSex);
        binding.tvVaccinatedContent.setText(Boolean.toString(petVaccinated));
        binding.tvDietContent.setText(petDiet);
        binding.tvDescriptionContent.setText(petDescription);
        binding.tvOwnerNameContent.setText(ownerName);

        if (currentUserId == null) {
            binding.btnRequestToAdopt.setEnabled(false);
        } else if (ownerId.equals(currentUserId)) {
            binding.btnRequestToAdopt.setText(R.string.bttnTextDeletPet);
        } else {
            if(isRequested) {
                binding.btnRequestToAdopt.setText(R.string.bttnAlreadyRequested);
                binding.btnRequestToAdopt.setTextColor(Color.GREEN);
                binding.btnRequestToAdopt.setEnabled(false);
            }
        }

        //initialize OnClickListener for buttons
        binding.btnBackPetDetails.setOnClickListener(this);
        binding.btnRequestToAdopt.setOnClickListener(this);

        if(getArguments()!=null)
        {
            byte[] data = getArguments().getByteArray("image_data");
            if (data != null)
            {
                final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);
                binding.ivPetImage.setImageBitmap(bmp);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(!ownerId.equals(currentUserId)) {
            if(firebaseUser != null) {
                if (view.getId() == R.id.btnRequestToAdopt) {
                    if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                        Toast.makeText(getActivity(), "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    DatabaseHandler.createMessage(petId, ownerId);
                    Log.d("btnRequestToAdopt.onClick", "RequestToAdaptHere");
                    ProgressDialog progress = ProgressDialog.show(getContext(), "Requesting", "Wait a second...");
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
            }

            if (view.getId() == R.id.btnBackPetDetails) {
                if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.startShowingProgressDialog(getContext());
                NavHostFragment.findNavController(PetDetailsFragment.this)
                        .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
            }
        } else if (ownerId.equals(currentUserId)){
            if (view.getId() == R.id.btnRequestToAdopt) {
                if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    DatabaseHandler.deletePetByID(petId);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error",Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("btnRequestToAdopt.onClick", "deletePetByID");
                Handler handler = new Handler();
                ProgressDialog progress = ProgressDialog.show(getContext(), "Deleting", "Wait a second...");
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (progress != null && progress.isShowing()){
                            progress.dismiss();
                        }
                    }
                }, 1500);
                Toast.makeText(getContext(), "Pet Deleted",Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(PetDetailsFragment.this)
                        .navigate(R.id.action_PetDetailsFragment_to_MyPetsFragment);
            }
            else if (view.getId() == R.id.btnBackPetDetails) {
                if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.startShowingProgressDialog(getContext());
                NavHostFragment.findNavController(PetDetailsFragment.this)
                        .navigate(R.id.action_PetDetailsFragment_to_MyPetsFragment);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}