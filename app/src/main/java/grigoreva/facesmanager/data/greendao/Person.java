package grigoreva.facesmanager.data.greendao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "PERSON".
 */
public class Person {

    private Long id;
    private String name;
    private String surname;
    private boolean isContact;

    // KEEP FIELDS - put your custom fields here
    private String mainPhoto;
    // KEEP FIELDS END

    public Person() {
    }

    public Person(Long id) {
        this.id = id;
    }

    public Person(Long id, String name, String surname, boolean isContact) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.isContact = isContact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean getIsContact() {
        return isContact;
    }

    public void setIsContact(boolean isContact) {
        this.isContact = isContact;
    }

    // KEEP METHODS - put your custom methods here

    public void setMainPhoto(String mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }
    // KEEP METHODS END

}
