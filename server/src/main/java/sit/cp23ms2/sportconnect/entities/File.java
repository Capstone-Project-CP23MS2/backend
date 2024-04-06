package sit.cp23ms2.sportconnect.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "files_sequence", allocationSize = 1)
    @Column(name = "fileId")
    private Integer fileId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User userFile;

    @ManyToOne
    @JoinColumn(name = "activityId")
    private Activity activityFile;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "fileUrl")
    private String fileUrl;

    @Column(name = "fileSize")
    private Integer fileSize;

    @Column(name = "createdAt")
    @CreationTimestamp
    private Instant createdAt;


}
