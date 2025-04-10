package com.parkinglot.role;

import com.parkinglot.privilege.Privilege;
import com.parkinglot.privilege.SystemPrivilege;

import java.lang.Boolean;
import java.util.Objects;

public enum SystemRole implements Role {
    ADMIN(SystemRole.ADMIN_ROLE_PASSKEY){

        @Override
        public Boolean hasPrivilege(SystemPrivilege privilege){
            return privilege.visitAdmin();
        }
    },
    PARKING_AGENT(SystemRole.PARKING_AGENT_ROLE_PASSKEY){
        @Override
        public Boolean hasPrivilege(SystemPrivilege privilege){
            return privilege.visitParkingAgent();
        }
    },
    GUEST(SystemRole.GUEST_ROLE_PASSKEY){
        @Override
        public Boolean hasPrivilege(SystemPrivilege privilege){
            return privilege.visitGuest();
        }
    };
    private final static String ADMIN_ROLE_PASSKEY = "admin123";
    private final static String PARKING_AGENT_ROLE_PASSKEY = "parkagent123";
    private final static String GUEST_ROLE_PASSKEY = "guest123";

    private String passKey;
    SystemRole(String passKey){
        this.passKey = passKey;
    }

    @Override
    public Boolean hasPrivilege(Privilege privilege) {
        if(privilege instanceof SystemPrivilege)
            return hasPrivilege((SystemPrivilege) privilege);
        return false;
    }
    public abstract Boolean hasPrivilege(SystemPrivilege privilege);
    public boolean isPassKeyMatch(String providedKey){
        if(providedKey == null) return false;
        return Objects.equals(this.passKey, providedKey);
    }

    public interface RoleVisitor<T>{
        T visitAdmin();
        T visitParkingAgent();
        T visitGuest();
    }
}
