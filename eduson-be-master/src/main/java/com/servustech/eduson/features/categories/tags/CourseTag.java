package com.servustech.eduson.features.categories.tags;

import com.servustech.eduson.features.products.courses.Course;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "CourseTag")
@Table(name = "course_tags")
public class CourseTag {
 
    @EmbeddedId
    private CourseTagId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;
 
    private CourseTag() {}
 
    public CourseTag(Course course, Tag tag) {
        this.course = course;
        this.tag = tag;
        this.id = new CourseTagId(course.getId(), tag.getId());
    }
 
    //Getters and setters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        CourseTag that = (CourseTag) o;
        return Objects.equals(course, that.course) &&
               Objects.equals(tag, that.tag);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(course, tag);
    }
}