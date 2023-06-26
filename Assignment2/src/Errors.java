public abstract class Errors {

    public static class ErroneousCommandException extends Exception {
        // constructor
        public ErroneousCommandException(String message) {
            super(message);
        }
    }

    public static class DeviceAlreadyExist extends ErroneousCommandException {
        // constructor
        public DeviceAlreadyExist(String message) {
            super(message);

        }
    }

    public static class IsNotAPositiveNumberException extends ErroneousCommandException {
        // constructor
        public IsNotAPositiveNumberException(String message) {
            super(message);
        }
    }

    public static class OutOfRange extends ErroneousCommandException {
        // constructor
        public OutOfRange(String message) {
            super(message);
        }
    }

    public static class AlreadySwitchedOffOrOn extends ErroneousCommandException {
        // constructor
        public AlreadySwitchedOffOrOn(String message) {
            super(message);
        }
    }

    // exception thrown when two device names are the same
    public static class SameNameException extends ErroneousCommandException {
        // constructor
        public SameNameException(String message) {
            super(message);
        }
    }
    public static class ThereIsNoSuchDeviceException extends ErroneousCommandException {
        // constructor
        public ThereIsNoSuchDeviceException(String message) {
            super(message);
        }
    }

    public static class IsNotAPlugException extends ErroneousCommandException {
        // constructor
        public IsNotAPlugException(String message) {
            super(message);
        }
    }

    public static class AlreadyPlugInOrOutException extends ErroneousCommandException {
        // constructor
        public AlreadyPlugInOrOutException(String message) {
            super(message);
        }
    }


    // the exception that is thrown if the first set of commands entered is not the Altime command
    public static class SetInitialTimeException extends ErroneousCommandException {
        // constructor
        public SetInitialTimeException(String message) {
            super(message);
        }
    }
}
