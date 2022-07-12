package com.servustech.eduson.features.products.webinars;

import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.account.User;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "WebinarCoordinator")
@Table(name = "webinar_coordinators")
public class WebinarCoordinator {

    @EmbeddedId
    private WebinarCoordinatorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("webinarId")
    private Webinar webinar;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("coordinatorId")
    private User coordinator;

    private WebinarCoordinator() {
    }

    public WebinarCoordinator(Webinar webinar, User coordinator) {
        this.webinar = webinar;
        this.coordinator = coordinator;
        this.id = new WebinarCoordinatorId(webinar.getId(), coordinator.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        WebinarCoordinator that = (WebinarCoordinator) o;
        return Objects.equals(webinar, that.webinar) &&
                Objects.equals(coordinator, that.coordinator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webinar, coordinator);
    }
}