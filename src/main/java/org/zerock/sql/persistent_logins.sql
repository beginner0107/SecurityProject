CREATE TABLE persistent_logins(
	username varchar2(64) NOT NULL,
	series varchar2(64) PRIMARY KEY,
	token varchar2(64) NOT NULL,
	last_used timestamp NOT NULL
);
