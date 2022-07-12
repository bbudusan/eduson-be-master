package com.servustech.eduson.features.products.courses;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class FavoriteCourseId
    implements Serializable {
 
    @Column(name = "course_id")
    private Long courseId;
 
    @Column(name = "user_id")
    private Long userId;
 
    private FavoriteCourseId() {}
 
    public FavoriteCourseId(
        Long courseId,
        Long userId) {
        this.courseId = courseId;
        this.userId = userId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        FavoriteCourseId that = (FavoriteCourseId) o;
        return Objects.equals(courseId, that.courseId) &&
               Objects.equals(userId, that.userId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(courseId, userId);
    }
}