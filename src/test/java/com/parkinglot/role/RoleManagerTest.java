package com.parkinglot.role;

import com.parkinglot.account.Account;
import com.parkinglot.privilege.AdminPrivilege;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RoleManagerTest {
    @Test
    public void getRoleManagerInstanceTest(){
        RoleManager rm = RoleManager.getInstance();
        assertNotNull(rm);
    }

    @Test
    public void sufficientPrivilegeTest(){
        Account testAdmin = new Account("username123", "password123");
        RoleManager rm = RoleManager.getInstance();
        rm.assignRoleToUser(testAdmin, TestRole.ADMIN, TestRole.ADMIN_ROLE_PASSKEY);

        AdminPrivilegeFulfiller fulfiller = new AdminPrivilegeFulfiller();
        boolean isAdmin = fulfiller.isAdmin(testAdmin);

        assertTrue(isAdmin);
    }

    @Test
    public void insufficientPrivilegeTest(){
        Account testGuest = new Account("iamguest1245", "password123");
        RoleManager rm = RoleManager.getInstance();

        rm.assignRoleToUser(testGuest, TestRole.GUEST, TestRole.GUEST_ROLE_PASSKEY);
        AdminPrivilegeFulfiller fulfiller = new AdminPrivilegeFulfiller();
        boolean isAdmin = fulfiller.isAdmin(testGuest);

        assertFalse(isAdmin);
    }

    @Test
    public void assignRoleToUserTest(){
        Account testGuest = new Account("iamgues123", "passworkd123");
        RoleManager rm = RoleManager.getInstance();
        rm.assignRoleToUser(testGuest, TestRole.GUEST, TestRole.GUEST_ROLE_PASSKEY);

        assertEquals(TestRole.GUEST, rm.getUserRole(testGuest).get());
    }

    @Test
    public void assignRoleToUserWrongPasskey(){
        Account testAdmin = new Account("iwanttobeadmin", "password123");
        RoleManager rm = RoleManager.getInstance();

        boolean success = rm.assignRoleToUser(testAdmin, TestRole.ADMIN, "wrongpasskey");
        assertFalse(success);

        //wrong authentication => it gets
        assertEquals(Optional.empty(), rm.getUserRole(testAdmin));
    }

    @Test
    public void malignantUserTest(){
        Account legitUser = new Account("legituser", "iamlegituser");
        Account malignantUser = new Account("legituser", "idkbutiamlegit");

        RoleManager rm = RoleManager.getInstance();

        //legit user gets assigned a role
        rm.assignRoleToUser(legitUser, TestRole.ADMIN, TestRole.ADMIN_ROLE_PASSKEY);

        //malignant user tries to mock the legit user and access same privileges
        AdminPrivilegeFulfiller fulfiller = new AdminPrivilegeFulfiller();
        boolean malignantUserIsAdmin = fulfiller.isAdmin(malignantUser);

        assertFalse(malignantUserIsAdmin);
    }

    @Test
    public void getUnregisteredUserRoleTest(){
        RoleManager rm = RoleManager.getInstance();
        Account someUser = new Account("justanotheruser", "itspassword");
        Optional<Role> role = rm.getUserRole(someUser);
        assertTrue(role.isEmpty());
    }

    class AdminPrivilegeFulfiller implements AdminPrivilege.IAdminPrivilegeFulfiller{
        @Override
        public boolean isAdmin(Account requester) {
            if(requesterHasPrivilege(new AdminPrivilege(), requester))
                return true;
            return false;
        }
    }
}
