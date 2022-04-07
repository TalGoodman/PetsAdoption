package sadna.java.petsadoption;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.PetsViewHolder>
{
    private Context _context;
    private List<Pet> _items;


    public void setItems(List<Pet> items)
    {
        this._items = items;
        notifyDataSetChanged();
    }


    public PetsAdapter(Context ct,byte[] image,
                       int specie,
                       String name,
                       Integer pet_id,
                       String ownerID,
                       int sex,
                       Boolean vaccinated,
                       int diet,
                       String description){
    }


    @NonNull
    @Override
    public PetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pets_list_item,parent,false);

        return new PetsViewHolder(view);
    }

    public void onBindViewHolder(@NonNull PetsViewHolder holder, int position) {
        holder._petName.setText((CharSequence) _items.get(position));
        holder._species.setText((CharSequence) _items.get(position));
    }

    public int getItemCount()
    {
        return _items != null ? _items.size() : 0;
    }

    public class PetsViewHolder extends RecyclerView.ViewHolder {
        public TextView _petName, _species;

        public PetsViewHolder(@NonNull View itemView){
            super(itemView);
            _species = itemView.findViewById(R.id.tvPetSpeciesRV);
            _petName = itemView.findViewById(R.id.tvPetNameRV);
        }
    }
}