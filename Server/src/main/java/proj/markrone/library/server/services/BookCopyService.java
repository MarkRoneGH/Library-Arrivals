package proj.markrone.library.server.services;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.BookCopy;
import proj.markrone.library.server.repositories.AbstractDAO;
import proj.markrone.library.server.repositories.BookCopyDAO;
import proj.markrone.library.server.repositories.IssueDAO;

import java.util.List;

public class BookCopyService extends AbstractService<BookCopy> {
    public static final String STATUS_RECEIVED = "RECEIVED";
    public static final String STATUS_IN_FUND = "IN_FUND";
    public static final String STATUS_ISSUED = "ISSUED";
    public static final String STATUS_WRITTEN_OFF = "WRITTEN_OFF";

    private final BookCopyDAO bookCopyDAO = new BookCopyDAO();
    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected AbstractDAO<BookCopy> getDao() {
        return bookCopyDAO;
    }

    @Override
    public void deleteEntity(BookCopy entity) {
        if (entity == null || entity.getId() == null)
            throw new IllegalArgumentException("Экземпляр для удаления должен иметь ID");
        BookCopy existing = bookCopyDAO.findById(entity.getId());
        if (existing == null) throw new ResponseException("Экземпляр не найден.");
        if (issueDAO.countByBookCopyId(existing.getId()) > 0)
            throw new ResponseException(
                    "Нельзя удалить экземпляр: есть записи о выдачах (включая возвращённые). "
                            + "Сначала удалите связанные выдачи в БД или оставьте экземпляр в каталоге.");
        bookCopyDAO.delete(existing);
    }

    public void includeInFund(int copyId) throws ResponseException {
        BookCopy copy = bookCopyDAO.findById(copyId);
        if (copy == null) throw new ResponseException("Экземпляр не найден.");
        if (!STATUS_RECEIVED.equals(copy.getStatus()))
            throw new ResponseException("Включить в фонд можно только экземпляр со статусом RECEIVED.");
        copy.setStatus(STATUS_IN_FUND);
        bookCopyDAO.update(copy);
    }

    public void writeOff(int copyId) throws ResponseException {
        BookCopy copy = bookCopyDAO.findById(copyId);
        if (copy == null) throw new ResponseException("Экземпляр не найден.");
        if (STATUS_ISSUED.equals(copy.getStatus()))
            throw new ResponseException("Нельзя списать выданный экземпляр. Сначала примите книгу.");
        copy.setStatus(STATUS_WRITTEN_OFF);
        bookCopyDAO.update(copy);
    }

    public void relocate(int copyId, String newShelfLocation) throws ResponseException {
        BookCopy copy = bookCopyDAO.findById(copyId);
        if (copy == null) throw new ResponseException("Экземпляр не найден.");
        copy.setShelfLocation(newShelfLocation);
        bookCopyDAO.update(copy);
    }

    public List<BookCopy> findAvailable() {
        return bookCopyDAO.findAvailable();
    }

    public List<BookCopy> findByStatus(String status) {
        return bookCopyDAO.findByStatus(status);
    }
}
