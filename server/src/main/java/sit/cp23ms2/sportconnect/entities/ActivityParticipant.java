package sit.cp23ms2.sportconnect.entities;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import sit.cp23ms2.sportconnect.entities.idclass.ActivityParticipantId;
import sit.cp23ms2.sportconnect.enums.PostgreSQLEnumType;
import sit.cp23ms2.sportconnect.enums.RSVPStatusParticipant;
import sit.cp23ms2.sportconnect.enums.StatusParticipant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "activityParticipants")
@IdClass(ActivityParticipantId.class)
@TypeDef(name = "enum_type", typeClass = PostgreSQLEnumType.class)
public class ActivityParticipant implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "activityId")
    private Activity activity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Type(type = "enum_type")
    private StatusParticipant status;

    @Enumerated(EnumType.STRING)
    @Column(name = "rsvpstatus")
    @Type(type = "enum_type")
    private RSVPStatusParticipant rsvpStatus;

    @CreationTimestamp
    @Column(name = "joinedAt")
    private Instant joinedAt;


}