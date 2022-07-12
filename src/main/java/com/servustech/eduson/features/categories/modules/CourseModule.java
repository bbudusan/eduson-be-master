package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.products.courses.Course;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "CourseModule")
@Table(name = "course_modules")
public class CourseModule {
 
    @EmbeddedId
    private CourseModuleId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("moduleId")
    private Module module;
 
    private CourseModule() {}
 
    public CourseModule(Course course, Module module) {
        this.course = course;
        this.module = module;
        this.id = new CourseModuleId(course.getId(), module.getId());
    }
 
    //Getters and setters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        CourseModule that = (CourseModule) o;
        return Objects.equals(course, that.course) &&
               Objects.equals(module, that.module);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(course, module);
    }
}