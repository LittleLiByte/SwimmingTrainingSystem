package com.example.swimmingtraningsystem.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.content.ContentValues;

import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.model.Score;
import com.example.swimmingtraningsystem.model.Temp;
import com.example.swimmingtraningsystem.model.User;
import com.example.swimmingtraningsystem.util.MyComparable;
import com.example.swimmingtraningsystem.util.XUtils;

/**
 * 数据库工具类
 * 
 * @author LittleByte
 * 
 */
public class DBManager {
	private static DBManager dbManager = new DBManager();

	private DBManager() {

	}

	/**
	 * 获取单例的DBManager
	 * 
	 * @return
	 */
	public static DBManager getInstance() {
		return dbManager;
	}

	public User getUser(long id) {
		return DataSupport.find(User.class, id);
	}


	/**
	 * 通过用户名查找其登录密码
	 * 
	 * @param name
	 * @return
	 */
	public String getPassword(String name) {
		List<User> users = DataSupport.select("password")
				.where("username=?", name).find(User.class);
		if (users.size() != 0)
			return users.get(0).getPassword();
		else
			return "";
	}

	/**
	 * 通过登录名查找user对象
	 * 
	 * @param name
	 *            从登录名获取，不会为空，因此不用判断
	 * @return
	 */
	public User getUserByName(String name) {
		User user = null;
		List<User> users = DataSupport.where("username=?", name).find(
				User.class);
		if (users.size() != 0) {
			user = users.get(0);
		}
		return user;
	}

	public int modifyUserPassword(long id, String newPassword) {
		ContentValues values = new ContentValues();
		values.put("password", newPassword);
		return DataSupport.update(User.class, values, id);
	}

	/**
	 * 添加一名运动员的信息到数据库
	 * 
	 * @param a
	 *            运动员
	 */
	public void addAthlete(Athlete a) {
		a.save();
	}

	public Athlete getLatestAthlete() {
		Athlete a = DataSupport.findLast(Athlete.class, true);
		return a;

	}

	/**
	 * 获取当前用户的运动员表中所有运动员
	 * 
	 * @return
	 */
	public List<Athlete> getAthletes(long userId) {
		String id = String.valueOf(userId);
		List<Athlete> athletes = DataSupport.where("user_id=?", id).find(
				Athlete.class);
		return athletes;
	}

	/**
	 * 根据当前用户id和运动员名字查询出运动员信息
	 * 
	 * @param name
	 *            运动员姓名
	 * @return
	 */
	public Athlete getAthleteByName(long userID, String name) {
		String id = String.valueOf(userID);
		List<Athlete> aths = DataSupport.where("user_id=? and name=? ", id,
				name).find(Athlete.class);
		if (aths.size() != 0) {
			Athlete athlete = aths.get(0);
			return athlete;
		}
		return null;
	}

	public List<Athlete> getAthleteByNames(List<String> names) {
		List<Athlete> lists = new ArrayList<Athlete>();
		for (String name : names) {
			Athlete athlete = DataSupport.where("name=?", name)
					.find(Athlete.class).get(0);
			lists.add(athlete);
		}
		return lists;
	}

	public String getAthleteNameByScoreID(long id) {
		String name = DataSupport.find(Score.class, id, true).getAthlete()
				.getName();
		return name;

	}

	/**
	 * 批量查询运动员
	 * 
	 * @param athID
	 * @return
	 */
	public List<Athlete> getAthletes(List<Long> athID) {
		List<Athlete> athletes = new ArrayList<Athlete>();
		for (long id : athID) {
			Athlete a = DataSupport.find(Athlete.class, id);
			athletes.add(a);
		}
		return athletes;
	}

	/**
	 * 更新 Athlete表的个人信息
	 * 
	 * @param list
	 * @param postion
	 * @param name
	 * @param age
	 * @param phone
	 * @param extras
	 */
	public void updateAthlete(List<Athlete> list, int postion, String name,
			String age, String phone, String extras) {

		Long id = list.get(postion).getId();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("age", Integer.parseInt(age));
		values.put("phone", phone);
		values.put("extras", extras);
		DataSupport.update(Athlete.class, values, id);
	}

	public void updateAthlete(Athlete a, int postion) {
		a.update(postion);
	}

	public int deleteAthlete(List<Athlete> list, int position) {
		String userID = String.valueOf(list.get(position).getId());
		return DataSupport.deleteAll(Athlete.class, "id=?", userID);
	}

