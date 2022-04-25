package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.exception.dto.ErrorDetailDto;
import propofol.userservice.api.exception.dto.ErrorDto;
import propofol.userservice.api.member.controller.dto.MemberResponseDto;
import propofol.userservice.api.member.controller.dto.SaveMemberDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.entity.Authority;
import propofol.userservice.domain.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// 사용자가 진행할 수 있는 기능 컨트롤러
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    // 비밀번호 암호화
    private final BCryptPasswordEncoder encoder;

    @GetMapping("/health-check")
    public String health(){
        return "Working!!";
    }

    // 이메일로 멤버 조회하기
    @GetMapping("/users/{email}")
    public MemberResponseDto getMemberByEmail(@PathVariable String email) {
        Member findMember = memberService.getMemberByEmail(email);
        // modelMapper를 활용하여 findMemberDto에 맞춰 객체 생성
        return modelMapper.map(findMember, MemberResponseDto.class);
    }

    // 멤버 저장 (회원가입)
    @PostMapping("/users")
    // @validated를 통해 원하는 속성에 대해서만 유효성 검사 가능
    // requestBody로 회원 가입 시 해당 멤버 정보 저장
    public Object saveMember(@Validated @RequestBody SaveMemberDto saveMemberDto, HttpServletResponse response){
        ErrorDto errorDto = new ErrorDto();

        // 이메일, 닉네임에 대한 중복 체크
        checkDuplicate(saveMemberDto, errorDto);

        // 만약 중복이 존재한다면
        if(errorDto.getErrors().size() != 0){
            // errorDto 객체를 통해 오류 정보 저장
            errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
            errorDto.setMessage("중복 오류!");
            // status 상태 설정
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return errorDto;
        }

        // 회원가입 받은 string 형 생년월일 정보를 날짜 형태로 변경
        String birth = saveMemberDto.getMemberBirth();
        LocalDate date = LocalDate.parse(birth, DateTimeFormatter.ISO_DATE);

        // member 객체 생성
        Member member = Member.createMember()
                .email(saveMemberDto.getEmail())
                // 비밀번호 설정 시 암호화해서 넣기
                .password(encoder.encode(saveMemberDto.getPassword()))
                .nickname(saveMemberDto.getNickname())
                .username(saveMemberDto.getUsername())
                .birth(date)
                .degree(saveMemberDto.getDegree())
                .phoneNumber(saveMemberDto.getPhoneNumber())
                .score(saveMemberDto.getScore())
                .authority(Authority.USER_BASIC)
                .build();

        // 회원 가입 진행
        memberService.saveMember(member);

        return "회원 가입 성공!";
    }

    // 중복 체크
    private void checkDuplicate(SaveMemberDto saveMemberDto, ErrorDto errorDto) {
        // 이메일 중복 체크
        if(memberService.checkDuplicateByEmail(saveMemberDto.getEmail())){
            errorDto.getErrors().add(new ErrorDetailDto("Email", "중복 오류"));
        }

        // 닉네임 중복 체크
        if(memberService.checkDuplicateByNickname(saveMemberDto.getNickname())){
            errorDto.getErrors().add(new ErrorDetailDto("Nickname", "중복 오류"));
        }
    }

}
