package com.crypto.blockfolio.domain;

import java.util.function.Predicate;

public interface Reportable<E> {

    String REPORTS_DIRECTORY = "Reports";

    void generateReport(Predicate<E> predicate);
}
