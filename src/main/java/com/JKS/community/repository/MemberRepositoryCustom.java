package com.JKS.community.repository;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberSearchCondition;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberDto> search(MemberSearchCondition condition);
}
