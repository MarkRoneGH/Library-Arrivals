package proj.markrone.library.client.gui.services;

import proj.markrone.library.api.enums.Operation;
import proj.markrone.library.api.model.entities.Issue;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.api.network.ServerClient;
import proj.markrone.library.api.serializer.Serializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IssueService {
    private static final Logger logger = LogManager.getLogger(IssueService.class);
    private final ServerClient serverClient = ServerClient.getInstance();

    public Response getAllIssues() {
        try {
            return serverClient.sendRequest(new Request(Operation.GET_ALL_ISSUES));
        } catch (Exception e) {
            logger.error("Ошибка получения списка выдач: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response getIssuesByReader(int readerId) {
        try {
            String json = Serializer.toJson(readerId);
            return serverClient.sendRequest(new Request(Operation.GET_ISSUES_BY_READER, json));
        } catch (Exception e) {
            logger.error("Ошибка получения выдач читателя: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response issueBook(int bookCopyId, int readerId, int loanDays) {
        try {
            Object[] data = new Object[]{ bookCopyId, readerId, loanDays > 0 ? loanDays : 14 };
            String json = Serializer.toJson(data);
            return serverClient.sendRequest(new Request(Operation.ISSUE_BOOK, json));
        } catch (Exception e) {
            logger.error("Ошибка выдачи книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response returnBook(int issueId) {
        try {
            String json = Serializer.toJson(issueId);
            return serverClient.sendRequest(new Request(Operation.RETURN_BOOK, json));
        } catch (Exception e) {
            logger.error("Ошибка возврата книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}
