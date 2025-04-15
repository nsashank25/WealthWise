package com.pes.financemanager.pesfinancemanager.report;

import com.pes.financemanager.pesfinancemanager.model.Expense;
import com.pes.financemanager.pesfinancemanager.model.User;

import java.io.IOException;
import java.util.List;

public interface Report {
    byte[] generate(User user, List<Expense> expenses) throws IOException;
    String getTitle();
}