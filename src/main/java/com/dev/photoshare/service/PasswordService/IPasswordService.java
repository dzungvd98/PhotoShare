package com.dev.photoshare.service.PasswordService;

public interface IPasswordService {
    String hash(String plainPassword);
    boolean verify(String plainPassword, String hashedPassword);
    void hashDummy();
    boolean isStrongPassword(String password);
}
