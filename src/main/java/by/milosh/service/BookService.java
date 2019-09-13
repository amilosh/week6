package by.milosh.service;

import by.milosh.filter.BookFilter;

public interface BookService {

    void generateCSV(BookFilter bookFilter) throws Exception;
}
