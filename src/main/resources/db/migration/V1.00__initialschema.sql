------------------------------------------------------------------------
-- Lookups 
------------------------------------------------------------------------
CREATE TABLE  IF NOT EXISTS  translation  (
	id serial4 NOT NULL,
	"key" varchar NOT NULL,
	locale varchar NULL,
	value varchar NULL,
	CONSTRAINT translation_pk PRIMARY KEY (id),
	CONSTRAINT translation_un UNIQUE ("key",locale)
);
 
 
CREATE TABLE  IF NOT EXISTS  entity_lk (
	id serial4 NOT NULL,
	"key" varchar NULL,
	value varchar NULL,
	"order" int4 NULL,
	deprecated boolean NULL DEFAULT false,
	CONSTRAINT entity_lk_pk PRIMARY KEY (id),
	CONSTRAINT entity_lk_un UNIQUE ("key")
); 
 