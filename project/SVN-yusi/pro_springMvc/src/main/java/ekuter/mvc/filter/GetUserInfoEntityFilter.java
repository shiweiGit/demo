package ekuter.mvc.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import ekuter.mvc.constants.Constants;
import mybatisPro.mybatisEntity.UserEntity;
import mybatisPro.mybatisService.UserSerImpl;

/**
 * 获取当前用户信息工具类
 * @author EKuter-si.yu
 * @date 2017/5/11
 * @version 1.0
 * */
public class GetUserInfoEntityFilter {
	
    private static UserSerImpl userService = new UserSerImpl();
	
	public static UserEntity getUserInfo(){
		
		UserEntity loginUser = null;
		Session session = SecurityUtils.getSubject().getSession();
		loginUser = (UserEntity) session.getAttribute(Constants.USER_INFO);
		
		if(null == loginUser){
			
			String username = (String)SecurityUtils.getSubject().getPrincipal();
			loginUser = userService.getUserInfoByUsername(username);
		}
//		System.out.println(loginUser);
		return loginUser;
	}
}