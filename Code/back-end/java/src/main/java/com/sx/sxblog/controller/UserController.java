package com.sx.sxblog.controller;

import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sx.sxblog.common.*;
import com.sx.sxblog.entity.User;
import com.sx.sxblog.service.impl.BlogServiceImpl;
import com.sx.sxblog.service.impl.UserServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lgx
 * @since 2020-07-08
 */


@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserServiceImpl userService;

    @Resource
    private BlogServiceImpl blogService;

    private UserAndUUID userAndUUID = new UserAndUUID();


    @RequestMapping(value = "/signIn",method = RequestMethod.POST)
    @ResponseBody
    public ReturnEntity signIn(@RequestBody Map<String,Object> user_info){

        boolean status = true;
        String msg = "";
        JSONObject data = new JSONObject();

        String username = (String) user_info.get("username");
        String password = (String) user_info.get("password");
        User user;
        user = userService.getUserByUsername(username);

        //添加userid到前端
        System.out.println("the user id is :");
        System.out.println(user.getUserId());
        data.put("userId", user.getUserId());

        boolean isNum;
        boolean isId = false;
        boolean isusername = false;
        boolean ac = false;


        //正则判断是否数字
        Pattern pattern = Pattern.compile("[0-9]*");
        isNum = pattern.matcher(username).matches();

        //数据库操作
        try{
            if(isNum){
                int userId = Integer.valueOf(username);

                user = userService.getUserById(userId);

                if(user!=null && user.getPassword().equals(password)){
                    isId = true;
                    ac = true;
                    data.put("username",user.getUserName());
                }
                else{
                    isId = false;
                }
            }
            else {
                if (isId){
                    isusername = false;
                }
                else {
                    user = userService.getUserByUsername(username);
                    if (user!=null && user.getPassword().equals(password)){
                        isusername = true;
                        ac = true;
                        data.put("username",user.getUserName());
                    }
                }
            }
            if(!ac){
                status = false;
                msg = "Wrong username or passwd";
            }
            else {
                //token相关
                status = true;
                String token= TokenUtil.creatToken(username);
                data.put("token",token);
            }


        }catch (Exception ex){
            //日志输出错误
            msg = "Service Worng";
            System.out.println("dberro");
        }


        ReturnEntity returnEntity = new ReturnEntity(status,msg,data);

        return returnEntity;
    }

    @PostMapping("/signUp")
    @ResponseBody
    public ReturnEntity signUp(@RequestBody Map<String,Object> user_info){
        String msg = "Sign Up Success";
        int code = 200;
        boolean status = true;

        User user = new User();
        String username = (String) user_info.get("username");
        String passwd = (String) user_info.get("password");
        String birthday = (String) user_info.get("birthday");
        String company = (String) user_info.get("company");
        String description = (String) user_info.get("description");
        String email = (String) user_info.get("email") ;
        String sex = (String) user_info.get("sex");

        user.setUserName(username);
        user.setPassword(passwd);
        user.setBirthday(birthday);
        user.setCompany(company);
        user.setDescription(description);
        user.setEmail(email);
        user.setLevel(0);
        user.setSex(sex);

        //不插入数据库先保存
//        try {
//            userService.insertUser(user);
//        }catch (Exception ex){
//            //日志记录错误
//            msg = "service worong";
//            status = false;
//            code = 500;
//        }
        //内存存user
        String uuid = UUIDUtil.getUUID();
        userAndUUID.addUser(uuid,user);

        //发送邮件
        try{
            SendMailUtil.sendEmail(user.getEmail(),"SXBLOG verfy","verify code is\n"+uuid);
        }catch (Exception exception) {
            status = false;
            msg = "email send failed";
        }

//        int id = userService.getUserByUsername(username).getUserId();
        JSONObject data = new JSONObject();
//        data.put("uuid",uuid);
        ReturnEntity returnEntity = new ReturnEntity(status,msg,data);
        return returnEntity;
    }

    @PostMapping("/email_verify")
    @ResponseBody
    public  ReturnEntity emailVerify(@RequestBody Map<String,Object> code_body){
        String msg ="";
        JSONObject data = new JSONObject();
        boolean status = true;

        String code = (String) code_body.get("code");
        User user = null;
        try {
            if (userAndUUID.isContent(code)){
                user = userAndUUID.getUser(code);
                userService.insertUser(user);
            }
        }catch (Exception exception){
            msg = "user have not signUp succeed";
        }

        if (user!=null){
            data.put("user_id",user.getUserId());
            data.put("user_name",user.getUserName());
        }else{
            msg = "user have not signUp succeed";
        }

        ReturnEntity returnEntity = new ReturnEntity(status,msg,data);
        return returnEntity;

    }
    

    @PostMapping("/change_data")
    @ResponseBody
    public ReturnEntity updateUserInfo(@RequestBody Map<String,Object> user_info){
        String msg = "Sign Up Success";
        int code = 200;
        boolean status = true;
        JSONObject data = new JSONObject();

        int id = 10;//不存在的用户如果后面没有赋值将会使用判空语句表明输入不正确

        User user ;
        String userid = (String) user_info.get("userid");
        String username = (String) user_info.get("username");
        String passwd = (String) user_info.get("password");
        String birthday = (String) user_info.get("birthday");
        String company = (String) user_info.get("company");
        String description = (String) user_info.get("description");
        String email = (String) user_info.get("email") ;
        String sex = (String) user_info.get("sex");


        try {
            //判断是否userid,不是的话返回worng input
            if(UserUtil.isId(userid)) {

                id = UserUtil.switchToint(userid);
                data.put("userid",userid);
                user = userService.getUserById(id);

                //改变user
                if(username!=null && username!="" ){user.setUserName(username);}
                if(passwd!=null && passwd!=""){user.setPassword(passwd);}

                if(birthday!=null && birthday!="") {
                    user.setBirthday(birthday);
                }
                if(company!=null && company!="") {
                    user.setCompany(company);
                }
                if(description!=null && description!="") {
                    user.setDescription(description);
                }
                if(email!=null && email!="") {
                    user.setEmail(email);
                }
                if(sex!=null && sex!="") {
                    user.setSex(sex);
                }
                userService.updateUser(user);

            }else {
                status = false;
                msg = "worng input";
            }

        }catch (Exception ex){
            //日志记录错误
            msg = "service worong";
            status = false;
            code = 500;
            ex.printStackTrace();
        }

        User userOutPut = userService.getUserById(id);
        if(userOutPut!=null) {
//            data.put("userid", userOutPut.getUserId());
            data.put("username", userOutPut.getUserName());
            data.put("birthday", userOutPut.getBirthday());
            data.put("company", userOutPut.getCompany());
            data.put("description", userOutPut.getDescription());
            data.put("email", userOutPut.getEmail());
            data.put("sex", userOutPut.getSex());
            data.put("password", userOutPut.getPassword());
        }
        else {
            if(status){
                msg = "no such user";
            }
            status = false;
        }
        ReturnEntity returnEntity = new ReturnEntity(status,msg,data);
        return returnEntity;

    }

    @PostMapping("/get_data")
    @ResponseBody
    public ReturnEntity getUserData(@RequestBody Map<String,Object> user_info){
        JSONObject data;
        boolean status = true;
        String msg = "";

        String username = (String) user_info.get("username");
        boolean isNum = false;
        User user;

        //正则判断是否数字
        Pattern pattern = Pattern.compile("[0-9]*");
        isNum = pattern.matcher(username).matches();
        try {
            if(isNum){
                int id = Integer.valueOf(username);
                user = userService.getUserById(id);
                data = UserUtil.putUserInJson(user);
            }
            else {
                user = userService.getUserByUsername(username);
                data = UserUtil.putUserInJson(user);
            }
        }catch (Exception exception){
            data = new JSONObject();
            status = false;
            msg = "user not exitds";
        }

        ReturnEntity returnEntity=new  ReturnEntity(status,msg,data);
        return returnEntity;
    }

    @PostMapping("/username_verify")
    @ResponseBody
    public ReturnEntity userNameVerify(@RequestBody Map<String,Object> userVer){
        String username = (String) userVer.get("username");
        boolean notExits = true;
        User user;

        //return info
        String msg = "";
        boolean status  = false;

        boolean isNum;
        //正则判断是否数字
        Pattern pattern = Pattern.compile("[0-9]*");
        isNum = pattern.matcher(username).matches();

        try {
            if (isNum) {
                int Id = Integer.valueOf(username);
                user = userService.getUserById(Id);
                if (user == null) {
                    notExits = true;
                } else {
                    notExits = false;
                }
            } else {
                user = userService.getUserByUsername(username);
                if (user == null) {
                    notExits = true;
                } else {
                    notExits = false;
                }
            }
        }catch (Exception exception){
            msg = "Servic worng";
            status = false;
        }
        if(notExits){
            msg = "Check username success";
            status = true;
        }
        else {
            msg = "username already exits";
            status = false;
        }

        JSONObject data  = new JSONObject();
        data.put("check_not_exits",notExits);

        ReturnEntity returnEntity = new ReturnEntity(status,msg,data);
        return returnEntity;
    }

    @PostMapping("/find_passwd")
    @ResponseBody
    public ReturnEntity findUserPasswd(@RequestBody Map<String,Object> find_info) throws Exception {
        String msg = "";
        JSONObject data = new JSONObject();
        boolean status = true;

        String email = (String) find_info.get("email");
        String userName = (String) find_info.get("username");
        User user;

        try {
            if (UserUtil.isId(userName)) {
                int id = UserUtil.switchToint(userName);
                user = userService.getUserById(id);
            }else {
                user = userService.getUserByUsername(userName);
            }

            if (!user.getEmail().equals(email)){
                msg = "worng eamil or worng email";
                status = false;
            }else {
                String newPasswd = UUIDUtil.getUUID();
                user.setPassword(newPasswd);
                userService.updateUser(user);
                SendMailUtil.sendEmail(user.getEmail(),"SXBLOG new password","your new password is \n"+newPasswd);
            }
        }catch (Exception ex){
            msg = "reset password and sent password to you was failed,please try again";
            status = false;
        }

        if(status){
            msg = "send successfully, please check your email";
        }
        ReturnEntity returnEntity = new ReturnEntity(status,msg,data);
        return  returnEntity;
    }


}


