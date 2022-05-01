package propofol.tilservice.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.api.controller.dto.image.ImageResponseDto;
import propofol.tilservice.api.controller.dto.image.ImagesResponseDto;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.exception.NotFoundFileException;
import propofol.tilservice.domain.file.entity.Image;
import propofol.tilservice.domain.file.repository.ImageRepository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final FileProperties fileProperties;
    private final ImageRepository imageRepository;

    // 게시글 파일 저장 기능
    public void saveBoardFile(String uploadDir, List<MultipartFile> files, Board board) {
        // 서버에 파일을 저장할 디렉토리 생성해주기
        // 해당 게시글에 대한 파일 저장소의 경로가 리턴된다.
        String path = createFolder(uploadDir, board);

        // 요청으로 들어온 파일에 대해서
        for (MultipartFile file : files) {
            // 원본 파일 이름
            String originalFilename = file.getOriginalFilename();
            // 확장자 추출
            String extType = getExt(originalFilename);
            // 서버에 저장될 파일 이름을 생성한다.
            String storeFilename = createStoreFilename(extType);

            try {
                // 파일 데이터를 지정한 특정 파일로 저장하기
                file.transferTo(new File(getFullPath(path, storeFilename)));
            } catch (IOException e) {
                throw new NotFoundFileException("파일을 찾을 수 없습니다.");
            }

            // 파일 객체 생성
            Image image = Image.createImage()
                    .storeFileName(storeFilename)
                    .uploadFileName(originalFilename)
                    .build();

            // 게시글에 해당 파일 추가해주기
            // board가 변경감지에 의해 감지되면 image도 함께 변하게 된다. (cascade)
            board.addImage(image);
        }
    }

    /******************************/

    // 파일을 저장할 폴더 생성해주기 (로컬에 저장하도록 구현)
    /** TODO 프로젝트 오타 검수 */
    private String createFolder (String uploadDir, Board board) {
        // 현재 디렉토리에 대한 상대경로 생성
        Path relativePath = Paths.get("");
        // 상대경로 -> 절대경로로 변경한 이후, 새로운 디렉토리를 만들기 위해 uploadDir 붙여주기.
        String path = relativePath.toAbsolutePath().toString() + "/" + uploadDir;

        // 위에서 지정한 경로로 디렉토리 생성해주기 (부모 디렉토리)
        File parentFolder = new File(path);

        // 이전에 생성된 적 없다면 새로 만들기
        if(!parentFolder.exists())
            parentFolder.mkdir();

        // 게시글의 id로 자식 폴더 생성 (즉, 해당 폴더에는 해당 게시글에 업로드된 파일이 담긴다.)
        path = path + "/" + board.getId();

        File childFolder = new File(path);

        if(!childFolder.exists())
            childFolder.mkdir();

        return path;
    }

    /******************************/

    // 파일 이름에서 확장자를 추출해준다.
    // ex) cat.png -> png
    private String getExt(String originalFilename){
        // .의 위치를 기준으로 그 다음부터가 확장자가 된다.
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    // 서버에 저장될 파일 이름 생성.
    // UUID + 기존 확장자로 생성해준다.
    private String createStoreFilename(String extType) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extType;
    }

    // 전체 파일의 경로를 얻는다.
    // 현재 게시글에 대한 디렉토리 + 파일 이름으로 생성
    public String getFullPath(String path, String filename){
        return path + "/" + filename;
    }

    /******************************/

    // 이미지 여러 개 전달
    // 이미지를 바이트로 변환하여 전달하기 - 프론트 단에서 가능할까...?
    public ImagesResponseDto getImages(Long boardId) {
        ImagesResponseDto responseImageDto = new ImagesResponseDto();

        // 게시글에 대한 경로 지정
        String path = findBoardPath();
        List<Image> images = getImagesByBoardId(boardId);

        images.forEach(image -> {
            // 서버 저장 경로
            String storeFileName = image.getStoreFileName();
            // 현재경로/업로드폴더/게시글id/파일이름 형태
            String file = path + "/" + boardId + storeFileName;

            // FileInputStream -> 파일로부터 바이트로 입력받아서, 바이트 단위로 출력할 수 있는 클래스.
            // file 경로에 있는 파일을 바이트 단위로 읽기!
            try {
                InputStream inputStream = new FileInputStream(file);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                responseImageDto.getImages().add(bytes);
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return responseImageDto;
    }

    /************************/

    // 이미지 한 개 전달
    public ImageResponseDto getImage(Long boardId, Long imageId) throws Exception {
        ImageResponseDto imageResponseDto = new ImageResponseDto();

        String boardDir = fileProperties.getBoardDir();
        String path = findBoardPath();

        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 이미지 1개 찾아오기
        Image image = findByImage(imageId);

        // 그 외 로직은 동일
        String storeFileName = image.getStoreFileName();
        try {
            String file = path + "/" + boardId + "/" + storeFileName;
            inputStream = new FileInputStream(file);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 이미지를 바이트로 변환하기
        int readCount = 0;
        byte[] buffer = new byte[1024];
        byte[] fileArray = null;

        try {
            while((readCount = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, readCount);
            }
            fileArray = outputStream.toByteArray();

            // 바이트 변환 결과
            imageResponseDto.setImage(fileArray);
            // 이미지 타입
            imageResponseDto.setImageType(image.getContentType());

            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            throw new Exception("파일을 변환하는데 문제가 발생했습니다.");
        }

        return imageResponseDto;
    }


    /******************************/

    // 게시글에 대한 경로 가져오기
    public String findBoardPath() {
        // 업로드할 디렉토리 경로.
        String uploadDir = fileProperties.getBoardDir();
        // 현재 디렉토리의 상대 경로
        Path relativePath = Paths.get("");
        // 절대 경로로 변경
        String path = relativePath.toAbsolutePath().toString() + "/" + uploadDir;
        return path;
    }

    // 게시글 id로 이미지 리스트 찾아오기
    private List<Image> getImagesByBoardId(Long boardId) {
        return imageRepository.findImages(boardId);
    }

    // 이미지 한 개만 찾아외 (id 이용)
    public Image findByImage(Long imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> {
            throw new NotFoundFileException("파일을 찾을 수 없습니다.");
        });
        return image;
    }

    /************************/

    // 게시글 삭제 시 이미지 벌크 삭제
    @Transactional
    public void deleteImages(Long boardId) {
        imageRepository.deleteBulkImages(boardId);
    }

}
