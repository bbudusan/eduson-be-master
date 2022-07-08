package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.account.User;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "FavoriteCourse")
@Table(name = "course_favorites")
public class FavoriteCourse {
 
    @EmbeddedId
    private FavoriteCourseId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;
 
    private FavoriteCourse() {}
 
    public FavoriteCourse(Course course, User user) {
        this.course = course;
        this.user = user;
        this.id = new FavoriteCourseId(course.getId(), user.getId());
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        FavoriteCourse that = (FavoriteCourse) o;
        return Objects.equals(course, that.course) &&
               Objects.equals(user, that.user);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(course, user);
    }
}