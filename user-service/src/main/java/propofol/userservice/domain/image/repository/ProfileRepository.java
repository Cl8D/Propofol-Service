package propofol.userservice.domain.image.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import propofol.userservice.domain.image.entity.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByMemberId(Long memberId);
}
