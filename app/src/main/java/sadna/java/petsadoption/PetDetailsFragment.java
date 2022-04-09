package sadna.java.petsadoption;

import android.app.Activity;
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

import sadna.java.petsadoption.databinding.FragmentPetDetailsBinding;

public class PetDetailsFragment extends Fragment {
    private FragmentPetDetailsBinding binding;

    private String petId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPetDetailsBinding.inflate(inflater, container, false);
        //petId = getArguments().getString("id");
        //String text = "Hello";
        //binding.tvPetNameContent.setText(text);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*((Activity) getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.tvPetNameContent.
            }
        });*/
        petId = getArguments().getString("id");
        binding.tvPetNameContent.setText(petId);
        //binding.tvPetNameContent.setText("Hello");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}