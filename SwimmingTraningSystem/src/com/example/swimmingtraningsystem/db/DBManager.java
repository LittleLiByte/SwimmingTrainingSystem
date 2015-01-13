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
 * ���ݿ⹤����
 * 
 * @author LittleByte
 * 
 */
public class DBManager {
	private static DBManager dbManager = new DBManager();

	private DBManager() {

	}

	/**
	 * ��ȡ������DBManager
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
	 * ͨ���û����������¼����
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
	 * ͨ����¼������user����
	 * 
	 * @param name
	 *            �ӵ�¼����ȡ������Ϊ�գ���˲����ж�
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
	 * ���һ���˶�Ա����Ϣ�����ݿ�
	 * 
	 * @param a
	 *            �˶�Ա
	 */
	public void addAthlete(Athlete a) {
		a.save();
	}

	public Athlete getLatestAthlete() {
		Athlete a = DataSupport.findLast(Athlete.class, true);
		return a;

	}

	/**
	 * ��ȡ��ǰ�û����˶�Ա���������˶�Ա
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
	 * ���ݵ�ǰ�û�id���˶�Ա���ֲ�ѯ���˶�Ա��Ϣ
	 * 
	 * @param name
	 *            �˶�Ա����
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
	 * ������ѯ�˶�Ա
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
	 * ���� Athlete��ĸ�����Ϣ
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
	 * �ж��Ƿ��Ѵ��ڸ������ֵ��˶�Ա
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
	 * ��Ӽƻ�
	 * 
	 * @param p
	 */
	public void addPlan(Plan p) {
		p.save();
	}

	/**
	 * ɾ��ѡ���ļƻ�
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
	 * ����table_plan�е�ǰ�û������мƻ�
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
	 * ���ݼƻ�id���Ҷ�Ӧ�ļƻ�
	 * 
	 * @param id
	 * @return
	 */
	public Plan queryPlan(long id) {
		Plan plan = DataSupport.find(Plan.class, id);
		return plan;
	}

	/**
	 * �ж�ָ���û��ļƻ������Ƿ���������������ͬ�ļƻ���
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
	 * ������ѯ����ѯ������
	 * 
	 * @param id
	 * @return
	 */
	public List<Athlete> getAthInPlan(long id) {
		Plan p = DataSupport.find(Plan.class, id, true);
		List<Athlete> athletes = p.getAthlete();
		return athletes;
	}

	// �ҳ��ɼ�����ָ���˶�Աid�ĳɼ����ݼ�
	public List<Score> getScoreByAth(Long id) {
		String ids = String.valueOf(id);
		List<Score> s = DataSupport.where("athlete_id=?", ids)
				.find(Score.class);
		return s;
	}

	/**
	 * ͨ�������ҳ��ɼ�
	 * 
	 * @param date
	 * @return
	 */
	public List<Score> getScoreByDate(String date) {
		List<Score> list = DataSupport.where("date=?", date).find(Score.class);
		return list;
	}

	/**
	 * ͨ���������ں���Ӿ�������ҳɼ�
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
	 * ͨ�����ڲ��ҳɼ�����ڵ��˶�Աid,Ȼ����ͨ�����ں��˶�Աid���ҳɼ���������Ӧ�� ��γɼ���������ܺ�
	 * 
	 * @param date
	 *            ����
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
