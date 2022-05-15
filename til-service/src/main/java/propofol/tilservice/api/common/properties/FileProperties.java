package propofol.tilservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
// application.yml에 있는 file에 대한 속성 매핑해주기
@ConfigurationProperties(prefix="file")
@ConstructorBinding
public class FileProperties {
    private final String boardDir;

    public FileProperties(String profileDir, String boardDir) {
        this.boardDir = boardDir;
    }
}
