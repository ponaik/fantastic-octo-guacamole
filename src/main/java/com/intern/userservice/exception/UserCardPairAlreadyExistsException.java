package com.intern.userservice.exception;

public class UserCardPairAlreadyExistsException extends RuntimeException {
    public UserCardPairAlreadyExistsException(Long id, String number) {
        super("Card with number " + number + " already exists for user with id " + id);
    }
}
