package com.scnu.swimmingtrainingsystem.util;

/**
 * ����ϵͳ���賣����
 * 
 * @author LittleByte
 */
public class Constants {

	/**
	 * ��ʱʱ������
	 */
	public static final int SOCKET_TIMEOUT = 100;

	/**
	 * �ɼ����� 1:��ͨ�ɼ�
	 */
	public static final int NORMALSCORE = 1;
	/**
	 * �ɼ����� 2:���μ�Ƶ�ɼ�
	 */
	public static final int FrequenceSCORE = 2;
	/**
	 * �ɼ����� 3:��̳ɼ�
	 */
	public static final int SPRINTSCORE = 3;

	/**
	 * SharePreference���õ�loginInfo�Ĺؼ���
	 */
	public static final String LOGININFO = "loginInfo";

	/**
	 * �����ǵڼ��μ�ʱ�������û��ǵڼ��μ�ʱ֮��
	 */
	public static final String CURRENT_SWIM_TIME = "current";

	/**
	 * ������ѡ�ļƻ�ID
	 */
	public static final String PLAN_ID = "planID";

	/**
	 * ���濪ʼ��ʱ������
	 */
	public static final String TEST_DATE = "testDate";

	/**
	 * �����ֶ�ƥ���ʱ���������е��˶�Ա����,�������һ�˼�ʱ�ⲻ���ٴ��϶��˶�Ա��������
	 */
	public static final String DRAG_NAME_LIST = "dragList";

	/**
	 * ���浱ǰ��¼���û�id
	 */
	public static final String CURRENT_USER_ID = "CurrentUser";

	/**
	 * ���浱ǰ�Ƿ�������ӷ�������״̬
	 */
	public static final String IS_CONNECT_SERVER = "isConnect";

	/**
	 * �ϴε�½��user_id
	 */
	public static final String LAST_LOGIN_USER_ID = "lastLoginUser";

	public static final String COMPLETE_NUMBER = "completeNumber";

	/**
	 * ���û��Ƿ��ǵ�һ�ε�½Ӧ��
	 */
	public static final String IS_THIS_USER_FIRST_LOGIN = "isThisUserFirstLogin";

	/**
	 * Log��Ϣ����tag--com.scnu.swimmingtrainingsystem
	 */
	public static final String TAG = "com.scnu.swimmingtrainingsystem";

	/**
	 * ȡ��
	 */
	public static final String CANCLE_STRING = "ȡ��";

	/**
	 * ȷ��
	 */
	public static final String OK_STRING = "ȷ��";

	/**
	 * ��ӳɹ�
	 */
	public static final String ADD_SUCCESS_STRING = "��ӳɹ�";

	public static final String SELECTED_POOL = "selectedPool";

	public static final String SWIM_DISTANCE = "swimDistance";

	public static final String FISRTOPENATHLETE = "fisrtOpenAthlete";

	public static final String FISRTOPENPLAN = "fisrtOpenPlan";

	public static final String FISRTSTARTTIMING = "fisrtStartTiming";

	public static final String CURRENT_DISTANCE = "currentDistance";

	public static final String SCORESJSON = "scoresJson";

	public static final String ATHLETEJSON = "athleteJson";
	/**
	 * ʹ��˵������
	 */
	public static final String[] TITLES = new String[] { "1��IP��˿�����˵��",
			"2����¼˵��", "3����ʱ��ģ��˵��", "4��С����ģ��˵��", "5���ɼ���ѯģ��" };

	/**
	 * ʹ��˵������
	 */
	public static final String[][] CONTENTS = new String[][] {
			{ "���Ҫ���ӷ����������������÷�������IP�Ͷ˿ڵ�ַ��������������IPһ��Ϊ192.168.x.xxx���˿ڵ�ַһ��Ϊ8080.�����δ������������"
					+ "���Ժ��Ա�����ʾ" },
			{ "��¼ʱ�û��������������Ϊ�գ������δע���ҷ�������δ����������ֱ��ʹ�á�Ĭ���ʺš����е�¼���á�����������Ѿ��ɹ�����������ʹ�ñ���"
					+ "������ע�����ʺţ���ʹ�����ʺŵ�¼ʹ�á���Ĭ���˺�ֻ�����ã�ֻ��ʹ��ע���ʺŲſ��Խ������ϴ��������������б�������ݷ�����" },
			{ "Ҫʹ�ñ�ģ�飬���Ⱦ���Ҫ¼���˶�Ա��Ϣ��Ȼ���ڼ�ʱ��ģ���У���ÿ�μ�ʱ֮ǰ��Ҫѡ���˶�Ա�����ҿ�������Ӿ�ش�С����Ӿ��Ŀ������Լ�����"
					+ "��ʱ�ı�ע����������ÿ�μ�ʱ�ɼ��� Ȼ����ܿ�ʼ��ʱ���ڼ�ʱҳ���У������ʱ���̼��ɿ�ʼ���ٴε��֮����¼�µ�ǰʱ�䣬���˼�ʱ��"
					+ "��֮�����ת������ҳ�棬���˶�Ա˳����е����͸����Լ�����ɾ��һЩ��ʱʱ����˶�Ա��Ҳ��ֱ�����������ڼ��걾�����гɼ�ʱ�����ܵĵ�����" },
			{ "��ģ�������μ�Ƶ�ͳ�̼�ʱ���ܣ������ɽ����л�������֧�ֽ�������沢�ϴ���������" },
			{ "������ʱ�����ɼ������Խ��б��ز�ѯ��������ѯ" } };

	public static final String CURRENT_DISTANCE_IS_NULL = "current_distance_is_null";

	public static final String NUMBER_NOT_EQUAL = "number_not_equal";

	public static final String CORRECT = "correct";
	
}
