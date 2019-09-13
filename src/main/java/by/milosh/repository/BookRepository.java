package by.milosh.repository;

import java.util.List;

public interface BookRepository {

    List<String> getAllAuthors();

    List<String> getAllGenres();
}
