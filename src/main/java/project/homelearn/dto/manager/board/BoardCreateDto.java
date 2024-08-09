package project.homelearn.dto.manager.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BoardCreateDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Boolean emergency;

    private MultipartFile file;
}

/*
  JSON 요청 BODY 체크 후, form-data 선택. Key값과 Value값으로 요청
  요청 경로 : localhost:8080/managers/notification-boards
  POST 요청
  key         value
  title       제목
  content     내용
  emergency   true(긴급 O) or false(긴급 X)
  file        추가 할 파일 선택(넣어도되고 안넣으면 null)

  %null로 하고 싶다면 file 입력할 필요없음
*/