package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.dto.UserAddDto;
import java.util.function.Supplier;

public interface SignUpService {

    void signUp(UserAddDto userAddDto, Supplier<String> waitForUserInput);

    boolean userExists(String username, String email);
}
