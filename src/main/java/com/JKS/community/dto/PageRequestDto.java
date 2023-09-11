package com.JKS.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter @Setter
public class PageRequestDto {
    private String sortField;
    private String sortOrder;
    private Integer page;
    private int size;

    @Builder
    public PageRequestDto(String sortField, String sortOrder, Integer page, int size) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.page = page;
        this.size = size;
    }

    public Pageable toPageable() {
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equals("desc") ? sort.descending() : sort.ascending();
        int pageNumber = (page != null) ? page : 0;
        return PageRequest.of(pageNumber, size, sort);
    }
}