	/**
	 * 判断是否已存在给定名字的运动员
	 * 
	 * @param id
	 * @param name
	 * @return
	 */
	public boolean isAthleteNameExsit(long id, String name) {
		String uid = String.valueOf(id);
		List<Athlete> athletes = DataSupport.where("user_id=?", uid).find(
				Athlete.class);
		for (int i = 0; i < athletes.size(); i++) {
			if (name.equals(athletes.get(i).getName())) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 添加计划
	 * 
	 * @param p
	 */
	public void addPlan(Plan p) {
		p.save();
	}

	/**
	 * 删除选定的计划
	 * 
	 * @param plans
	 */
	public void deletePlans(List<Plan> plans) {
		for (Plan p : plans) {
			DataSupport.delete(Plan.class, p.getId());
		}
	}

	public List<Plan> getAllPlans() {
		List<Plan> plans = DataSupport.findAll(Plan.class);
		return plans;
	}

	/**
	 * 查找table_plan中当前用户的所有计划
	 * 
	 * @return
	 */
	public List<Plan> getUserPlans(long userID) {
		String user = String.valueOf(userID);
		List<Plan> plans = DataSupport.where("user_id=?", user)
				.find(Plan.class);
		return plans;
	}

	public Long getLatestPlanId() {
		int size = DataSupport.count(Plan.class);
		if (size != 0) {
			return DataSupport.findLast(Plan.class).getId();
		} else {
			return 0L;
		}
	}

	/**
	 * 根据计划id查找对应的计划
	 * 
	 * @param id
	 * @return
	 */
	public Plan queryPlan(long id) {
		Plan plan = DataSupport.find(Plan.class, id);
		return plan;
	}

	/**
	 * 判断指定用户的计划表中是否存在与给定名字相同的计划表
	 * 
	 * @param id
	 * @param name
	 * @return
	 */
	public boolean isNameExsit(long id, String name) {
		boolean isExsit = false;
		for (Plan p : getUserPlans(id)) {
			if (name.equals(p.getName())) {
				isExsit = true;
				break;
			}
		}
		return isExsit;

	}

	public int getPlanCount() {
		int count = 0;
		count = DataSupport.count(Plan.class);
		return count;
	}

	public List<Plan> getPlanByPName(String name) {
		List<Plan> plans = DataSupport.where("name=?", name).find(Plan.class,
				true);
		return plans;

	}

	/**
	 * 激进查询，查询关联表
	 * 
	 * @param id
	 * @return
	 */
	public List<Athlete> getAthInPlan(long id) {
		Plan p = DataSupport.find(Plan.class, id, true);
		List<Athlete> athletes = p.getAthlete();
		return athletes;
	}

	// 找出成绩表中指定运动员id的成绩数据集
	public List<Score> getScoreByAth(Long id) {
		String ids = String.valueOf(id);
		List<Score> s = DataSupport.where("athlete_id=?", ids)
				.find(Score.class);
		return s;
	}

	/**
	 * 通过日期找出成绩
	 * 
	 * @param date
	 * @return
	 */
	public List<Score> getScoreByDate(String date) {
		List<Score> list = DataSupport.where("date=?", date).find(Score.class);
		return list;
	}

	/**
	 * 通过测试日期和游泳趟数查找成绩
	 * 
	 * @param date
	 * @param times
	 * @return List<Score> score
	 */
	public List<Score> getScoreByDateAndTimes(String date, int times) {
		String str = String.valueOf(times);
		List<Score> scores = DataSupport.where("date=? and times=?", date, str)
				.find(Score.class);
		return scores;

	}

	public List<List<Score>> getScoreByDate(List<String> date, long athId) {
		String athleteID = String.valueOf(athId);
		List<List<Score>> s = new ArrayList<List<Score>>();
		for (String str : date) {
			s.add(DataSupport.where("date=? and athlete_id=?", str, athleteID)
					.find(Score.class));
		}
		return s;
	}

	public List<String> getPlanInScoreByDate(List<String> date) {
		List<String> planNames = new ArrayList<String>();
		for (String str : date) {
			List<Score> list = DataSupport.where("date=?", str).find(
					Score.class, true);
			planNames.add(list.get(0).getP().getName());
		}
		return planNames;
	}

	public List<Long> getPlanInScoreByDate(List<String> date, long athId) {
		String athleteID = String.valueOf(athId);
		List<Long> plans = new ArrayList<Long>();
		for (String str : date) {
			List<Score> list = DataSupport.where(
					"date=? and athlete_id=? and times=?", str, athleteID, "1")
					.find(Score.class, true);
			for (Score s : list) {
				Plan p = DataSupport.find(Score.class, s.getId(), true).getP();
				plans.add(p.getId());
			}

		}
		return plans;

	}

	public List<Score> getAthleteNumberInScoreByDate(String date) {

		List<Score> list = DataSupport.select("athlete_id")
				.where("date=? and times=?", date, "1").find(Score.class, true);
		return list;
	}

	/**
	 * 通过日期查找成绩表存在的运动员id,然后再通过日期和运动员id查找成绩表查找其对应的 多次成绩并计算出总和
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public List<Temp> getAthleteIdInScoreByDate(String date, List<Long> list) {
		List<Temp> temps = new ArrayList<Temp>();
		List<Score> scores = new ArrayList<Score>();
		for (long id : list) {
			scores = DataSupport.select("score", "athlete_id")
					.where("date=? and athlete_id=?", date, String.valueOf(id))
					.find(Score.class, true);

			List<String> sum = new ArrayList<String>();
			for (Score s : scores) {
				sum.add(s.getScore());
			}

			Temp p = new Temp();
			p.setAthleteName(DataSupport.find(Athlete.class, id).getName());
			p.setScore(XUtils.scoreSum(sum));
			temps.add(p);
		}
		Collections.sort(temps, new MyComparable());
		return temps;
	}

	// public List<Integer> getRecentDateInScore() {
	// List<Integer> ints = new ArrayList<Integer>();
	// List<String> dates = new ArrayList<String>();
	// List<Score> scores = DataSupport.findAll(Score.class);
	// for (Score s : scores) {
	// if (!dates.contains(s.getDate())) {
	// dates.add(s.getDate());
	// List<Score> lists = DataSupport.where("date=?", s.getDate())
	// .find(Score.class);
	// }
	// }
	// return ints;
	// }

	public long getLatestScoreID() {
		int size = DataSupport.count(Score.class);
		if (size != 0) {
			return DataSupport.findLast(Score.class).getId();
		} else {
			return 0L;
		}
	}
}
