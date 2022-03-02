package sadna.java.petsadoption;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ListItem {

    private ImageView petImage;
    private String petText;
    private Button btnView;

    public ListItem(ImageView petImage, String petText, Button btnView) {
        this.petImage = petImage;
        this.petText = petText;
        this.btnView = btnView;
    }

}