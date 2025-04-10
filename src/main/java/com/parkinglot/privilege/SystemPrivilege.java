package com.parkinglot.privilege;

import com.parkinglot.role.SystemRole;

public abstract class SystemPrivilege
        extends Privilege
        implements SystemRole.RoleVisitor<Boolean> {

}
