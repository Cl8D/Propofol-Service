package propofol.userservice.api.member.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import propofol.userservice.api.common.exception.SaveProfileException;
import propofol.userservice.api.common.properties.ProfileProperties;
import propofol.userservice.api.member.controller.dto.member.ProfileResponseDto;
import propofol.userservice.domain.image.entity.Profile;
import propofol.userservice.domain.image.repository.ProfileRepository;
import propofol.userservice.domain.member.entity.Member;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileProperties profileProperties;

    // 프로필 저장. 전반적인 로직은 til-service에서 이미지 저장하는 것과 동일함!
    @Transactional
    public ProfileResponseDto saveProfile(MultipartFile file, Member member) throws Exception {
        // 프로필을 저장할 디렉토리 생성 (path 리턴)
        String profileDirPath = createProfileDir();
        // 프로필 이미지의 확장자 추출하기
        String extType = getExt(file.getOriginalFilename());
        // 서버에 저장될 파일 이름 얻기
        String storeFileName = createStoreFileName(extType);

        try {
            // 파일을 디렉토리에 저장
            file.transferTo(new File(getFullPath(profileDirPath, storeFileName)));
        } catch (IOException e) {
            throw new SaveProfileException("프로필 저장 오류 발생!");
        }

        Profile profile = null;
        // 해당 멤버의 기존 프로필 가져오기
        Profile findProfile = profileRepository.findByMemberId(member.getId()).orElse(null);

        try {
            // 기존에 저장된 프로필이 존재한다면 업데이트
            if(findProfile != null)
                return getUpdateProfileResponseDto(file, member, profileDirPath, storeFileName, findProfile);
            // 존재하지 않는다면 새로운 프로필 생성하기
            else
                return getProfileResponseDto(file, member, storeFileName);

        } catch (Exception e){
            throw new Exception("파일 저장 오류");
        }

    }

    /***********************************/

    // 멤버의 프로필 정보 리턴
    public ProfileResponseDto getProfile(Long memberId) {
        Profile profile = profileRepository.findByMemberId(memberId).orElse(null);

        if(profile != null)
        // 응답 DTO - 멤버 정보, 이미지 바이트, 이미지 타입
            return new ProfileResponseDto(memberId, getProfileByte(profile.getStoreFileName()), profile.getContentType());
        else
            return new ProfileResponseDto(memberId, null, null);
    }

    /***********************************/

    private String createProfileDir() {
        String path = getProfileDirPath();
        File dir = new File(path);
        if (!dir.exists()) dir.mkdir();

        return path;
    }

    private String getExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String createStoreFileName(String extType) {
        return UUID.randomUUID().toString() + "." + extType;
    }


    private String getFullPath(String profileDirPath, String storeFileName) {
        return profileDirPath + "/" + storeFileName;
    }

    /***********************************/

    private String getProfileDirPath() {
        // 파일 저장 위치의 절대경로 가져오기
        String profileDir = profileProperties.getProfileDir();
        Path relativePath = Paths.get("");

        return relativePath.toAbsolutePath() + "/" + profileDir;
    }

    /***********************************/

    // 프로필 생성
    private ProfileResponseDto getProfileResponseDto(MultipartFile file, Member member, String storeFileName) {
        Profile profile;
        // 프로필 객체를 생성해준다.
        profile = Profile.createProfile()
                .uploadFileName(file.getOriginalFilename())
                .storeFileName(storeFileName)
                .contentType(file.getContentType())
                .build();

        // 해당 프로필의 멤버 정보 입력
        profile.changeMember(member);

        // 레포지토리에 저장
        profileRepository.save(profile);

        return new ProfileResponseDto(member.getId(),
                getProfileByte(profile.getStoreFileName()), profile.getContentType());
    }

    // 프로필 업데이트
    private ProfileResponseDto getUpdateProfileResponseDto(MultipartFile file, Member member, String profileDirPath,
                                                           String storeFileName, Profile findProfile) {
        // 해당 디렉토리에 저장된 파일 찾기
        File findFile = new File(getFullPath(profileDirPath, findProfile.getStoreFileName()));
        // 이미 존재한다면 삭제 처리
        if(!findFile.exists())
            findFile.delete();

        // 새로운 파일로 프로필 업데이트 (원본 이름, 서버 저장 이름, 파일 타입)
        findProfile.updateProfile(file.getOriginalFilename(), storeFileName, file.getContentType());

        // DTO 리턴 - 여기서 이미지는 바이트 배열로 리턴
        return new ProfileResponseDto(member.getId(),
                getProfileByte(findProfile.getStoreFileName()), findProfile.getContentType());
    }

    /***********************************/

    // 이미지를 바이트 배열로 만들어주기
    private byte[] getProfileByte(String fileName){
        // 해당 파일의 전체 경로 가져오기 (절대 경로)
        String path = getFullPath(getProfileDirPath(), fileName);
        byte[] bytes = null;

        try {
            InputStream imageStream = new FileInputStream(path);
            bytes = IOUtils.toByteArray(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

}