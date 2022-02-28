CREATE TABLE tbl_reply(
	rno number(10,0),
	bno number(10,0) NOT NULL,
	reply varchar2(1000) NOT NULL,
	replyer varchar2(50) NOT NULL,
	replyDate DATE DEFAULT sysdate,
	updateDate DATE DEFAULT sysdate
);

CREATE SEQUENCE seq_reply;

ALTER TABLE tbl_reply ADD CONSTRAINT pk_reply PRIMARY KEY(rno);

ALTER TABLE tbl_reply ADD CONSTRAINT fk_reply_board
FOREIGN KEY (bno) REFERENCES tbl_board(bno);