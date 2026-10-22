package proj.markrone.library.server.services;

import proj.markrone.library.server.config.SessionConfig;
import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.BookCopy;
import proj.markrone.library.server.model.entities.Issue;
import proj.markrone.library.server.model.entities.User;
import proj.markrone.library.server.repositories.AbstractDAO;
import proj.markrone.library.server.repositories.BookCopyDAO;
import proj.markrone.library.server.repositories.IssueDAO;
import proj.markrone.library.server.repositories.UserDAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IssueService extends AbstractService<Issue> {
    private final IssueDAO issueDAO = new IssueDAO();
    private final BookCopyDAO bookCopyDAO = new BookCopyDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected AbstractDAO<Issue> getDao() {
        return issueDAO;
    }

    public Issue issueBook(int bookCopyId, int readerId, int loanDays) throws ResponseException {
        BookCopy copy = bookCopyDAO.findById(bookCopyId);
        if (copy == null) throw new ResponseException("Экземпляр книги не найден.");
        if (!BookCopyService.STATUS_IN_FUND.equals(copy.getStatus()))
            throw new ResponseException("Книга недоступна для выдачи (статус: " + copy.getStatus() + ").");

        User reader = userDAO.findById(readerId);
        if (reader == null) throw new ResponseException("Читатель не найден.");

        Issue issue = new Issue();
        issue.setBookCopy(copy);
        issue.setReader(reader);
        issue.setIssuedAt(LocalDateTime.now());
        issue.setDueDate(LocalDate.now().plusDays(loanDays > 0 ? loanDays : 14));
        issue.setReturnedAt(null);

        SessionFactory sf = SessionConfig.getInstance().getSessionFactory();
        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            BookCopy managedCopy = session.merge(copy);
            User managedReader = session.merge(reader);
            issue.setBookCopy(managedCopy);
            issue.setReader(managedReader);
            session.persist(issue);
            managedCopy.setStatus(BookCopyService.STATUS_ISSUED);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new ResponseException("Ошибка при выдаче книги: " + e.getMessage());
        }
        return issue;
    }

    public void returnBook(int issueId) throws ResponseException {
        Issue issue = issueDAO.findById(issueId);
        if (issue == null) throw new ResponseException("Выдача не найдена.");
        if (issue.getReturnedAt() != null) throw new ResponseException("Книга уже возвращена.");

        issue.setReturnedAt(LocalDateTime.now());

        SessionFactory sf = SessionConfig.getInstance().getSessionFactory();
        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(issue);
            BookCopy managedCopy = session.merge(issue.getBookCopy());
            managedCopy.setStatus(BookCopyService.STATUS_IN_FUND);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new ResponseException("Ошибка при возврате книги: " + e.getMessage());
        }
    }

    public List<Issue> findByReaderId(int readerId) {
        return issueDAO.findByReaderId(readerId);
    }

    public List<Issue> findOverdue() {
        return issueDAO.findOverdue(LocalDate.now());
    }

    public List<Issue> findActive() {
        return issueDAO.findActive();
    }

    public List<Issue> findReturned() {
        return issueDAO.findReturned();
    }
}
