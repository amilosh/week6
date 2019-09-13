package by.milosh.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRepositoryImpl implements BookRepository {

    private static final String FIND_ALL_AUTHORS = "select author.name as name from author";
    private static final String FIND_ALL_GENRES = "select genre.name as name from genre";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<String> getAllAuthors() {
        return jdbcTemplate.query(FIND_ALL_AUTHORS, (rs, rowNum) -> new String(rs.getString("name")));
    }

    @Override
    public List<String> getAllGenres() {
        return jdbcTemplate.query(FIND_ALL_GENRES, (rs, rowNum) -> new String(rs.getString("name")));
    }
}
