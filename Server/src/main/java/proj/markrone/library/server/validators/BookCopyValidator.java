package proj.markrone.library.server.validators;

import proj.markrone.library.server.interfaces.Validatable;
import proj.markrone.library.server.model.entities.BookCopy;

import java.util.Set;

public class BookCopyValidator implements Validatable<BookCopy> {

    private static final Set<String> VALID_STATUSES = Set.of(
            "RECEIVED", "IN_FUND", "ISSUED", "WRITTEN_OFF"
    );

    @Override
    public boolean isValid(BookCopy copy) {
        if (copy == null) return false;
        if (copy.getInventoryNumber() == null || copy.getInventoryNumber().isBlank()) return false;
        if (copy.getStatus() != null && !VALID_STATUSES.contains(copy.getStatus())) return false;
        return true;
    }
}
