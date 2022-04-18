package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<ParseObject> petsList;
    private String currentUserId;
    private Fragment fragment;


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView petImage;
        public TextView petName;
        public TextView petSpecie;
        public Button btnView;

        public ItemViewHolder(View v) {
            super(v);
            petImage = (ImageView) v.findViewById(R.id.ivPetSmallImage);
            petName = (TextView) v.findViewById(R.id.tvPetNameRV);
            petSpecie = (TextView) v.findViewById(R.id.tvPetSpeciesRV);
            btnView = (Button) v.findViewById(R.id.btnViewPetRV);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.pbItemLoading);
        }
    }

    //ToDo: is it possible to do it with a list of requested pets? it should be much smaller
    public ListAdapter(List<ParseObject> petsList, String currentUserId, Fragment fragment) {
        this.petsList = petsList;
        this.currentUserId = currentUserId;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_ITEM) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pets_list_item, parent, false);
            return new ItemViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            return;
        }
        ListAdapter.ItemViewHolder itemHolder = (ListAdapter.ItemViewHolder)holder;
        String petName = petsList.get(position).get("pet_name").toString();
        String petSpecie = petsList.get(position).get("species").toString();
        String petId = petsList.get(position).get("pet_id").toString();
        DatabaseHandler.getPetImage(petsList.get(position), itemHolder);
        Bundle bundle = createBundle(petsList.get(position));
        boolean isRequested = DatabaseHandler.findIfPetRequested(petId, currentUserId);
        if (isRequested) {
            bundle.putBoolean("isRequested", true);
        } else {
            bundle.putBoolean("isRequested", false);
        }

        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemHolder.petName.setText(petName);
                itemHolder.petSpecie.setText(petSpecie);
                if (isRequested) {
                    itemHolder.btnView.setText("Details\n(Requested)");
                    itemHolder.btnView.setTextColor(Color.GREEN);
                } else {
                    itemHolder.btnView.setText("Details");
                    itemHolder.btnView.setTextColor(Color.BLACK);
                }

                itemHolder.btnView .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(fragment.getClass() == WatchPetsFragment.class){
                            NavHostFragment.findNavController(fragment)
                                    .navigate(R.id.action_WatchPetsFragment_to_PetDetailsFragment, bundle);
                        } else if(fragment.getClass() == MyPetsFragment.class){
                            NavHostFragment.findNavController(fragment)
                                    .navigate(R.id.action_MyPetsFragment_to_PetDetailsFragment, bundle);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return petsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (petsList.get(position) == null) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    private Bundle createBundle(ParseObject object){
        Bundle bundle = new Bundle();
        String name = object.get("pet_name").toString();
        String specie = object.get("species").toString();
        String petId = object.get("pet_id").toString();
        String sex = object.get("gander").toString();
        String diet = object.get("diet").toString();
        boolean vaccinated = object.getBoolean("vaccinated");
        String description = object.get("description").toString();
        String ownerId = object.get("owner_id").toString();
        bundle.putString("name", name);
        bundle.putString("specie", specie);
        bundle.putString("petId", petId);
        bundle.putString("sex", sex);
        bundle.putString("diet", diet);
        bundle.putBoolean("vaccinated", vaccinated);
        bundle.putString("description", description);
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
}
