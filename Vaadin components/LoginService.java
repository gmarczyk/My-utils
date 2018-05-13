package com.scheduler.application;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scheduler.infrastructure.users.UserRepositoryUNAWARE;
import com.configuration.servlet.MainUI;
import com.scheduler.shared.core.structure.ApplicationService;
import com.scheduler.shared.users.domain.users.User;
import com.scheduler.shared.users.domain.users.UserRegisteredEvent;
import com.scheduler.shared.users.domain.users.UserId;
import com.scheduler.shared.users.domain.multitenancy.TenantId;
import com.scheduler.shared.users.domain.users.UserRepository;
import com.scheduler.shared.users.domain.users.UserRole;

/**
 * <b>(!) This service is based on MULTITENANCY, users are grouped by tenants.</b>
 */
@Component
public class LoginService {

    @Autowired
    private UserRepository tenantAwareUserRepository;
    @Autowired
    private UserRepositoryUNAWARE UNAWAREUserRepository;

    private static final int BCRYPT_WORK_FACTOR = 12;
    public static final String TENANT_ID = "TenantId";
    public static final String USER_ID = "UserId";

    private MainUI sessionHoldingUI;
    public void setSessionHoldingUI(final MainUI sessionHoldingUI) {
        this.sessionHoldingUI = sessionHoldingUI;
    }

    public TenantId getCurrentlyLoggedTenantId() {
        Object attribute = sessionHoldingUI.getSession().getAttribute(TENANT_ID);
        return (TenantId) attribute;
    }

    public UserId getCurrentlyLoggedUserId() {
        Object attribute = sessionHoldingUI.getSession().getAttribute(USER_ID);
        return (UserId) attribute;
    }

    public boolean tryLoggingIn(String username, String password) {
        User byUsername = UNAWAREUserRepository.findByName(username);
        if(byUsername != null) {
            if (bcrypt_checkPassword(password, byUsername.getPassword())) {
                sessionHoldingUI.getSession().setAttribute(TENANT_ID, byUsername.ownerTenantId());
                sessionHoldingUI.getSession().setAttribute(USER_ID, byUsername.getUserId());
                return true;
            }
        }

        return false;
    }

    public void logout() {
        sessionHoldingUI.getSession().close();
        sessionHoldingUI.getPage().setLocation("/app");
    }

    public boolean tryRegisteringNewTenantAdminUser(String username, String password, TenantId tenantId, UserRole role) {
        User byUsername = UNAWAREUserRepository.findByName(username);
        if(byUsername != null) {
            return false;
        }

        User us = new User();
        us.setUserId(new UserId(UUID.randomUUID().toString()));
        us.setTenantId(tenantId);
        us.setUsername(username);
        us.setPassword(bcrypt_hashPassword(password));
        us.setRole(role);

        UNAWAREUserRepository.createByInternalPanel(us);

        return true;
    }

    public void registerNewUser(UserRegisteredEvent command) {
        final String password = bcrypt_hashPassword(command.password);
        User user = new User(new UserId(UUID.randomUUID().toString()), command.role, command.uname, password);
        tenantAwareUserRepository.create(user);
    }

    public boolean isAnyOfRoles(UserRole... role) {
        User byId = this.tenantAwareUserRepository.findById(getCurrentlyLoggedUserId());
        if(byId == null) {
            throw new RuntimeException("No user currently logged in!");
        }

        for (final UserRole userRole : role) {
            if(byId.getRole().equals(userRole)) {
                return true;
            }
        }

        return false;
    }

    public static String bcrypt_hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(BCRYPT_WORK_FACTOR);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);

        return(hashed_password);
    }


    public static boolean bcrypt_checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }

}
