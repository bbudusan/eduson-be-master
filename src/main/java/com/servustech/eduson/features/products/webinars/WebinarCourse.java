package com.servustech.eduson.features.products.webinars;

import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.courses.Course;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "WebinarCourse")
@Table(name = "webinar_courses")
public class WebinarCourse {
 
    @EmbeddedId
    private WebinarCourseId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("webinarId")
    private Webinar webinar;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;
 
    @Column(name = "place")
    private Long place = 0L;

    private WebinarCourse() {}
 
    public WebinarCourse(Webinar webinar, Course course, Long place) {
        this.webinar = webinar;
        this.course = course;
        this.place = place;
        this.id = new WebinarCourseId(webinar.getId(), course.getId());
    }
 
    //Getters and setters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        WebinarCourse that = (WebinarCourse) o;
        return Objects.equals(webinar, that.webinar) &&
               Objects.equals(course, that.course);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(webinar, course);
    }
}