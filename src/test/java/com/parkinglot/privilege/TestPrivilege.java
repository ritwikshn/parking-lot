package com.parkinglot.privilege;

import com.parkinglot.role.TestRole;

public abstract class TestPrivilege
        extends Privilege
        implements TestRole.TestRoleVisitor<Boolean> {
}
