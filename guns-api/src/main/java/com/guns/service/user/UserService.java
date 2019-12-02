package com.guns.service.user;

import com.guns.dto.RegisterRequest;
import com.guns.vo.BaseRespVO;

public interface UserService {
    BaseRespVO register(RegisterRequest registerRequest);

    BaseRespVO login(String username, String password);
}
