package sadna.java.petsadoption;

import android.graphics.Bitmap;

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

    private static final int SEX_MALE = 21;
    private static final int SEX_FEMALE = 22;

    private static final int DIET_VEGETARIAN = 31;
    private static final int DIET_CARNIVOROUS = 32;
    private static final int DIET_OMNIVORE = 33;

    private Bitmap Image;
    private int Specie;
    private String Name;
    private Integer Identifier;
    private String Breed;
    private int Sex;
    private Integer Age;
    private Integer Weight;
    private Boolean Vaccinated;
    private int Diet;
    private String Description;

    public Pet(Bitmap image, int specie, String name,
               Integer identifier, String breed, int sex,
               Integer age, Integer weight, Boolean vaccinated,
               int diet, String description) {
        Image = image;
        Specie = specie;
        Name = name;
        Identifier = identifier;
        Breed = breed;
        Sex = sex;
        Age = age;
        Weight = weight;
        Vaccinated = vaccinated;
        Diet = diet;
        Description = description;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public int getSpecie() {
        return Specie;
    }

    public void setSpecie(int specie) {
        Specie = specie;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(Integer identifier) {
        Identifier = identifier;
    }

    public String getBreed() {
        return Breed;
    }

    public void setBreed(String breed) {
        Breed = breed;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int sex) {
        Sex = sex;
    }

    public Integer getAge() {
        return Age;
    }

    public void setAge(Integer age) {
        Age = age;
    }

    public Integer getWeight() {
        return Weight;
    }

    public void setWeight(Integer weight) {
        Weight = weight;
    }

    public Boolean getVaccinated() {
        return Vaccinated;
    }

    public void setVaccinated(Boolean vaccinated) {
        Vaccinated = vaccinated;
    }

    public int getDiet() {
        return Diet;
    }

    public void setDiet(int diet) {
        Diet = diet;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
