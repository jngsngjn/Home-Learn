package project.homelearn.service.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.homelearn.dto.common.FileDto;
import project.homelearn.dto.student.homework.HomeworkListDto;
import project.homelearn.dto.student.homework.HomeworkSubmitDto;
import project.homelearn.dto.student.homework.HomeworkUpdateDto;
import project.homelearn.entity.curriculum.Curriculum;
import project.homelearn.entity.homework.Homework;
import project.homelearn.entity.homework.StudentHomework;
import project.homelearn.entity.student.Student;
import project.homelearn.dto.student.dashboard.ViewHomeworkDto;
import project.homelearn.repository.curriculum.CurriculumRepository;
import project.homelearn.repository.homework.HomeworkRepository;
import project.homelearn.repository.homework.StudentHomeworkRepository;
import project.homelearn.repository.user.StudentRepository;
import project.homelearn.service.common.StorageService;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static project.homelearn.config.storage.FolderType.HOMEWORK;

/**
 * Author : 동재완
 */
@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class StudentHomeworkService {

    private final StorageService storageService;
    private final StudentRepository studentRepository;
    private final HomeworkRepository homeworkRepository;
    private final StudentHomeworkRepository studentHomeworkRepository;
    private final CurriculumRepository curriculumRepository;

    public boolean addHomework(String username, HomeworkSubmitDto homeWorkSubmitDto) {
        try {
            Student student = studentRepository.findByUsername(username);
            if (student == null) {
                throw new RuntimeException("Student not found");
            }

            Homework homework = homeworkRepository.findHomeworkAndCurriculum(homeWorkSubmitDto.getHomeworkId());
            if (homework == null) {
                throw new RuntimeException("Homework not found");
            }

            boolean isDuplicate = studentHomeworkRepository.existsByStudentAndHomework(student, homework);
            if (isDuplicate) {
                throw new RuntimeException("Homework duplicate submit");
            }

            StudentHomework studentHomework = new StudentHomework();
            studentHomework.setUser(student);
            studentHomework.setDescription(homeWorkSubmitDto.getDescription());
            studentHomework.setHomework(homework);

            MultipartFile file = homeWorkSubmitDto.getFile();
            if (file != null && !file.isEmpty()) {
                String folderPath = storageService.getFolderPath(student, HOMEWORK);
                FileDto fileDto = storageService.uploadFile(file, folderPath);
                studentHomework.setUploadFileName(fileDto.getOriginalFileName());
                studentHomework.setStoreFileName(fileDto.getUploadFileName());
                studentHomework.setFilePath(fileDto.getFilePath());
            }

            studentHomeworkRepository.save(studentHomework);
            return true;
        } catch (Exception e) {
            log.error("Error creating homework", e);
            return false;
        }
    }

    public boolean updateHomework(Long homeworkId, String username, HomeworkUpdateDto homeWorkUpdateDto) {
        try {
            StudentHomework homework = studentHomeworkRepository.findById(homeworkId)
                    .orElseThrow(() -> new RuntimeException("Error getting homework" + homeworkId));

            LocalDateTime deadline = homework.getHomework().getDeadline();
            if (LocalDateTime.now().isAfter(deadline)) {
                throw new RuntimeException("Cannot update homework after the deadline:" + homeworkId);
            }

            homework.setDescription(homeWorkUpdateDto.getDescription());

            MultipartFile file = homeWorkUpdateDto.getFile();
            String previousFilePath = homework.getFilePath();

            if (homeWorkUpdateDto.isUseDefaultFile() || file != null && !file.isEmpty()) {
                if (previousFilePath != null) {
                    storageService.deleteFile(previousFilePath);
                    homework.setUploadFileName(null);
                    homework.setStoreFileName(null);
                    homework.setFilePath(null);
                }
            }

            Student student = studentRepository.findByUsername(username);
            if (file != null && !file.isEmpty()) {
                String folderPath = storageService.getFolderPath(student, HOMEWORK);
                FileDto fileDto = storageService.uploadFile(file, folderPath);
                homework.setUploadFileName(fileDto.getOriginalFileName());
                homework.setStoreFileName(fileDto.getUploadFileName());
                homework.setFilePath(fileDto.getFilePath());
            }

            studentHomeworkRepository.save(homework);
            return true;
        } catch (Exception e) {
            log.error("Error updating homework", e);
            return false;
        }
    }

    public boolean deleteHomework(Long homeworkId) {
        try {
            StudentHomework homework = studentHomeworkRepository.findById(homeworkId)
                    .orElseThrow(() -> new RuntimeException("Error getting homework" + homeworkId));

            LocalDateTime deadline = homework.getHomework().getDeadline();
            if (LocalDateTime.now().isAfter(deadline)) {
                throw new RuntimeException("Cannot delete homework after the deadline:" + homeworkId);
            }

            String file = homework.getFilePath();
            if (file != null) {
                storageService.deleteFile(file);
            }

            studentHomeworkRepository.deleteById(homeworkId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting homework", e);
            return false;
        }
    }
    // 미제출우선 최근 2개 과제
    public List<ViewHomeworkDto> getHomeworkTop2(String username){
        return homeworkRepository.findHomeworkTop2(username);
    }

    // 진행중인 과제 페이지 리스트 - 마감기한 기준 오름 차순
    public Page<HomeworkListDto> getHomeworkProceeding(String username, Pageable pageable) {
        Curriculum curriculum = curriculumRepository.findCurriculumByUsername(username);

        Page<Homework> homeworks = homeworkRepository.findProceedingHomework(curriculum, LocalDateTime.now(), pageable);

        List<HomeworkListDto> homeworkList = getHomeworkListDtos(homeworks);

        return new PageImpl<>(homeworkList, pageable, homeworks.getTotalElements());
    }

    // 마감된 과제들
    public Page<HomeworkListDto> getHomeworkClosed(String username, Pageable pageable) {
        Curriculum curriculum = curriculumRepository.findCurriculumByUsername(username);

        Page<Homework> homeworks = homeworkRepository.findClosedHomework(curriculum, LocalDateTime.now(), pageable);

        List<HomeworkListDto> homeworkList = getHomeworkListDtos(homeworks);

        return new PageImpl<>(homeworkList, pageable, homeworks.getTotalElements());
    }

    // 페이지 DTO 변환
    private List<HomeworkListDto> getHomeworkListDtos(Page<Homework> homeworks) {
        return homeworks.stream()
                .map(homework -> new HomeworkListDto(
                        homework.getId(),
                        homework.getTitle(),
                        homework.getDescription(),
                        homework.getDeadline(),
                        homework.getStudentHomeworks().size()))
                .collect(Collectors.toList());
    }
}