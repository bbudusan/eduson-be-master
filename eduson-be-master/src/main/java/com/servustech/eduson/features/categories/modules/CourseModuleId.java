package com.servustech.eduson.features.categories.modules;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class CourseModuleId
    implements Serializable {
 
    @Column(name = "course_id")
    private Long courseId;
 
    @Column(name = "module_id")
    private Long moduleId;
 
    private CourseModuleId() {}
 
    public CourseModuleId(
        Long courseId,
        Long moduleId) {
        this.courseId = courseId;
        this.moduleId = moduleId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        CourseModuleId that = (CourseModuleId) o;
        return Objects.equals(courseId, that.courseId) &&
               Objects.equals(moduleId, that.moduleId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(courseId, moduleId);
    }
}