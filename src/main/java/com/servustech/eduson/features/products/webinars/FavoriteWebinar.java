package com.servustech.eduson.features.products.webinars;

import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.account.User;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "FavoriteWebinar")
@Table(name = "webinar_favorites")
public class FavoriteWebinar {
 
    @EmbeddedId
    private FavoriteWebinarId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("webinarId")
    private Webinar webinar;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;
 
    private FavoriteWebinar() {}
 
    public FavoriteWebinar(Webinar webinar, User user) {
        this.webinar = webinar;
        this.user = user;
        this.id = new FavoriteWebinarId(webinar.getId(), user.getId());
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        FavoriteWebinar that = (FavoriteWebinar) o;
        return Objects.equals(webinar, that.webinar) &&
               Objects.equals(user, that.user);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(webinar, user);
    }
}