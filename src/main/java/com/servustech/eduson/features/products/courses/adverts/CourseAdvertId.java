package com.servustech.eduson.features.products.courses;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class CourseAdvertId
    implements Serializable {
 
    @Column(name = "course_id")
    private Long courseId;
 
    @Column(name = "advert_id")
    private Long advertId;
 
    private CourseAdvertId() {}
 
    public CourseAdvertId(
        Long courseId,
        Long advertId) {
        this.courseId = courseId;
        this.advertId = advertId;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        CourseAdvertId that = (CourseAdvertId) o;
        return Objects.equals(advertId, that.advertId) &&
               Objects.equals(courseId, that.courseId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(courseId, advertId);
    }
}