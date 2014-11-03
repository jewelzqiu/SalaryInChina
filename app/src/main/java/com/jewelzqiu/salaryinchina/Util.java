package com.jewelzqiu.salaryinchina;

/**
 * Created by Jewelz on 11月3日.
 */
public class Util {

    public static final double SOCIAL_SECURITY_MAX = 15108;
    public static final double SOCIAL_SECURITY_MIN = 3022;
    public static final double HOUSING_FUND_MAX = 15108;
    public static final double HOUSING_FUND_MIN = 1620;
    public static final double TAX_FREE_MAX = 3500;

    public static final double INDIVIDUAL_ENDOWMENT_RATE = 0.08;
    public static final double INDIVIDUAL_MEDICAL_RATE = 0.02;
    public static final double INDIVIDUAL_UNEMPLOYMENT_RATE = 0.005;

    public static final double COMPANY_ENDOWMENT_RATE = 0.21;
    public static final double COMPANY_MEDICAL_RATE = 0.11;
    public static final double COMPANY_UNEMPLOYMENT_RATE = 0.015;
    public static final double COMPANY_MATERNITY_RATE = 0.01;
    public static final double COMPANY_INJURY_RATE = 0.08;

    public static double getSalaryTax(double salary) {
        double tax;
        if (salary <= 0) {
            tax = 0;
        } else if (salary <= 1500) {
            tax = salary * 0.03;
        } else if (salary <= 4500) {
            tax = salary * 0.1 - 105;
        } else if (salary <= 9000) {
            tax = salary * 0.2 - 555;
        } else if (salary <= 35000) {
            tax = salary * 0.25 - 1005;
        } else if (salary <= 55000) {
            tax = salary * 0.3 - 2755;
        } else if (salary <= 80000) {
            tax = salary * 0.35 - 5505;
        } else {
            tax = salary * 0.45 - 13505;
        }
        return tax;
    }

    public static double getBonusTax(double salary, double bonus) {
        double bonusMonth = bonus / 12;
        if (salary < TAX_FREE_MAX) {
            bonusMonth -= TAX_FREE_MAX - salary;
        }
        double tax;
        if (bonusMonth <= 0) {
            tax = 0;
        } else if (bonusMonth <= 1500) {
            tax = bonus * 0.03;
        } else if (bonusMonth <= 4500) {
            tax = bonus * 0.1 - 105;
        } else if (bonusMonth <= 9000) {
            tax = bonus * 0.2 - 555;
        } else if (bonusMonth <= 35000) {
            tax = bonus * 0.25 - 1005;
        } else if (bonusMonth <= 55000) {
            tax = bonus * 0.3 - 2755;
        } else if (bonusMonth <= 80000) {
            tax = bonus * 0.35 - 5505;
        } else {
            tax = bonus * 0.45 - 13505;
        }
        return tax;
    }

}
