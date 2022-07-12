package com.servustech.eduson.features.categories.tags;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class WebinarTagId
    implements Serializable {
 
    @Column(name = "webinar_id")
    private Long webinarId;
 
    @Column(name = "tag_id")
    private Long tagId;
 
    private WebinarTagId() {}
 
    public WebinarTagId(
        Long webinarId,
        Long tagId) {
        this.webinarId = webinarId;
        this.tagId = tagId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        WebinarTagId that = (WebinarTagId) o;
        return Objects.equals(webinarId, that.webinarId) &&
               Objects.equals(tagId, that.tagId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(webinarId, tagId);
    }
}