package application;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class Example {
    
    @Autowired
    BookRepository bookRepo;
    
    @RequestMapping("/test")
    public Iterable<Book> example() {
        return bookRepo.findAll();
    }
    
    @PostConstruct
    public void afterInit() {
        // adding a simple condition to check if data exists in the book table and insert data
        // this prevent app inserting data into the db everytime it's restarted
        if (bookRepo.count() == 0) {
            List<Book> books = new ArrayList<Book>();
            books.add(new Book("In Search of Lost Time"));
            books.add(new Book("Ulysses"));
            books.add(new Book("Don Quixote"));
            bookRepo.saveAll(books);
        }
    }
    
}
