package project.homelearn.service.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.homelearn.dto.student.badge.BadgeViewDto;
import project.homelearn.entity.student.Student;
import project.homelearn.entity.student.badge.Badge;
import project.homelearn.entity.student.badge.StudentBadge;
import project.homelearn.entity.user.User;
import project.homelearn.repository.badge.BadgeRepository;
import project.homelearn.repository.badge.StudentBadgeRepository;
import project.homelearn.repository.user.StudentRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final StudentBadgeRepository studentBadgeRepository;
    private final StudentNotificationService notificationService;
    private final StudentRepository studentRepository;

    public void getBadge(User student, String badgeName) {
        Badge badge = badgeRepository.findByName(badgeName);

        // 이미 획득했다면 return
        if (studentBadgeRepository.existsByUserAndBadge(student, badge)) {
            return;
        }
        studentBadgeRepository.save(new StudentBadge(student, badge));
        notificationService.badgeNotify(student, badge); // 알림

        log.info("[{}] 학생이 [{}] 뱃지 획득", student.getName(), badge.getName());
    }

    public List<BadgeViewDto> getAllBadges(String username) {
        Student student = studentRepository.findByUsername(username);
        return studentBadgeRepository.findAllBadges(student);
    }

    public List<BadgeViewDto> getEarnBadges(String username) {
        Student student = studentRepository.findByUsername(username);
        return studentBadgeRepository.findEarnBadges(student);
    }

    public List<BadgeViewDto> getNoEarnBadges(String username) {
        Student student = studentRepository.findByUsername(username);
        return studentBadgeRepository.findNoEarnBadges(student);
    }
}