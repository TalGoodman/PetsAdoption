package sadna.java.petsadoption;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import sadna.java.petsadoption.databinding.PetsListItemBinding;

public class ListItem extends Fragment{
    private PetsListItemBinding binding;

    private String petId;
    private ImageView petImage;
    private String petText;
    private Button btnView;

    public ListItem(ImageView petImage, String petText, Button btnView) {
        this.petImage = petImage;
        this.petText = petText;
        this.btnView = btnView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PetsListItemBinding.inflate(inflater, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.btnViewPetRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(getParentFragment())
                        .navigate(R.id.action_WatchPetsFragment_to_PetDetailsFragment);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}