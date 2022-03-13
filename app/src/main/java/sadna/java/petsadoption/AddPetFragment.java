package sadna.java.petsadoption;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import sadna.java.petsadoption.databinding.FragmentAddPetBinding;


public class AddPetFragment extends Fragment {
    private FragmentAddPetBinding binding;

    private byte[] petImageByteArray;
    private Pet newPet;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPetBinding.inflate(inflater, container, false);
        binding.spSpecieContentAdd.setSelection(0);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Add Pet Button
        binding.btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Specie = binding.spSpecieContentAdd.getSelectedItemPosition();
                String Name = binding.etPetNameContentAdd.getText().toString();
                if (Name.equals("")) {
                    Toast.makeText(getActivity(), "Invalid Pet Name",Toast.LENGTH_LONG).show();
                    return;
                }
                Integer Identifier = 0;  //will be set later after the pet is added to the DB
                String userID = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getUid();
                int Sex = binding.spSexContentAdd.getSelectedItemPosition();
                Boolean Vaccinated = binding.cbVaccinatedAdd.isChecked();
                int Diet = binding.spDietContentAdd.getSelectedItemPosition();
                String Description = binding.etDescriptionContentAdd.getText().toString();

                Pet newPet = new Pet(petImageByteArray, Specie, Name, Identifier, userID, Sex,
                        Vaccinated, Diet, Description);
                String petJSON = newPet.toJSON();
                Toast.makeText(getActivity(), petJSON, Toast.LENGTH_LONG).show();
                //TODO: add newPet to the database and then set newPet's Identifier

                //Web Request To GScript
                String my_url = "https://script.google.com/macros/s/AKfycbwN-2oGeltVPkN6QbeO244cF_M-W3ia_F_Mjw0D0Gw3IkJLUIejnON1XUEwt_cjof08/exec";
                String my_data = petJSON;// Replace this with your data
                MyHttpRequestTask api_request = new MyHttpRequestTask();
                Log.e("PetAdded",petJSON);
                api_request.execute(my_url,my_data);

                NavHostFragment.findNavController(AddPetFragment.this)
                        .navigate(R.id.action_AddPetFragment_to_OfferToAdoptionFragment);
            }
        });

        //Add Photo Button
        binding.btnSetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 9002);
            }
        });

        //Back Button
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 9002){
                final Uri imageUri;
                InputStream imageStream = null;
                ByteArrayOutputStream byteStream = null;
                try {
                    imageUri = data.getData();
                    imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    byteStream = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteStream);
                    petImageByteArray = byteStream.toByteArray();
                    if (petImageByteArray.length > 15000000) {
                        Toast.makeText(getActivity(), "File size must be at most 15MB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    binding.ivPetImageAdd.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                } finally {
                    if (imageStream != null){
                        try {
                            imageStream.close();
                        } catch (IOException ioException) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if (byteStream != null) {
                        try {
                            byteStream.close();
                        } catch (IOException ioException) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}