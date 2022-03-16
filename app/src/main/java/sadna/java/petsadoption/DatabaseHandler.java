package sadna.java.petsadoption;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class DatabaseHandler {
//Create Person
    public static void createPerson(String person_name) {
        //Saving your First data object on Back4App
        ParseObject person = new ParseObject("Person");
        person.put("name", person_name);
        person.put("age", 27);
        person.saveInBackground();

        //Reading your First Data Object from Back4App
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
        query.getInBackground("mhPFDlCahj", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your person
                } else {
                    // something went wrong
                }
            }
        });
    }

    public static void createPet(String owner_id,String species,String gander, String pet_name, byte[] pet_image) {
        //String pet_id = System.currentTimeMillis().toHexString();
        String pet_id = Long.toString(System.currentTimeMillis(), 32).toUpperCase();

        ParseObject pet = new ParseObject("pets");
            pet.put("pet_id", pet_id);
            pet.put("owner_id",owner_id);
            pet.put("pet_name", pet_name);
            pet.put("species", species);
            pet.put("gander", gander);
            pet.put("pet_image", new ParseFile(pet_name+".png", pet_image)); //Will Be The Pet Image
        pet.saveInBackground(e -> {
            if (e==null){
                //Save was done
                Log.v("PetCreation:",pet.toString());
            }else{
                //Something went wrong
                Log.v("PetCreationError",e.getMessage());
            }
        });
    }

};


