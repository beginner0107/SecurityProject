# SecurityProject

<img width="619" alt="image" src="https://user-images.githubusercontent.com/81161819/156528840-436c3ef8-0e9a-4bf0-9123-9cc725ffd871.png">

<h2>적용 기법</h2>
1. Oracle Hint 를 이용한 Index 활용과 Paging<br><br>
2. Mybatis 동적 쿼리를 이용한 검색 기능<br><br>
3. Rest방식으로 댓글 CRUD<br><br>
4. Ajax를 이용하는 파일 업로드 + 썸네일<br><br>
5. 게시글 삭제할 때 @Transaction <br><br>
6. Quartz라이브러리를 활용하여 DB에 있는 이미지와 실제 업로드 경로에 있는 파일 매칭, 불필요한 파일이 있으면 삭제(새벽 1시)<br><br>
7. Security를 이용하여 csrf공격 방지. 각 페이지에 Post 방식의 경우 csrf를 전송시켜주는 방식으로 전환<br><br>
8. taglib lib = "security" perfix="sec" -> 이용하여 jsp 페이지에서 로그인과 본인이 작성한 글or댓글 수정/삭제. <br>
