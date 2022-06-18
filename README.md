# PetsAdoption
The application manages a system with multiple users where they can offer to hand over pets or request to adopt pets.
The app has a few screens: A home screen with a sign in button and buttons for navigating to the other screens, a screen for offering a new pet, a screen for showing a list of pets offered by other users, a screen for showing a list of the pets offered by the user itself and a screen for showing adoption request sent to the user.
The app is also connected to a database for storing the pets, users, and request messages.
When the user is offering a pet he/she can choose a picture of the pet and choose or type some details about it, like the pet's name, specie, sex, diet and a description.
The lists are beign loaded dynamically from the database when scrolling down.
Each item in the list of pets available to adoption has a picture of the pet, the pet's name and specie and a button that leads to a screen of the pets details where the user can choose to adopt the pet.
Each item in the list of the pets which are offered by the user itself is the same as in the other pets list but when they user clicks the details button it would lead to the pet details screen with the option to delete the pet instead of requesting it.
Each item in the request messages list has a text with details about the requesting user and the relevant pet and also some buttons for sending email, copy details to clipboard or deleteing the request message.
