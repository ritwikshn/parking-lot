package com.parkinglot.privilege;

import com.parkinglot.account.Account;

public final class ForceOpenExitGateSystemPrivilege extends SystemPrivilege {
    @Override
    public Boolean visitAdmin() {
        return true;
    }

    @Override
    public Boolean visitParkingAgent(){
        return false;
    }

    @Override
    public Boolean visitGuest() {
        return false;
    }

    public interface IForceOpenExitGatePrivilegeFulfiller extends PrivilegeFulfiller{
        boolean forceOpenExitGate(Account requester);
    }
}
