package project.homelearn.service.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.homelearn.dto.teacher.board.NoticeReadDto;
import project.homelearn.entity.curriculum.Curriculum;
import project.homelearn.entity.teacher.TeacherBoard;
import project.homelearn.repository.board.TeacherBoardRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeTeacherService {

    private final TeacherBoardRepository teacherBoardRepository;

    // 조회
    public Page<NoticeReadDto> getTeacherNotice(Curriculum curriculum, Pageable pageable) {
        Page<TeacherBoard> noticeListPage = teacherBoardRepository.findTeacherBoardsBy(curriculum, pageable);
        return noticeListPage.map(this::convertToListWithFileDto);
    }

    // 조회를 위한 DTO변환
    private NoticeReadDto convertToListWithFileDto (TeacherBoard teacherBoard) {
        return new NoticeReadDto(
                teacherBoard.getId(),
                teacherBoard.getTitle(),
                teacherBoard.getContent(),
                teacherBoard.getCreatedDate(),
                teacherBoard.isEmergency(),
                teacherBoard.getFilePath(),
                teacherBoard.getUploadFileName()
        );
    }
}
