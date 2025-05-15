package com.surofu.madeinrussia.core.model.session;

import com.surofu.madeinrussia.core.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SessionWithUser extends Session {

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
}
