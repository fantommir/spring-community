package com.JKS.community.repository;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<MemberDto> search(MemberSearchCondition condition, Pageable pageable);
}
