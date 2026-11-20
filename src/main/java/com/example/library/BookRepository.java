package com.example.library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {
  private final JdbcTemplate jdbc;

  public BookRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private static final RowMapper<Book> ROW = (ResultSet rs, int i) -> new Book(
      rs.getLong("id"),
      rs.getString("title"),
      rs.getString("author"),
      rs.getString("lent_to"),
      rs.getObject("lent_at", OffsetDateTime.class),
      rs.getObject("created_at", OffsetDateTime.class));

  public List<Book> all() {
    return jdbc.query("select * from books order by id", ROW);
  }

  public Optional<Book> find(long id) {
    return jdbc.query("select * from books where id = ?", ROW, id).stream().findFirst();
  }

  public Book create(String title, String author) {
    return jdbc.queryForObject(
        "insert into books (title, author) values (?, ?) returning *", ROW, title, author);
  }

  /** Marks a book as lent. Known bug kept for the demo: a book already out can be lent again. */
  public Optional<Book> lend(long id, String to) {
    return jdbc
        .query("update books set lent_to = ?, lent_at = now() where id = ? returning *", ROW, to, id)
        .stream()
        .findFirst();
  }

  public Optional<Book> giveBack(long id) {
    return jdbc
        .query("update books set lent_to = null, lent_at = null where id = ? returning *", ROW, id)
        .stream()
        .findFirst();
  }
}
