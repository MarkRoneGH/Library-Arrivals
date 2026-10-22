package proj.markrone.library.server.repositories;

import proj.markrone.library.server.model.entities.Person;


public class PersonDAO extends AbstractDAO<Person> {

    public PersonDAO() {
        super(Person.class);
    }
}
