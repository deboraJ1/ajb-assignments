package model;
/**
 * This class extends @see Exception and is thrown when an invalid TreeNode value is tried to be set.
 * Invalid TreeNode values are values, which are null or blank.
 */
public class InvalidValueException extends Exception {


        public InvalidValueException() {
            super("An invalid value for a TreeNode was found. ");
        }

        public InvalidValueException(String message) {
            super(message);
        }

}
