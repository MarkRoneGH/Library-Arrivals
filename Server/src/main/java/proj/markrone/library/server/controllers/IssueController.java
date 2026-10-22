package proj.markrone.library.server.controllers;

import proj.markrone.library.server.exceptions.ResponseException;
import proj.markrone.library.server.model.entities.Issue;
import proj.markrone.library.api.network.Request;
import proj.markrone.library.api.network.Response;
import proj.markrone.library.server.serializer.Deserializer;
import proj.markrone.library.server.serializer.Serializer;
import proj.markrone.library.server.services.IssueService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class IssueController {
    private static final Logger logger = LogManager.getLogger(IssueController.class);
    private final IssueService issueService = new IssueService();

    public Response issueBook(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Object[] arr) || arr.length < 2
                    || !(arr[0] instanceof Number) || !(arr[1] instanceof Number)) {
                return new Response(false, "Некорректные данные (ID экземпляра, ID читателя [, срок в днях])", null);
            }
            int copyId = ((Number) arr[0]).intValue();
            int readerId = ((Number) arr[1]).intValue();
            int loanDays = arr.length > 2 && arr[2] instanceof Number ? ((Number) arr[2]).intValue() : 14;
            Issue issue = issueService.issueBook(copyId, readerId, loanDays);
            return new Response(true, "Книга выдана читателю", Serializer.toJson(issue));
        } catch (ResponseException e) {
            logger.warn("Ошибка выдачи книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response returnBook(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Number)) {
                return new Response(false, "Некорректный ID выдачи", null);
            }
            int issueId = ((Number) data).intValue();
            issueService.returnBook(issueId);
            return new Response(true, "Книга принята обратно", null);
        } catch (ResponseException e) {
            logger.warn("Ошибка возврата книги: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response getIssuesByReader(Request request) {
        try {
            Object data = new Deserializer().extractData(request);
            if (!(data instanceof Number)) {
                return new Response(false, "Некорректный ID читателя", null);
            }
            int readerId = ((Number) data).intValue();
            List<Issue> list = issueService.findByReaderId(readerId);
            return new Response(true, "Выдачи читателя получены", Serializer.toJson(list));
        } catch (Exception e) {
            logger.warn("Ошибка получения выдач читателя: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response getOverdueIssues() {
        try {
            List<Issue> list = issueService.findOverdue();
            return new Response(true, "Просроченные выдачи получены", Serializer.toJson(list));
        } catch (Exception e) {
            logger.error("Ошибка получения просроченных выдач", e);
            return new Response(false, "Ошибка при получении списка", null);
        }
    }

    public Response getAllIssues() {
        try {
            List<Issue> list = issueService.findAllEntities();
            return new Response(true, "Список выдач получен", Serializer.toJson(list));
        } catch (Exception e) {
            logger.error("Ошибка получения списка выдач", e);
            return new Response(false, "Ошибка при получении списка выдач", null);
        }
    }

    public Response getActiveIssues() {
        try {
            var issues = issueService.findActive();
            return new Response(true, "Активные выдачи получены", Serializer.toJson(issues));
        } catch (Exception e) {
            logger.error("Ошибка получения активных выдач", e);
            return new Response(false, "Ошибка при получении активных выдач", null);
        }
    }

    public Response getReturnedIssues() {
        try {
            var issues = issueService.findReturned();
            return new Response(true, "Возвращённые выдачи получены", Serializer.toJson(issues));
        } catch (Exception e) {
            logger.error("Ошибка получения возвращённых выдач", e);
            return new Response(false, "Ошибка при получении возвращённых выдач", null);
        }
    }
}
