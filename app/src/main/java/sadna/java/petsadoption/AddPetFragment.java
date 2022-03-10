package sadna.java.petsadoption;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.FileNotFoundException;
import java.io.InputStream;

import sadna.java.petsadoption.databinding.FragmentAddPetBinding;


public class AddPetFragment extends Fragment {
    private FragmentAddPetBinding binding;

    private Bitmap selectedImage;
    private Pet newPet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPetBinding.inflate(inflater, container, false);
        binding.spSpecieContentAdd.setSelection(0);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Specie = binding.spSpecieContentAdd.getSelectedItemPosition();
                String Name = binding.etPetNameContentAdd.getText().toString();
                if (Name.equals("")) {
                    Toast.makeText(getActivity(), "Invalid Pet Name",Toast.LENGTH_LONG).show();
                    return;
                }
                Integer Identifier = null;  //will be set later after the pet is added to the DB
                String Breed = binding.etBreedContentAdd.getText().toString();
                int Sex = binding.spSexContentAdd.getSelectedItemPosition();
                Integer Age;
                if (binding.etAgeContentAdd.getText().toString().equals("")) {
                    Age = null;
                } else {
                    try {
                        Age = Integer.parseInt(binding.etAgeContentAdd.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Invalid Age Input",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Integer Weight;
                if (binding.etWeightContentAdd.getText().toString().equals("")) {
                    Weight = null;
                } else {
                    try {
                        Weight = Integer.parseInt(binding.etWeightContentAdd.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Invalid Weight Input",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Boolean Vaccinated = binding.cbVaccinatedAdd.isChecked();
                int Diet = binding.spDietContentAdd.getSelectedItemPosition();
                String Description = binding.etDescriptionContentAdd.getText().toString();

                Pet newPet = new Pet(selectedImage, Specie, Name, Identifier, Breed, Sex, Age,
                        Weight, Vaccinated, Diet, Description);

                //TODO: add newPet to the database and then set newPet's Identifier

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

        binding.btnSetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 9002);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 9002){
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    binding.ivPetImageAdd.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}