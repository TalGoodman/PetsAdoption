package sadna.java.petsadoption;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Pet {

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
    private String Diet;
    private String Description;

    public Pet(
            byte[] image,
            String specie,
            String ownerID,
            String name,
            String pet_id,
            String sex,
            Boolean vaccinated,
            String diet,
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
    public String getDiet() {
        return this.Diet;
    }
    public void setDiet(String diet) {
        this.Diet = diet;
    }

    //Pet Description
    public String getDescription() {
        return this.Description;
    }
    public void setDescription(String description) {
        this.Description = description;
    }

    /**Create A Jason
     *
     * @return the pet object in a JSON format
     */
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
            e.printStackTrace();
            return "";
        }
    }

    /**
     *
     * @return Pet As ParseObject
     */
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

    /**
     * adds a pet to database
     */
    public void addToDatabase() {

        ParseObject pet = new ParseObject("pets");

        pet.put("pet_id", "A string");
        pet.put("owner_id", ParseUser.getCurrentUser());
        pet.put("pet_name", "A string");
        pet.put("species", new ParseObject("Species"));
        pet.put("gander", "A string");
        pet.put("pet_image", new ParseFile("resume.txt", "My string content".getBytes()));

        // Saves the new object.
        pet.saveInBackground(e -> {
            if (e==null){
                //Save was done
                created = true;
            }else{
                //Something went wrong
            }
        });

    }
}