package proj.markrone.library.server.services;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.Person;
import proj.markrone.library.server.repositories.AbstractDAO;
import proj.markrone.library.server.repositories.PersonDAO;

public class PersonService extends AbstractService<Person> {
    private final PersonDAO personDAO = new PersonDAO();

    @Override
    protected AbstractDAO<Person> getDao() {
        return personDAO;
    }

    @Override
    public void deleteEntity(Person person) throws ResponseException {
        if (person == null) {
            throw new ResponseException("Person not found.");
        }
        personDAO.delete(person);
    }
}
