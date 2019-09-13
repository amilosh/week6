package by.milosh.controller;

import by.milosh.filter.BookFilter;
import by.milosh.repository.BookRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Path("/books")
public class BookControllerSecond {

    private static final String FIND_BOOKS = "select book.id as id, book.name as name, book.creation_year as creationYear, genre.name as genreName, author.name as authorName"
            + " from book"
            + " JOIN genre ON genre.id = book.genre_id"
            + " JOIN author ON author.id = book.author_id"
            + " WHERE author.name IN (:authors)"
            + " AND genre.name IN (:genres)"
            + " AND CASE"
            + "         WHEN :startDate is not null"
            + "             THEN book.creation_year > :startDate"
            + "         ELSE TRUE"
            + "     END"
            + " AND CASE"
            + "         WHEN :endDate is not null"
            + "             THEN book.creation_year < :endDate"
            + "         ELSE TRUE"
            + "     END";

    private static final String FILE_NAME = "books.csv";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private NamedParameterJdbcTemplate template;


    @POST
    @Path("responseCSV")
    public void getCSVBooksByFilter(@RequestBody BookFilter bookFilter, HttpServletResponse response) {
        if (!bookFilter.validateFilter()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + FILE_NAME + "\"");
            try (CSVPrinter printer = new CSVPrinter(response.getWriter(),
                    CSVFormat.DEFAULT.withHeader("Id", "Name", "CreationYear", "GenreName", "AuthorName"))) {
                Map<String, Object> params = parseFilterToMap(bookFilter);
                template.query(FIND_BOOKS, params, new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet resultSet) throws SQLException {
                        do {
                            try {
                                printer.printRecords(
                                        resultSet.getInt("id"),
                                        resultSet.getString("name"),
                                        resultSet.getInt("creationYear"),
                                        resultSet.getString("genreName"),
                                        resultSet.getString("authorName"));
                            } catch (IOException e) {
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            }
                        } while (resultSet.next());
                    }
                });
                printer.flush();
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private Map<String, Object> parseFilterToMap(BookFilter bookFilter) {
        Map<String, Object> params = new HashMap<>();
        List<String> authors = bookFilter.getAuthors();
        List<String> genres = bookFilter.getGenres();
        params.put("authors", authors != null ? authors : bookRepository.getAllAuthors());
        params.put("genres", genres != null ? genres : bookRepository.getAllGenres());
        params.put("startDate", bookFilter.getStartDate());
        params.put("endDate", bookFilter.getEndDate());
        return params;
    }
}
