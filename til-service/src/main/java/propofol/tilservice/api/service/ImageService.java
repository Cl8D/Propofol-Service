package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.exception.NotFoundFileException;
import propofol.tilservice.domain.file.entity.Image;
import propofol.tilservice.domain.file.repository.ImageRepository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final FileProperties fileProperties;
    private final ImageRepository imageRepository;

    // 게시글 파일 저장 기능
    public Image saveImage(MultipartFile file) throws IOException{
        // 서버에 파일을 저장할 디렉토리 생성해주기
        // 해당 게시글에 대한 파일 저장소의 경로가 리턴된다.
        String path = createFolder();

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

        // 저장된 이미지 리턴
        return imageRepository.save(image);
//        // 게시글에 해당 파일 추가해주기
//        // board가 변경감지에 의해 감지되면 image도 함께 변하게 된다. (cascade)
//        board.addImage(image);
    }

    /******************************/

    // 파일을 저장할 폴더 생성해주기 (로컬에 저장하도록 구현)
    /** TODO 프로젝트 오타 검수 */
    /** 폴더 생성 시 부모 디렉토리 + 게시글 id로 하위 디렉토리에 파일 저장되도록 구현으나
     * 부모 디렉토리 밑에 한 번에 저장되도록 변경 */
    private String createFolder () {
//        // 현재 디렉토리에 대한 상대경로 생성
//        Path relativePath = Paths.get("");
//        // 상대경로 -> 절대경로로 변경한 이후, 새로운 디렉토리를 만들기 위해 uploadDir 붙여주기.
//        String path = relativePath.toAbsolutePath().toString() + "/" + uploadDir;
        String path = findBoardPath(getUploadDir());
        // 위에서 지정한 경로로 디렉토리 생성해주기 (부모 디렉토리)
        File parentFolder = new File(path);

        // 이전에 생성된 적 없다면 새로 만들기
        if(!parentFolder.exists())
            parentFolder.mkdir();
//        // 게시글의 id로 자식 폴더 생성 (즉, 해당 폴더에는 해당 게시글에 업로드된 파일이 담긴다.)
//        path = path + "/" + board.getId();
//
//        File childFolder = new File(path);
//
//        if(!childFolder.exists())
//            childFolder.mkdir();
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

    // 프론트에게 이미지를 바이트 배열로 전달해주기 (인코딩되어 있는 상태)
    // 인자로 서버에 저장된 파일 이름을 전달해준다.
    /** DTO -> byte[]로 리턴 형태 변경,
     * DTO로 전달해주면 프론트에서 CORB 에러 발생
     * (프론트단에서 이미지를 출력할 때 <img src="~~"> 형태로 만들어주는데,
     * 이때 src에 dto 관련 정보가 들어가면 브라우저 상에서 막히게 되는 것!*/
    public byte[] getImageBytes(String fileName) {
        // 게시글에 대한 경로 지정
        String path = findBoardPath(getUploadDir());
        byte[] bytes = null;

        // FileInputStream -> 파일로부터 바이트로 입력받아서, 바이트 단위로 출력할 수 있는 클래스.
        // file 경로에 있는 파일을 바이트 단위로 읽기!
        try {
            String file = path + "/" + fileName;
            InputStream inputStream = new FileInputStream(file);
            bytes = IOUtils.toByteArray(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /************************/
//    // 이미지 한 개 전달
//    public ImageResponseDto getImage(Long boardId, Long imageId) throws Exception {
//        ImageResponseDto imageResponseDto = new ImageResponseDto();
//
//        String boardDir = fileProperties.getBoardDir();
//        String path = findBoardPath();
//
//        FileInputStream inputStream = null;
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        // 이미지 1개 찾아오기
//        Image image = findByImage(imageId);
//
//        // 그 외 로직은 동일
//        String storeFileName = image.getStoreFileName();
//        try {
//            String file = path + "/" + boardId + "/" + storeFileName;
//            inputStream = new FileInputStream(file);
//        }catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // 이미지를 바이트로 변환하기
//        int readCount = 0;
//        byte[] buffer = new byte[1024];
//        byte[] fileArray = null;
//        try {
//            while((readCount = inputStream.read(buffer)) != -1){
//                outputStream.write(buffer, 0, readCount);
//            }
//            fileArray = outputStream.toByteArray();
//            // 바이트 변환 결과
//            imageResponseDto.setImage(fileArray);
//            // 이미지 타입
//            imageResponseDto.setImageType(image.getContentType());
//            inputStream.close();
//            outputStream.close();
//        } catch (IOException e) {
//            throw new Exception("파일을 변환하는데 문제가 발생했습니다.");
//        }
//        return imageResponseDto;
//    }

//    // 이미지 한 개만 찾아오기 (id 이용)
//    public Image findByImage(Long imageId) {
//        Image image = imageRepository.findById(imageId).orElseThrow(() -> {
//            throw new NotFoundFileException("파일을 찾을 수 없습니다.");
//        });
//        return image;
//    }

    /******************************/

    // 업로드할 디렉토리 경로 리턴
    public String getUploadDir() {
        // 프로퍼티에 저장된 디렉토리 이름!
        return fileProperties.getBoardDir();
    }

    /******************************/

    // 게시글에 대한 경로 가져오기
    public String findBoardPath(String dir) {
        // 현재 디렉토리의 상대 경로
        Path relativePath = Paths.get("");
        // 절대 경로로 변경 (파라미터로 dir 받아서 접근)
        String path = relativePath.toAbsolutePath().toString() + "/" + dir;
        return path;
    }

    /************************/

    // 파일에 대한 게시글 정보 변경
    @Transactional
    public void changeImageBoardId (List<String> fileNames, Board saveBoard) {
        // 먼저 이미지들을 찾아오기!
        List<Image> allImage = getAllImage(fileNames);
        // 각각의 이미지들에 대해 게시글 정보 변경해주기
        allImage.forEach(image -> {
            image.changeBoard(saveBoard);
        });
    }

    // 파일 이름으로 이미지 찾기
    public List<Image> getAllImage (List<String> storeFileNames) {
        return imageRepository.findImagesInNames(storeFileNames);
    }

    /******************************/

    // 파일 목록을 받으면 서버에 저장되는 파일 이름의 리스트를 반환해주는 함수!
    // 클라이언트는 이를 받아서 그대로 게시글 정보 + 파일 정보 줄 때 다시 준다!
    @Transactional
    public List<String> getStoreImageNames(List<MultipartFile> files) {
        List<String> fileNames = new ArrayList<>();

        // 이때 서버에서는 path + 파일명 형태로 경로 지정
        // 여기서 localhost:8000 부분은 클라이언트에서 SERVER_URL:8000 형태로 처리해줌
        String path = "http://localhost:8000/til-service/api/v1/images";

        files.forEach(file -> {
            try {
                // 이미지 저장
                Image savedImage = saveImage(file);
                // 서버 저장 파일 이름들 리스트에 추가해주기!
                fileNames.add(path + "/" + savedImage.getStoreFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return fileNames;
    }

    /******************************/

    // 게시글 목록 보여줄 때 대표 이미지(썸네일) 뽑는 함수
    public Image getTopImage(Long boardId) {
        return imageRepository.findTopByBoardId(boardId).orElse(null);
    }

    /******************************/

    // 썸네일 이미지를 byte 배열로 전달
    public byte[] getTopImageBytes(Image image) {
        // 이미지 없으면 null로 전달
        if(image == null)
            return null;
        return getImageBytes(image.getStoreFileName());
    }


    /******************************/

    // 이미지 타입 가져오기
    public String getImageType(Image image) {
        if(image == null)
            return null;
        // image/png 같은 형식으로 지정되어 있다.
        return image.getContentType();
    }

    /******************************/

    // 해당 게시글에 존재하는 파일 리스트 전부 가져오기
    public List<Image> getImagesByBoardId(Long boardId){
        return imageRepository.findAllByBoardId(boardId);
    }

    /******************************/

    // 게시글 삭제 시 이미지 벌크 삭제
    @Transactional
    public void deleteImages(Long boardId) {
        imageRepository.deleteImages(boardId);
    }

}
