package by.milosh.controller;

import by.milosh.filter.BookFilter;
import by.milosh.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/books")
public class BookControllerFirst {

    @Autowired
    private BookService bookService;

    @RequestMapping(path = "/generateCSV", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity findBooksByFilter(@RequestBody BookFilter bookFilter) {
        try {
            bookService.generateCSV(bookFilter);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
