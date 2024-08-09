package project.homelearn.controller.student.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.homelearn.dto.manager.board.BoardReadDto;
import project.homelearn.dto.teacher.board.NoticeReadDto;
import project.homelearn.entity.curriculum.Curriculum;
import project.homelearn.repository.curriculum.CurriculumRepository;
import project.homelearn.service.student.NoticeManagerService;
import project.homelearn.service.student.NoticeTeacherService;

import java.security.Principal;

@RestController
@RequestMapping("/students/notification-boards")
@RequiredArgsConstructor
public class StudentNotificationViewController {

    private final NoticeManagerService noticeManagerService;
    private final NoticeTeacherService noticeTeacherService;
    private final CurriculumRepository curriculumRepository;

    // 매니저 공지사항 조회
    @GetMapping("/managers")
    public ResponseEntity<?> readManagerNotifications(@RequestParam(name = "page", defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<BoardReadDto> noticeList = noticeManagerService.getManagerNotice(pageable);

        if (noticeList.hasContent()) {
            return new ResponseEntity<>(noticeList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 강사 공지사항 조회
    @GetMapping("/teachers")
    public ResponseEntity<?> readTeacherNotifications(Principal principal,
                                        @RequestParam(name = "page", defaultValue = "0") int page) {

        String username = principal.getName();
        Curriculum curriculum = curriculumRepository.findCurriculumByUsername(username);

        if (curriculum == null) {
            return new ResponseEntity<>("Not found Teacher curriculum", HttpStatus.NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, 5);
        Page<NoticeReadDto> noticeListBoard = noticeTeacherService.getTeacherNotice(curriculum, pageable);

        if (noticeListBoard.hasContent()) {
            return new ResponseEntity<>(noticeListBoard, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}