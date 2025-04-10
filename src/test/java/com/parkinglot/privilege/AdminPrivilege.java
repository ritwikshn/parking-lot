package com.parkinglot.privilege;

import com.parkinglot.account.Account;

public class AdminPrivilege extends TestPrivilege{


    @Override
    public Boolean visitAdmin() {
        return true;
    }

    @Override
    public Boolean visitGuest() {
        return false;
    }

    public interface IAdminPrivilegeFulfiller extends PrivilegeFulfiller{
        boolean isAdmin(Account requester);
    }
}
