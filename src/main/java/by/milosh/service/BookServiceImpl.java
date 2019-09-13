package by.milosh.service;

import by.milosh.filter.BookFilter;
import by.milosh.repository.BookRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

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

    @Override
    public void generateCSV(BookFilter bookFilter) throws Exception {
        if (!bookFilter.validateFilter()) {
            throw new Exception("Not valid Book Filter");
        }
        try (FileWriter out = new FileWriter(FILE_NAME);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("Id", "Name", "CreationYear", "GenreName", "AuthorName"))) {
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

                        }
                    } while (resultSet.next());
                }
            });
            printer.flush();
        } catch (Exception e) {
            throw new Exception("Cannot generate *.csv file");
        }
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
