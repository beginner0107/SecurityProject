package org.zerock.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDTO {
	// 시작 페이지
	private int startPage;
	// 분기 별 끝 페이지
	private int endPage;
	// prev : 시작 페이지가 1 초과면 존재
	// next : 분기 별 끝 페이지(endPage)가 끝 페이지 보다 작을 때  존재
	private boolean prev, next;
	
	private int total;
	private Criteria cri;
	
	public PageDTO(Criteria cri, int total) {
		this.cri = cri;
		this.total = total;
		// 만약 5페이지에 있다고 치면 5/10 = 0.5 -> Math.ceil(0.5) -> 1 * 10 = 10 페이지
		this.endPage = (int)(Math.ceil(cri.getPageNum()/10.0)) * 10;
		
		this.startPage = this.endPage - 9;
		
		// 끝 페이지 
		int realEnd = (int)(Math.ceil((total*1.0)/cri.getAmount()));
		// 끝 페이지보다 endPage가 크면?
		if(realEnd < this.endPage) {
			this.endPage = realEnd;
		}
		
		this.prev = this.startPage > 1;
		
		this.next = this.endPage < realEnd;
		
	}
}
