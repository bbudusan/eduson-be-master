package com.servustech.eduson.features.products.webinars;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class FavoriteWebinarId
    implements Serializable {
 
    @Column(name = "webinar_id")
    private Long webinarId;
 
    @Column(name = "user_id")
    private Long userId;
 
    private FavoriteWebinarId() {}
 
    public FavoriteWebinarId(
        Long webinarId,
        Long userId) {
        this.webinarId = webinarId;
        this.userId = userId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        FavoriteWebinarId that = (FavoriteWebinarId) o;
        return Objects.equals(webinarId, that.webinarId) &&
               Objects.equals(userId, that.userId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(webinarId, userId);
    }
}