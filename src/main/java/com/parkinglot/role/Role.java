package com.parkinglot.role;

import com.parkinglot.privilege.Privilege;

public interface Role {
    Boolean hasPrivilege(Privilege privilege);
    boolean isPassKeyMatch(String providedKey);
}
