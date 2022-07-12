package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.products.courses.Advert;
import com.servustech.eduson.features.products.courses.Course;
import lombok.Data;
import javax.persistence.*;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.sql.Time;

@Data
@Entity(name = "CourseAdvert")
@Table(name = "course_ads")
public class CourseAdvert {
 
    @EmbeddedId
    private CourseAdvertId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("advertId")
    private Advert advert;
 
    @Column(name = "start")
    private Time start;
    @Column(name = "priority")
    private Integer priority;
    @Column(name = "rule_id")
    private Long rule;

    private CourseAdvert() {}
 
    public CourseAdvert(Course course, Advert advert, Time start, Integer priority, Long rule) {
        this.course = course;
        this.advert = advert;
        this.start = start;
        this.priority = priority;
        this.rule = rule;
        this.id = new CourseAdvertId(course.getId(), advert.getId());
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        CourseAdvert that = (CourseAdvert) o;
        return Objects.equals(advert, that.advert) &&
               Objects.equals(course, that.course);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(course, advert);
    }
}