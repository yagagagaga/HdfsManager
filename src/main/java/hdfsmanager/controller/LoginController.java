package hdfsmanager.controller;

import hdfsmanager.api.Controller;
import hdfsmanager.model.LoginModel;
import hdfsmanager.view.LoginView;

public class LoginController extends Controller<LoginModel, LoginView> {

	public LoginController() {
		super(LoginModel.class, LoginView.class);
	}

	public void login(String loginUrl, String loginUser) {
		try {
			model.login(loginUrl, loginUser);
			view.dispose(); // 成功：让登录框消失
		} catch (Exception e) {
			view.playFailedLoginAnimation(); // 失败：播放登录失败的动画
			e.printStackTrace();
		}
	}
}
