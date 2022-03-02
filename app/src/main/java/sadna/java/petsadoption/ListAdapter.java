package sadna.java.petsadoption;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ImageView[] petImagesList;
    private String[] petTextsList;
    private Button[] petButtonsList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView petImage;
        public TextView petText;
        public Button btnView;

        public ViewHolder(View v) {
            super(v);
            petImage = (ImageView) v.findViewById(R.id.ivPetSmallImage);
            petText = (TextView) v.findViewById(R.id.tvPet);
            btnView = (Button) v.findViewById(R.id.btnViewPet);
        }
    }

    public ListAdapter(ImageView[] petImagesList, String[] petTextsList, Button[] petButtonsList) {
        this.petImagesList = petImagesList;
        this.petTextsList = petTextsList;
        this.petButtonsList = petButtonsList;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView image = petImagesList[position];
        String text = petTextsList[position];
        Button button = petButtonsList[position];
        holder.petImage = image;
        holder.petText.setText(text);
        holder.btnView = button;
    }

    @Override
    public int getItemCount() {
            int i;
            for (i = 0; i < petTextsList.length; i++) {
                if (petTextsList[i] == "") {
                    return i;
                }
            }
            return i;
    }
}
