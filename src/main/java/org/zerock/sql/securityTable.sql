CREATE TABLE users (
	username varchar2(50) NOT NULL PRIMARY KEY,
	password varchar2(50) NOT NULL,
	enabled char(1) DEFAULT '1'
);

CREATE TABLE authorities(
	username varchar2(50) NOT NULL,
	authority varchar2(50) NOT NULL,
	CONSTRAINT fk_authorities_user FOREIGN KEY (username) REFERENCES users(username)
);

CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);

INSERT INTO users (username, password) VALUES ('user00', 'pw00');
INSERT INTO users (username, password) VALUES ('member00', 'pw00');
INSERT INTO users (username, password) VALUES ('admin00', 'pw00');

INSERT INTO authorities (username, authority) VALUES ('user00', 'ROLE_USER');
INSERT INTO authorities (username, authority) VALUES ('member00', 'ROLE_MANAGER');
INSERT INTO authorities (username, authority) VALUES ('admin00', 'ROLE_MANAGER');
INSERT INTO authorities (username, authority) VALUES ('admin00', 'ROLE_ADMIN');

SELECT * FROM users;

SELECT * FROM authorities;


CREATE TABLE tbl_member(
	userid varchar2(50) NOT NULL PRIMARY KEY,
	userpw varchar2(100) NOT NULL,
	username varchar2(100) NOT NULL,
	regdate date DEFAULT sysdate,
	updatedate date DEFAULT sysdate,
	enabled char(1) DEFAULT '1'
);

CREATE TABLE tbl_member_auth(
	userid varchar2(50) NOT NULL,
	auth varchar2(50) NOT NULL,
	CONSTRAINT fk_member_auth FOREIGN KEY(userid) REFERENCES tbl_member(userid)
);

SELECT * FROM tbl_member ;
SELECT * FROM tbl_member_auth ;

SELECT userid username, userpw password, enabled FROM tbl_member WHERE userid = 'admin90';

SELECT userid username, auth authority FROM tbl_member_auth WHERE userid = 'admin90';

