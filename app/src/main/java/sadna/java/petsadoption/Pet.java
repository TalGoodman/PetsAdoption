package sadna.java.petsadoption;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

//Used Integers instead of ENUMS because ENUMS requires too much memory and
//it might slow the application
//https://stackoverflow.com/questions/9246934/working-with-enums-in-android
public class Pet {
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
    private int Specie;
    private String Name;
    private Integer PetId;
    private String OwnerID;
    private String Breed;
    private int Sex;
    private Boolean Vaccinated;
    private int Diet;
    private String Description;

    public Pet(
            byte[] image,
            int specie,
            String name,
            Integer pet_id,
            String ownerID,
            int sex,
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
    public int getSpecies() {
        return this.Specie;
    }
    public void setSpecie(int specie) {
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
    public Integer getPetId() {
        return this.PetId;
    }
    public void setPetId(Integer identifier) {
        this.PetId = identifier;
    }

    //Owner ID
    public String getOwnerID() { return this.OwnerID; }
    public void setOwnerID(String userID) { this.OwnerID = userID; }

    //Pet Sex
    public int getSex() {
        return this.Sex;
    }
    public void setSex(int sex) {
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
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("Image",getImage());
            jsonObject.put("Species",getSpecies());
            jsonObject.put("Name",getName());
            jsonObject.put("pet_id",getPetId());
            jsonObject.put("OwnerID",getOwnerID());
            jsonObject.put("Sex",getSex());
            jsonObject.put("Vaccinated",getVaccinated());
            jsonObject.put("Diet",getDiet());
            jsonObject.put("Description",getDescription());

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    boolean created = false;
    public void addToDatabase() {

        ParseObject entity = new ParseObject("pets");

        entity.put("pet_id", "A string");
        entity.put("owner_id", ParseUser.getCurrentUser());
        entity.put("pet_name", "A string");
        entity.put("species", new ParseObject("Species"));
        entity.put("gander", "A string");
        entity.put("pet_image", new ParseFile("resume.txt", "My string content".getBytes()));

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        entity.saveInBackground(e -> {
            if (e==null){
                //Save was done

            }else{
                //Something went wrong
                //this. (this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}