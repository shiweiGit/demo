package ekuter.mvc.customRealm;

import java.io.UnsupportedEncodingException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import ekuter.mvc.constants.Constants;
import ekuter.mvc.exception.BusinessException;
import mybatisPro.mybatisEntity.UserEntity;
import mybatisPro.mybatisService.impl.UserSer;


/**
 * <p>自定义Realm(登录认证和权限控制)
 * @author EKuter-si.yu
 * @date 2016/8/11
 * @version 1.0
 * */
public class CustomRealm extends AuthorizingRealm{
	
	//注入Service
	@Autowired
	private UserSer userService;

	//设置realm名称
	public void setName(String name){
		super.setName("CustomRealm");
	}
	
	
	/**
	 * <p>用户授权
	 * @param token
	 * @return
	 * */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		
//		String username = (String)principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		
		try {
//			authorizationInfo.setRoles(userService.findRoles(username));
//			authorizationInfo.setStringPermissions(userService.findPermissions(username));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_AUTHORIZED"));
		}
		
		return authorizationInfo;
	}
	
	
	/**
	 * <p>身份验证
	 * @param token
	 * @return
	 * */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		
		// 从token中取出用户名
		String username = (String)token.getPrincipal();
		//解决中文用户名乱码问题
		try {
			username = new String(username.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		//根据用户输入的username从数据库查询
		UserEntity userInfo = null;
		
		try {
			userInfo = userService.getUserInfoByUsername(username);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_AUTHENTICED"));
		}
		
		//判断库中是否能够查到用户名
		if(null == userInfo){
			throw new UnknownAccountException();
		}else{
			Subject subject = SecurityUtils.getSubject();
		    Session session = subject.getSession();
		    session.setAttribute(Constants.USER_INFO, userInfo);
		}
		
//		if(Boolean.TRUE.equals(userInfo.getLocked())) {
//			throw new LockedAccountException(); //帐号锁定
//		}
		
		String account = userInfo.getUsername();
		String salt = userInfo.getSalt();
		
		//交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，可以自定义
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
				account, //username
				userInfo.getPassword(), //password
				ByteSource.Util.bytes(account+salt), //salt = name + salt  
				getName());

		return authenticationInfo;
	}
	
}
