package sadna.java.petsadoption;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ArrayList<String> petNamesTextList;
    private ArrayList<String> petSpeciesTextList;
    private ArrayList<Button> petButtonsList;


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

    public ListAdapter(ArrayList<String> petNamesList, ArrayList<String> petSpeciesTextList, ArrayList<Button> petButtonsList) {
        this.petNamesTextList = petNamesList;
        this.petSpeciesTextList = petSpeciesTextList;
        this.petButtonsList = petButtonsList;
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
        Button button = petButtonsList.get(position);
        addImage(holder, petName);
        holder.petName.setText(petName);
        holder.petSpecie.setText(petSpecie);
        holder.btnView = button;
    }

    @Override
    public int getItemCount() {
        return petNamesTextList.size();
    }

    private void addImage(ViewHolder holder, String petName){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("pets");
        query.whereEqualTo("pet_name", petName);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object != null) {
                    ParseFile file = (ParseFile)object.get("pet_image");
                    file.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                Bitmap bitmap;
                                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                holder.petImage.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            }
        });
    }
}
