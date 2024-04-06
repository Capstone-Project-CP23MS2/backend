package sit.cp23ms2.sportconnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "activities_sequence", allocationSize = 1)
    @Column(name = "activityId", nullable = false)
    private Integer activityId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "hostUserId", nullable = false)
    private User hostUser;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category categoryId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "locationId")
    private Location location;

    @Column(name = "dateTime")
    private Instant dateTime;

    @Column(name = "duration")
    private Integer duration;

    @CreationTimestamp
    @Column(name = "createdAt")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Column(name = "noOfMembers")
    private Integer noOfMembers;

    @Column(name = "memberCounts")
    private Integer memberCounts;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "activityParticipants"
            ,joinColumns = {@JoinColumn(name = "activityId")}
            ,inverseJoinColumns = {@JoinColumn(name = "userId")})
    private Set<User> users = new HashSet<>();





}