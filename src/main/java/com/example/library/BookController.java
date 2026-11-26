package com.example.library;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
  private final BookRepository books;

  public BookController(BookRepository books) {
    this.books = books;
  }

  public record NewBook(String title, String author) {}

  public record Lend(String to) {}

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok");
  }

  @GetMapping("/books")
  public List<Book> list() {
    return books.all();
  }

  @GetMapping("/books/{id}")
  public ResponseEntity<?> one(@PathVariable long id) {
    return books.find(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElse(notFound());
  }

  @PostMapping("/books")
  public ResponseEntity<?> create(@RequestBody NewBook body) {
    if (body == null || body.title() == null || body.title().isBlank())
      return ResponseEntity.badRequest().body(Map.of("error", "title is required"));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(books.create(body.title(), body.author() == null ? "" : body.author()));
  }

  @PostMapping("/books/{id}/lend")
  public ResponseEntity<?> lend(@PathVariable long id, @RequestBody Lend body) {
    if (body == null || body.to() == null || body.to().isBlank())
      return ResponseEntity.badRequest().body(Map.of("error", "to is required"));
    var lent = books.lend(id, body.to());
    if (lent.isPresent()) return ResponseEntity.ok(lent.get());
    // Nothing updated: the book is missing, or someone already has it.
    return books.find(id)
        .<ResponseEntity<?>>map(b -> ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", "already lent", "to", String.valueOf(b.lentTo()))))
        .orElse(notFound());
  }

  @PostMapping("/books/{id}/return")
  public ResponseEntity<?> giveBack(@PathVariable long id) {
    return books.giveBack(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElse(notFound());
  }

  private static ResponseEntity<Map<String, String>> notFound() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "not found"));
  }
}
