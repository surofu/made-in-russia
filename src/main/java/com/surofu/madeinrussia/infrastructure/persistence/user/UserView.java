package com.surofu.madeinrussia.infrastructure.persistence.user;

import com.surofu.madeinrussia.core.model.user.*;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.VendorDetailsView;

public interface UserView {
    Long getId();

    UserIsEnabled getIsEnabled();

    UserEmail getEmail();

    UserLogin getLogin();

    UserPhoneNumber getPhoneNumber();

    UserRegion getRegion();

    UserRole getRole();

    UserAvatar getAvatar();

    UserRegistrationDate getRegistrationDate();

    UserLastModificationDate getLastModificationDate();

    // External

    VendorDetailsView getVendorDetails();
}
