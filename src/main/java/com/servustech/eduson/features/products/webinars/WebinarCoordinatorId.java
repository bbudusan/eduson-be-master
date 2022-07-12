package com.servustech.eduson.features.products.webinars;
import lombok.Data;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.*;
import java.util.Objects;

@Data
@Embeddable
public class WebinarCoordinatorId
    implements Serializable {
 
    @Column(name = "webinar_id")
    private Long webinarId;
 
    @Column(name = "coordinator_id")
    private Long coordinatorId;
 
    private WebinarCoordinatorId() {}
 
    public WebinarCoordinatorId(
        Long webinarId,
        Long coordinatorId) {
        this.webinarId = webinarId;
        this.coordinatorId = coordinatorId;
    }
 
    //Getters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        WebinarCoordinatorId that = (WebinarCoordinatorId) o;
        return Objects.equals(webinarId, that.webinarId) &&
               Objects.equals(coordinatorId, that.coordinatorId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(webinarId, coordinatorId);
    }
}