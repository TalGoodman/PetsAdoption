package sadna.java.petsadoption;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import sadna.java.petsadoption.databinding.FragmentPetDetailsBinding;

public class PetDetailsFragment extends Fragment {
    private FragmentPetDetailsBinding binding;

    private String petId;
    private String petName;
    private String petSpecie;
    private String petSex;
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

        petId = getArguments().getString("id");
        petName = getArguments().getString("name");
        petSpecie = getArguments().getString("specie");
        petSex = getArguments().getString("sex");
        ownerId = getArguments().getString("ownerId");

        binding.tvPetNameContent.setText(petName);
        binding.tvSpecieContent.setText(petSpecie);
        binding.tvSexContent.setText(petSex);

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