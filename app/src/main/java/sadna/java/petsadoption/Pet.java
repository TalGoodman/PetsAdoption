package sadna.java.petsadoption;

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
    private Integer Identifier;
    private String UserID;
    private String Breed;
    private int Sex;
    private Integer Age;
    private Integer Weight;
    private Boolean Vaccinated;
    private int Diet;
    private String Description;

    public Pet(byte[] image, int specie, String name,
               Integer identifier, String userID, String breed, int sex,
               Integer age, Integer weight, Boolean vaccinated,
               int diet, String description) {
        this.Image = image;
        this.Specie = specie;
        this.Name = name;
        this.Identifier = identifier;
        this.UserID = userID;
        this.Breed = breed;
        this.Sex = sex;
        this.Age = age;
        this.Weight = weight;
        this.Vaccinated = vaccinated;
        this.Diet = diet;
        this.Description = description;
    }

    public byte[] getImage() { return this.Image; }

    public void setImage(byte[] image) {
        this.Image = image;
    }

    public int getSpecie() {
        return this.Specie;
    }

    public void setSpecie(int specie) {
        this.Specie = specie;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public Integer getIdentifier() {
        return this.Identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.Identifier = identifier;
    }

    public String getOwnerID() { return this.UserID; }

    public void setOwnerID(String userID) { this.UserID = userID; }

    public String getBreed() {
        return this.Breed;
    }

    public void setBreed(String breed) {
        this.Breed = breed;
    }

    public int getSex() {
        return this.Sex;
    }

    public void setSex(int sex) {
        this.Sex = sex;
    }

    public Integer getAge() {
        return this.Age;
    }

    public void setAge(int age) {
        this.Age = age;
    }

    public Integer getWeight() {
        return this.Weight;
    }

    public void setWeight(Integer weight) {
        this.Weight = weight;
    }

    public Boolean getVaccinated() {
        return this.Vaccinated;
    }

    public void setVaccinated(Boolean vaccinated) {
        this.Vaccinated = vaccinated;
    }

    public int getDiet() {
        return this.Diet;
    }

    public void setDiet(int diet) {
        this.Diet = diet;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }
}
