// Exception for invalid employee type
class InvalidEmployeeTypeException extends Exception {
    public InvalidEmployeeTypeException(String message) {
        super(message);
    }
}

// Exception for empty employee list
class NoEmployeesException extends Exception {
    public NoEmployeesException(String message) {
        super(message);
    }
}

// Exception for duplicate SSN
class DuplicateSSNException extends Exception {
    public DuplicateSSNException(String message) {
        super(message);
    }
}
