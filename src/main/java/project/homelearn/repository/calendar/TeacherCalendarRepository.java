package project.homelearn.repository.calendar;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.homelearn.entity.calendar.TeacherCalendar;

public interface TeacherCalendarRepository extends JpaRepository<TeacherCalendar, Long> {

    @Query("select c from TeacherCalendar c join fetch c.user where c.id =:calendarId")
    TeacherCalendar findTeacherCalendarAndTeacher(@Param("calendarId") Long calendarId);
}