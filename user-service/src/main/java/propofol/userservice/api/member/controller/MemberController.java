package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.common.exception.dto.ErrorDetailDto;
import propofol.userservice.api.common.exception.dto.ErrorDto;
import propofol.userservice.api.member.controller.dto.MemberResponseDto;
import propofol.userservice.api.member.controller.dto.SaveMemberDto;
import propofol.userservice.api.member.controller.dto.UpdateRequestDto;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.entity.Authority;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// 사용자가 진행할 수 있는 기능 컨트롤러
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    @GetMapping("/health-check")
    public String health(){
        return "Working!!";
    }

    /**************************/

    // 이메일로 멤버 조회하기 (x)
    // + 기존 코드에서 업그레이드 -> @Token 어노테이션을 통해서 회원을 찾을 수 있도록!
//    @GetMapping("/users/{email}")
//    public MemberResponseDto getMemberByEmail(@PathVariable String email) {
    @GetMapping
    public MemberResponseDto getMemberByEmail(@Token Long memberId) {
        // email 대신 pk 값을 활용해서 멤버를 조회할 수 있도록.
//        Member findMember = memberService.getMemberByEmail(email);

        Member findMember = memberService.getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });

        // modelMapper를 활용하여 findMemberDto에 맞춰 객체 생성
        return modelMapper.map(findMember, MemberResponseDto.class);
    }

    /**********************/

    // 회원 정보 수정
    @PostMapping("/update")
    public String updateMember(@RequestBody UpdateRequestDto dto, @Token Long memberId) {
        // 사용자의 회원 정보 수정을 UpdateMemberDto로 매핑
        // 즉, dto->dto 매핑이지만 api-domain 계층을 분리하기 위해서 이런 식으로 구성.
        UpdateMemberDto updateMemberDto = modelMapper.map(dto, UpdateMemberDto.class);
        memberService.updateMember(updateMemberDto, memberId);

        return "ok";

    }



}
