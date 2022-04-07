package sadna.java.petsadoption;

import android.util.Log;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

//Used Integers instead of ENUMS because ENUMS requires too much memory and
//it might slow the application
//https://stackoverflow.com/questions/9246934/working-with-enums-in-android
public class Pet /*extends ParseObject*/{
    //TODO: add more animals
    private static final int GENUS_CAT = 11;
    private static final int GENUS_DOG = 12;
    private static final int GENUS_FISH = 13;
    private static final int GENUS_CHICKEN = 14;
    private static final int GENUS_RABBIT = 15;
    private static final int GENUS_OTHER = 16;

    private static final int SEX_MALE = 21;
    private static final int SEX_FEMALE = 22;

    private static final int DIET_VEGETARIAN = 31;
    private static final int DIET_CARNIVOROUS = 32;
    private static final int DIET_OMNIVORE = 33;

    private byte[] Image;
    private String Specie;
    private String Name;
    private String PetId;
    private String OwnerID;
    private String Breed;
    private String Sex;
    private Boolean Vaccinated;
    private int Diet;
    private String Description;

    public Pet(
            byte[] image,
            String specie,
            String ownerID,
            String name,
            String pet_id,
            String sex,
            Boolean vaccinated,
            int diet,
            String description
    ) {
        this.Image = image;
        this.Specie = specie;
        this.Name = name;
        this.PetId = pet_id;
        this.OwnerID = ownerID;
        this.Sex = sex;
        this.Vaccinated = vaccinated;
        this.Diet = diet;
        this.Description = description;
    }

    //Pet Image
    public byte[] getImage() { return this.Image; }
    public void setImage(byte[] image) {
        this.Image = image;
    }

    //Pet Species
    public String getSpecies() {
        return this.Specie;
    }
    public void setSpecie(String specie) {
        this.Specie = specie;
    }

    //Pet Name
    public String getName() {
        return this.Name;
    }
    public void setName(String name) {
        this.Name = name;
    }

    //Pet ID
    public String getPetId() {
        return this.PetId;
    }
    public void setPetId(String identifier) {
        this.PetId = identifier;
    }

    //Owner ID
    public String getOwnerID() { return this.OwnerID; }
    public void setOwnerID(String userID) { this.OwnerID = userID; }

    //Pet Sex
    public String getSex() {
        return this.Sex;
    }
    public void setSex(String sex) {
        this.Sex = sex;
    }

    //Pet Is Vaccinated
    public Boolean getVaccinated() {
        return this.Vaccinated;
    }
    public void setVaccinated(Boolean vaccinated) {
        this.Vaccinated = vaccinated;
    }

    //Pet Diet
    public int getDiet() {
        return this.Diet;
    }
    public void setDiet(int diet) {
        this.Diet = diet;
    }

    //Pet Description
    public String getDescription() {
        return this.Description;
    }
    public void setDescription(String description) {
        this.Description = description;
    }

    //Create A Jason
    public String toJSON(){
        JSONObject jsonPet= new JSONObject();
        try {
            jsonPet.put("Image",getImage());
            jsonPet.put("Species",getSpecies());
            jsonPet.put("Name",getName());
            jsonPet.put("pet_id",getPetId());
            jsonPet.put("OwnerID",getOwnerID());
            jsonPet.put("Sex",getSex());
            jsonPet.put("Vaccinated",getVaccinated());
            jsonPet.put("Diet",getDiet());
            jsonPet.put("Description",getDescription());

            return jsonPet.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public ParseObject toParseObject(){
        return (new ParseObject(this.toJSON()));
    }

    @Override
    public String toString() {
        return "Pet{" +
                "Image=" + Arrays.toString(Image) +
                ", Specie=" + Specie +
                ", Name='" + Name + '\'' +
                ", PetId='" + PetId + '\'' +
                ", OwnerID='" + OwnerID + '\'' +
                ", Breed='" + Breed + '\'' +
                ", Sex=" + Sex +
                ", Vaccinated=" + Vaccinated +
                ", Diet=" + Diet +
                ", Description='" + Description + '\'' +
                ", created=" + created +
                '}';
    }

    boolean created = false;
    public void addToDatabase() {

        ParseObject pet = new ParseObject("pets");

        pet.put("pet_id", "A string");
        pet.put("owner_id", ParseUser.getCurrentUser());
        pet.put("pet_name", "A string");
        pet.put("species", new ParseObject("Species"));
        pet.put("gander", "A string");
        pet.put("pet_image", new ParseFile("resume.txt", "My string content".getBytes()));

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        pet.saveInBackground(e -> {
            if (e==null){
                //Save was done
                created = true;
                Log.d("addToDatabase()","New Pet Have Been Added To Database\n");
            }else{
                //Something went wrong
                //this. (this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}