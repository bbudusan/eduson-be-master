package com.servustech.eduson.features.categories.tags;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class CourseTagId
    implements Serializable {
 
    @Column(name = "course_id")
    private Long courseId;
 
    @Column(name = "tag_id")
    private Long tagId;
 
    private CourseTagId() {}
 
    public CourseTagId(
        Long courseId,
        Long tagId) {
        this.courseId = courseId;
        this.tagId = tagId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        CourseTagId that = (CourseTagId) o;
        return Objects.equals(courseId, that.courseId) &&
               Objects.equals(tagId, that.tagId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(courseId, tagId);
    }
}