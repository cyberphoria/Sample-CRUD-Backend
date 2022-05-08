DROP TABLE IF EXISTS items;

CREATE TABLE IF NOT EXISTS items (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  price INTEGER DEFAULT 0,
  stock INTEGER DEFAULT 0,
  CHECK (length(name) > 0 AND
    price >= 0 AND
    stock >= 0));
