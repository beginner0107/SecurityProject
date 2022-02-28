package org.zerock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.mapper.BoardAttachMapper;
import org.zerock.mapper.BoardMapper;
import org.zerock.mapper.ReplyMapper;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
@Log4j
@Service
@AllArgsConstructor
public class BoardServiceImpl implements BoardService{
	
	// spring 4.3 이상에서 자동 처리
	@Setter(onMethod = @__(@Autowired))
	private BoardMapper mapper;
	
	@Setter(onMethod = @__(@Autowired))
	private ReplyMapper replyMapper;
	
	@Setter(onMethod = @__(@Autowired))
	private BoardAttachMapper attachMapper;
	
	@Transactional
	@Override
	public void register(BoardVO board) {
		log.info("register....."+board);
		
		mapper.insertSelectKey(board);
		
		if(board.getAttachList() == null || board.getAttachList().size() <=0) {
			return;
		}
		
		board.getAttachList().forEach(attach -> {
			attach.setBno(board.getBno());
			attachMapper.insert(attach);
		});
	}

	@Override
	public BoardVO get(Long bno) {
		log.info("get........" + bno);
		BoardVO board = mapper.read(bno);
		return board;
	}
	// 삭제/수정은 메서드의 리턴 타입을 void로 설계할 수도 있지만 엄격하게 처리하기 위해서 Boolean 타입으로 처리
	@Override
	public boolean modify(BoardVO board) {
		log.info("modify.........." + board);
		
		attachMapper.deleteAll(board.getBno());
		
		boolean modifyResult = mapper.update(board) == 1;
		
		if(modifyResult && board.getAttachList()!=null && board.getAttachList().size() > 0) {
			board.getAttachList().forEach(attach -> {
				attach.setBno(board.getBno());
				attachMapper.insert(attach);
			});
		}
		
		return modifyResult;
	}

	@Transactional
	@Override
	public boolean remove(Long bno) {
		log.info("remove...." + bno);
		
		replyMapper.deleteByBno(bno);
		attachMapper.deleteAll(bno);
		
		return mapper.delete(bno)==1;
	}

//	@Override
//	public List<BoardVO> getList() {
//		log.info("getList................");
//		return mapper.getList();
//	}
	
	@Override
	public List<BoardVO> getList(Criteria cri) {
		log.info("get List with criteria : " + cri);
		return mapper.getListWithPaging(cri);
	}

	@Override
	public int getTotal(Criteria cri) {
		log.info("get total count");
		return mapper.getTotalCount(cri);
	}

	@Override
	public List<BoardAttachVO> getAttachList(Long bno) {
		log.info("get Attach list by bno" + bno);
		return attachMapper.findByBno(bno);
	}
	

}
