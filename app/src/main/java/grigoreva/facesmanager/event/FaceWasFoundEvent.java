package grigoreva.facesmanager.event;

import grigoreva.facesmanager.data.greendao.Person;

/**
 * Created by Лена on 10.06.2017.
 */
public class FaceWasFoundEvent implements MessageEvent<Person> {
    private Person person;

    public FaceWasFoundEvent(Person person) {
        this.person = person;
    }

    @Override
    public Person getData() {
        return person;
    }
}