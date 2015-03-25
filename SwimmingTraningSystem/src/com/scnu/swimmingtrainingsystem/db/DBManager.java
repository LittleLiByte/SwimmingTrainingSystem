package com.scnu.swimmingtrainingsystem.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.content.ContentValues;

import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.Score;
import com.scnu.swimmingtrainingsystem.model.Temp;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;

/**
 * ���ݿ������
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

	/**
	 * ����id��ȡ�û�
	 * 
	 * @param id
	 *            �û�id
	 * @return
	 */
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

	/**
	 * �޸ĸ�id�ĵ�¼����
	 * 
	 * @param id
	 *            �û�id
	 * @param newPassword
	 *            ������
	 * @return
	 */
	public int modifyUserPassword(long id, String newPassword) {
		ContentValues values = new ContentValues();
		values.put("password", newPassword);
		return DataSupport.update(User.class, values, id);
	}

	/**
	 * ��ȡ��ǰ�û����˶�Ա���������˶�Ա
	 * 
	 * @return
	 */
	public List<Athlete> getAthletes(long userId) {
		String id = String.valueOf(userId);
		List<Athlete> athletes = DataSupport.where("user_id=?", id).find(
				Athlete.class, true);
		return athletes;
	}

	public Athlete getAthletesByAid(int aid) {
		String athlete_id = String.valueOf(aid);
		Athlete athlete = DataSupport.where("aid=?", athlete_id)
				.find(Athlete.class).get(0);
		return athlete;
	}

	/**
	 * ��ȡ���û�������ѯ�ƻ����˶�Ա
	 * 
	 * @param athIds
	 * @param userId
	 * @return
	 */
	public List<Athlete> getAthletesByAid(Integer[] athIds, Long userId) {
		String thisUserId = String.valueOf(userId);
		List<Athlete> athleteList = new ArrayList<Athlete>();
		for (int aid : athIds) {
			String id = String.valueOf(aid);
			List<Athlete> athletes = DataSupport.where("user_id=? and aid=? ",
					thisUserId, id).find(Athlete.class);
			if (athletes.size() != 0) {
				athleteList.add(athletes.get(0));
			}
		}
		return athleteList;

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

	/**
	 * �����������ݻ�ȡ��Ӧ���˶�Ա����
	 * 
	 * @param names
	 *            �˶�Ա����
	 * @return �˶�Ա�����б�
	 */
	public List<Athlete> getAthleteByNames(List<String> names) {
		List<Athlete> lists = new ArrayList<Athlete>();
		for (String name : names) {
			Athlete athlete = DataSupport.where("name=?", name)
					.find(Athlete.class).get(0);
			lists.add(athlete);
		}
		return lists;
	}

	/**
	 * ͨ��������ѯ�����ݳɼ�id�����˶�Ա����
	 * 
	 * @param id
	 *            �ɼ�id
	 * @return �˶�Ա����
	 */
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
			String age, String gender, String phone, String extras) {

		Long id = list.get(postion).getId();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("age", Integer.parseInt(age));
		values.put("gender", gender);
		values.put("phone", phone);
		values.put("extras", extras);
		DataSupport.update(Athlete.class, values, id);
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
	 * ����table_plan�е�ǰ�û������мƻ�
	 * 
	 * @return �ƻ��б�
	 */
	public List<Plan> getUserPlans(long userID) {
		String user = String.valueOf(userID);
		List<Plan> plans = DataSupport.where("user_id=?", user)
				.find(Plan.class);
		return plans;
	}

	/**
	 * ��ȡ���µļƻ�ID
	 * 
	 * @return ���µļƻ�id
	 */
	public Long getLatestPlanId() {
		long id = DataSupport.max(Plan.class, "id", Long.class);
		return id;
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
	 * ͨ���ƻ����ֻ�ȡ�ƻ�����
	 * 
	 * @param name
	 *            �ƻ�����
	 * @return �ƻ�����
	 */
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

	/**
	 * �ҳ��ɼ�����ָ���˶�Աid�ĳɼ����ݼ�
	 * 
	 * @param id
	 * @return
	 */
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
	 * ͨ�������ڳɼ����в��Ҷ�Ӧ���˶�ԱID��������Aid
	 * 
	 * @param date
	 * @return
	 */
	public List<Integer> getAthlteAidInScoreByDate(String date) {
		List<Integer> aidList = new ArrayList<Integer>();
		List<Score> scList = DataSupport.where("date=?", date).find(
				Score.class, true);
		int n = scList.size();
		for (int i = 0; i < n; i++) {
			aidList.add(scList.get(i).getAthlete().getAid());
		}
		return aidList;
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
				.find(Score.class, true);
		return scores;
	}

	/**
	 * ͨ�����ڲ��ҳɼ�
	 * 
	 * @param date
	 * @param athId
	 * @return
	 */
	public List<List<Score>> getScoreByDate(List<String> date, long athId) {
		String athleteID = String.valueOf(athId);
		List<List<Score>> s = new ArrayList<List<Score>>();
		for (String str : date) {
			s.add(DataSupport.where("date=? and athlete_id=?", str, athleteID)
					.find(Score.class));
		}
		return s;
	}

	public Plan getPlanByPid(int pid) {
		String planid = String.valueOf(pid);
		Plan plan = DataSupport.where("pid=?", planid).find(Plan.class).get(0);
		return plan;
	}

	/**
	 * ͨ�����ڻ�ȡ�����ɼ��ļƻ���Ȼ���ȡ������
	 * 
	 * @param date
	 * @return
	 */
	public Plan getPlanInScoreByDate(String date) {
		List<Score> scores = DataSupport.where("date=?", date).find(
				Score.class, true);
		if (scores.size() != 0) {
			return scores.get(0).getP();
		}
		return null;
	}

	/**
	 * ͨ�����ҳɼ����е����ڻ�ö�Ӧ�ƻ�id
	 * 
	 * @param date
	 *            ��������
	 * @param athId
	 *            �˶�Աid
	 * @return �ƻ�id
	 */
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

	/**
	 * �����ض�������һ�ֲ��Ե��˶�Աid,�������������ܳɼ�
	 * 
	 * @param date
	 * @return
	 */
	public List<Long> getAthleteNumberInScoreByDate(String date) {
		List<Long> athIds = new ArrayList<Long>();
		List<Score> list = DataSupport.select("athlete_id")
				.where("date=? and times=?", date, "1").find(Score.class, true);
		for (Score s : list) {
			athIds.add(s.getAthlete().getId());
		}
		return athIds;
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
			p.setScore(CommonUtils.scoreSum(sum));
			temps.add(p);
		}
		Collections.sort(temps, new ScoreComparable());
		return temps;
	}

	/**
	 * ͨ���˶�ԱID���Ҷ�Ӧ�ɼ�
	 * 
	 * @param athIDs
	 * @return
	 */
	public List<String> getScoresByAthleteId(List<Long> athIDs) {
		List<String> dates = new ArrayList<String>();
		List<Score> scores = new ArrayList<Score>();
		for (long athId : athIDs) {
			scores.addAll(DataSupport.where("athlete_id=?",
					String.valueOf(athId)).find(Score.class, true));
		}
		for (Score s : scores) {
			if (!dates.contains(s.getDate()))
				dates.add(s.getDate());
		}
		Collections.sort(dates, new DateComparable());
		return dates;

	}

	/**
	 * �ɼ��Ƚ�
	 * 
	 * @author LittleByte
	 * 
	 */
	class ScoreComparable implements Comparator<Temp> {

		@Override
		public int compare(Temp lhs, Temp rhs) {
			// TODO Auto-generated method stub
			Temp temp1 = lhs;
			Temp temp2 = rhs;
			int num = temp1.getScore().compareTo(temp2.getScore());
			if (num == 0)
				return temp1.getAthleteName().compareTo(temp2.getAthleteName());
			return num;
		}

	}

	/**
	 * ���ڱȽ�
	 * 
	 * @author LittleByte
	 * 
	 */
	class DateComparable implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			// TODO Auto-generated method stub
			String s1 = lhs;
			String s2 = rhs;
			int num = s2.compareTo(s1);
			return num;
		}

	}

}
