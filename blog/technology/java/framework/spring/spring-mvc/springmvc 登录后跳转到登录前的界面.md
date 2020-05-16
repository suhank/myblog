# springmvc 登录后跳转到登录前的界面

主要是利用request的Header的Referer属性实现登录后跳转到登录前的界面

```java
	//登录前的页面
    public final static String  BEFORE_LOGIN_PAGE = "BEFORE_LOGIN_PAGE";
	/**
	 * 获取登录的jsp页面
	 * @return 登录的jsp页面
	 */
	@RequestMapping(method= {RequestMethod.GET},value="loginUI")
	public String loginUI(HttpServletRequest request) {
		//在session中保存进入登录之前的页面
		HttpSession session = request.getSession();
	
		//保存登录前的页面到session中
        saveBeforeLoginPage(request);
		return "login";
	}


    private void saveBeforeLoginPage(HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        if (StringUtils.isBlank(referer)) {
            request.getSession().removeAttribute(BEFORE_LOGIN_PAGE);
            return;
        }

        try {
            String path = new UrlResource(referer).getURL().getPath();
            //排除登录相关的路径
            if (!StringUtils.contains("/", path)) {
                request.getSession().setAttribute(BEFORE_LOGIN_PAGE, path);
            } else {
                request.getSession().removeAttribute(BEFORE_LOGIN_PAGE);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
	
	/**
	 * 登录功能
	 * @return 
	 */
	@RequestMapping(method= {RequestMethod.POST},value= "login")
	public String login(ClassUser classUser,HttpServletRequest request) {
		//登录功能
		ClassUser loginUser = userService.login(classUser);
		/*判断是否登录成功，如果成功加入到Session中，不成功这返回到登录页面*/
		if(loginUser==null) {
			//登录失败
			return "redirect:loginUI";
		}
		//登录成功
		HttpSession session = request.getSession();
		Object userLogin = session.getAttribute("USERLOGIN");
		session.setAttribute("USERLOGIN", userLogin);
		//判断用户来源
		//获取用户登录前以页面
		Object privatePage = session.getAttribute(BEFORE_LOGIN_PAGE);
		if(privatePage==null) {
			//说明直接登录
			return "redirect:mapUI";
		}else {
			return "redirect:"+privatePage.toString();
		}
	}

```





https://blog.csdn.net/qq_35448976/article/details/78825709