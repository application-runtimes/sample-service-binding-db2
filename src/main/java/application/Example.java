package application;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class Example {

    @Value("${spring.datasource.url}")
    String url;
    
    @Autowired
    BookRepository repo;
    
    @RequestMapping("/test")
    public Iterable<Book> example() {
        return repo.findAll();
    }
    
    @PostConstruct
    public void afterInit() {
        List<Book> books = new ArrayList<Book>();
        books.add(new Book("In Search of Lost Time"));
        books.add(new Book("Ulysses"));
        books.add(new Book("Don Quixote"));
        repo.saveAll(books);
    }
    
}
