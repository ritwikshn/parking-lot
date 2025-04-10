package com.parkinglot.role;

import com.parkinglot.privilege.Privilege;
import com.parkinglot.privilege.TestPrivilege;

import java.util.Objects;

public enum TestRole implements Role {
    ADMIN(TestRole.ADMIN_ROLE_PASSKEY){

        @Override
        public Boolean hasPrivilege(TestPrivilege privilege){
            return privilege.visitAdmin();
        }
    },
    GUEST(TestRole.GUEST_ROLE_PASSKEY){
        @Override
        public Boolean hasPrivilege(TestPrivilege privilege) {
            return privilege.visitGuest();
        }
    };
    public final static String ADMIN_ROLE_PASSKEY = "ADMINTEST123";
    public final static String GUEST_ROLE_PASSKEY = "GUESTTEST123";
    private String passKey;
    TestRole(String passKey){
        this.passKey = passKey;
    }
    @Override
    public Boolean hasPrivilege(Privilege privilege){
        if(privilege instanceof TestPrivilege)
            return hasPrivilege((TestPrivilege) privilege);
        return false;
    }
    public abstract Boolean hasPrivilege(TestPrivilege privilege);
    public boolean isPassKeyMatch(String providedKey){
        if(providedKey == null) return false;
        return Objects.equals(this.passKey, providedKey);
    }

    public interface TestRoleVisitor<T>  {
        T visitAdmin();
        T visitGuest();
    }
}
