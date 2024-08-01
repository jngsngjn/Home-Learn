package project.homelearn.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.homelearn.entity.inquiry.ManagerInquiry;
import project.homelearn.entity.inquiry.TeacherInquiry;
import project.homelearn.entity.notification.student.StudentNotification;
import project.homelearn.entity.notification.student.StudentNotificationType;
import project.homelearn.entity.notification.teacher.TeacherNotification;
import project.homelearn.entity.notification.teacher.TeacherNotificationType;
import project.homelearn.entity.user.Role;
import project.homelearn.entity.user.User;
import project.homelearn.repository.notification.StudentNotificationRepository;
import project.homelearn.repository.notification.TeacherNotificationRepository;
import project.homelearn.repository.user.UserRepository;

import static project.homelearn.entity.user.Role.*;

/**
 * Author : 정성진
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CommonNotificationService {

    private final UserRepository userRepository;
    private final StudentNotificationRepository studentNotificationRepository;
    private final TeacherNotificationRepository teacherNotificationRepository;

    // 매니저가 학생 또는 강사의 문의에 답변했을 때 해당 인원에게 알림
    public void notifyManageResponse(ManagerInquiry inquiry) {
        User user = userRepository.findUserByManagerInquiry(inquiry);
        Role role = user.getRole();

        if (role.equals(ROLE_STUDENT)) {
            StudentNotification notification = new StudentNotification();
            notification.setUser(user);
            notification.setManagerInquiry(inquiry);
            notification.setType(StudentNotificationType.MANAGER_REPLY_TO_INQUIRY);
            studentNotificationRepository.save(notification);
        } else {
            TeacherNotification notification = new TeacherNotification();
            notification.setUser(user);
            notification.setManagerInquiry(inquiry);
            notification.setType(TeacherNotificationType.MANAGER_REPLY_TO_INQUIRY);
            teacherNotificationRepository.save(notification);
        }
    }

    // 강사가 학생의 문의에 답변했을 때 해당 인원에게 알림
    public void notifyTeacherResponse(TeacherInquiry teacherInquiry) {
        User user = userRepository.findUserByTeacherInquiry(teacherInquiry);

        StudentNotification notification = new StudentNotification();
        notification.setUser(user);
        notification.setTeacherInquiry(teacherInquiry);
        notification.setType(StudentNotificationType.TEACHER_REPLY_TO_INQUIRY);
        studentNotificationRepository.save(notification);
    }
}