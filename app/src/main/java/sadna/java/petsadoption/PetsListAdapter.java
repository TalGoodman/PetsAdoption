package sadna.java.petsadoption;

import android.graphics.Color;
import android.os.Bundle;
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

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

/*
    Adapter for recycler views of pet items
 */
public class PetsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_ITEM = 0;   //Pet item
    private final int VIEW_TYPE_LOADING = 1;    //Loading progress bar

    private List<ParseObject> petsList;     //list of pets
    private String currentUserId;       //Id of the current user
    private Fragment fragment;      //The fragment which setAdapter was called from


    //Inner class for pet list item view
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView petImage;
        public TextView petName;
        public TextView petSpecie;
        public Button btnView;

        //constructor for inner class
        public ItemViewHolder(View v) {
            super(v);
            petImage = (ImageView) v.findViewById(R.id.ivPetSmallImage);    //view is in pets_list_item.xml
            petName = (TextView) v.findViewById(R.id.tvPetNameRV);          //view is in pets_list_item.xml
            petSpecie = (TextView) v.findViewById(R.id.tvPetSpeciesRV);     //view is in pets_list_item.xml
            btnView = (Button) v.findViewById(R.id.btnViewPetRV);           //view is in pets_list_item.xml
        }
    }

    //Inner class for loading item view
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.pbItemLoading);       //view is in item_loading.xml
        }
    }

    //constructor for PetsListAdapter
    public PetsListAdapter(List<ParseObject> petsList, String currentUserId, Fragment fragment) {
        this.petsList = petsList;
        this.currentUserId = currentUserId;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView;
        //determine if the view should be a load item or pet item
        //viewType is determined by the method getItemViewType
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
    public int getItemViewType(int position) {
        //null item in the list indicated there should be a loading item
        if (petsList.get(position) == null) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            //if the item is loading item there is nothing else to set
            return;
        }
        //item is pet item:
        PetsListAdapter.ItemViewHolder itemHolder = (PetsListAdapter.ItemViewHolder)holder;
        //retrieve details about the pet from the list of pets
        String petName = petsList.get(position).get("pet_name").toString();
        String petSpecie = petsList.get(position).get("species").toString();
        String petId = petsList.get(position).get("pet_id").toString();
        DatabaseHandler.getPetImage(petsList.get(position), itemHolder);
        Bundle bundle = createBundle(petsList.get(position));   //create a bundle of data to pass to the next fragment
        boolean isRequested = DatabaseHandler.findIfPetRequested(petId, currentUserId);
        if (isRequested) {
            bundle.putBoolean("isRequested", true);
        } else {
            bundle.putBoolean("isRequested", false);
        }

        //set the UI of pet item on UI thread
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemHolder.petName.setText(petName);
                itemHolder.petSpecie.setText(petSpecie);
                if (isRequested) {
                    //pet was requested by the user
                    itemHolder.btnView.setText("Details\n(Requested)");
                    itemHolder.btnView.setTextColor(Color.GREEN);
                } else {
                    //pet was not requested by the user
                    itemHolder.btnView.setText("Details");
                    itemHolder.btnView.setTextColor(Color.LTGRAY);
                }

                itemHolder.btnView .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(fragment.getClass() == WatchPetsFragment.class){
                            //if the current fragment is WatchPetsFragment
                            //then the button in the list item should
                            //call navigate with the following navigation action
                            NavHostFragment.findNavController(fragment)
                                    .navigate(R.id.action_WatchPetsFragment_to_PetDetailsFragment, bundle);
                        } else if(fragment.getClass() == MyPetsFragment.class){
                            //if the current fragment is MyPetsFragment
                            //then the button in the list item should
                            //call navigate with the following navigation action
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


    //creates the bundle and adds data to it
    //the bundle is used for passing data to the next fragment
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
