package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Reportable;
import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.dto.UserAddDto;
import com.crypto.blockfolio.persistence.entity.User;

public interface UserService extends Service<User>, Reportable<User> {

    User getByUsername(String username);

    User getByEmail(String email);

    User add(UserAddDto userAddDto);
}
