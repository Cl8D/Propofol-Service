package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.userservice.domain.exception.SameMemberFollowingException;
import propofol.userservice.domain.member.entity.Following;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.FollowingRepository;

@Service
@RequiredArgsConstructor
public class FollowingService {
    private final FollowingRepository followingRepository;

    // 팔로잉 정보 저장
    public String saveFollowing(Member findMember, Long followingMemberId) {
        // 자기자신은 팔로잉 못하도록
        if(findMember.getId() == followingMemberId)
            throw new SameMemberFollowingException("자기 자신을 팔로잉 할 수 없습니다.");

        Following findFollowing = followingRepository.findByFollowingMemberIdAndMemberId(followingMemberId, findMember.getId())
                .orElse(null);

        if(findFollowing == null) {
            // 팔로잉 정보가 없다면 새로 생성
            Following following = Following.createFollowing()
                    .followingMemberId(followingMemberId).build();
            // 팔로잉에 저장된 멤버 정보 변경
            following.changeMember(findMember);

            // 저장
            followingRepository.save(following);
            return "팔로잉 완료";
        }
        else {
            // 기존에 존재한다면 팔로잉 취소 기능!
            followingRepository.delete(findFollowing);
            return "팔로잉 취소";
        }
    }

    /*************************/

    // 팔로잉하고 있는 회원 수 조회
    public int getFollowers(Long memberId) {
        return followingRepository.getFollowers(memberId);
    }
}
