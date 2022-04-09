package sadna.java.petsadoption;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ArrayList<Bitmap> petImagesList;
    private ArrayList<String> petNamesTextList;
    private ArrayList<String> petSpeciesTextList;
    private ArrayList<String> petIdsList;
    private Fragment fragment;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView petImage;
        public TextView petName;
        public TextView petSpecie;
        public Button btnView;

        public ViewHolder(View v) {
            super(v);
            petImage = (ImageView) v.findViewById(R.id.ivPetSmallImage);
            petName = (TextView) v.findViewById(R.id.tvPetNameRV);
            petSpecie = (TextView) v.findViewById(R.id.tvPetSpeciesRV);
            btnView = (Button) v.findViewById(R.id.btnViewPetRV);
        }
    }

    public ListAdapter(ArrayList<Bitmap> petImagesList, ArrayList<String> petNamesList, ArrayList<String> petSpeciesTextList, ArrayList<String> petIdsList, Fragment fragment) {
        this.petImagesList = petImagesList;
        this.petNamesTextList = petNamesList;
        this.petSpeciesTextList = petSpeciesTextList;
        this.petIdsList = petIdsList;
        this.fragment = fragment;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pets_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String petName = petNamesTextList.get(position);
        String petSpecie = petSpeciesTextList.get(position);
        //holder.petImage.setImageBitmap(petImagesList.get(position));
        DatabaseHandler.addImage(holder, petName);
        String petId = petIdsList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", petId);
        holder.petName.setText(petName);
        holder.petSpecie.setText(petSpecie);
        holder.btnView .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_WatchPetsFragment_to_PetDetailsFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return petNamesTextList.size();
    }
}
