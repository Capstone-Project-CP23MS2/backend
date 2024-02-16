package sit.cp23ms2.sportconnect.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import sit.cp23ms2.sportconnect.enums.Gender;
import sit.cp23ms2.sportconnect.enums.PostgreSQLEnumType;
import sit.cp23ms2.sportconnect.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
@TypeDef(name = "enum_type", typeClass = PostgreSQLEnumType.class)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "users_sequence", allocationSize = 1)
    @Column(name = "userId", nullable = false)
    private Integer userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Type(type = "enum_type")
    private Role role;

    @Column(name = "profilePicture")
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    @Type(type = "enum_type")
    private Gender gender;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "dateOfBirth")
    private Date dateOfBirth;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "lineId")
    private String lineId;

    @Column(name = "lastLogin")
    private Instant lastLogin;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "registrationDate", nullable = false)
    private LocalDate registrationDate;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "activityParticipants"
            ,joinColumns = {@JoinColumn(name = "userId")}
            ,inverseJoinColumns = {@JoinColumn(name = "activityId")})
    private Set<Activity> userActivities = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "userInterests"
            ,joinColumns = {@JoinColumn(name = "userId")}
            ,inverseJoinColumns = {@JoinColumn(name = "categoryId")})
    private Set<Category> userInterests = new HashSet<>();

//    @JsonIgnore
//    @OneToMany(mappedBy = "hostUser")
//    private Set<Activity> activities = new LinkedHashSet<>();

}