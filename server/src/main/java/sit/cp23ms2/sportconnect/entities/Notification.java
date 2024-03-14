package sit.cp23ms2.sportconnect.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import sit.cp23ms2.sportconnect.enums.NotificationType;
import sit.cp23ms2.sportconnect.enums.PostgreSQLEnumType;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "notification")
@TypeDef(name = "enum_type", typeClass = PostgreSQLEnumType.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "notifications_sequence", allocationSize = 1)
    @Column(name = "notificationId")
    private Integer notificationId;

    @ManyToOne
    @JoinColumn(name = "targetId")
    private User targetId;

    @Column(name = "unread")
    private Boolean unRead;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @Type(type = "enum_type")
    private NotificationType type;

    @Column(name = "message")
    private String message;

    @Column(name = "createdAt")
    @CreationTimestamp
    private Instant createdAt;
}
