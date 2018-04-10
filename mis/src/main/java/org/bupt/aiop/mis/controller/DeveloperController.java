package org.bupt.aiop.mis.controller;

import com.github.pagehelper.PageInfo;
import org.apache.commons.fileupload.util.Streams;
import org.bupt.aiop.mis.constant.EnvConsts;
import org.bupt.aiop.mis.pojo.po.Ability;
import org.bupt.aiop.mis.pojo.po.App;
import org.bupt.aiop.mis.pojo.po.AppAbility;
import org.bupt.aiop.mis.pojo.po.User;
import org.bupt.aiop.mis.pojo.vo.AbilityUnderApp;
import org.bupt.aiop.mis.service.AbilityService;
import org.bupt.aiop.mis.service.AppService;
import org.bupt.aiop.mis.service.DeveloperService;
import org.bupt.common.bean.PageResult;
import org.bupt.common.bean.ResponseResult;
import org.bupt.common.constant.OauthConsts;
import org.bupt.common.constant.ResponseConsts;
import org.bupt.common.util.FileUtil;
import org.bupt.common.util.TimeUtil;
import org.bupt.common.util.UUIDUtil;
import org.bupt.common.util.Validator;
import org.bupt.common.util.token.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 应用控制器
 */
@RestController
@RequestMapping("api/developer")
public class DeveloperController {

	private static final Logger logger = LoggerFactory.getLogger(DeveloperController.class);

	@Autowired
	private AppService appService;

	@Autowired
	private DeveloperService developerService;


	@Autowired
	private EnvConsts envConsts;


	/**
	 * 查询应用列表(分页)
	 *
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "list", method = RequestMethod.POST)
	public ResponseResult listDeveloper(@RequestBody Map<String, Object> params, HttpSession session) {

		Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);

		Integer pageNow = (Integer) params.get("pageNow");
		Integer pageSize = (Integer) params.get("pageSize");

		String name = (String) params.get("name");
		String email = (String) params.get("email");
		String mobile = (String) params.get("mobile");

		List<User> list = developerService.listDeveloper(pageNow, pageSize, name, email , mobile);
		PageResult pageResult = new PageResult(new PageInfo<>(list));

		logger.debug("查询开发者列表成功, {}, {}, {}", name, email, mobile);
		return ResponseResult.success("查询成功", pageResult);
	}


	/**
	 * 删除应用
	 * @param Id
	 * @return
	 */
	@RequestMapping(value = "{Id}" , method = RequestMethod.DELETE)
	public ResponseResult deleteApp(@PathVariable Integer Id) {

		if (developerService.deleteDeveloper(Id) == ResponseConsts.CRUD_ERROR) {
			logger.debug("删除{}应用失败", Id);
			return ResponseResult.error("删除失败");
		}

		logger.debug("删除{}应用成功", Id);
		return ResponseResult.success("删除成功");
	}

	/**
	 * 获取单个应用
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "get" , method = RequestMethod.POST)
	public ResponseResult get(@RequestBody Map<String, Object> params,HttpSession session) {

		Integer pageNow = (Integer) params.get("pageNow");
		Integer pageSize = (Integer) params.get("pageSize");

		String name = (String) params.get("name");
		String status = (String) params.get("status");
		Date updateDate = TimeUtil.parseDate((String) params.get("updateDate"));
		String id = (String) params.get("developerId");
		Integer developerId = Integer.valueOf(id);

		List<App> list = appService.listApp(pageNow, pageSize, developerId, name, status, updateDate);
		PageResult pageResult = new PageResult(new PageInfo<>(list));

		// 加上图片目录前缀
		for (App app : list) {
			app.setLogoFile("/" + envConsts.FILE_APP_LOGO_DIC + "/" + app.getLogoFile());
		}

		logger.debug("查询应用列表成功, {}, {}, {},{} ", name, status, updateDate,id);
		return ResponseResult.success("查询成功", pageResult);
	}


}
