package com.hoiae.mygoods.member.dao;

import com.hoiae.mygoods.member.dto.FindOrderDTO;
import com.hoiae.mygoods.member.dto.MemberDTO;
import com.hoiae.mygoods.member.dto.OrderHistoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.lang.reflect.Member;

import java.util.List;

@Mapper
public interface MemberMapper {
    String selectMemberById(String memberId);

    int insertMember(MemberDTO member);

    String selectEncryptedPwd(MemberDTO member);

    MemberDTO selectMember(MemberDTO member);

    MemberDTO findByMemberId(String memberId);

    int updateMember(MemberDTO member);

    int deleteMember(MemberDTO member);


    List<OrderHistoryDTO> findOrderList(int memberNo);

    int selectMemberNoById(String username);

    List<Member> findAllMember();

}
