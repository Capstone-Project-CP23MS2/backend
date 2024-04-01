package sit.cp23ms2.sportconnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "reviewUser")
public class ReviewUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "\"reviews_user_sequence\"", allocationSize = 1)
    @Column(name = "reviewId")
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "reviewerId")
    private User reviewer;

    @Column(name = "comment")
    private String comment;

    @Column(name = "createdAt")
    @CreationTimestamp
    private Instant createdAt;


}
