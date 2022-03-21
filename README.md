# SecurityProject

<img width="619" alt="image" src="https://user-images.githubusercontent.com/81161819/156528840-436c3ef8-0e9a-4bf0-9123-9cc725ffd871.png">

<h2>적용 기법</h2>
1. Oracle Hint 를 이용한 Index 활용과 Paging<br><br>

```sql
<select id="getListWithPaging" resultType="org.zerock.domain.BoardVO">
<![CDATA[
  SELECT bno, title, writer, content, regdate, updatedate, replycnt
  FROM (
    SELECT /*+ INDEX_DESC(tbl_board pk_board) */
    rownum rn, bno, title, writer, content , regdate, updatedate, replycnt
    FROM TBL_BOARD
    WHERE 
]]>
<include refid="criteria"></include>
<![CDATA[
    rownum <= #{pageNum} * #{amount}
  ) 
  WHERE rn > (#{pageNum} -1) * #{amount}
]]>
</select>
```
2. Mybatis 동적 쿼리를 이용한 검색 기능<br><br>

```sql
<sql id="criteria">
  <trim prefix="(" suffix=") AND " prefixOverrides="OR">
    <foreach item="type" collection="typeArr">
      <trim prefix="OR">
        <choose>
          <when test="type=='T'.toString()">
          title like '%'|| #{keyword}||'%'
        </when>
        <when test="type=='C'.toString()">
          content like '%'|| #{keyword}||'%'
        </when>
        <when test="type=='W'.toString()">
          writer like '%'|| #{keyword}||'%'
        </when>
        </choose>
      </trim>
    </foreach>
  </trim>
</sql>
```

4. Rest방식으로 댓글 CRUD<br><br>

```java
	private ReplyService service;
	// Insert
	@PreAuthorize("isAuthenticated()")
	@PostMapping(value="/new",
			consumes = "application/json", // Content-Type(header)
			produces = {MediaType.TEXT_PLAIN_VALUE}) // 문자로 리턴 success 200(상태 코드와)
	public ResponseEntity<String> create(@RequestBody ReplyVO vo){
										// 문자열을 받아 {"bno" : 201, "reply":"Hello Reply", "replyer":"user00"}을 ReplyVO 객체에 받는다.
		log.info("ReplyVO: " + vo);
		
		int insertCount = service.register(vo);
		
		log.info("Reply INSERT COUNT : " + insertCount);
		
		return insertCount == 1
		? new ResponseEntity<>("success", HttpStatus.OK)
		: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		// 삼항 연산자 처리
	}
```

6. Ajax를 이용하는 파일 업로드 + 썸네일<br><br>

```javaScript
	(function(){
		
		var bno = '<c:out value="${board.bno}"/>';
		
		$.getJSON("/board/getAttachList", {bno: bno}, function(arr){
			console.log(arr);
			
			var str = "";
			
			$(arr).each(function(i, attach){
				
				// image type
				if(attach.fileType){
					var fileCallPath = encodeURIComponent(attach.uploadPath+"/s_"+attach.uuid+"_"+attach.fileName);
					
					str += "<li data-path='"+attach.uploadPath+"' data-uuid='"+attach.uuid+"' data-filename='"+attach.fileName+"' data-type='"+attach.fileType+"'><div>";
					str += "<img src='/display?fileName="+fileCallPath+"'>";
					str += "</div>";
					str += "</li>";
				}else{
					
					str += "<li data-path='"+attach.uploadPath+"' data-uuid='"+attach.uuid+"' data-filename='"+attach.fileName+"' data-type='"+attach.fileType+"' ><div>";
					str += "<span> "+ attach.fileName+"</span><br/>";
					str += "<img src='/resources/img/attach.png'>";
					str += "</div>";
					str += "</li>";
				}
			});
			
			$(".uploadResult ul").html(str);
			
		}); // end getjson
		
	})(); // end function
```
8. 게시글 삭제할 때 @Transaction <br><br>

```java
	@Transactional
	@Override
	public boolean remove(Long bno) {
		log.info("remove...." + bno);
		
		replyMapper.deleteByBno(bno);
		attachMapper.deleteAll(bno);
		
		return mapper.delete(bno)==1;
	}
```

10. Quartz라이브러리를 활용하여 DB에 있는 이미지와 실제 업로드 경로에 있는 파일 매칭, 불필요한 파일이 있으면 삭제(새벽 1시)<br><br>

```java
public class FileCheckTask {
	
	@Setter(onMethod = @__(@Autowired))
	private BoardAttachMapper attachMapper;
	
	private String getFolerYesterDat() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DATE, -1);
		
		String str = sdf.format(cal.getTime());
		
		return str.replace("-", File.separator);
	}
	
	@Scheduled(cron="0 0 2 * * *")
	public void checkFiles()throws Exception{
		
		log.warn("File Check Task run...............");
		log.warn(new Date());
		// file list in database
		List<BoardAttachVO> fileList = attachMapper.getOldFiles();
		
		// ready for check file in directory with database file list
		List<Path> fileListPaths = fileList.stream()
				.map(vo -> Paths.get("C:\\upload", vo.getUploadPath(), "s_" + vo.getUuid() + "_" + vo.getFileName()))
				.collect(Collectors.toList());
		
		// image file has thumnail file
		fileList.stream().filter(vo -> vo.isFileType() == true)
			.map(vo -> Paths.get("C:\\upload", vo.getUploadPath(), "s_" + vo.getUuid() + "_" + vo.getFileName()))
			.forEach(p -> fileListPaths.add(p));
		
		fileListPaths.forEach(p -> log.warn(p));
		
		log.warn("==================================================");
		
		// files in yesterday directory
		File targetDir = Paths.get("C:\\upload", getFolerYesterDat()).toFile();
		
		File[] removeFiles = targetDir.listFiles(file -> fileListPaths.contains(file.toPath())==false);
		
		log.warn("==================================================");
		
		for(File file : removeFiles) {
			log.warn(file.getAbsolutePath());
			
			file.delete();
			
		}
	}
}
```
12. Security를 이용하여 csrf공격 방지. 각 페이지에 Post 방식의 경우 csrf를 전송시켜주는 방식으로 전환<br><br>

```javaScript
// Ajax spring security header....
$(document).ajaxSend(function(e, xhr, options){
  xhr.setRequestHeader(csrfHeaderName, csrfTokenValue);
});  
```

14. taglib lib = "security" perfix="sec" -> 이용하여 jsp 페이지에서 로그인과 본인이 작성한 글or댓글 수정/삭제. <br>

```html
<sec:authorize access="isAuthenticated()">
  <c:if test="${pinfo.username eq board.writer }">
  <button type="submit" data-oper='modify' class="btn btn-default">Modify</button>
  <button type="submit" data-oper='remove' class="btn btn-danger">Remove</button>
  <button type="submit" data-oper='list' class="btn btn-info">List</button>
  </c:if>
</sec:authorize>
```
