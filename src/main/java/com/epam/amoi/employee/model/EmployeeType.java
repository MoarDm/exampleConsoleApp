package com.epam.amoi.employee.model;


public enum EmployeeType {
    NONE(""),
    MANAGER("M"),
    DEVELOPER("D"),
    ;

    private final String typeCode;

    EmployeeType(final String typeCode) {
        this.typeCode = typeCode;
    }

    public static EmployeeType ofCode(final String code) {
        for (final EmployeeType employeeType : EmployeeType.values()) {
            if (employeeType.getTypeCode().equals(code)) return employeeType;
        }
        return NONE;
    }

    public static EmployeeType ofOrdinalCode(final int ordinalCode) {
        for (final EmployeeType employeeType : EmployeeType.values()) {
            if (employeeType.ordinal() == ordinalCode) return employeeType;
        }
        return NONE;
    }

    public String getTypeCode() {
        return typeCode;
    }
}
