package com.surofu.madeinrussia.core.model.session;

import com.surofu.madeinrussia.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "sessions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_sessions_user_id_device_id",
                        columnNames = {"user_id", "device_id"}
                ),
                @UniqueConstraint(
                        name = "uk_sessions_device_id_user_id",
                        columnNames = {"device_id", "user_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_sessions_user_id_device_id",
                        columnList = "user_id,device_id"
                ),
                @Index(
                        name = "idx_sessions_device_id_user_id",
                        columnList = "device_id,user_id"
                )
        }
)
public class Session implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sessions_user_id")
    )
    private User user;

    @Embedded
    private SessionDeviceId deviceId;

    @Embedded
    private SessionDeviceType deviceType;

    @Embedded
    private SessionBrowser browser;

    @Embedded
    private SessionOs os;

    @Embedded
    private SessionIpAddress ipAddress;

    @Embedded
    private SessionCreationDate creationDate;

    @Embedded
    private SessionLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        return id != null && id.equals(((Session) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
