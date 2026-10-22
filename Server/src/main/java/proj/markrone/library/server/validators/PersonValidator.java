package proj.markrone.library.server.validators;

import proj.markrone.library.server.interfaces.Validatable;
import proj.markrone.library.server.model.entities.Person;


public class PersonValidator implements Validatable<Person> {

    /**
     * Проверяет, что объект Person не null и содержит непустые имя и фамилию.
     *
     * @param person проверяемый объект Person
     * @return true, если объект корректен; false в противном случае
     */
    @Override
    public boolean isValid(Person person) {
        return person != null &&
                person.getFirstName() != null &&
                !person.getFirstName().isEmpty() &&
                person.getLastName() != null &&
                !person.getLastName().isEmpty();
    }
}
