-- Seed data, so every fork starts with a small library. Nothing here is real.
insert into books (title, author, lent_to, lent_at) values
  ('The Pragmatic Programmer', 'Hunt and Thomas', null, null),
  ('Designing Data-Intensive Applications', 'Martin Kleppmann', 'priya', now() - interval '3 days'),
  ('Release It!', 'Michael Nygard', null, null),
  ('A Philosophy of Software Design', 'John Ousterhout', null, null);
