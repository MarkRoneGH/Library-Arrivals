package proj.markrone.library.server.controllers;

import proj.markrone.library.api.network.Response;
import proj.markrone.library.server.model.entities.Book;
import proj.markrone.library.server.services.BookService;
import proj.markrone.library.server.utils.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class ExportController {
    private static final Logger logger = LogManager.getLogger(ExportController.class);
    private final BookService bookService = new BookService();

    public Response exportBooksExcel() {
        return exportBooks("Excel", (data, out, title) -> new ExcelExporter().export(data, out, title));
    }

    public Response exportBooksPdf() {
        return exportBooks("PDF", (data, out, title) -> new PdfExporter().export(data, out, title));
    }

    public Response exportBooksJson() {
        return exportBooks("JSON", (data, out, title) -> new JsonExporter().export(data, out, title));
    }

    private Response exportBooks(String format, ExportAction action) {
        try {
            List<Book> books = bookService.findAllEntities();
            List<Map<String, Object>> data = new ArrayList<>();
            for (Book b : books) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("ID", b.getId());
                m.put("Название", b.getTitle());
                m.put("Автор", b.getAuthor());
                m.put("ISBN", b.getIsbn());
                m.put("Год", b.getYearPublished());
                data.add(m);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            action.export(data, baos, "Каталог книг");
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            logger.info("Экспорт в {} выполнен, {} байт", format, baos.size());
            return new Response(true, "Экспорт в " + format + " выполнен", base64);
        } catch (Exception e) {
            logger.error("Ошибка экспорта в {}", format, e);
            return new Response(false, "Ошибка экспорта: " + e.getMessage(), null);
        }
    }

    @FunctionalInterface
    private interface ExportAction {
        void export(List<Map<String, Object>> data, ByteArrayOutputStream out, String title) throws Exception;
    }
}
