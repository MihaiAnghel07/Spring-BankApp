package com.luxoft.bankapp.model;

import com.luxoft.bankapp.exceptions.OverDraftLimitExceededException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class CheckingAccount extends AbstractAccount {

    private double overdraft = 0;

    @Bean(name = "checkingAccount2")
    public CheckingAccount getDemoCheckingAccount2()
    {
        return new CheckingAccount(1500);
    }

    @Bean(name = "checkingAccount1")
    public CheckingAccount getDemoCheckingAccount1()
    {
        return new CheckingAccount(1000);
    }

    public CheckingAccount() {
    }

    public CheckingAccount(double overdraft) {

        setOverdraft(overdraft);
    }

    public void setOverdraft(double amount) {

        if (overdraft < 0) {
            return;
        }

        overdraft = amount;
    }

    public double getOverdraft() {

        return overdraft;
    }

    @Override
    public void withdraw(double amount) throws OverDraftLimitExceededException {

        if (getBalance() + overdraft < amount) {

            throw new OverDraftLimitExceededException(
                    this.getClass().getSimpleName(), getBalance() + overdraft);
        }

        setBalance(getBalance() - amount);
    }
}
