package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.guns.bo.UserInfoBO;
import com.guns.service.user.UserService;
import com.guns.utils.Md5Utils;
import com.guns.vo.UserInfoVo;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private MtimeUserTMapper mtimeUserTMapper;

    @Override
    public boolean isUsernameExist(String username) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_name", username);
        Integer count = mtimeUserTMapper.selectCount(wrapper);
        if (count==0){
            return false;
        }
        return true;
    }

    @Override
    public int userRegist(UserInfoBO userInfoBO) {
        MtimeUserT mtimeUserT = new MtimeUserT();
        mtimeUserT.setUserName(userInfoBO.getUsername());
        // 对密码进行MD5加密
        String encryptPwd = Md5Utils.getDefaultMd5Encrypt(userInfoBO.getPassword());
        mtimeUserT.setUserPwd(encryptPwd);
        mtimeUserT.setEmail(userInfoBO.getEmail());
        mtimeUserT.setUserPhone(userInfoBO.getMobile());
        mtimeUserT.setAddress(userInfoBO.getAddress());
        Integer insert = mtimeUserTMapper.insert(mtimeUserT);
        return insert;
    }

    @Override
    public UserInfoVo selectUserInfoVo(String username) {
        UserInfoVo userInfoVo = new UserInfoVo();
        EntityWrapper<MtimeUserT> wrapper = new EntityWrapper<>();
        List<MtimeUserT> mtimeUserTList = mtimeUserTMapper.selectList(wrapper.gt("user_name", username));
        if (!CollectionUtils.isEmpty(mtimeUserTList)){
            for (MtimeUserT mtimeUserT : mtimeUserTList) {
                userInfoVo.setUuid(mtimeUserT.getUuid());
                userInfoVo.setUsername(mtimeUserT.getUserName());
                userInfoVo.setAddress(mtimeUserT.getAddress());
                userInfoVo.setEmail(mtimeUserT.getEmail());
                userInfoVo.setPhone(mtimeUserT.getUserPhone());
                userInfoVo.setBiography(mtimeUserT.getBiography());
                userInfoVo.setHeadAddress(mtimeUserT.getHeadUrl());
                userInfoVo.setBirthday(mtimeUserT.getBirthday());
                userInfoVo.setBeginTime(mtimeUserT.getBeginTime().getTime());
                userInfoVo.setNickname(mtimeUserT.getNickName());
                userInfoVo.setLifeState(mtimeUserT.getLifeState());
                userInfoVo.setSex(mtimeUserT.getUserSex());
                userInfoVo.setUpdateTime(mtimeUserT.getUpdateTime().getTime());
            }
        }
        return userInfoVo;
    }

    @Override
    public int updateUserInfo(UserInfoVo userInfoVo) {
        MtimeUserT mtimeUserT = new MtimeUserT();
        mtimeUserT.setUuid(userInfoVo.getUuid());
        mtimeUserT.setUserName(userInfoVo.getUsername());
        mtimeUserT.setAddress(userInfoVo.getAddress());
        mtimeUserT.setEmail(userInfoVo.getEmail());
        mtimeUserT.setUserPhone(userInfoVo.getPhone());
        mtimeUserT.setBiography(userInfoVo.getBiography());
        mtimeUserT.setHeadUrl(userInfoVo.getHeadAddress());
        mtimeUserT.setBirthday(userInfoVo.getBirthday());
        mtimeUserT.setNickName(userInfoVo.getNickname());
        mtimeUserT.setLifeState(userInfoVo.getLifeState());
        mtimeUserT.setUserSex(userInfoVo.getSex());
        Integer integer = mtimeUserTMapper.updateAllColumnById(mtimeUserT);
        return integer;
    }
}
