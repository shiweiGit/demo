package ekuter.mvc.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.filter.PathMatchingFilter;

import ekuter.mvc.constants.Constants;
import mybatisPro.mybatisEntity.UserEntity;


/**
 * @author EKuter-si.yu
 * @date 2016/8/15
 * @version 1.0
 * */
public class SysUserFilter extends PathMatchingFilter{

//  @Autowired
//  private UserService userService;

  /**
   * <p>根据当前登录用户身份获取User信息放入request；
   * 然后就可以通过request获取User。</p>
   * @param request
   * @param response
   * @param mappedValue
   * @return boolean 
   * */
  @Override
  protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

//    String username = (String)SecurityUtils.getSubject().getPrincipal();
//    UserEntity userInfo = userService.findUserInfoByUsername(username);
  	UserEntity userInfo = GetUserInfoEntityFilter.getUserInfo();
    
    if(null != userInfo){
    	request.setAttribute(Constants.CURRENT_USER, userInfo);
    }
    return true;
  }
}