package sadna.java.petsadoption;

import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private List<ParseObject> petsList;
    private List<ParseObject> not_requested_pets_list;
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

    public ListAdapter(List<ParseObject> petsList, List<ParseObject> not_requested_pets_list, Fragment fragment) {
        this.petsList = petsList;
        this.not_requested_pets_list = not_requested_pets_list;
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
        String petName = petsList.get(position).get("pet_name").toString();
        String petSpecie = petsList.get(position).get("species").toString();
        DatabaseHandler.getPetImage(petsList.get(position), holder);
        holder.petName.setText(petName);
        holder.petSpecie.setText(petSpecie);
        Bundle bundle = createBundle(petsList.get(position));
        holder.btnView .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragment.getClass() == WatchPetsFragment.class){
                    NavHostFragment.findNavController(fragment)
                            .navigate(R.id.action_WatchPetsFragment_to_PetDetailsFragment, bundle);
                } else if(fragment.getClass() == OfferToAdoptionFragment.class){
                    NavHostFragment.findNavController(fragment)
                            .navigate(R.id.action_OfferToAdoptionFragment_to_PetDetailsFragment, bundle);
                }
            }
        });
        if(requested(petsList.get(position).get("pet_id").toString())){
            bundle.putBoolean("isRequested", true);
            holder.btnView.setText("View Pet\n(Requested)");
            holder.btnView.setTextColor(Color.GREEN);
        } else {
            bundle.putBoolean("isRequested", false);
        }
    }

    @Override
    public int getItemCount() {
        return petsList.size();
    }

    private Bundle createBundle(ParseObject object){
        Bundle bundle = new Bundle();
        String name = object.get("pet_name").toString();
        String specie = object.get("species").toString();
        String petId = object.get("pet_id").toString();
        String sex = object.get("gander").toString();
        String ownerId = object.get("owner_id").toString();
        bundle.putString("name", name);
        bundle.putString("specie", specie);
        bundle.putString("petId", petId);
        bundle.putString("sex", sex);
        bundle.putString("ownerId", ownerId);
        ParseFile parseFile = (ParseFile) object.get("pet_image");
        try {
            bundle.putByteArray("image_data", parseFile.getData());
        } catch (ParseException e) {
            bundle.putByteArray("image_data", null);
            e.printStackTrace();
        }

        return bundle;
    }

    private boolean requested(String pet_id) {
        for(int i = 0; i < not_requested_pets_list.size(); i++){
            String pet_num_i_id = not_requested_pets_list.get(i).get("pet_id").toString();
            if(pet_id.equals(pet_num_i_id)){
                return false;
            }
        }
        return true;
    }
}
