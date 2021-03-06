package com.scmaster.gittest;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scmaster.gittest.dao.ShareDAO;
import com.scmaster.gittest.dao.UserDAO;
import com.scmaster.gittest.vo.Daily_Scd;
import com.scmaster.gittest.vo.Shared_Scd;
import com.scmaster.gittest.vo.User;

@Controller
public class ShareController {
	private static final Logger logger = LoggerFactory.getLogger(ShareController.class);
	
	@Autowired
	UserDAO userDao;
	
	@Autowired
	ShareDAO shareDao;
	
	@ResponseBody
	@RequestMapping(value = "checkId", method = RequestMethod.POST)
	public int shareAdmin(String user_id){
		System.out.println(user_id);
		User user = userDao.login_User(user_id);
		if(user != null)
			return 1;
		return 0;
	}
	
	@ResponseBody
	@RequestMapping(value = "read_shared_scd", method = RequestMethod.GET)
	public ArrayList<Integer> read_shared_scd (String userId){
		ArrayList<Integer> sharedScdSqList = shareDao.getSharedScdSq(userId);
		
		return sharedScdSqList;
	}
	
	@ResponseBody
	@RequestMapping(value = "get_Daily_Scd", method = RequestMethod.POST)
	public int get_Daily_Scd(int dtl_sq){
		Daily_Scd daily_scd = shareDao.get_Daily_Scd(dtl_sq);
		int daily_sq = daily_scd.getDaily_sq();
		int daily_ord = shareDao.get_daily_ord(daily_sq);
		return daily_ord;
	}
	
	@ResponseBody
	@RequestMapping(value = "addShareAuthority", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	public String authorityDupChk(String searchId, int scd_sq, HttpSession session){
		HashMap<String, Object> map = new HashMap<>();
		map.put("searchId", searchId);
		map.put("scd_sq", scd_sq);
		map.put("user_id", session.getAttribute("user_id"));
		Shared_Scd shared_scd = shareDao.authorityDupChk(map);
		String inform = null;
		if(shared_scd == null){
			int result = 0;
			result = shareDao.addShareAuthority(map);
			if (result == 1) {
				inform = map.get("searchId") + "님과 함께 " + map.get("scd_sq") + " 의 일정을 공유하실 수 있습니다.";
				logger.info(inform);
				return inform;
			} else {
				inform = map.get("scd_sq") + " 의 일정의 공유 권한을  " + map.get("searchId") + "님에게 전송 실패하였습니다.";
				logger.info(inform);
				return inform;
			}
		} else{
			inform = "이 일정을 이미 공유하고 있습니다.";
			logger.info(inform);
			return inform;
		}
	}
	
	
}
