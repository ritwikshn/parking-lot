package com.parkinglot.privilege;

import com.parkinglot.account.Account;
import com.parkinglot.role.Role;
import com.parkinglot.role.RoleManager;

import java.util.Optional;

public interface PrivilegeFulfiller {
    default boolean requesterHasPrivilege(Privilege privilege, Account requester){
        Optional<Role> userRole = RoleManager.getInstance().getUserRole(requester);
        if(userRole.isEmpty()) return false;
        return userRole.get().hasPrivilege(privilege);
    }
}
