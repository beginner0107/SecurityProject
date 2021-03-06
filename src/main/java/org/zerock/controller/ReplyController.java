package org.zerock.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyPageDTO;
import org.zerock.domain.ReplyVO;
import org.zerock.service.ReplyService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@RequestMapping("/replies/")
@RestController
@Log4j
@AllArgsConstructor
public class ReplyController {
	
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
	
	// Read 특정 게시글의 댓글 목록을 확인
	@GetMapping(value="/pages/{bno}/{page}",
			produces = {
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_UTF8_VALUE // Return XML, JSON
			})
	public ResponseEntity<ReplyPageDTO> getList( 
			@PathVariable("bno") Long bno,
			@PathVariable("page") int page){ 
		log.info("getList......................");
		Criteria cri = new Criteria(page, 10);
		
		log.info("get Reply List bno : " + bno);
		
		log.info(cri);
		
		return new ResponseEntity<>(service.getListPage(cri, bno) ,HttpStatus.OK);
	}
	
	// Read 개별 댓글 확인
	@GetMapping(value="/{rno}",
			produces = {MediaType.APPLICATION_XML_VALUE,
						MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<ReplyVO> get(@PathVariable("rno") Long rno){
		log.info("get : " + rno);
		
		return new ResponseEntity<>(service.get(rno), HttpStatus.OK);
	}
	
	@PreAuthorize("principal.username == #vo.replyer")
	@DeleteMapping(value="/{rno}", produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> remove(@RequestBody ReplyVO vo, @PathVariable("rno") Long rno){
		
		log.info("ReplyVO: " + vo.toString());
		log.info("remove: " + rno);
		
		return service.remove(rno) == 1
		  ? new ResponseEntity<>("success", HttpStatus.OK)
		  : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PreAuthorize("principal.username == #vo.replyer")
	@RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
			value = "/{rno}",
			consumes = "application/json",
			produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> modify(
					@RequestBody ReplyVO vo,
					@PathVariable("rno") Long rno){
		vo.setRno(rno);
		
		log.info("rno : " + rno);
		
		log.info("modify : " + vo);
		
		return service.modify(vo) == 1
					? new ResponseEntity<>("success", HttpStatus.OK)
					: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
