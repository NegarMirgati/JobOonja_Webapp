package Exceptions;

public class UserNotFoundException extends Throwable {
    public UserNotFoundException(String str){
        super(str);
    }
}
