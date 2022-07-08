package com.servustech.eduson.features.products.webinars;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class WebinarCourseId
    implements Serializable {
 
    @Column(name = "webinar_id")
    private Long webinarId;
 
    @Column(name = "course_id")
    private Long courseId;
 
    private WebinarCourseId() {}
 
    public WebinarCourseId(
        Long webinarId,
        Long courseId) {
        this.webinarId = webinarId;
        this.courseId = courseId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        WebinarCourseId that = (WebinarCourseId) o;
        return Objects.equals(webinarId, that.webinarId) &&
               Objects.equals(courseId, that.courseId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(webinarId, courseId);
    }
}