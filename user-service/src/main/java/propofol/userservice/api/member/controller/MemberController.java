package propofol.userservice.api.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.userservice.api.auth.controller.dto.ResponseDto;
import propofol.userservice.api.common.annotation.Token;
import propofol.userservice.api.member.controller.dto.member.MemberResponseDto;
import propofol.userservice.api.member.controller.dto.member.ProfileResponseDto;
import propofol.userservice.api.member.controller.dto.member.UpdateRequestDto;
import propofol.userservice.api.member.controller.dto.streak.StreakDetailResponseDto;
import propofol.userservice.api.member.controller.dto.streak.StreakRequestDto;
import propofol.userservice.api.member.controller.dto.streak.StreakResponseDto;
import propofol.userservice.api.member.service.MemberBoardService;
import propofol.userservice.api.member.service.ProfileService;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;
import propofol.userservice.domain.streak.entity.Streak;
import propofol.userservice.domain.streak.service.StreakService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 사용자가 진행할 수 있는 기능 컨트롤러
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final MemberBoardService memberBoardService;
    private final StreakService streakService;
    private final ProfileService profileService;
//    private final FollowingService followingService;

    /**************************/

    // 이메일로 멤버 조회하기 (x)
    /** 기존 코드에서 업그레이드 -> @Token 어노테이션을 통해서 회원을 찾을 수 있도록! */
//    @GetMapping("/users/{email}")
//    public MemberResponseDto getMemberByEmail(@PathVariable String email) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMemberByMemberId(@Token Long memberId) {
        // email 대신 pk 값을 활용해서 멤버를 조회할 수 있도록.
//        Member findMember = memberService.getMemberByEmail(email);

        Member findMember = memberService.getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });

        // modelMapper를 활용하여 findMemberDto에 맞춰 객체 생성
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 조회 성공!", modelMapper.map(findMember, MemberResponseDto.class));
    }

    /**********************/

    // 회원 정보 수정
    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto updateMember(@RequestBody UpdateRequestDto dto, @Token Long memberId) {
        // 사용자의 회원 정보 수정을 UpdateMemberDto로 매핑
        // 즉, dto->dto 매핑이지만 api-domain 계층을 분리하기 위해서 이런 식으로 구성.
        UpdateMemberDto updateMemberDto = modelMapper.map(dto, UpdateMemberDto.class);
        memberService.updateMember(updateMemberDto, memberId);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 수정 성공!", "ok");
    }

    /*******************/

    // 자기 자신의 게시글 가져오기
    // localhost:8081/api/v1/members/myboards?page=1
    @GetMapping("/myboards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMyBoards(
            // 파라미터로 페이지 정보
            @RequestParam Integer page,
            // http-header 중 authorization의 내용을(요청된 헤더값을)
            // token이라는 파라미터로 전달해주기
            // 여기에는 Bearer ewfijeoif3294 같은 정보가 들어가있다!
            @RequestHeader(name = "Authorization") String token) {

        // 최종적으로 responseDto (총 페이지, 게시글 수, 게시글 정보가 담김)정보를 리턴받아서 정보를 뿌려준다.
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 게시글 조회 성공!", memberBoardService.getMyBoards(page, token));
    }

    /*******************/

    // 회원의 스트릭 정보 가져오기
    // DTO -> year, List<StreakDetailResponseDto>
    // StreakDetailResponseDto -> workingDate, working
    @GetMapping("/streak")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getStreaks(@Token Long memberId) {
        StreakResponseDto responseDto = new StreakResponseDto();

        // 현재 날짜의 년도 가져오기
        int year = LocalDate.now().getYear();
        responseDto.setYear(String.format(year + "년"));

        // 현재 년도의 1월 1일~12월 31일까지의 정보 설정
        // LocalDate.of() -> 주어진 날짜 정보를 바탕으로 LocalDate 객체 리턴
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

//        List<StreakDetailResponseDto> responseDtoStreaks = responseDto.getStreaks();
        List<StreakDetailResponseDto> responseDtoStreaks = new ArrayList<>(); // 메모리는 더 쓰지만 조금 더 직관적인 코드

        List<Streak> streaks = streakService.getStreakByMemberId(memberId, start, end);
        streaks.forEach(streak -> {
            // 스트릭에 있는 각 정보들을 dto에 담아주기
            responseDtoStreaks.add(new StreakDetailResponseDto(streak.getWorkingDate(), streak.getWorking()));
        });

        /** TODO 본 프로젝트에서도 set으로 설정해주기 */
        responseDto.setStreaks(responseDtoStreaks);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 스트릭 조회 성공!", responseDto);
    }

    /*******************/

    // 스트릭 저장하기
    // requestDto -> date / working (오늘 했는지 안 했는지)
    @PostMapping("/streak")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveStreak(@Token Long memberId,
                           @RequestBody StreakRequestDto requestDto) {

        streakService.saveStreak(memberId, requestDto);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "회원 스트릭 저장 성공!", "ok");
    }

    /*******************/

    // 회원의 닉네임 반환 (til-service에서 호출)
    @GetMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public String getMemberNickname(@PathVariable("memberId") Long memberId) {
        Member findMember = memberService.getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원 조회에 실패하였습니다.");
        });

        return findMember.getNickname();
    }

    /*******************/

    // 회원 프로필 수정 (저장)
    @PostMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveMemberProfile(
            // 사용자의 프로필 이미지
            @RequestParam("profile")MultipartFile file,
            @Token Long memberId) throws Exception {

        Member findMember = memberService.getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });

        ProfileResponseDto profileResponseDto = profileService.saveProfile(file, findMember);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "프로필 저장 성공", profileResponseDto);
    }

    /*******************/

    // 회원 프로필 조회
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMemberProfile(@Token Long memberId) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "프로필 조회 성공", profileService.getProfile(memberId));
    }


    /*******************/
    // 회원의 팔로잉 정보 저장
//    @PostMapping("/following")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseDto saveFollowing(@RequestBody FollowingSaveRequestDto requestDto,
//                                     @Token Long memberId){
//        Member findMember = memberService.getMemberById(memberId).orElseThrow(() -> {
//            throw new NotFoundMember("회원을 찾을 수 없습니다.");
//        });
//
//        return new ResponseDto(HttpStatus.OK.value(), "success", "Following 성공!",
//                followingService.saveFollowing(findMember, Long.parseLong(requestDto.getFollowingMemberId())));
//    }

}
