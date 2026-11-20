create table books (
  id bigserial primary key,
  title text not null,
  author text not null default '',
  lent_to text,
  lent_at timestamptz,
  created_at timestamptz not null default now()
);
