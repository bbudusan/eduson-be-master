package com.servustech.eduson.features.categories.tags;

import com.servustech.eduson.features.products.webinars.Webinar;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "WebinarTag")
@Table(name = "webinar_tags")
public class WebinarTag {

    @EmbeddedId
    private WebinarTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("webinarId")
    private Webinar webinar;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;

    private WebinarTag() {
    }

    public WebinarTag(Webinar webinar, Tag tag) {
        this.webinar = webinar;
        this.tag = tag;
        this.id = new WebinarTagId(webinar.getId(), tag.getId());
    }

    // Getters and setters omitted for brevity

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        WebinarTag that = (WebinarTag) o;
        return Objects.equals(webinar, that.webinar) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webinar, tag);
    }
}