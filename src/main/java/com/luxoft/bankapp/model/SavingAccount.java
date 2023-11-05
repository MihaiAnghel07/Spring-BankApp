package com.luxoft.bankapp.model;

import com.luxoft.bankapp.exceptions.NotEnoughFundsException;
import org.springframework.context.annotation.Bean;

public class SavingAccount extends AbstractAccount {

    public SavingAccount() {
    }

    public SavingAccount(double initialBalance) {

        if (initialBalance >= 0) {
            setBalance(initialBalance);
        }
    }

    @Bean(name = "savingAccount1")
    public SavingAccount getDemoSavingAccount1()
    {
        return new SavingAccount(1000);
    }

    @Override
    public void withdraw(double amount) {

        if (getBalance() < amount) {
            throw new NotEnoughFundsException(amount);
        }

        setBalance(getBalance() - amount);
    }
}
