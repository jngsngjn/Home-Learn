package project.homelearn.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.homelearn.dto.common.HeaderCommonDto;
import project.homelearn.dto.manager.header.NotificationDto;
import project.homelearn.entity.curriculum.Curriculum;
import project.homelearn.entity.notification.manager.ManagerNotification;
import project.homelearn.entity.notification.manager.ManagerNotificationType;
import project.homelearn.entity.notification.student.StudentNotification;
import project.homelearn.entity.notification.student.StudentNotificationType;
import project.homelearn.entity.notification.teacher.TeacherNotification;
import project.homelearn.entity.notification.teacher.TeacherNotificationType;
import project.homelearn.entity.user.Role;
import project.homelearn.entity.user.User;
import project.homelearn.repository.notification.ManagerNotificationRepository;
import project.homelearn.repository.notification.StudentNotificationRepository;
import project.homelearn.repository.notification.TeacherNotificationRepository;
import project.homelearn.repository.user.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static project.homelearn.entity.notification.manager.ManagerNotificationType.STUDENT_INQUIRY;
import static project.homelearn.entity.notification.manager.ManagerNotificationType.TEACHER_INQUIRY;
import static project.homelearn.entity.notification.student.StudentNotificationType.*;
import static project.homelearn.entity.notification.student.StudentNotificationType.MANAGER_REPLY_TO_INQUIRY;
import static project.homelearn.entity.notification.teacher.TeacherNotificationType.*;
import static project.homelearn.entity.user.Role.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeaderService {

    private final UserRepository userRepository;
    private final ManagerNotificationRepository managerNotificationRepository;
    private final TeacherNotificationRepository teacherNotificationRepository;
    private final StudentNotificationRepository studentNotificationRepository;

    public HeaderCommonDto getHeaderCommon(String username) {
        User user = userRepository.findUserAndCurriculum(username);
        Role role = user.getRole();
        if (role.equals(ROLE_MANAGER)) {
            return null;
        }

        Curriculum curriculum = user.getCurriculum();
        LocalDate startDate = curriculum.getStartDate();
        LocalDate endDate = curriculum.getEndDate();
        LocalDate currentDate = LocalDate.now();

        double progressRate = calculateProgressRate(startDate, endDate, currentDate);
        return HeaderCommonDto
                .builder()
                .curriculumFullName(curriculum.getFullName())
                .name(user.getName())
                .imagePath(user.getImagePath())
                .progressRate(progressRate)
                .build();
    }

    private double calculateProgressRate(LocalDate startDate, LocalDate endDate, LocalDate currentDate) {
        if (currentDate.isBefore(startDate)) {
            return 0.0;
        } else if (currentDate.isAfter(endDate)) {
            return 100.0;
        } else {
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            long daysPassed = ChronoUnit.DAYS.between(startDate, currentDate);
            double progress = (double) daysPassed / totalDays * 100;
            return Math.round(progress * 10) / 10.0;
        }
    }

    public boolean deleteNotification(Long id, String username) {
        User user = userRepository.findByUsername(username);
        Role role = user.getRole();

        if (role.equals(ROLE_STUDENT)) {
            studentNotificationRepository.deleteById(id);
            return true;
        }

        if (role.equals(ROLE_TEACHER)) {
            teacherNotificationRepository.deleteById(id);
            return true;
        }

        managerNotificationRepository.deleteById(id);
        return true;
    }

    public boolean deleteAllNotifications(String username) {
        User user = userRepository.findByUsername(username);
        Role role = user.getRole();

        if (role.equals(ROLE_STUDENT)) {
            studentNotificationRepository.deleteAllByUser(user);
            return true;
        }

        if (role.equals(ROLE_TEACHER)) {
            teacherNotificationRepository.deleteAllByUser(user);
            return true;
        }

        managerNotificationRepository.deleteAll();
        return true;
    }

    public NotificationDto getManagerNotification() {
        List<ManagerNotification> notifications = managerNotificationRepository.findAll();

        NotificationDto result = new NotificationDto();
        result.setCount(notifications.size());

        List<NotificationDto.NotificationSubDto> subList = new ArrayList<>();
        for (ManagerNotification notification : notifications) {
            NotificationDto.NotificationSubDto subDto = new NotificationDto.NotificationSubDto();
            subDto.setInquiryId(notification.getManagerInquiry().getId());

            ManagerNotificationType type = notification.getType();
            if (type.equals(STUDENT_INQUIRY)) {
                subDto.setMessage("학생 1:1 문의가 등록되었습니다.");
            }

            if (type.equals(TEACHER_INQUIRY)) {
                subDto.setMessage("강사 1:1 문의가 등록되었습니다.");
            }
            subList.add(subDto);
        }
        result.setNotifications(subList);
        return result;
    }

    public NotificationDto getTeacherNotification(String username) {
        User user = userRepository.findByUsername(username);
        List<TeacherNotification> notifications = teacherNotificationRepository.findAllByUser(user);

        NotificationDto result = new NotificationDto();
        result.setCount(notifications.size());

        List<NotificationDto.NotificationSubDto> subList = new ArrayList<>();
        for (TeacherNotification notification : notifications) {
            NotificationDto.NotificationSubDto subDto = new NotificationDto.NotificationSubDto();
            subDto.setInquiryId(notification.getManagerInquiry().getId());

            TeacherNotificationType type = notification.getType();
            if (type.equals(QUESTION_POSTED)) {
                subDto.setMessage("");
            }

            if (type.equals(REPLY_TO_COMMENT)) {
                subDto.setMessage("");
            }

            if (type.equals(STUDENT_INQUIRY_TO_TEACHER)) {
                subDto.setMessage("");
            }

            if (type.equals(TeacherNotificationType.MANAGER_REPLY_TO_INQUIRY)) {
                subDto.setMessage("");
            }
            subList.add(subDto);
        }
        result.setNotifications(subList);
        return result;
    }

    public NotificationDto getStudentNotification(String username) {
        User user = userRepository.findByUsername(username);
        List<StudentNotification> notifications = studentNotificationRepository.findAllByUser(user);

        NotificationDto result = new NotificationDto();
        result.setCount(notifications.size());

        List<NotificationDto.NotificationSubDto> subList = new ArrayList<>();
        for (StudentNotification notification : notifications) {
            NotificationDto.NotificationSubDto subDto = new NotificationDto.NotificationSubDto();
            subDto.setInquiryId(notification.getManagerInquiry().getId());

            StudentNotificationType type = notification.getType();
            if (type.equals(HOMEWORK_UPLOADED)) {
                subDto.setMessage("");
            }

            if (type.equals(REPLY_TO_QUESTION)) {
                subDto.setMessage("");
            }

            if (type.equals(MANAGER_REPLY_TO_INQUIRY)) {
                subDto.setMessage("");
            }

            if (type.equals(TEACHER_REPLY_TO_INQUIRY)) {
                subDto.setMessage("");
            }

            if (type.equals(SURVEY)) {
                subDto.setMessage("");
            }

            if (type.equals(COMMENT_ON_MY_FREE_BOARD)) {
                subDto.setMessage("");
            }
            subList.add(subDto);
        }
        result.setNotifications(subList);
        return result;
    }
}