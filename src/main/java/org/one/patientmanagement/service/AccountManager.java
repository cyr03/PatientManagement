package org.one.patientmanagement.service;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.one.patientmanagement.domain.models.Account;

public interface AccountManager {

    Account register(@Nonnull Account account);

    void delete(long id);
    
    Account update(@Nonnull Account account);

    Optional<Account> getById(long id);

    /**
     *
     * @param user phone number or email of patient
     * @param password hashed password
     * @return the account
     */
    Account authenticate(@Nonnull String user, @Nonnull String password);
}
